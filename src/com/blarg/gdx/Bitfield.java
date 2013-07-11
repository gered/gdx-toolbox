package com.blarg.gdx;

// this class exists because i am stupid and can never remember the operators used for setting/clearing/toggling bits

public final class Bitfield {
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
}
