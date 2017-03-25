package eu.selfhost.dlk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Collections;

import eu.selfhost.dlk.exception.NotConnectedException;

public class DavidHome {

	private static final String HOST = "10.100.0.1";
	private static final int PORT = 2012;
	private static final int SOCKET_TIMEOUT = 10000;

	private static final int NUMBER_SHORTS = 220;
	private static final int NUMBER_FLOATS = 220;
	private static final int NUMBER_BOOLEANS = 960;

	private static DavidHome davidHome;
	private Packet lastPacket;

	private DavidHome() {
	}

	public static DavidHome getInstance() {
		if (davidHome == null) {
			davidHome = new DavidHome();
		}
		return davidHome;
	}

	public Packet receive() {
		send(requestPacket());
		return lastPacket;
	}

	public void send(Packet p) {
		try (Socket socket = new Socket(HOST, PORT);
				DataOutputStream request = new DataOutputStream(socket.getOutputStream());
				DataInputStream response = new DataInputStream(socket.getInputStream())) {
			socket.setSoTimeout(SOCKET_TIMEOUT);

			request.write(p.asByteArray());

			byte[] responseArray = new byte[NUMBER_SHORTS * 2 + NUMBER_FLOATS * 4 + NUMBER_BOOLEANS / 8];
			response.read(responseArray);

			lastPacket = new Packet(NUMBER_SHORTS, NUMBER_FLOATS, NUMBER_BOOLEANS, responseArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Packet requestPacket() {
		return new Packet(Collections.nCopies(NUMBER_SHORTS, (short) 0), Collections.nCopies(NUMBER_FLOATS, 0f),
				Collections.nCopies(NUMBER_BOOLEANS, false));
	}

	public short getShort(int index) throws NotConnectedException {
		if (lastPacket == null) {
			throw new NotConnectedException("no connection to PLC established");
		}
		return lastPacket.getShorts().get(index);
	}

	public void setShort(int index, short value) throws NotConnectedException {
		if (lastPacket == null) {
			throw new NotConnectedException("no connection to PLC established");
		}
		lastPacket.setShort(index, value);
	}

	public float getFloat(int index) throws NotConnectedException {
		if (lastPacket == null) {
			throw new NotConnectedException("no connection to PLC established");
		}
		return lastPacket.getFloats().get(index);
	}

	public void setFloat(int index, float value) throws NotConnectedException {
		if (lastPacket == null) {
			throw new NotConnectedException("no connection to PLC established");
		}
		lastPacket.setFloat(index, value);
	}

	public boolean getBoolean(int index) throws NotConnectedException {
		if (lastPacket == null) {
			throw new NotConnectedException("no connection to PLC established");
		}
		return lastPacket.getBooleans().get(index);
	}

	public void setBoolean(int index, boolean value) throws NotConnectedException {
		if (lastPacket == null) {
			throw new NotConnectedException("no connection to PLC established");
		}
		lastPacket.setBoolean(index, value);
	}

}
