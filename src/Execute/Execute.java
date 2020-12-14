package Execute;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import Methods._01_Simple;
import Methods._02_VE;
import Methods._03_Heuristic;
import Utils.Input;
import Utils.Method;

/**
 * This class represents the probabilistic inference executor.
 * @author Yahav Karpel
 */

public class Execute {
	public static void main(String[] args) throws IOException {
		PrintWriter output = new PrintWriter(new FileWriter("Output.txt"));

		while (!Input.queries.isEmpty()) {
			String current = Input.queries.remove(0);
			int index = current.lastIndexOf(",");

			String query = current.substring(0, index);
			Method method = Method.convert(current.substring(index + 1));

			if (method == Method.SIMPLE) {
				output.print(new _01_Simple().inference(query));
			} else if (method == Method.VE) {
				output.print(new _02_VE().inference(query));
			} else if (method == Method.HEURISTIC) {
				output.print(new _03_Heuristic().inference(query));
			}

			if (!Input.queries.isEmpty()) {
				output.println();
			}
		}

		output.close();
	}
}
