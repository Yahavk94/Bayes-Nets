package Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import Infrastructure.BN;
import Infrastructure.Node;

public class Extract {
	/**
	 * This method returns the leftmost random variable of the given query.
	 */
	public static String QX(String query) {
		return query.substring(0, query.indexOf("="));
	}

	/**
	 * This method returns the value of the leftmost random variable of the given query.
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
	public static Map<String, String> queryNode(String query) {
		Map<String, String> qn = new LinkedHashMap<>();

		StringTokenizer st;
		if (!query.contains("|")) {
			st = new StringTokenizer(query, "=");
		} else {
			st = new StringTokenizer(query.substring(0, query.indexOf("|")), "=");
		}

		qn.put(st.nextToken(), st.nextToken());
		return qn;
	}

	/**
	 * This method returns the evidence nodes.
	 */
	public static Map<String, String> evidenceNodes(String query) {
		Map<String, String> nonhidden = new LinkedHashMap<>();

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
	 * This method returns the hidden nodes.
	 */
	public static List<String> hiddenNodes(String query) {
		List<String> hidden = new ArrayList<>();
		Map<String, String> nonhidden = nonHiddenNodes(query);
		Iterator<Node> iterator = BN.getInstance().iterator();

		while (iterator.hasNext()) {
			String X = iterator.next().getName();
			if (!nonhidden.containsKey(X)) {
				hidden.add(X);
			}
		}

		return hidden;
	}

	public static Map<String, String> nonHiddenNodes(String query) {
		Map<String, String> nonhidden = evidenceNodes(query);
		nonhidden.putAll(queryNode(query));
		return nonhidden;
	}

	public static Set<String> ordered(String query) {
		Set<String> set = new TreeSet<>();

		StringTokenizer st = new StringTokenizer(query, "|[] ,");
		while (st.hasMoreTokens()) {
			set.add(st.nextToken());
		}

		return set;
	}
}
