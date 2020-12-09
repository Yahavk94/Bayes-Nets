package Utils;

/**
 * This enum represents the methods.
 * @author Yahav Karpel
 */

public enum Method {
	SIMPLE, VE, HEURISTIC;

	/**
	 * This method converts the given string to the corresponding method.
	 */
	public static Method convert(String method) throws RuntimeException {
		if (method.equals("1")) {
			return SIMPLE;
		} if (method.equals("2")) {
			return VE;
		} if (method.equals("3")) {
			return HEURISTIC;
		}

		throw new RuntimeException("ERROR");
	}
}
