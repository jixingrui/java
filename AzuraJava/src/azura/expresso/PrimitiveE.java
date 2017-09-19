package azura.expresso;


public enum PrimitiveE {
	Int, String, Boolean, Bytes, Double;

	public static PrimitiveE valueOf(int value) {
		PrimitiveE[] values = PrimitiveE.values();
		if (value < 0 || value >= values.length)
			return null;
		else
			return values[value];
	}
}
