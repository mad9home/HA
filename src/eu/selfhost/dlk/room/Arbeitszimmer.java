package eu.selfhost.dlk.room;

import eu.selfhost.dlk.DavidHome;
import eu.selfhost.dlk.exception.NotConnectedException;

public class Arbeitszimmer implements Room {

	private static final int HOME_OFFICE_TEMPERATURE_INDEX = 0;

	private DavidHome home;
	private float currentTemperature;

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
		currentTemperature = home.getFloat(HOME_OFFICE_TEMPERATURE_INDEX);
	}

}
