package org.openhab.binding.spsbus.internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.openhab.binding.spsbus.internal.exception.InvalidPacketException;

/**
 *
 * @author David
 *
 */
public class Packet {

    // may be changeable in the future
    private static final ByteOrder BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    private int nShorts;
    private int nFloats;
    private int nBooleans;

    private List<Short> shorts;
    private List<Float> floats;
    private List<Boolean> booleans;

    public Packet(int nShorts, int nFloats, int nBooleans) {
        this.nShorts = nShorts;
        this.nFloats = nFloats;
        this.nBooleans = nBooleans;

        shorts = new ArrayList<>(nShorts);
        floats = new ArrayList<>(nFloats);
        booleans = new ArrayList<>(nBooleans);
    }

    public Packet(int nShorts, int nFloats, int nBooleans, byte[] input) {
        this(nShorts, nFloats, nBooleans);
        parse(input);
    }

    public Packet(List<Short> shorts, List<Float> floats, List<Boolean> booleans) {
        this.nShorts = shorts.size();
        this.nFloats = floats.size();
        this.nBooleans = booleans.size();

        this.shorts = shorts;
        this.floats = floats;
        this.booleans = booleans;
    }

    public void parse(byte[] input) {
        ByteBuffer shortBuffer = ByteBuffer.allocate(nShorts * 2);
        shortBuffer.order(BYTE_ORDER);
        shortBuffer.put(input, 0, nShorts * 2);
        shortBuffer.position(0);

        ByteBuffer floatBuffer = ByteBuffer.allocate(nFloats * 4);
        floatBuffer.order(BYTE_ORDER);
        floatBuffer.put(input, shortBuffer.limit(), nFloats * 4);
        floatBuffer.position(0);

        ByteBuffer lastBuffer = ByteBuffer.allocate(nBooleans / 8);
        lastBuffer.order(BYTE_ORDER);
        lastBuffer.put(input, shortBuffer.limit() + floatBuffer.limit(), nBooleans / 8);
        lastBuffer.position(0);

        while (shortBuffer.hasRemaining()) {
            shorts.add(shortBuffer.getShort());
        }

        while (floatBuffer.hasRemaining()) {
            floats.add(floatBuffer.getFloat());
        }

        BitSet bits = BitSet.valueOf(lastBuffer);
        for (int j = 0; j < nBooleans; j++) {
            booleans.add(bits.get(j));
        }
    }

    public byte[] asByteArray() throws InvalidPacketException {
        if (nShorts <= 0 && nFloats <= 0 && nBooleans <= 0) {
            throw new InvalidPacketException("at least one of the lists (shorts, floats, booleans) was empty");
        }
        ByteBuffer result = ByteBuffer.allocate(nShorts * 2 + nFloats * 4 + nBooleans / 8);
        result.order(BYTE_ORDER);
        result.position(0);
        for (Short s : shorts) {
            result.putShort(s);
        }
        for (Float f : floats) {
            result.putFloat(f);
        }
        BitSet bits = new BitSet(nBooleans);
        for (int i = 0; i < nBooleans; i++) {
            bits.set(i, booleans.get(i));
        }
        result.put(bits.toByteArray());
        return result.array();
    }

    public List<Short> getShorts() {
        return Collections.unmodifiableList(shorts);
    }

    public List<Float> getFloats() {
        return Collections.unmodifiableList(floats);
    }

    public List<Boolean> getBooleans() {
        return Collections.unmodifiableList(booleans);
    }

    public void setShort(int index, short value) {
        shorts.set(index, value);
    }

    public void setFloat(int index, float value) {
        floats.set(index, value);
    }

    public void setBoolean(int index, boolean value) {
        booleans.set(index, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Packet) {
            Packet p = (Packet) obj;
            return this.shorts.equals(p.shorts) && this.floats.equals(p.floats) && this.booleans.equals(p.booleans);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + shorts.hashCode();
        hash = hash * 31 + floats.hashCode();
        hash = hash * 13 + booleans.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Shorts(").append(nShorts).append("): ");

        for (Short s : shorts) {
            sb.append(s).append(", ");
        }
        sb.append("\n");

        sb.append("Floats(").append(nFloats).append("): ");
        for (Float f : floats) {
            sb.append(f).append(", ");
        }
        sb.append("\n");

        sb.append("Booleans(").append(nBooleans).append("): ");
        for (Boolean b : booleans) {
            sb.append(b).append(", ");
        }
        sb.append("\n");
        return sb.toString();
    }
}
