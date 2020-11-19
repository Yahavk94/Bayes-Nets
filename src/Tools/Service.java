package Tools;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import Infrastructure.BN;
import Infrastructure.Node;
import Utils.Cpt;
import Utils.Extract;

/**
 * This class represents the methods which are used by the algorithms.
 * @author Yahav Karpel
 */

public class Service {
	/**
	 * This method returns the sample set associated with the given query.
	 */
	public static Stack<String> getSamples(String query) {
		Stack<String> samples = Utensil.getComplementaryQueries(query);
		samples.push(query);
		return samples;
	}

	/**
	 * This method calculates and returns the probability of the given query.
	 */
	public static double calculateProbability(String query) {
		Node node = BN.getInstance().getNode(Extract.QX(query));

		if (node.getCpt().containsKey(query)) {
			return node.getCpt().get(query);
		}

		// Calculate the probability of the complementary queries
		double probability = 0;
		Stack<String> ce = Utensil.getComplementaryQueries(query);
		while (!ce.isEmpty()) {
			probability += node.getCpt().get(ce.pop());
		}

		return 1 - probability;
	}

	/**
	 * This method returns the given query as a BN formula.
	 */
	public static Queue<Stack<String>> getBNFormula(String query) {
		Stack<Map<String, String>> cpf = Utensil.completeProbabilityFormula(query);
		if (cpf == null) /* The formula cannot be created */ {
			return null;
		}

		// The distinct parts of the formula
		Queue<Stack<String>> formula = new LinkedList<>();

		while (!cpf.isEmpty()) {
			Stack<String> stack = new Stack<>();
			Map<String, String> map = cpf.pop();
			Iterator<String> mapIterator = map.keySet().iterator();

			while (mapIterator.hasNext()) {
				Node node = BN.getInstance().getNode(mapIterator.next());
				Iterator<String> iterator = node.parentsIterator();

				if (!iterator.hasNext()) {
					stack.push(node.getName() + "=" + map.get(node.getName()));
					continue;
				}

				Set<String> ordered = new HashSet<>();

				while (iterator.hasNext()) {
					String parent = iterator.next();
					ordered.add(parent + "=" + map.get(parent));
				}

				stack.push(node.getName() + "=" + map.get(node.getName()) + "|" + ordered);
			}

			formula.add(stack);
		}

		return formula;
	}

	/**
	 * This method returns the factors of the nodes in the network.
	 */
	public static Stack<Cpt> getFactors(String query) {
		Map<String, String> evidence = Extract.evidenceNodes(query);
		Iterator<Node> iterator = BN.getInstance().iterator();

		// The factors of the nodes
		Stack<Cpt> factors = new Stack<>();

		while (iterator.hasNext()) {
			Node node = iterator.next();
			Iterator<String> cptIterator = node.getCpt().keySet().iterator();

			Cpt cpt = new Cpt();

			if (evidence.containsKey(node.getName())) /* Not all queries must be considered */ {
				while (cptIterator.hasNext()) {
					String cp = cptIterator.next();

					if (cp.contains(node.getName() + "=" + evidence.get(node.getName()))) {
						Set<String> ordered = Extract.orderedEvidence(cp);

						// Omit the evidence
						ordered.remove(node.getName() + "=" + evidence.get(node.getName()));

						cpt.insert(ordered.toString(), node.getCpt().get(cp));
						continue;
					}

					Stack<String> ce = Utensil.getComplementaryQueries(cp);
					while (!ce.isEmpty()) {
						cp = ce.pop();
						cpt.insert(Extract.orderedEvidence(cp).toString(), calculateProbability(cp));
					}
				}

				factors.push(cpt);
				continue;
			}

			while (cptIterator.hasNext()) /* All queries must be considered */ {
				String cp = cptIterator.next();
				cpt.insert(Extract.orderedEvidence(cp).toString(), node.getCpt().get(cp));

				Stack<String> ce = Utensil.getComplementaryQueries(cp);
				while (!ce.isEmpty()) {
					cp = ce.pop();
					cpt.insert(Extract.orderedEvidence(cp).toString(), calculateProbability(cp));
				}
			}

			factors.push(cpt);
		}

		return factors;
	}

	/**
	 * This method joins the given factors.
	 */
	public static Queue<Cpt> joinFactors(Queue<Cpt> minHeap) {
		Cpt first = minHeap.remove();

		while (!minHeap.isEmpty()) {
			Cpt second = minHeap.remove();

			// Temporary
			Cpt cpt = new Cpt();

			Iterator<String> firstIterator = first.keySet().iterator();

			while (firstIterator.hasNext()) {
				Set<String> orderedOuter = Extract.orderedEvidence(firstIterator.next());
				Iterator<String> secondIterator = second.keySet().iterator();

				while (secondIterator.hasNext()) {
					Set<String> orderedInner = Extract.orderedEvidence(secondIterator.next());

					if (Collections.disjoint(orderedInner, orderedOuter)) {
						continue;
					}

					Set<String> clone = new TreeSet<>(orderedOuter);
					clone.addAll(orderedInner);

					cpt.insert(clone.toString(), first.get(orderedOuter.toString()) * second.get(orderedInner.toString()));
				}
			}

			first = cpt;
		}

		minHeap.add(first);

		return minHeap;
	}
}
