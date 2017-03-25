package eu.selfhost.dlk.room;

import eu.selfhost.dlk.exception.NotConnectedException;

public interface Room {

	float getTemperature();

	void setTemperature(float temperature) throws NotConnectedException;

	void update() throws NotConnectedException;
}
