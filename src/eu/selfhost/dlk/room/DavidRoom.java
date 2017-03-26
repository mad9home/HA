package eu.selfhost.dlk.room;

import eu.selfhost.dlk.PLCConnector;
import eu.selfhost.dlk.exception.NotConnectedException;

public abstract class DavidRoom implements Room {

	private PLCConnector connector;
	private float temperature;
	private boolean light1;
	private boolean light2;

	public DavidRoom(PLCConnector connector) {
		setPLCConnector(connector);
	}

	@Override
	public void setPLCConnector(PLCConnector connector) {
		this.connector = connector;
	}

	@Override
	public float getTemperature() {
		return temperature;
	}

	@Override
	public boolean isLight1TurnedOn() {
		return light1;
	}

	@Override
	public boolean isLight2TurnedOn() {
		return light2;
	}

	@Override
	public void setTemperature(float temperature) throws NotConnectedException {
		connector.setFloat(getTemperatureIndex(), temperature);
	}

	@Override
	public void toggleLight1() {
		connector.setBoolean(getLight1Index(), false);
		connector.setBoolean(getLight1Index(), true);
	}

	@Override
	public void toggleLight2() {
		connector.setBoolean(getLight2Index(), false);
		connector.setBoolean(getLight2Index(), true);
	}

	@Override
	public void update() throws NotConnectedException {
		temperature = connector.getFloat(getTemperatureIndex());
		light1 = connector.getBoolean(getLight1Index());
		if (isSecondLightSupported()) {
			light2 = connector.getBoolean(getLight2Index());
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Arbeitszimmer: Temperature (").append(temperature).append(") Light1 (").append(light1)
				.append(") Light2 (").append(light2).append(")");
		return sb.toString();
	}

}
