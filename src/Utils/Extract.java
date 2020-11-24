package Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
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
	 * This method returns the query node.
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
		Map<String, String> nonhidden = new HashMap<>();

		if (!query.contains("|")) {
			return nonhidden;
		}

		StringTokenizer st = new StringTokenizer(query.substring(query.indexOf("|") + 1), "[]= ,");
		while (st.hasMoreTokens()) {
			nonhidden.put(st.nextToken(), st.nextToken());
		}

		return nonhidden;
	}

	/**
	 * This method returns the hidden nodes of the given query.
	 */
	public static List<Node> hiddenNodes(String query) {
		Iterator<Node> iterator = BN.getInstance().iterator();

		List<Node> hidden = new ArrayList<>();
		Map<String, String> nonhidden = nonHiddenNodes(query);

		while (iterator.hasNext()) {
			Node node = iterator.next();
			if (!nonhidden.containsKey(node.getName())) {
				hidden.add(node);
			}
		}

		return hidden;
	}

	/**
	 * This method returns the non hidden nodes of the given query.
	 */
	public static Map<String, String> nonHiddenNodes(String query) {
		Map<String, String> nonhidden = evidenceNodes(query);
		nonhidden.put(QX(query), QV(query));
		return nonhidden;
	}

	public static Set<String> ordered(String query) {
		Set<String> set = new HashSet<>();

		StringTokenizer st = new StringTokenizer(query, "|[] ,");
		while (st.hasMoreTokens()) {
			set.add(st.nextToken());
		}

		return set;
	}

	public static int asciiSum(String query) {
		Iterator<String> iterator = ordered(query).iterator();

		int ascii = 0;
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
