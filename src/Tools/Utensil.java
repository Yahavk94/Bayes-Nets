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
		Map<String, String> nonhidden = Extract.nonHiddenNodes(query);
		List<String> hidden = Extract.hiddenNodes(query);

		Queue<Queue<String>> cartesian = cartesianProduct(new ArrayList<>(hidden));
		if (cartesian == null) /* The formula cannot be created */ {
			return null;
		}

		// The distinct parts of the given query
		Stack<Map<String, String>> cpf = new Stack<>();

		while (!cartesian.isEmpty()) /* Generate the distinct parts of the formula */ {
			Map<String, String> dp = new HashMap<>(nonhidden);
			Queue<String> values = cartesian.remove();
			Iterator<String> iterator = hidden.iterator();

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
		Node node = BN.getInstance().getNode(Extract.QX(query));
		String value = Extract.QV(query);

		// The complementary queries of the given query
		Stack<String> ce = new Stack<>();

		Iterator<String> valuesIterator = node.valuesIterator();

		while (valuesIterator.hasNext()) {
			String candidate = valuesIterator.next();
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
			Iterator<String> valuesIterator = BN.getInstance().getNode(hidden.remove(0)).valuesIterator();

			while (valuesIterator.hasNext()) {
				Queue<String> queue = new LinkedList<>();
				queue.add(valuesIterator.next());
				cartesian.add(queue);
			}

			return cartesian;
		}

		Iterator<String> outer = BN.getInstance().getNode(hidden.remove(0)).valuesIterator();
		while (outer.hasNext()) {
			String value = outer.next();
			Iterator<String> inner = BN.getInstance().getNode(hidden.get(0)).valuesIterator();
			while (inner.hasNext()) {
				Queue<String> queue = new LinkedList<>();
				queue.add(value);
				queue.add(inner.next());
				cartesian.add(queue);
			}
		}

		hidden.remove(0);
		while (!hidden.isEmpty()) {
			Queue<Queue<String>> temp = new LinkedList<>();
			Node node = BN.getInstance().getNode(hidden.remove(0));

			while (!cartesian.isEmpty()) {
				Queue<String> values = cartesian.remove();
				Iterator<String> valuesIterator = node.valuesIterator();

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
