package Tools;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
	 * This method returns the sample set associated with the given query.
	 */
	public static Stack<String> getSamples(String query) {
		adds = muls = 0;

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
	public static Queue<Stack<String>> getBNFormula(String query) {
		Stack<Map<String, String>> cpf = Utensil.completeProbabilityFormula(query);
		if (cpf == null) /* The formula cannot be created */ {
			return null;
		}

		adds += cpf.size() - 1;

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

				Set<String> set = new HashSet<>();

				while (iterator.hasNext()) {
					String parent = iterator.next();
					set.add(parent + "=" + map.get(parent));
				}

				stack.push(node.getName() + "=" + map.get(node.getName()) + "|" + set);
			}

			formula.add(stack);
			muls += stack.size() - 1;
		}

		return formula;
	}

	/**
	 * This method returns the factors of the nodes in the network.
	 */
	public static Stack<Cpt> getFactors(String query) {
		Iterator<Node> iterator = BN.getInstance().iterator();
		Map<String, String> evidence = Extract.evidenceNodes(query);

		// The factors of the nodes
		Stack<Cpt> factors = new Stack<>();

		while (iterator.hasNext()) {
			Iterator<String> cptIterator = iterator.next().getCpt().iterator();

			Cpt cpt = new Cpt();

			while (cptIterator.hasNext()) {
				Stack<String> samples = getSamples(cptIterator.next());

				while (!samples.isEmpty()) {
					Iterator<String> mapIterator = evidence.keySet().iterator();

					boolean indicator = false;
					String sample = samples.pop();
					Set<String> set = Extract.ordered(sample);

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

	/**
	 * This method joins the given factors.
	 */
	public static Queue<Cpt> mulFactors(Queue<Cpt> minHeap) {
		Cpt min = minHeap.remove();

		while (!minHeap.isEmpty()) {
			Iterator<String> minIterator = min.iterator();
			Cpt top = minHeap.remove();

			Cpt cpt = new Cpt();

			while (minIterator.hasNext()) {
				Iterator<String> topIterator = top.iterator();
				Set<String> outer = Extract.ordered(minIterator.next());

				while (topIterator.hasNext()) {
					Set<String> inner = Extract.ordered(topIterator.next());

					if (Collections.disjoint(outer, inner)) {
						continue;
					}

					Set<String> set = new HashSet<>(outer);
					set.addAll(inner);

					cpt.put(set.toString(), min.get(outer.toString()) * top.get(inner.toString()));
					muls += 1;
				}
			}

			min = cpt;
		}

		minHeap.add(min);
		return minHeap;
	}

	/**
	 * This method eliminates the given factor.
	 */
	public static Cpt eliminateFactor(Cpt top, Node node) {
		Iterator<String> iterator = top.iterator();

		Cpt cpt = new Cpt();

		while (iterator.hasNext()) {
			String value = node.valuesIterator().next();

			String current = iterator.next();
			Set<String> set = Extract.ordered(current);

			double probability = top.get(set.toString());

			if (current.contains(node.getName() + "=" + value)) {
				Iterator<String> valuesIterator = node.valuesIterator();

				while (valuesIterator.hasNext()) {
					String candidate = valuesIterator.next();

					if (!candidate.equals(value)) {
						String temp = current.replace(node.getName() + "=" + value, node.getName() + "=" + candidate);
						probability += top.get(Extract.ordered(temp).toString());
						adds += 1;
					}
				}

				set.remove(node.getName() + "=" + value);
				cpt.put(set.toString(), probability);
			}
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
