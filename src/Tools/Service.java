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

			// A distinct part of the complete probability formula
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
		Iterator<Node> iterator = BN.getInstance().iterator();

		// The factors of the nodes
		Stack<Cpt> factors = new Stack<>();

		Map<String, String> evidence = Extract.evidenceNodes(query);

		while (iterator.hasNext()) {
			Iterator<String> cptIterator = iterator.next().getCpt().iterator();

			Cpt cpt = new Cpt();

			while (cptIterator.hasNext()) {
				Stack<String> samples = getSamples(cptIterator.next());

				while (!samples.isEmpty()) {
					Iterator<String> mapIterator = evidence.keySet().iterator();

					boolean indicator = false;
					String sample = samples.pop();
					Set<String> ordered = Extract.ordered(sample);

					while (mapIterator.hasNext()) {
						if (ordered.isEmpty()) {
							break;
						}

						String next = mapIterator.next();

						if (ordered.contains(next + "=" + evidence.get(next))) /* Omit unnecessary information */ {
							indicator = true;
							ordered.remove(next + "=" + evidence.get(next));
						}
					}

					if (indicator) {
						if (!ordered.isEmpty()) {
							cpt.put(ordered.toString(), calculateProbability(sample));
						}

						continue;
					}

					// Note that indicator = false
					Iterator<String> setIterator = ordered.iterator();

					while (setIterator.hasNext()) {
						String next = setIterator.next();
						if (evidence.containsKey(Extract.QX(next))) {
							indicator = true;
							break;
						}
					}

					if (!indicator) /* Avoid adding unnecessary information */ {
						cpt.put(ordered.toString(), calculateProbability(sample));
					}
				}
			}

			if (!cpt.isEmpty()) /* Unable to add an empty cpt */ {
				factors.push(cpt);
			}
		}

		return factors;
	}

	/**
	 * This method joins the given factors.
	 */
	public static Queue<Cpt> mulFactors(Queue<Cpt> minHeap) {
		Cpt min = minHeap.remove();

		while (!minHeap.isEmpty()) {
			Iterator<String> minIterator = min.iterator();

			Cpt cpt = new Cpt();
			Cpt top = minHeap.remove();

			while (minIterator.hasNext()) {
				Iterator<String> topIterator = top.iterator();

				Set<String> outer = Extract.ordered(minIterator.next());

				while (topIterator.hasNext()) {
					Set<String> inner = Extract.ordered(topIterator.next());

					if (Collections.disjoint(outer, inner)) /* Unable to join */ {
						continue;
					}

					Set<String> clone = new TreeSet<>(outer);
					clone.addAll(inner);

					cpt.put(clone.toString(), min.get(outer.toString()) * top.get(inner.toString()));
				}
			}

			min = cpt;
		}

		minHeap.add(min);
		return minHeap;
	}
}
