package preRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({ "nls", "unused" })
public class LambdaForEachCollectRule {

	List<String> listField = new ArrayList<>();

	public String convertForEachToCollect(String input) {
		List<String> oStrings = new ArrayList<>();
		List<String> objectList = new ArrayList<>();

		/* lambda param comment */
		/* lambda body */
		// save me
		// comment after oStrings
		oStrings.addAll(objectList.stream().map(o -> o.substring(0)) // comment after map
				.collect(Collectors.toList()));

		return oStrings.stream().collect(Collectors.joining(","));
	}

	public String convertForEachExpressionToCollect(String input) {
		List<String> oStrings = new ArrayList<>();
		List<String> objectList = new ArrayList<>();

		oStrings.addAll(objectList.stream().map(o -> o).collect(Collectors.toList()));

		return oStrings.stream().collect(Collectors.joining(","));
	}

	public String multiDeclarationFragment(String input) {
		List<String> oStrings = new ArrayList<>();
		List<String> ostrings2;
		List<String> ostrings3 = new ArrayList<>();
		List<String> objectList = new ArrayList<>();

		oStrings.addAll(objectList.stream().map(o -> o.substring(0)).collect(Collectors.toList()));

		return oStrings.stream().collect(Collectors.joining(","));
	}

	public String nonEmptyCollection(String input) {
		List<String> oStrings = new ArrayList<>();
		List<String> objectList = new ArrayList<>();

		oStrings.add("");
		oStrings.addAll(objectList.stream().map(o -> o.substring(0)).collect(Collectors.toList()));

		return oStrings.stream().collect(Collectors.joining(","));
	}

	public String addAfterForEach(String input) {
		List<String> oStrings = new ArrayList<>();
		List<String> objectList = new ArrayList<>();

		oStrings.addAll(objectList.stream().map(o -> o.substring(0)).collect(Collectors.toList()));

		oStrings.add("-");
		return oStrings.stream().collect(Collectors.joining(","));
	}

	public String blockBody(String input) {
		List<String> oStrings = new ArrayList<>();
		List<String> objectList = new ArrayList<>();

		oStrings.addAll(objectList.stream().map(o -> o.substring(0)).collect(Collectors.toList()));

		return oStrings.stream().collect(Collectors.joining(","));
	}

	public String multipleBodyStatements(String input) {
		List<String> oStrings = new ArrayList<>();
		List<String> objectList = new ArrayList<>();

		objectList.stream().map(o -> o.substring(0)).forEach((String oString) -> {
			oStrings.add(oString);
			if (oString.isEmpty()) {
				oStrings.add(input);
			}
		});

		return oStrings.stream().collect(Collectors.joining(","));
	}

	public String referencingOtherLocalVars(String input) {
		List<String> oStrings = new ArrayList<>();
		List<String> objectList = new ArrayList<>();
		String s = "";
		oStrings.addAll(objectList.stream().filter(oString -> oString.equals(s)).map(o -> o.substring(0))
				.collect(Collectors.toList()));

		return oStrings.stream().collect(Collectors.joining(","));
	}

	public String nestedBlocks(String input) {
		List<String> oStrings = new ArrayList<>();
		{
			List<String> objectList = new ArrayList<>();
			String s = "";
			if (s.isEmpty()) {
				oStrings.addAll(objectList.stream().filter(oString -> oString.equals(s)).map(o -> o.substring(0))
						.collect(Collectors.toList()));
			}
		}

		return oStrings.stream().collect(Collectors.joining(","));
	}

	public String addingToFieldMethod(String input) {
		List<String> objectList = new ArrayList<>();
		String s = "";
		listField.addAll(objectList.stream().filter(oString -> oString.equals(s)).map(o -> o.substring(0))
				.collect(Collectors.toList()));

		return listField.stream().collect(Collectors.joining(","));
	}

	public String thisAddInvocation(String input) {
		List<String> objectList = new ArrayList<>();
		String s = "";
		objectList.stream().filter(oString -> oString.equals(s)).map(o -> o.substring(0)).forEach(this::add);

		return listField.stream().collect(Collectors.joining(","));
	}

	private void add(String string) {
		listField.add(string);
	}

	public String collectRawList(String input) {
		List raw = Arrays.asList(input);
		List<Object> typedList = new ArrayList<>();
		raw.stream().forEach(typedList::add);

		return typedList.stream().map(Object::toString).collect(Collectors.joining());
	}
}
