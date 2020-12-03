package Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import Infrastructure.BN;
import Infrastructure.Node;

public class Extract {
	/**
	 * This method extracts the query variable out of the given query.
	 */
	public static String QX(String query) {
		return query.substring(0, query.indexOf("="));
	}

	/**
	 * This method extracts the value of the query variable out of the given query.
	 */
	public static String QV(String query) {
		if (!query.contains("|")) {
			return query.substring(query.indexOf("=") + 1);
		}

		return query.substring(query.indexOf("=") + 1, query.indexOf("|"));
	}

	/**
	 * This method extracts the query node out of the given query.
	 */
	public static String QN(String query) {
		if (!query.contains("|")) {
			return query;
		}

		return query.substring(0, query.indexOf("|"));
	}

	/**
	 * This method returns the evidence nodes of the given query.
	 */
	public static Map<String, String> evidenceNodes(String query) {
		Map<String, String> evidence = new HashMap<>();

		if (!query.contains("|")) {
			return evidence;
		}

		StringTokenizer st = new StringTokenizer(query.substring(query.indexOf("|") + 1), "[]= ,");
		while (st.hasMoreTokens()) {
			evidence.put(st.nextToken(), st.nextToken());
		}

		return evidence;
	}

	/**
	 * This method returns the hidden nodes of the given query.
	 */
	public static List<Node> hiddenNodes(String query) {
		List<Node> HN = new ArrayList<>();
		Map<String, String> NHN = nonHiddenNodes(query);

		Iterator<Node> iterator = BN.getInstance().iterator();
		while (iterator.hasNext()) {
			Node node = iterator.next();
			if (!NHN.containsKey(node.getName())) {
				HN.add(node);
			}
		}

		return HN;
	}

	/**
	 * This method returns the nonhidden nodes of the given query.
	 */
	public static Map<String, String> nonHiddenNodes(String query) {
		Map<String, String> NHN = evidenceNodes(query);
		NHN.put(QX(query), QV(query));
		return NHN;
	}

	public static Set<String> ordered(String query) {
		Set<String> set = new TreeSet<>();

		StringTokenizer st = new StringTokenizer(query, "|[] ,");
		while (st.hasMoreTokens()) {
			set.add(st.nextToken());
		}

		return set;
	}

	/**
	 * This method calculates and returns the ascii sum of the nodes of the given query.
	 */
	public static int asciiSum(String query) {
		int ascii = 0;

		Iterator<String> iterator = ordered(query).iterator();
		while (iterator.hasNext()) {
			String X = QX(iterator.next());
			while (!X.isEmpty()) {
				ascii += (int)X.charAt(0);
				X = X.substring(1);
			}
		}

		return ascii;
	}
}
