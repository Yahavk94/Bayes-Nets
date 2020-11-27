package Tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import Infrastructure.BN;
import Infrastructure.Node;
import Utils.Extract;

/**
 * This class represents the methods which are used by the service.
 * @author Yahav Karpel
 */

public class Utensil {
	/**
	 * This method breaks up the given query calculations into distinct parts.
	 */
	protected static Queue<Map<String, String>> completeProbabilityFormula(String query) {
		List<Node> hidden = Extract.hiddenNodes(query);

		Queue<Queue<String>> cartesian = cartesianProduct(new ArrayList<>(hidden));
		if (cartesian == null) /* The formula cannot be created */ {
			return null;
		}

		Map<String, String> nonhidden = Extract.nonHiddenNodes(query);

		// The distinct parts of the given query
		Queue<Map<String, String>> cpf = new LinkedList<>();

		while (!cartesian.isEmpty()) {
			Queue<String> values = cartesian.remove();
			Map<String, String> dp = new HashMap<>(nonhidden);

			Iterator<Node> nodesIterator = hidden.iterator();
			while (nodesIterator.hasNext()) {
				dp.put(nodesIterator.next().getName(), values.remove());
			}

			cpf.add(dp);
		}

		return cpf;
	}

	public static Set<String> filter(String query) {
		Map<String, String> nonhidden = Extract.nonHiddenNodes(query);

		Set<String> set = new HashSet<>();

		Iterator<Node> nodesIterator = BN.getInstance().iterator();
		while (nodesIterator.hasNext()) {
			Node node = nodesIterator.next();

			if (nonhidden.containsKey(node.getName())) {
				continue;
			}

			boolean indicator = false;

			Iterator<String> iterator = nonhidden.keySet().iterator();
			while (iterator.hasNext()) {
				if (BN.getInstance().getNode(iterator.next()).containsAncestor(node.getName())) {
					indicator = true;
					break;
				}
			}

			if (!indicator) {
				set.add(node.getName());
			}
		}

		return set;
	}

	/**
	 * This method returns the complementary queries of the given query.
	 */
	protected static Stack<String> getComplementaryQueries(String query) {
		String value = Extract.QV(query);

		// The complementary queries
		Stack<String> cq = new Stack<>();

		Iterator<String> nodesIterator = BN.getInstance().getNode(Extract.QX(query)).valuesIterator();
		while (nodesIterator.hasNext()) {
			String candidate = nodesIterator.next();
			if (!candidate.equals(value)) {
				cq.push(query.replaceFirst(value, candidate));
			}
		}

		return cq;
	}

	/**
	 * This method returns the cartesian product of the values of the hidden nodes.
	 */
	private static Queue<Queue<String>> cartesianProduct(List<Node> hidden) {
		if (hidden.isEmpty()) /* The product cannot be performed */ {
			return null;
		}

		Queue<Queue<String>> cartesian = new LinkedList<>();

		if (hidden.size() == 1) {
			Iterator<String> nodesIterator = hidden.remove(0).valuesIterator();
			while (nodesIterator.hasNext()) {
				Queue<String> queue = new LinkedList<>();
				queue.add(nodesIterator.next());
				cartesian.add(queue);
			}

			return cartesian;
		}

		// Generate all possible pairs
		Iterator<String> outer = hidden.remove(0).valuesIterator();
		while (outer.hasNext()) {
			String value = outer.next();

			Iterator<String> inner = hidden.get(0).valuesIterator();
			while (inner.hasNext()) {
				Queue<String> queue = new LinkedList<>();
				queue.add(value);
				queue.add(inner.next());
				cartesian.add(queue);
			}
		}

		hidden.remove(0);
		while (!hidden.isEmpty()) /* Generate all possible n tuples */ {
			Queue<Queue<String>> temp = new LinkedList<>();
			Node current = hidden.remove(0);

			while (!cartesian.isEmpty()) {
				Queue<String> values = cartesian.remove();

				Iterator<String> valuesIterator = current.valuesIterator();
				while (valuesIterator.hasNext()) {
					Queue<String> queue = new LinkedList<>(values);
					queue.add(valuesIterator.next());
					temp.add(queue);
				}
			}

			cartesian = temp;
		}

		return cartesian;
	}
}
