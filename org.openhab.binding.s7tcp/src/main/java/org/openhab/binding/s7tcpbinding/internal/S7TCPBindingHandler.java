/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.s7tcpbinding.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.s7tcpbinding.handler.S7ThingHandler;
import org.openhab.binding.s7tcpbinding.internal.config.S7TCPBindingConfiguration;
import org.openhab.binding.s7tcpbinding.internal.exception.NotConnectedException;
import org.openhab.binding.s7tcpbinding.internal.plc.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David
 *
 */
public class S7TCPBindingHandler extends BaseBridgeHandler {
    private static final Logger logger = LoggerFactory.getLogger(S7TCPBindingHandler.class);

    public static final int NUMBER_SHORTS = 220;
    public static final int NUMBER_FLOATS = 220;
    public static final int NUMBER_BOOLEANS = 960;

    private static final int SOCKET_TIMEOUT = 2000;
    private @Nullable String host;
    private int port;
    private @Nullable Socket socket;
    private DataOutputStream request;
    private DataInputStream response;
    private final Object lock = new Object();
    private @Nullable ScheduledFuture<?> refreshJob;

    private Packet receivePacket;
    private Packet sendPacket;
    private long lastSentAt;

    public S7TCPBindingHandler(Bridge thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initialising S7TCPBinding Bridge handler...");

        S7TCPBindingConfiguration config = getConfigAs(S7TCPBindingConfiguration.class);
        host = config.host;
        port = config.port;
        sendPacket = createInitialPacket();
        receivePacket = createInitialPacket();
        Runnable refreshS7Units = () -> {
            try {
                checkConnection();
                updateStatus(ThingStatus.ONLINE);
                receive();
                for (Thing t : getThing().getThings()) {
                    S7ThingHandler h = (S7ThingHandler) t.getHandler();
                    if (h != null) {
                        h.refresh();
                    }
                }
            } catch (S7TCPClientError e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            }
        };
        refreshJob = scheduler.scheduleWithFixedDelay(refreshS7Units, 0, config.refresh, TimeUnit.SECONDS);
    }

    @Override
    public void dispose() {
        if (refreshJob != null) {
            refreshJob.cancel(true);
        }
        super.dispose();
    }

    public boolean isConnected() {
        synchronized (this.lock) {
            return socket != null && socket.isConnected() && !socket.isClosed();
        }
    }

    public Packet receiveFromCache() throws S7TCPClientError {
        return receivePacket;
    }

    public Packet receive() throws S7TCPClientError {
        send(sendPacket);
        return receivePacket;
    }

    private void send(Packet p) throws S7TCPClientError {
        synchronized (this.lock) {
            checkConnection();
            try {

                request.write(p.asByteArray());

                byte[] responseArray = new byte[NUMBER_SHORTS * 2 + NUMBER_FLOATS * 4 + NUMBER_BOOLEANS / 8];
                response.read(responseArray);
                receivePacket = new Packet(NUMBER_SHORTS, NUMBER_FLOATS, NUMBER_BOOLEANS, responseArray);

            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    /*
     * Verify that the client socket is connected and responding, and try to reconnect if possible.
     * May block for 1-2 seconds.
     *
     * Throws S7TCPClientError if there is a connection problem.
     *
     */
    public void checkConnection() throws S7TCPClientError {
        throttle();
        synchronized (this.lock) {
            try {
                if (!isConnected()) {
                    connect();
                    if (!isConnected()) {
                        throw new S7TCPClientError(String.format("Failed to connect to %s:%s", host, port));
                    }
                }
            } catch (IOException e) {
                disconnect();
                logger.error("{}", e.getLocalizedMessage(), e);
                throw new S7TCPClientError(String.format("No response from S7TCP unit %s:%s", host, port));
            }
        }
    }

    private void connect() throws IOException {
        synchronized (this.lock) {
            try {
                socket = new Socket(host, port);
                socket.setSoTimeout(SOCKET_TIMEOUT);
                request = new DataOutputStream(socket.getOutputStream());
                response = new DataInputStream(socket.getInputStream());
            } catch (UnknownHostException e) {
                logger.error("unknown socket host {}", host);
                socket = null;
            } catch (SocketException e) {
                logger.error("{}", e.getLocalizedMessage(), e);
                socket = null;
            }
        }
    }

    private void throttle() {
        // only if necessary
        if (System.currentTimeMillis() - lastSentAt < 50) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error("", e);
            }
        }
        lastSentAt = System.currentTimeMillis();
    }

    public void disconnect() {
        synchronized (this.lock) {
            try {
                socket.close();

            } catch (IOException e1) {
                logger.error("{}", e1.getLocalizedMessage(), e1);
            }
            try {
                request.close();

            } catch (IOException e1) {
                logger.error("{}", e1.getLocalizedMessage(), e1);
            }
            try {
                response.close();

            } catch (IOException e1) {
                logger.error("{}", e1.getLocalizedMessage(), e1);
            }
            socket = null;
        }
    }

    public class S7TCPClientError extends Exception {
        private static final long serialVersionUID = 1L;

        public S7TCPClientError(String message) {
            super(message);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public Packet createInitialPacket() {
        List<Short> initialShorts = new ArrayList<>();
        for (int i = 0; i < NUMBER_SHORTS; i++) {
            initialShorts.add((short) 0);
        }

        List<Float> initialFloats = new ArrayList<>();
        for (int i = 0; i < NUMBER_FLOATS; i++) {
            if (i < 9) {
                initialFloats.add(i, 22.0f);
            } else {
                initialFloats.add(i, 0.0f);
            }
        }

        List<Boolean> initialBooleans = new ArrayList<>();
        for (int i = 0; i < NUMBER_BOOLEANS; i++) {
            initialBooleans.add(false);
        }

        return new Packet(initialShorts, initialFloats, initialBooleans);
    }

    public short getShort(int index) throws NotConnectedException {
        if (receivePacket == null) {
            throw new NotConnectedException("no connection to PLC established");
        }
        return receivePacket.getShorts().get(index);
    }

    public void setShort(int index, short value) throws S7TCPClientError {
        sendPacket.setShort(index, value);
        send(sendPacket);
    }

    public float getFloat(int index) throws NotConnectedException {
        if (receivePacket == null) {
            throw new NotConnectedException("no connection to PLC established");
        }
        return receivePacket.getFloats().get(index);
    }

    public void setFloat(int index, float value) throws S7TCPClientError {
        sendPacket.setFloat(index, value);
        send(sendPacket);
    }

    public boolean getBoolean(int index) throws NotConnectedException {
        if (receivePacket == null) {
            throw new NotConnectedException("no connection to PLC established");
        }
        return receivePacket.getBooleans().get(index);
    }

    public void setBoolean(int index, boolean value) throws S7TCPClientError {
        sendPacket.setBoolean(index, value);
        send(sendPacket);
    }

}
