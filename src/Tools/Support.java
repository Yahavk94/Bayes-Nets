package Tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import Infrastructure.BN;
import Infrastructure.Node;
import Utils.Extract;

/**
 * This class represents the methods which are used by the service.
 * @author Yahav Karpel
 */

public class Support {
	/**
	 * This method breaks up the given query calculations into distinct parts.
	 */
	protected static Queue<Map<String, String>> completeProbabilityFormula(String query) {
		Queue<Map<String, String>> cpf = new LinkedList<>();

		List<Node> HN = Extract.hiddenNodes(query);
		Map<String, String> NHN = Extract.nonHiddenNodes(query);

		if (HN.isEmpty()) /* The formula consists of one part */ {
			cpf.add(NHN);
			return cpf;
		}

		// The formula consists of several parts
		Queue<Queue<String>> cartesian = cartesianProduct(new ArrayList<>(HN));
		while (!cartesian.isEmpty()) {
			Queue<String> values = cartesian.remove();
			Map<String, String> dp = new HashMap<>(NHN);

			Iterator<Node> HNIterator = HN.iterator();
			while (HNIterator.hasNext()) {
				dp.put(HNIterator.next().getName(), values.remove());
			}

			cpf.add(dp);
		}

		return cpf;
	}

	/**
	 * This method returns the unnecessary hidden nodes.
	 */
	protected static Set<String> HNFilter(String query) {
		Map<String, String> NHN = Extract.nonHiddenNodes(query);

		Set<String> set = new TreeSet<>();

		Iterator<Node> nodesIterator = BN.getInstance().iterator();
		while (nodesIterator.hasNext()) {
			Node node = nodesIterator.next();

			if (NHN.containsKey(node.getName())) {
				continue;
			}

			boolean indicator = false;

			Iterator<String> NHNIterator = NHN.keySet().iterator();
			while (NHNIterator.hasNext()) {
				if (BN.getInstance().getNode(NHNIterator.next()).containsAncestor(node.getName())) {
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
		Stack<String> cq = new Stack<>();

		String value = Extract.QV(query);

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
	private static Queue<Queue<String>> cartesianProduct(List<Node> HN) {
		Queue<Queue<String>> cartesian = new LinkedList<>();

		if (HN.size() == 1) {
			Iterator<String> HNIterator = HN.remove(0).valuesIterator();
			while (HNIterator.hasNext()) {
				Queue<String> queue = new LinkedList<>();
				queue.add(HNIterator.next());
				cartesian.add(queue);
			}

			return cartesian;
		}

		// Generate all possible pairs
		Iterator<String> outer = HN.remove(0).valuesIterator();
		while (outer.hasNext()) {
			String value = outer.next();

			Iterator<String> inner = HN.get(0).valuesIterator();
			while (inner.hasNext()) {
				Queue<String> queue = new LinkedList<>();
				queue.add(value);
				queue.add(inner.next());
				cartesian.add(queue);
			}
		}

		// Generate all possible n tuples
		HN.remove(0);
		while (!HN.isEmpty()) {
			Queue<Queue<String>> temp = new LinkedList<>();
			Node current = HN.remove(0);

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
