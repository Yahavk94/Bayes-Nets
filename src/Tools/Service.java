package Tools;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import Infrastructure.BN;
import Infrastructure.Node;
import Utils.Cpt;
import Utils.Extract;

/**
 * This class represents the methods which are used by the algorithms.
 * @author Yahav Karpel
 */

public class Service {
	private static int adds = Integer.MIN_VALUE;
	private static int muls = Integer.MIN_VALUE;

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

		if (node.getCpt().containsKey(query)) /* Direct retrieval from the table */ {
			return node.getCpt().get(query);
		}

		// Indirect retrieval from the table
		double probability = 0;
		Stack<String> cq = Support.getComplementaryQueries(query);
		while (!cq.isEmpty()) {
			probability += node.getCpt().get(cq.pop());
		}

		return 1 - probability;
	}

	/**
	 * This method returns the given query as a BN formula.
	 */
	public static Queue<Queue<String>> getBNFormula(String query) {
		Queue<Queue<String>> formula = new LinkedList<>();

		// The distinct parts of the complete probability formula
		Queue<Map<String, String>> cpf = Support.completeProbabilityFormula(query);

		// The representation separates the distinct parts by a plus sign
		adds += cpf.size() - 1;

		while (!cpf.isEmpty()) /* Create the distinct parts of the BN formula */ {
			Map<String, String> map = cpf.remove();
			Queue<String> dp = new LinkedList<>();

			// Create the current distinct part of the BN formula
			Iterator<String> mapIterator = map.keySet().iterator();
			while (mapIterator.hasNext()) {
				Node current = BN.getInstance().getNode(mapIterator.next());

				Iterator<String> currentParentsIterator = current.parentsIterator();
				if (!currentParentsIterator.hasNext()) {
					dp.add(current.getName() + "=" + map.get(current.getName()));
					continue;
				}

				SortedSet<String> parents = new TreeSet<>();

				while (currentParentsIterator.hasNext()) {
					String name = currentParentsIterator.next();
					parents.add(name + "=" + map.get(name));
				}

				dp.add(current.getName() + "=" + map.get(current.getName()) + "|" + parents);
			}

			formula.add(dp);

			// The representation separates the calculations in the distinct parts by a multiplication sign
			muls += dp.size() - 1;
		}

		return formula;
	}

	/**
	 * This method returns the hidden nodes of the given query after filtering out those unnecessary ones.
	 */
	public static List<Node> getFilteredHiddenNodes(String query) {
		List<Node> HN = Support.getHiddenNodes(query);

		if (!HN.isEmpty()) {
			Iterator<String> iterator = Support.getUnnecessaryHiddenNodes(query).iterator();
			while (iterator.hasNext()) {
				HN.remove(BN.getInstance().getNode(iterator.next()));
			}
		}

		return HN;
	}

	/**
	 * This method returns the initial factors of the nodes in the network.
	 */
	public static Stack<Cpt> getFactors(String query) {
		Stack<Cpt> factors = new Stack<>();

		Map<String, String> evidence = Extract.getEvidenceNodes(query);
		Set<String> unnecessaryHN = Support.getUnnecessaryHiddenNodes(query);

		Iterator<Node> nodesIterator = BN.getInstance().iterator();
		while (nodesIterator.hasNext()) {
			Node current = nodesIterator.next();

			if (unnecessaryHN.contains(current.getName())) /* Filter out the unnecessary factors */ {
				continue;
			}

			Cpt cpt = new Cpt();

			Iterator<String> cptIterator = current.getCpt().iterator();
			while (cptIterator.hasNext()) {
				Stack<String> samples = getSamples(cptIterator.next());

				while (!samples.isEmpty()) {
					String sample = samples.pop();
					SortedSet<String> set = Extract.getSortedSet(sample);

					boolean indicator = false;

					Iterator<String> mapIterator = evidence.keySet().iterator();
					while (mapIterator.hasNext()) {
						String name = mapIterator.next();

						if (set.contains(name + "=" + evidence.get(name))) {
							if (set.size() == 1) {
								indicator = true;
								break;
							}

							set.remove(name + "=" + evidence.get(name));

							if (set.size() != 1) {
								continue;
							}

							if (evidence.containsKey(Extract.QX(set.iterator().next()))) {
								indicator = true;
								break;
							}
						}
					}

					if (!indicator) {
						Iterator<String> setIterator = set.iterator();
						while (setIterator.hasNext()) {
							if (evidence.containsKey(Extract.QX(setIterator.next()))) {
								indicator = true;
								break;
							}
						}

						if (!indicator) {
							cpt.put(set.toString(), computeProbability(sample));
						}
					}
				}
			}

			if (cpt.size() > 1) {
				factors.push(cpt);
			}
		}

		return factors;
	}

	/**
	 * This method multiplies the given factors.
	 */
	public static Cpt mulFactors(Queue<Cpt> minHeap) {
		Cpt cpt = minHeap.remove();

		while (!minHeap.isEmpty()) {
			Cpt current = minHeap.remove();

			Cpt temp = new Cpt();

			Iterator<String> cptIterator = cpt.iterator();
			while (cptIterator.hasNext()) {
				SortedSet<String> outer = Extract.getSortedSet(cptIterator.next());

				Iterator<String> currentIterator = current.iterator();
				while (currentIterator.hasNext()) {
					SortedSet<String> inner = Extract.getSortedSet(currentIterator.next());

					if (!Collections.disjoint(outer, inner)) {
						Set<String> union = Stream.concat(outer.stream(), inner.stream()).collect(Collectors.toSet());

						// Associate the resulting probability with the new query
						temp.put(new TreeSet<>(union).toString(), cpt.get(outer.toString()) * current.get(inner.toString()));

						muls += 1;
					}
				}
			}

			cpt = temp;
		}

		return cpt;
	}

	/**
	 * This method removes the given factor from the cpt.
	 */
	public static Cpt removeFactor(Cpt top, Node node) {
		Cpt cpt = new Cpt();

		// A random value from the values set
		String random = node.getRandomValue();

		Iterator<String> cptIterator = top.iterator();
		while (cptIterator.hasNext()) {
			String current = cptIterator.next();

			if (!current.contains(node.getName() + "=" + random)) {
				continue;
			}

			SortedSet<String> newQuery = Extract.getSortedSet(current);

			// Initialize the resulting probability
			double probability = top.get(newQuery.toString());

			// Remove the given factor and the random value selected
			newQuery.remove(node.getName() + "=" + random);

			if (newQuery.isEmpty()) {
				continue;
			}

			// Calculate the resulting probability
			Iterator<String> valuesIterator = node.valuesIterator();
			while (valuesIterator.hasNext()) {
				String candidate = valuesIterator.next();

				if (!candidate.equals(random)) {
					SortedSet<String> temp = new TreeSet<>(newQuery);

					temp.add(node.getName() + "=" + candidate);
					probability += top.get(temp.toString());

					adds += 1;
				}
			}

			// Associate the resulting probability with the new query
			cpt.put(newQuery.toString(), probability);
		}

		return cpt;
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
	 * This method returns the number of additions and multiplications.
	 */
	public static String getComplexity() {
		return adds + "," + muls;
	}
}
