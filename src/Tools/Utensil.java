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
	protected static List<Map<String, String>> totalProbabilityRule(String query) {
		List<Map<String, String>> tpr = new ArrayList<>();
		Map<String, String> map = tokenize(query);
		List<Node> nodes = missingNodes(map);

		List<List<String>> cartesian = Utensil.cartesianProduct(nodes);
		while (!cartesian.isEmpty()) {
			Map<String, String> m = Utensil.clone(map);
			List<String> element = cartesian.remove(0);

			Iterator<Node> iterator = nodes.iterator();
			while (iterator.hasNext()) {
				m.put(iterator.next().getName(), element.remove(0));
			}

			tpr.add(m);
		}

		return tpr;
	}

	private static Map<String, String> tokenize(String query) {
		Map<String, String> qe = new LinkedHashMap<>();
		StringTokenizer tokenizer = new StringTokenizer(query.substring(1), "|(,)=");
		while (tokenizer.hasMoreTokens()) {
			qe.put(tokenizer.nextToken(), tokenizer.nextToken());
		}

		return qe;
	}

	private static List<Node> missingNodes(Map<String, String> map) {
		List<Node> nodes = new ArrayList<>();

		Iterator<Node> iterator = BN.getInstance().iteration();
		while (iterator.hasNext()) {
			Node node = iterator.next();
			if (!map.containsKey(node.getName())) {
				nodes.add(node);
			}
		}

		return nodes;
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
				List<String> element = cartesian.remove(0);
				for (int v = 0; v < nodes.get(i).getValues().size(); v += 1) {
					List<String> lst = clone(element);
					lst.add(nodes.get(i).getValues().get(v));
					temp.add(lst);
				}
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
		for (int i = 0; i < list.size(); i += 1) {
			cloneList.add(list.get(i));
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
