package Tools;
import java.util.Collections;
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
		Stack<String> samples = Support.getComplementaryQueries(query);
		samples.push(query);
		return samples;
	}

	/**
	 * This method computes and returns the probability of the given query.
	 */
	public static double computeProbability(String query) {
		Node node = BN.getInstance().getNode(Extract.QX(query));

		if (node.getCpt().containsKey(query)) {
			return node.getCpt().get(query);
		}

		double probability = 0;

		Stack<String> cq = Support.getComplementaryQueries(query);
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
		Queue<Queue<String>> formula = new LinkedList<>();

		Queue<Map<String, String>> cpf = Support.completeProbabilityFormula(query);
		adds += cpf.size() - 1;

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

				Set<String> set = new TreeSet<>();

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
		Stack<Cpt> factors = new Stack<>();

		Set<String> filter = Support.HNFilter(query);
		Map<String, String> evidence = Extract.evidenceNodes(query);

		Iterator<Node> nodesIterator = BN.getInstance().iterator();
		while (nodesIterator.hasNext()) {
			Node current = nodesIterator.next();

			if (filter.contains(current.getName())) /* Ignore unnecessary factors */ {
				continue;
			}

			Cpt cpt = new Cpt();

			Iterator<String> cptIterator = current.getCpt().iterator();
			while (cptIterator.hasNext()) {
				Stack<String> samples = getSamples(cptIterator.next());

				while (!samples.isEmpty()) {
					String sample = samples.pop();
					Set<String> set = Extract.ordered(sample);

					boolean indicator = false;

					Iterator<String> mapIterator = evidence.keySet().iterator();
					while (mapIterator.hasNext()) {
						String name = mapIterator.next();

						if (set.contains(name + "=" + evidence.get(name))) /* Omit unnecessary information */ {
							indicator = true;

							set.remove(name + "=" + evidence.get(name));
							if (set.isEmpty()) {
								break;
							}
						}
					}

					if (indicator) {
						if (set.size() == 1) {
							if (evidence.containsKey(Extract.QX(set.iterator().next()))) {
								continue;
							}
						}

						indicator = false;
					}

					Iterator<String> setIterator = set.iterator();
					while (setIterator.hasNext()) {
						if (evidence.containsKey(Extract.QX(setIterator.next()))) {
							indicator = true;
							break;
						}
					}

					if (!indicator) /* Ignore unnecessary information */ {
						if (!set.isEmpty()) {
							cpt.put(set.toString(), computeProbability(sample));
						}
					}
				}
			}

			if (!cpt.isEmpty()) /* Unable to push an empty cpt */ {
				factors.push(cpt);
			}
		}

		return factors;
	}

	/**
	 * This method returns the filtered hidden nodes of the given query
	 */
	public static List<Node> getFHN(String query) {
		List<Node> HN = Extract.hiddenNodes(query);

		if (!HN.isEmpty()) {
			Iterator<String> setIterator = Support.HNFilter(query).iterator();
			while (setIterator.hasNext()) {
				HN.remove(BN.getInstance().getNode(setIterator.next()));
			}
		}

		return HN;
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
						Set<String> union = new TreeSet<>(outer);
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
	public static Cpt removeFactor(Cpt top, Node node) {
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
					Set<String> temp = new TreeSet<>(set);
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
