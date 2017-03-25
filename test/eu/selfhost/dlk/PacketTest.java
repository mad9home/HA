package eu.selfhost.dlk;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import eu.selfhost.dlk.exception.InvalidPacketException;

public class PacketTest {

	@Test
	public void testParse() throws Exception {
		Packet p = new Packet(Arrays.asList(new Short[] { 1, 2 }), Arrays.asList(new Float[] { 1.2f }),
				Arrays.asList(new Boolean[] { false }));
		Packet p2 = new Packet(2, 1, 1);
		p2.parse(p.asByteArray());
		assertEquals(p, p2);

	}

	@Test(expected = InvalidPacketException.class)
	public void testAsByteArrayEmptyList() throws Exception {
		new Packet(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()).asByteArray();
	}

}
