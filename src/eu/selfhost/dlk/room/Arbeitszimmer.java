package eu.selfhost.dlk.room;

import eu.selfhost.dlk.DavidHome;
import eu.selfhost.dlk.exception.NotConnectedException;

public class Arbeitszimmer implements Room {

	private static final int HOME_OFFICE_TEMPERATURE_INDEX = 0;
	private static final int HOME_OFFICE_LIGHT1_INDEX = 0;

	private DavidHome home;
	private float currentTemperature;
	private boolean light1;

	public Arbeitszimmer() {
		this.home = DavidHome.getInstance();
	}

	@Override
	public float getTemperature() {
		return currentTemperature;
	}

	@Override
	public void setTemperature(float temperature) throws NotConnectedException {
		home.setFloat(HOME_OFFICE_TEMPERATURE_INDEX, temperature);
	}

	@Override
	public void update() throws NotConnectedException {
		home.receive();
		currentTemperature = home.getFloat(HOME_OFFICE_TEMPERATURE_INDEX);
		light1 = home.getBoolean(HOME_OFFICE_LIGHT1_INDEX);
	}

	@Override
	public void toggleLight1() {
		home.setBoolean(HOME_OFFICE_LIGHT1_INDEX, false);
		home.setBoolean(HOME_OFFICE_LIGHT1_INDEX, true);
	}

	@Override
	public void toggleLight2() {
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Arbeitszimmer: Temperature (").append(currentTemperature).append(") Light1 (").append(light1)
				.append(")");
		return sb.toString();
	}

}
