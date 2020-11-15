package Tools;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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
	protected static Stack<Map<String, String>> completeProbabilityFormula(String query) {
		Map<String, String> map = getQueryNodes(query);
		List<String> nodes = getNonQueryNodes(map);
		List<List<String>> cartesian = Utensil.cartesianProduct(clone(nodes));
		if (cartesian == null) /* The formula cannot be created */ {
			return null;
		}

		// The distinct parts of the given query
		Stack<Map<String, String>> cpf = new Stack<>();

		while (!cartesian.isEmpty()) /* Generate the distinct parts of the formula */ {
			Map<String, String> dp = Utensil.clone(map);
			Iterator<String> iterator = nodes.iterator();

			while (iterator.hasNext()) {
				dp.put(BN.getInstance().getNode(iterator.next()).getName(), cartesian.get(0).remove(0));
			}

			cartesian.remove(0);
			cpf.push(dp);
		}

		return cpf;
	}

	/**
	 * This method returns the complementary events of the given query.
	 */
	protected static Stack<String> getComplementaryEvents(String query) {
		Stack<String> ce = new Stack<>();

		Node node = BN.getInstance().getNode(Pruner.getX(query));
		String current = Pruner.getVX(query);
		Iterator<String> iterator = node.valuesIterator();

		while (iterator.hasNext()) {
			String candidate = iterator.next();
			if (!current.equals(candidate)) {
				ce.push(query.replaceFirst(current, candidate));
			}
		}

		return ce;
	}

	/**
	 * This method returns the nodes that are in the given query.
	 */
	private static Map<String, String> getQueryNodes(String query) {
		Map<String, String> qn = new LinkedHashMap<>();

		StringTokenizer st = new StringTokenizer(query, "|[]= ,");
		while (st.hasMoreTokens()) {
			qn.put(st.nextToken(), st.nextToken());
		}

		return qn;
	}

	/**
	 * This method returns the nodes that are not in the given query.
	 */
	private static List<String> getNonQueryNodes(Map<String, String> qn) {
		List<String> nqn = new ArrayList<>();
		Iterator<Node> iterator = BN.getInstance().nodesIterator();

		while (iterator.hasNext()) {
			Node node = iterator.next();
			if (!qn.containsKey(node.getName())) {
				nqn.add(node.getName());
			}
		}

		return nqn;
	}

	/**
	 * This method returns the cartesian product of the values of the given nodes.
	 */
	private static List<List<String>> cartesianProduct(List<String> nodes) {
		if (nodes.isEmpty()) /* The cartesian product cannot be performed */ {
			return null;
		}

		// The cartesian product of the values of the nodes
		List<List<String>> cartesian = new ArrayList<>();

		if (nodes.size() == 1) {
			Iterator<String> iterator = BN.getInstance().getNode(nodes.remove(0)).valuesIterator();

			while (iterator.hasNext()) {
				List<String> list = new ArrayList<>();
				list.add(iterator.next());
				cartesian.add(list);
			}

			return cartesian;
		}

		Iterator<String> outer = BN.getInstance().getNode(nodes.remove(0)).valuesIterator();
		while (outer.hasNext()) {
			String value = outer.next();
			Iterator<String> inner = BN.getInstance().getNode(nodes.get(0)).valuesIterator();
			while (inner.hasNext()) {
				List<String> list = new ArrayList<>();
				list.add(value);
				list.add(inner.next());
				cartesian.add(list);
			}
		}

		nodes.remove(0);
		while (!nodes.isEmpty()) {
			List<List<String>> temp = new ArrayList<>();
			Node node = BN.getInstance().getNode(nodes.remove(0));

			while (!cartesian.isEmpty()) {
				Iterator<String> iterator = node.valuesIterator();

				while (iterator.hasNext()) {
					List<String> list = clone(cartesian.get(0));
					list.add(iterator.next());
					temp.add(list);
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
