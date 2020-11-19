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

		int additions = 0;
		int multiplications = 0;

		// The samples set
		Stack<String> samples = Service.getSamples(query);

		// The probabilities of the queries in the samples set
		Queue<Double> probabilities = new LinkedList<>();

		while (!samples.isEmpty()) {
			String sample = samples.pop();

			// The given query as a BN formula
			Queue<Stack<String>> formula = Service.getBNFormula(sample);

			if (formula == null) /* The formula could not be created */ {
				System.out.println(df.format(Service.calculateProbability(sample)) + "," + 0 + "," + 0);
				return df.format(Service.calculateProbability(sample)) + "," + 0 + "," + 0;
			}

			double result = 0;
			additions += formula.size() - 1;

			while (!formula.isEmpty()) /* Calculate the probability of each part in the formula */ {
				double probability = 1;
				multiplications += formula.peek().size() - 1;

				while (!formula.peek().isEmpty()) {
					probability = probability * Service.calculateProbability(formula.peek().pop());
				}

				formula.remove();
				result += probability;
			}

			probabilities.add(result);
		}

		// Normalization
		double result = probabilities.remove();
		double sum = result;
		while (!probabilities.isEmpty()) {
			sum += probabilities.remove();
			additions += 1;
		}

		System.out.println(df.format(result / sum) + "," + additions + "," + multiplications);
		return df.format(result / sum) + "," + additions + "," + multiplications;
	}
}
