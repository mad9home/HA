package eu.selfhost.dlk.room;

import eu.selfhost.dlk.PLCConnector;
import eu.selfhost.dlk.exception.NotConnectedException;

public interface Room {

	void setPLCConnector(PLCConnector connector);

	// receive / read

	float getTemperature();

	boolean isLight1TurnedOn();

	boolean isLight2TurnedOn();

	void update() throws NotConnectedException;

	// send / write

	void setTemperature(float temperature) throws NotConnectedException;

	void toggleLight1();

	void toggleLight2();

	// room configuration

	int getTemperatureIndex();

	int getLight1Index();

	int getLight2Index();

	boolean isSecondLightSupported();
}
