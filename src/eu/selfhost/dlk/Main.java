package eu.selfhost.dlk;

import eu.selfhost.dlk.room.Room;

public class Main {

	public static void main(String[] args) {
		DavidHome home = new DavidHome();
		home.update();
		Room arbeitsZimmer = home.getArbeitsZimmer();
		System.out.println(arbeitsZimmer);
	}

}
