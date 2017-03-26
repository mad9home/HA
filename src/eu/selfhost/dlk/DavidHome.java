package eu.selfhost.dlk;

import eu.selfhost.dlk.exception.NotConnectedException;
import eu.selfhost.dlk.room.Room;
import eu.selfhost.dlk.room.impl.Arbeitszimmer;

public class DavidHome {

	private PLCConnector connector;
	private Room arbeitsZimmer;

	public DavidHome() {
		connector = PLCConnector.getInstance();
		arbeitsZimmer = new Arbeitszimmer(connector);
	}

	public void update() {
		try {
			connector.receive();
			arbeitsZimmer.update();
		} catch (NotConnectedException e) {
			// TODO reconnect and try again
			e.printStackTrace();
		}
	}
	
	public Room getArbeitsZimmer() {
		return arbeitsZimmer;
	}

}
