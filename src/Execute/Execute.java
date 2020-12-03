package Execute;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import Methods._01_Simple;
import Methods._02_VE;
import Methods._03_Heuristic;
import Utils.Input;

/**
 * This class represents the probabilistic inference executor.
 * @author Yahav Karpel
 */

public class Execute {
	public static void main(String[] args) throws IOException {
		PrintWriter output = new PrintWriter(new FileWriter("Output.txt"));

		Queue<String> queries = new LinkedList<>();
		Queue<Integer> methods = new LinkedList<>();

		while (!Input.queries.isEmpty()) {
			String query = Input.queries.remove(0);
			int index = query.lastIndexOf(",");
			queries.add(query.substring(0, index));
			methods.add(Integer.parseInt(query.substring(index + 1)));
		}

		while (!queries.isEmpty()) {
			int method = methods.remove();
			if (method == 1) {
				output.println(new _01_Simple().inference(queries.remove()));
			} else if (method == 2) {
				output.println(new _02_VE().inference(queries.remove()));
			} else if (method == 3) {
				output.println(new _03_Heuristic().inference(queries.remove()));
			}
		}

		output.close();
	}
}
