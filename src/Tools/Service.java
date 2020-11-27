package Tools;
import java.util.Collections;
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
import Utils.Cpt;
import Utils.Extract;

/**
 * This class represents the methods which are used by the algorithms.
 * @author Yahav Karpel
 */

public class Service {
	private static int adds;
	private static int muls;

	/**
	 * This method resets the number of additions and multiplications.
	 */
	public static void reset() {
		adds = muls = 0;
	}

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

		Stack<String> cq = Utensil.getComplementaryQueries(query);
		while (!cq.isEmpty()) {
			probability += node.getCpt().get(cq.pop());
		}

		return 1 - probability;
	}

	/**
	 * This method returns the normalization of the required probability.
	 */
	public static double normalization(Queue<Double> probabilities) {
		double required = probabilities.remove();
		double total = required;

		while (!probabilities.isEmpty()) {
			total += probabilities.remove();
			adds += 1;
		}

		return required / total;
	}

	/**
	 * This method returns the given query as a BN formula.
	 */
	public static Queue<Queue<String>> getBNFormula(String query) {
		Queue<Map<String, String>> cpf = Utensil.completeProbabilityFormula(query);
		if (cpf == null) /* The formula cannot be created */ {
			return null;
		}

		adds += cpf.size() - 1;

		// The distinct parts of the formula
		Queue<Queue<String>> formula = new LinkedList<>();

		while (!cpf.isEmpty()) {
			Queue<String> queue = new LinkedList<>();
			Map<String, String> map = cpf.remove();

			Iterator<String> mapIterator = map.keySet().iterator();
			while (mapIterator.hasNext()) {
				Node current = BN.getInstance().getNode(mapIterator.next());

				Iterator<String> parentsIterator = current.parentsIterator();
				if (!parentsIterator.hasNext()) {
					queue.add(current.getName() + "=" + map.get(current.getName()));
					continue;
				}

				Set<String> set = new HashSet<>();

				while (parentsIterator.hasNext()) {
					String parent = parentsIterator.next();
					set.add(parent + "=" + map.get(parent));
				}

				queue.add(current.getName() + "=" + map.get(current.getName()) + "|" + set);
			}

			formula.add(queue);
			muls += queue.size() - 1;
		}

		return formula;
	}

	/**
	 * This method returns the initial factors of the nodes in the network.
	 */
	public static Stack<Cpt> getFactors(String query) {
		Map<String, String> evidence = Extract.evidenceNodes(query);

		// The initial factors
		Stack<Cpt> factors = new Stack<>();

		Iterator<Node> nodesIterator = BN.getInstance().iterator();
		while (nodesIterator.hasNext()) {
			Node current = nodesIterator.next();

			if (Utensil.filter(query).contains(current.getName())) {
				continue;
			}

			Cpt cpt = new Cpt();

			Iterator<String> cptIterator = current.getCpt().iterator();
			while (cptIterator.hasNext()) {
				Stack<String> samples = getSamples(cptIterator.next());

				while (!samples.isEmpty()) {
					boolean indicator = false;
					String sample = samples.pop();
					Set<String> set = Extract.ordered(sample);

					Iterator<String> mapIterator = evidence.keySet().iterator();
					while (mapIterator.hasNext()) {
						String X = mapIterator.next();

						if (set.contains(X + "=" + evidence.get(X))) /* Omit unnecessary information */ {
							indicator = true;

							set.remove(X + "=" + evidence.get(X));
							if (set.isEmpty()) {
								break;
							}
						}
					}

					if (indicator) {
						if (!set.isEmpty()) {
							cpt.put(set.toString(), calculateProbability(sample));
						}

						continue;
					}

					// Note that indicator = false
					Iterator<String> setIterator = set.iterator();
					while (setIterator.hasNext()) {
						if (evidence.containsKey(Extract.QX(setIterator.next()))) {
							indicator = true;
							break;
						}
					}

					if (!indicator) /* Avoid adding unnecessary information */ {
						cpt.put(set.toString(), calculateProbability(sample));
					}
				}
			}

			if (!cpt.isEmpty()) /* Unable to add an empty cpt */ {
				factors.push(cpt);
			}
		}

		return factors;
	}

	public static List<Node> HNFilter(String query) {
		List<Node> hidden = Extract.hiddenNodes(query);

		Iterator<String> setIterator = Utensil.filter(query).iterator();
		while (setIterator.hasNext()) {
			hidden.remove(BN.getInstance().getNode(setIterator.next()));
		}

		return hidden;
	}

	/**
	 * This method joins the given factors.
	 */
	public static Queue<Cpt> mulFactors(Queue<Cpt> minHeap) {
		Cpt min = minHeap.remove();

		while (!minHeap.isEmpty()) {
			Cpt top = minHeap.remove();
			Cpt cpt = new Cpt();

			Iterator<String> minIterator = min.iterator();
			while (minIterator.hasNext()) {
				Set<String> outer = Extract.ordered(minIterator.next());

				Iterator<String> topIterator = top.iterator();
				while (topIterator.hasNext()) {
					Set<String> inner = Extract.ordered(topIterator.next());

					if (!Collections.disjoint(outer, inner)) {
						Set<String> union = new HashSet<>(outer);
						union.addAll(inner);

						cpt.put(union.toString(), min.get(outer.toString()) * top.get(inner.toString()));
						muls += 1;
					}
				}
			}

			min = cpt;
		}

		minHeap.add(min);
		return minHeap;
	}

	/**
	 * This method eliminates the given factor from the cpt.
	 */
	public static Cpt eliminateFactor(Cpt top, Node node) {
		String chosen = node.valuesIterator().next();

		Cpt cpt = new Cpt();

		Iterator<String> cptIterator = top.iterator();
		while (cptIterator.hasNext()) {
			String current = cptIterator.next();

			if (!current.contains(node.getName() + "=" + chosen)) {
				continue;
			}

			Set<String> set = Extract.ordered(current);
			double probability = top.get(set.toString());

			set.remove(node.getName() + "=" + chosen);

			Iterator<String> valuesIterator = node.valuesIterator();
			while (valuesIterator.hasNext()) {
				String candidate = valuesIterator.next();

				if (!candidate.equals(chosen)) {
					Set<String> temp = new HashSet<>(set);
					temp.add(node.getName() + "=" + candidate);
					probability += top.get(temp.toString());
					adds += 1;
				}
			}

			cpt.put(set.toString(), probability);
		}

		return cpt;
	}

	/**
	 * This method returns the number of additions and multiplications.
	 */
	public static String getComplexity() {
		return adds + "," + muls;
	}
}
