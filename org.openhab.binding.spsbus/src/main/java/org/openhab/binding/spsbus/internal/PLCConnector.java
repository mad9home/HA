package org.openhab.binding.spsbus.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.openhab.binding.spsbus.internal.exception.NotConnectedException;

/**
 *
 * @author David
 *
 */
public class PLCConnector {

    private static final String HOST = "10.100.0.1";
    private static final int PORT = 2012;
    private static final int SOCKET_TIMEOUT = 10000;

    public static final int NUMBER_SHORTS = 220;
    public static final int NUMBER_FLOATS = 220;
    public static final int NUMBER_BOOLEANS = 960;

    private static PLCConnector connector;
    private Packet receivePacket;
    private Packet sendPacket;
    private long lastSentAt;

    private PLCConnector() {
        sendPacket = createInitialPacket();
    }

    public static PLCConnector getInstance() {
        if (connector == null) {
            connector = new PLCConnector();
        }
        return connector;
    }

    public Packet receive() {
        send(sendPacket);
        return receivePacket;
    }

    public void send(Packet p) {
        throttle();
        try (Socket socket = new Socket(HOST, PORT);
                DataOutputStream request = new DataOutputStream(socket.getOutputStream());
                DataInputStream response = new DataInputStream(socket.getInputStream())) {
            socket.setSoTimeout(SOCKET_TIMEOUT);

            request.write(p.asByteArray());

            byte[] responseArray = new byte[NUMBER_SHORTS * 2 + NUMBER_FLOATS * 4 + NUMBER_BOOLEANS / 8];
            response.read(responseArray);

            receivePacket = new Packet(NUMBER_SHORTS, NUMBER_FLOATS, NUMBER_BOOLEANS, responseArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void throttle() {
        // only if necessary
        if (System.currentTimeMillis() - lastSentAt < 50) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastSentAt = System.currentTimeMillis();
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

    public void setShort(int index, short value) {
        sendPacket.setShort(index, value);
        send(sendPacket);
    }

    public float getFloat(int index) throws NotConnectedException {
        if (receivePacket == null) {
            throw new NotConnectedException("no connection to PLC established");
        }
        return receivePacket.getFloats().get(index);
    }

    public void setFloat(int index, float value) {
        sendPacket.setFloat(index, value);
        send(sendPacket);
    }

    public boolean getBoolean(int index) throws NotConnectedException {
        if (receivePacket == null) {
            throw new NotConnectedException("no connection to PLC established");
        }
        return receivePacket.getBooleans().get(index);
    }

    public void setBoolean(int index, boolean value) {
        sendPacket.setBoolean(index, value);
        send(sendPacket);
    }

}
