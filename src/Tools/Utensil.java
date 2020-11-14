package Tools;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import Infrastructure.BN;
import Infrastructure.Node;

/**
 * This class represents the methods which are used by the service.
 * @author Yahav Karpel
 */

public class Utensil {
	/**
	 * This method breaks up the given query calculations into distinct parts.
	 */
	protected static List<Map<String, String>> completeProbabilityFormula(String query) {
		List<Map<String, String>> tpr = new ArrayList<>();

		Map<String, String> map = getQueryNodes(query);
		List<Node> nodes = getNonQueryNodes(map);

		List<List<String>> cartesian = Utensil.cartesianProduct(nodes);
		while (!cartesian.isEmpty()) {
			Map<String, String> clone = Utensil.clone(map);

			Iterator<Node> iterator = nodes.iterator();
			while (iterator.hasNext()) {
				clone.put(iterator.next().getName(), cartesian.get(0).remove(0));
			}

			cartesian.remove(0);
			tpr.add(clone);
		}

		return tpr;
	}

	/**
	 * This method returns the complementary events of the given query.
	 */
	protected static List<String> getComplementaryEvents(String query) {
		List<String> ce = new ArrayList<>();

		StringTokenizer st = new StringTokenizer(query, "|");
		st = new StringTokenizer(st.nextToken(), "=");
		Node node = BN.getInstance().getNode(st.nextToken());
		String value = st.nextToken();

		for (int i = 0; i < node.getValues().size(); i += 1) {
			if (!node.getValues().get(i).equals(value)) {
				ce.add(query.replaceFirst(value, node.getValues().get(i)));
			}
		}

		return ce;
	}

	/**
	 * This method returns the nodes that are in the given query.
	 */
	private static Map<String, String> getQueryNodes(String query) {
		Map<String, String> qn = new LinkedHashMap<>();

		StringTokenizer st = new StringTokenizer(query, "|(=),");
		while (st.hasMoreTokens()) {
			qn.put(st.nextToken(), st.nextToken());
		}

		return qn;
	}

	/**
	 * This method returns the nodes that are not in the given query.
	 */
	private static List<Node> getNonQueryNodes(Map<String, String> map) {
		List<Node> nqn = new ArrayList<>();

		Iterator<Node> iterator = BN.getInstance().iteration();
		while (iterator.hasNext()) {
			Node node = iterator.next();
			if (!map.containsKey(node.getName())) {
				nqn.add(node);
			}
		}

		return nqn;
	}

	/**
	 * This method returns a cartesian product of the values of the nodes.
	 */
	private static List<List<String>> cartesianProduct(List<Node> nodes) {
		List<List<String>> cartesian = new ArrayList<>();

		if (nodes.size() == 1) {
			for (int i = 0; i < nodes.get(0).getValues().size(); i += 1) {
				List<String> element = new ArrayList<>();
				element.add(nodes.get(0).getValues().get(i));
				cartesian.add(element);
			}

			return cartesian;
		}

		for (int first = 0; first < nodes.get(0).getValues().size(); first += 1) {
			for (int second = 0; second < nodes.get(1).getValues().size(); second += 1) {
				List<String> element = new ArrayList<>();
				element.add(nodes.get(0).getValues().get(first));
				element.add(nodes.get(1).getValues().get(second));
				cartesian.add(element);
			}
		}

		for (int i = 2; i < nodes.size(); i += 1) {
			List<List<String>> temp = new ArrayList<>();

			while (!cartesian.isEmpty()) {
				for (int v = 0; v < nodes.get(i).getValues().size(); v += 1) {
					List<String> lst = clone(cartesian.get(0));
					lst.add(nodes.get(i).getValues().get(v));
					temp.add(lst);
				}

				cartesian.remove(0);
			}

			cartesian = temp;
		}

		return cartesian;
	}

	/**
	 * This method returns a deep copy of the given list.
	 */
	private static List<String> clone(List<String> list) {
		List<String> cloneList = new ArrayList<>();

		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			cloneList.add(iterator.next());
		}

		return cloneList;
	}

	/**
	 * This method returns a deep copy of the given map.
	 */
	private static Map<String, String> clone(Map<String, String> map) {
		Map<String, String> cloneMap = new LinkedHashMap<>();

		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String X = iterator.next();
			cloneMap.put(X, map.get(X));
		}

		return cloneMap;
	}
}
