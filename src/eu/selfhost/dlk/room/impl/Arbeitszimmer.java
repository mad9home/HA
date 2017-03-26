package eu.selfhost.dlk.room.impl;

import eu.selfhost.dlk.PLCConnector;
import eu.selfhost.dlk.room.DavidRoom;

public class Arbeitszimmer extends DavidRoom {

	private static final int HOME_OFFICE_TEMPERATURE_INDEX = 0;
	private static final int HOME_OFFICE_LIGHT1_INDEX = 0;

	public Arbeitszimmer(PLCConnector connector) {
		super(connector);
	}

	@Override
	public int getTemperatureIndex() {
		return HOME_OFFICE_TEMPERATURE_INDEX;
	}

	@Override
	public int getLight1Index() {
		return HOME_OFFICE_LIGHT1_INDEX;
	}

	@Override
	public int getLight2Index() {
		// TODO throw unsupported exception
		return 0;
	}

	@Override
	public boolean isSecondLightSupported() {
		return false;
	}

}
