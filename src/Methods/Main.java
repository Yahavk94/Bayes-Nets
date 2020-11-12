package Methods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import Infrastructure.BN;
import Infrastructure.Node;
import Tools.Service;
import Tools.Utensil;

public class Main {

	public static void main(String[] args) {
		Iterator<List<String>> it = Service.getBNFormula("P(B=true|J=true,M=true)").iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		
		System.out.println(Service.getLX("P(B=true|J=true,M=true)"));
	}
}
