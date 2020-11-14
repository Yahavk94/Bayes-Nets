package Methods;
import java.util.Iterator;
import java.util.List;
import Tools.Service;

public class Main {

	public static void main(String[] args) {
		Iterator<List<String>> it = Service.getBNFormula("B=true|J=true,M=true").iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}
}
