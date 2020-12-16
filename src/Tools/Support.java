package Tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import Infrastructure.BN;
import Infrastructure.Node;
import Utils.Cpt;
import Utils.Extract;

/**
 * This class represents the methods which are used by the service.
 * @author Yahav Karpel
 */

public class Support {
	/**
	 * This method returns the complementary queries of the given query.
	 */
	protected static Stack<String> getComplementaryQueries(String query) {
		Stack<String> cq = new Stack<>();

		String value = Extract.QV(query);

		Iterator<String> iterator = BN.getInstance().getNode(Extract.QX(query)).valuesIterator();
		while (iterator.hasNext()) {
			String candidate = iterator.next();
			if (!candidate.equals(value)) {
				cq.push(query.replaceFirst(value, candidate));
			}
		}

		return cq;
	}

	/**
	 * This method breaks up the given query calculations into distinct parts.
	 */
	protected static Queue<Map<String, String>> completeProbabilityFormula(String query) {
		Queue<Map<String, String>> cpf = new LinkedList<>();

		List<Node> HN = getHiddenNodes(query);
		Map<String, String> NHN = getNonHiddenNodes(query);

		if (HN.isEmpty()) /* The formula consists of one part */ {
			cpf.add(NHN);
			return cpf;
		}

		// The formula consists of several parts
		Queue<Queue<String>> cartesian = cartesianProduct(new ArrayList<>(HN));
		while (!cartesian.isEmpty()) {
			Queue<String> values = cartesian.remove();
			Map<String, String> dp = new HashMap<>(NHN);

			Iterator<Node> iterator = HN.iterator();
			while (iterator.hasNext()) {
				dp.put(iterator.next().getName(), values.remove());
			}

			cpf.add(dp);
		}

		return cpf;
	}

	/**
	 * This method returns the unnecessary hidden nodes.
	 */
	protected static Set<String> getUnnecessaryHiddenNodes(String query) {
		SortedSet<String> unnecessaryHN = new TreeSet<>();

		Map<String, String> NHN = getNonHiddenNodes(query);

		Iterator<Node> nodesIterator = BN.getInstance().iterator();
		while (nodesIterator.hasNext()) {
			Node node = nodesIterator.next();

			if (NHN.containsKey(node.getName())) /* Necessary */ {
				continue;
			}

			boolean indicator = false;

			Iterator<String> NHNIterator = NHN.keySet().iterator();
			while (NHNIterator.hasNext()) {
				if (BN.getInstance().getNode(NHNIterator.next()).containsAncestor(node.getName())) /* Necessary */ {
					indicator = true;
					break;
				}
			}

			if (!indicator) {
				unnecessaryHN.add(node.getName());
			}
		}

		return unnecessaryHN;
	}

	/**
	 * This method returns true if the given cpt is legal.
	 */
	protected static boolean isLegalCpt(Cpt cpt) {
		if (cpt.isEmpty()) /* A meaningless cpt */ {
			return false;
		}

		if (!cpt.getRandomQuery().contains(",")) {
			return cpt.size() == BN.getInstance().getNode(Extract.QX(cpt.getRandomQuery())).domainSize();
		}

		return true;
	}

	/**
	 * This method returns true if the given factors can be multiplied.
	 */
	protected static boolean canBeMultiplied(SortedSet<String> outer, SortedSet<String> inner) {
		Iterator<String> iterator = outer.iterator();
		while (iterator.hasNext()) {
			String query = iterator.next();
			if (inner.contains(query)) {
				continue;
			}

			Stack<String> cq = getComplementaryQueries(query);
			while (!cq.isEmpty()) {
				if (inner.contains(cq.pop())) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * This method returns the cartesian product of the values of the hidden nodes.
	 */
	private static Queue<Queue<String>> cartesianProduct(List<Node> HN) {
		Queue<Queue<String>> cartesian = new LinkedList<>();

		if (HN.size() == 1) {
			Iterator<String> iterator = HN.remove(0).valuesIterator();
			while (iterator.hasNext()) {
				Queue<String> queue = new LinkedList<>();
				queue.add(iterator.next());
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

		// Generate all possible tuples
		HN.remove(0);
		while (!HN.isEmpty()) {
			Queue<Queue<String>> temp = new LinkedList<>();
			Node current = HN.remove(0);

			while (!cartesian.isEmpty()) {
				Queue<String> values = cartesian.remove();

				Iterator<String> iterator = current.valuesIterator();
				while (iterator.hasNext()) {
					Queue<String> queue = new LinkedList<>(values);
					queue.add(iterator.next());
					temp.add(queue);
				}
			}

			cartesian = temp;
		}

		return cartesian;
	}

	/**
	 * This method returns the hidden nodes of the given query.
	 */
	protected static List<Node> getHiddenNodes(String query) {
		List<Node> HN = new ArrayList<>();
		Map<String, String> NHN = getNonHiddenNodes(query);

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
	private static Map<String, String> getNonHiddenNodes(String query) {
		Map<String, String> NHN = Extract.getEvidenceNodes(query);
		NHN.put(Extract.QX(query), Extract.QV(query));
		return NHN;
	}
}
