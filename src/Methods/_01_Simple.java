package Methods;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import Tools.Service;

/**
 * This class implements a simple probabilistic inference algorithm.
 * @author Yahav Karpel
 */

public class _01_Simple implements Probable {
	@Override
	public String inference(String query) {
		DecimalFormat df = new DecimalFormat("0.00000");

		// The samples set
		Stack<String> samples = Service.getSamples(query);

		// The results of the queries in the samples set
		Queue<Double> results = new LinkedList<>();

		while (!samples.isEmpty()) {
			String sample = samples.pop();

			// The given query as a BN formula
			Queue<Stack<String>> formula = Service.getBNFormula(sample);

			if (formula == null) /* The formula could not be created */ {
				System.out.println(df.format(Service.calculateProbability(sample)) + "," + Service.getComplexity());
				return null;
			}

			double result = 0;

			while (!formula.isEmpty()) /* Calculate the probability of each part in the formula */ {
				Stack<String> stack = formula.remove();

				double probability = 1;

				while (!stack.isEmpty()) {
					probability = probability * Service.calculateProbability(stack.pop());
				}

				result += probability;
			}

			results.add(result);
		}

		System.out.println(df.format(Service.normalization(results)) + "," + Service.getComplexity());
		return null;
	}
}
