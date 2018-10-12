package preRule;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Matthias Webhofer
 * @since 2.1.1
 */
@SuppressWarnings("nls")
public class FlatMapInsteadOfNestedLoopsRule {

	public void test() {
		List<List<List<String>>> matrix2 = Arrays.asList(Arrays.asList(Arrays.asList("asdf", "jkl")));
		// I don't want to break anything
		// I don't want to break anything
		// inner comment one
		/* lambda leading comment */
		/* lambda-body leading comment */
		// inner comment two
		// inner comment three
		/* inner comment four */
		/* lambda-body trailing comment */
		// trailing comment two
		// trailing comment one
		// outer comment
		matrix2.stream().filter(row -> !row.isEmpty()) // I don't want to break anything
				.flatMap(List::stream).filter(col -> !col.isEmpty()).flatMap(List::stream)
				.filter(element -> !element.isEmpty()).map(element -> element.substring(0, 1))
				.forEach(System.out::print);

		List<List<List<List<String>>>> matrix3 = Arrays
				.asList(Arrays.asList(Arrays.asList(Arrays.asList("asdf", "jkl"))));
		matrix3.stream().filter(row -> !row.isEmpty()).flatMap(List::stream).filter(col -> !col.isEmpty())
				.flatMap(List::stream).filter(cell -> !cell.isEmpty()).flatMap(List::stream)
				.filter(element -> !element.isEmpty()).map(element -> element.substring(0, 1))
				.forEach(System.out::print);

		List<List<String>> matrix = Arrays.asList(Arrays.asList("asdf", "jkl"));
		matrix.stream().filter(row -> !row.isEmpty()).forEach(row -> {
			System.out.print(row);
			row.stream().filter(element -> !element.isEmpty()).map(element -> element.substring(0, 1))
					.forEach(System.out::print);
		});

		/* comment inside filter */
		/* some comment inside map */
		// some comment here
		matrix.stream().filter(row -> !row.isEmpty()).flatMap(List::stream).filter(element -> !element.isEmpty())
				.map(element -> element.substring(0, 1)).forEach(element -> {
					System.out.print(element);
					System.out.print(element);
				});

		matrix.stream().flatMap(List::stream).filter(element -> !element.isEmpty())
				.map(element -> element.substring(0, 1)).forEach(System.out::print);

		matrix.stream().filter(row -> !row.isEmpty()).flatMap(List::stream).forEach(System.out::print);

		matrix.stream().flatMap(List::stream).forEach(System.out::print);

		matrix.stream().flatMap(List::stream).forEach(System.out::print);

		matrix.stream().flatMap(List::stream).forEach(System.out::print);

		matrix.stream().flatMap(List::stream).forEach(System.out::print);

		matrix.stream().flatMap(List::stream).forEach(System.out::print);

		matrix.stream().forEach(row -> matrix.get(row.size()).stream().forEach(System.out::println));

		matrix.stream().forEach(row -> row.stream().filter(element -> !element.isEmpty())
				.forEach(element -> System.out.println(row + element)));

		class TestObject {
			List<String> testList = Arrays.asList("asdf", "jkl");

			public List<String> getTestList() {
				return testList;
			}
		}

		List<TestObject> matrix4 = Arrays.asList(new TestObject(), new TestObject());
		matrix4.forEach(t -> t.getTestList().forEach(System.out::println));
	}

	public void testAvoidingOuterMostLoop() {
		List<List<List<String>>> matrix2 = Arrays.asList(Arrays.asList(Arrays.asList("asdf", "jkl")));
		matrix2.stream().filter(row -> !row.isEmpty()).forEach(row -> {
			/*
			 * Some statement just to avoid transformation
			 */
			if (matrix2.size() == 2) {
				return;
			}
			row.stream().filter(col -> !col.isEmpty()).flatMap(List::stream).filter(element -> !element.isEmpty())
					.map(element -> element.substring(0, 1)).forEach(System.out::print);
		});
	}

	public void testAvoidInnerMostLoop() {
		List<List<List<String>>> matrix2 = Arrays.asList(Arrays.asList(Arrays.asList("asdf", "jkl")));
		matrix2.stream().filter(first -> !first.isEmpty()).flatMap(List::stream).filter(second -> !second.isEmpty())
				.forEach(second -> {
					if (matrix2.size() == 2) {
						return;
					}
					second.stream().filter(third -> !third.isEmpty()).map(third -> third.substring(0, 1))
							.forEach(System.out::print);
				});
	}

	public void testQuartedNestedStreams() {
		List<List<List<List<String>>>> matrix3 = Arrays
				.asList(Arrays.asList(Arrays.asList(Arrays.asList("asdf", "jkl"))));
		/*
		 * Some statement just to avoid transformation
		 */
		matrix3.stream().flatMap(List::stream).forEach(second -> {
			int size = matrix3.size();
			second.forEach(third -> third.forEach(System.out::print));
		});
	}
}
