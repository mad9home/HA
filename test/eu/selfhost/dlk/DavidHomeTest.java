package eu.selfhost.dlk;

import static org.junit.Assert.fail;

import org.junit.Test;

public class DavidHomeTest {

	@Test
	public void testReceive() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetShort() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetShort() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFloat() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetFloat() throws Exception {
		DavidHome home = DavidHome.getInstance();
		home.receive();
		home.setFloat(0, 0f);
	}

	@Test
	public void testGetBoolean() {
		fail("Not yet implemented");
	}

}
