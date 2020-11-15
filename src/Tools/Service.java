package Tools;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import Infrastructure.BN;
import Infrastructure.Node;

/**
 * This class represents the methods which are used by the algorithms.
 * @author Yahav Karpel
 */

public class Service {
	/**
	 * This method returns the sample set associated with the given query.
	 */
	public static Stack<String> getSamples(String query) {
		Stack<String> sample = Utensil.getComplementaryEvents(query);
		sample.push(query);
		return sample;
	}

	/**
	 * This method returns the given query as a BN formula.
	 */
	public static List<List<String>> getBNFormula(String query) {
		Stack<Map<String, String>> cpf = Utensil.completeProbabilityFormula(query);
		if (cpf == null) /* The formula cannot be created */ {
			return null;
		}

		// The distinct parts of the formula
		List<List<String>> formula = new ArrayList<>();

		while (!cpf.isEmpty()) {
			List<String> list = new ArrayList<>();
			Map<String, String> map = cpf.pop();
			Iterator<String> mapIterator = map.keySet().iterator();

			while (mapIterator.hasNext()) {
				Node node = BN.getInstance().getNode(mapIterator.next());
				Iterator<String> iterator = node.parentsIterator();

				if (!iterator.hasNext()) {
					list.add(node.getName() + "=" + map.get(node.getName()));
					continue;
				}

				Set<String> set = new HashSet<>();

				while (iterator.hasNext()) {
					String parent = iterator.next();
					set.add(parent + "=" + map.get(parent));
				}

				list.add(node.getName() + "=" + map.get(node.getName()) + "|" + set);
			}

			formula.add(list);
		}

		return formula;
	}

	/**
	 * This method calculates the probability of the given query.
	 */
	public static double calculateProbability(String query) {
		Node node = BN.getInstance().getNode(Pruner.getX(query));
		if (node.containsProbability(query)) {
			return node.getProbability(query);
		}

		double probability = 0;
		Stack<String> ce = Utensil.getComplementaryEvents(query);

		while (!ce.isEmpty()) {
			probability += node.getProbability(ce.pop());
		}

		return 1 - probability;
	}
}
