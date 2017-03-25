package eu.selfhost.dlk;

import eu.selfhost.dlk.exception.NotConnectedException;
import eu.selfhost.dlk.room.Arbeitszimmer;

public class Main {

	public static void main(String[] args) throws NotConnectedException {
		DavidHome home = DavidHome.getInstance();
		home.receive();
//		System.out.println(home.receive());
		
		Arbeitszimmer az = new Arbeitszimmer();
		az.update();
		System.out.println(az.getTemperature());
		az.setTemperature(20f);
		System.out.println(az.getTemperature());
	}
}
