package Tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
	protected static Stack<Map<String, String>> completeProbabilityFormula(String query) {
		List<String> hidden = Extract.hiddenNodes(query);

		// The cartesian product of the values of the hidden nodes
		Queue<Queue<String>> cartesian = cartesianProduct(new ArrayList<>(hidden));

		if (cartesian == null) /* The formula cannot be created */ {
			return null;
		}

		Map<String, String> nonhidden = Extract.nonHiddenNodes(query);

		// The distinct parts of the given query
		Stack<Map<String, String>> cpf = new Stack<>();

		while (!cartesian.isEmpty()) /* Generate the distinct parts of the formula */ {
			Iterator<String> iterator = hidden.iterator();
			Queue<String> values = cartesian.remove();

			// A distinct part of the formula
			Map<String, String> dp = new HashMap<>(nonhidden);

			while (iterator.hasNext()) {
				dp.put(BN.getInstance().getNode(iterator.next()).getName(), values.remove());
			}

			cpf.push(dp);
		}

		return cpf;
	}

	/**
	 * This method returns the complementary queries of the given query.
	 */
	protected static Stack<String> getComplementaryQueries(String query) {
		String value = Extract.QV(query);
		Iterator<String> iterator = BN.getInstance().getNode(Extract.QX(query)).valuesIterator();

		// The complementary queries of the given query
		Stack<String> ce = new Stack<>();

		while (iterator.hasNext()) {
			String candidate = iterator.next();
			if (!candidate.equals(value)) {
				ce.push(query.replaceFirst(value, candidate));
			}
		}

		return ce;
	}

	/**
	 * This method returns the cartesian product of the values of the hidden nodes.
	 */
	private static Queue<Queue<String>> cartesianProduct(List<String> hidden) {
		if (hidden.isEmpty()) /* The product cannot be performed */ {
			return null;
		}

		// The cartesian product of the values of the hidden nodes
		Queue<Queue<String>> cartesian = new LinkedList<>();

		if (hidden.size() == 1) {
			Iterator<String> iterator = BN.getInstance().getNode(hidden.remove(0)).valuesIterator();

			while (iterator.hasNext()) {
				Queue<String> queue = new LinkedList<>();
				queue.add(iterator.next());
				cartesian.add(queue);
			}

			return cartesian;
		}

		// Create all possible pairs
		Iterator<String> outer = BN.getInstance().getNode(hidden.remove(0)).valuesIterator();

		while (outer.hasNext()) {
			Iterator<String> inner = BN.getInstance().getNode(hidden.get(0)).valuesIterator();

			String value = outer.next();

			while (inner.hasNext()) {
				Queue<String> queue = new LinkedList<>();
				queue.add(value);
				queue.add(inner.next());
				cartesian.add(queue);
			}
		}

		hidden.remove(0);
		while (!hidden.isEmpty()) /* Create all possible N tuples */ {
			Queue<Queue<String>> temp = new LinkedList<>();
			Node node = BN.getInstance().getNode(hidden.remove(0));

			while (!cartesian.isEmpty()) {
				Iterator<String> iterator = node.valuesIterator();

				Queue<String> values = cartesian.remove();

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
}
