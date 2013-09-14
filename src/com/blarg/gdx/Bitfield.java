package com.blarg.gdx;

import com.blarg.gdx.math.MathHelpers;

// this class exists because i am stupid and can never remember the operators used for setting/clearing/toggling bits

public final class Bitfield {

	// checking for individual bits

	public static boolean isSet(long bit, long bitfield) {
		return (bitfield & bit) != 0;
	}

	public static boolean isSet(int bit, int bitfield) {
		return (bitfield & bit) != 0;
	}

	public static boolean isSet(short bit, short bitfield) {
		return (bitfield & bit) != 0;
	}

	public static boolean isSet(byte bit, byte bitfield) {
		return (bitfield & bit) != 0;
	}

	// setting individual bits

	public static long set(long bit, long bitfield) {
		bitfield |= bit;
		return (bitfield | bit);
	}

	public static int set(int bit, int bitfield) {
		bitfield |= bit;
		return bitfield;
	}

	public static short set(short bit, short bitfield) {
		bitfield |= bit;
		return bitfield;
	}

	public static byte set(byte bit, byte bitfield) {
		bitfield |= bit;
		return bitfield;
	}

	// clearing individual bits

	public static long clear(long bit, long bitfield) {
		bitfield &= ~bit;
		return bitfield;
	}

	public static int clear(int bit, int bitfield) {
		bitfield &= ~bit;
		return bitfield;
	}

	public static short clear(short bit, short bitfield) {
		bitfield &= ~bit;
		return bitfield;
	}

	public static byte clear(byte bit, byte bitfield) {
		bitfield &= ~bit;
		return bitfield;
	}

	// toggling individual bits on/off

	public static long toggle(long bit, long bitfield) {
		bitfield ^= bit;
		return bitfield;
	}

	public static int toggle(int bit, int bitfield) {
		bitfield ^= bit;
		return bitfield;
	}

	public static short toggle(short bit, short bitfield) {
		bitfield ^= bit;
		return bitfield;
	}

	public static byte toggle(byte bit, byte bitfield) {
		bitfield ^= bit;
		return bitfield;
	}

	// extracting smaller int-type values from a subset of bits out of a larger value (bitfield)

	public static long extract(long mask, int shift, long bitfield) {
		return (bitfield & mask) >> shift;
	}

	public static int extract(int mask, int shift, int bitfield) {
		return (bitfield & mask) >> shift;
	}

	public static short extract(short mask, int shift, short bitfield) {
		return (short)((bitfield & mask) >> shift);
	}

	public static byte extract(byte mask, int shift, byte bitfield) {
		return (byte)((bitfield & mask) >> shift);
	}

	// setting smaller int-type values in a subset of bits in a larger value (bitfield)

	public static long embed(long value, long bitmask, int shift, int valueMaxBitLength, long bitfield) {
		long maxValue = MathHelpers.pow(2, valueMaxBitLength) - 1;
		long actualValue = (value > maxValue ? maxValue : value) << shift;
		return (clear(bitmask, bitfield) | actualValue);
	}

	public static int embed(int value, int bitmask, int shift, int valueMaxBitLength, int bitfield) {
		int maxValue = MathHelpers.pow(2, valueMaxBitLength) - 1;
		int actualValue = (value > maxValue ? maxValue : value) << shift;
		return (clear(bitmask, bitfield) | actualValue);
	}

	public static short embed(short value, short bitmask, int shift, int valueMaxBitLength, short bitfield) {
		short maxValue = (short)(MathHelpers.pow(2, valueMaxBitLength) - 1);
		short actualValue = (short)((value > maxValue ? maxValue : value) << shift);
		return (short)(clear(bitmask, bitfield) | actualValue);
	}

	public static byte embed(byte value, byte bitmask, int shift, int valueMaxBitLength, byte bitfield) {
		byte maxValue = (byte)(MathHelpers.pow(2, valueMaxBitLength) - 1);
		byte actualValue = (byte)((value > maxValue ? maxValue : value) << shift);
		return (byte)(clear(bitmask, bitfield) | actualValue);
	}


	public static int getMaskFor(int startBit, int numBits) {
		int temp = MathHelpers.pow(2, startBit);
		return (temp * (MathHelpers.pow(2, numBits))) - temp;
	}
}
