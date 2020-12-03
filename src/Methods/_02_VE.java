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

public class _02_VE extends _01_Simple implements Sortable {
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

			// The filtered hidden nodes of the given query
			List<Node> HN = Service.getFHN(query);

			// The elimination order
			sort(HN);

			// The initial factors
			Stack<Cpt> factors = Service.getFactors(query);

			while (!HN.isEmpty()) /* Join and eliminate the factors of the hidden nodes */ {
				Node current = HN.remove(0);

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

				// Join
				minHeap = Service.mulFactors(minHeap);

				// Eliminate
				minHeap.add(Service.removeFactor(minHeap.remove(), current));
			}

			while (!factors.isEmpty()) /* Join the remaining factors */ {
				minHeap.add(factors.pop());
				minHeap = Service.mulFactors(minHeap);
			}

			Cpt cpt = minHeap.remove();

			// The results of the queries in the remaining cpt
			Queue<Double> results = new LinkedList<>();

			Stack<Double> temp = new Stack<>();
			String qn = Extract.QN(query);

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
