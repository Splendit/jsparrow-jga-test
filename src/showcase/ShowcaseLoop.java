package showcase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShowcaseLoop {

	public void showcase(List<String> list) {
		Iterator<String> iterator = list.iterator();
		List<String> filteredList = new ArrayList<String>();
		while (iterator.hasNext()) {
			String string = iterator.next();
			if(string.startsWith("a")) {
				filteredList.add(string);
			}
		}
	}
}
