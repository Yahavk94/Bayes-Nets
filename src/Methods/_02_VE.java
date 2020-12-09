package Methods;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import Infrastructure.Node;
import Tools.Service;
import Utils.Cpt;
import Utils.Extract;

/**
 * This class implements VE algorithm.
 * @author Yahav Karpel
 */

public class _02_VE implements Sortable {
	@Override
	public String inference(String query) {
		DecimalFormat df = new DecimalFormat("0.00000");

		// Reset the number of additions and multiplications
		Service.reset();

		try {
			return df.format(Service.computeProbability(query)) + "," + Service.getComplexity();
		}

		catch (Exception e) {
			Queue<Cpt> minHeap = new PriorityQueue<>();

			// The hidden nodes of the given query after filtering out those unnecessary ones
			List<Node> HN = Service.getFilteredHiddenNodes(query);

			// Impose a total ordering on the hidden nodes
			sort(HN);

			// The initial factors
			Stack<Cpt> factors = Service.getFactors(query);

			while (!HN.isEmpty()) {
				Node current = HN.remove(0);

				// Associate the specified factors with the specified hidden node
				Stack<Cpt> temp = new Stack<>();
				while (!factors.isEmpty()) {
					temp.push(factors.pop());

					Iterator<String> cptIterator = temp.peek().iterator();
					while (cptIterator.hasNext()) {
						if (cptIterator.next().contains(current.getName())) {
							minHeap.add(temp.pop());
							break;
						}
					}
				}

				factors = temp;

				if (minHeap.isEmpty()) {
					continue;
				}

				// Multiply the factors
				minHeap.add(Service.mulFactors(minHeap));

				// Remove the current hidden node
				Cpt cpt = Service.removeFactor(minHeap.remove(), current);

				if (cpt.size() < 2) /* A meaningless cpt */ {
					continue;
				}

				if (cpt.size() == 2) {
					factors.add(cpt);
				} else {
					minHeap.add(cpt);
				}
			}

			while (!factors.isEmpty()) /* Multiply the remaining factors */ {
				minHeap.add(factors.pop());
				minHeap.add(Service.mulFactors(minHeap));
			}

			Cpt cpt = minHeap.remove();

			// The results of the queries in the remaining cpt
			Queue<Double> results = new LinkedList<>();

			String qn = Extract.QN(query);
			Stack<Double> temp = new Stack<>();

			Iterator<String> cptIterator = cpt.iterator();
			while (cptIterator.hasNext()) {
				String current = cptIterator.next();
				if (current.contains(qn)) {
					results.add(cpt.get(current));
					continue;
				}

				temp.push(cpt.get(current));
			}

			while (!temp.isEmpty()) {
				results.add(temp.pop());
			}

			return df.format(Service.normalization(results)) + "," + Service.getComplexity();
		}
	}

	@Override
	public void sort(List<Node> HN) {
		return;
	}
}
