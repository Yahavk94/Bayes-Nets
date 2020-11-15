package Tools;

/**
 * This class represents the methods which are used by the service.
 * @author Yahav Karpel
 */

public class Pruner {
	/**
	 * This method returns the leftmost random variable.
	 */
	protected static String getX(String query) {
		return query.substring(0, query.indexOf("="));
	}

	/**
	 * This method returns the value of the leftmost random variable.
	 */
	protected static String getVX(String query) {
		if (!query.contains("|")) {
			return query.substring(query.indexOf("=") + 1);
		}

		return query.substring(query.indexOf("=") + 1, query.indexOf("|"));
	}
}
