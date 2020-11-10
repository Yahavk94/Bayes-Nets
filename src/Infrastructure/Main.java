package Infrastructure;
import java.util.Iterator;

import Utils.Input;

public class Main {
	public static void main(String[] args) {
		Iterator<String> iterator = BN.getBN().getNode("A").getCpt().keySet().iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}

		System.out.println();

		Iterator<String> iter = Input.getQList().iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
}
