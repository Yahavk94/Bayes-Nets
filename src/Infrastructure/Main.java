package Infrastructure;
import java.util.Iterator;

public class Main {
	public static void main(String[] args) {
		Iterator<String> iterator = BN.getInstance().getNode("A").getCpt().keySet().iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
}
