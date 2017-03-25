package eu.selfhost.dlk.room;

import eu.selfhost.dlk.exception.NotConnectedException;

public interface Room {

	float getTemperature();

	void setTemperature(float temperature) throws NotConnectedException;

	void toggleLight1();

	void toggleLight2();

	void update() throws NotConnectedException;
}
