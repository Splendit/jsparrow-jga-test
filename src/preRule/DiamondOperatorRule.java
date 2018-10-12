package preRule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "unchecked", "rawtypes", "nls"})
public class DiamondOperatorRule {
	
	private class GenericSample<T> {
		private T t;
		
		public GenericSample(T t) {
			this.t = t;
		}
		
		public T getValue() {
			return t;
		}
	}
	
	private abstract class Foo<T> {
		private T t;
		private List<T> field = new ArrayList<T>();
		private List<String>[] arrayList;
		
		
		public Foo (T t) {
			setValue(t);
			arrayList = new ArrayList[2];
		}
		
		private void setValue(T t) {
			this.t = t;
		}
		
		public T getValue() {
			return t;
		}
		
		public <TypeVar> List<TypeVar> genericOverloaded(List<TypeVar>typeVariable) {
			return new ArrayList</*save me */TypeVar>();
		}
		
		public ArrayList<String> genericOverloaded(ArrayList<String>typeVariable) {
			return new ArrayList<// line comment doesn't change
					String>();
		}
		
		public <TypeVar> List<TypeVar> genericOverloaded(List<TypeVar>typeVariable, int i) {
			return new ArrayList<TypeVar>(i);
		}
		
		public ArrayList<String> genericOverloaded(ArrayList<String>typeVariable, int i) {
			return new ArrayList<String>();
		}
		
		public <TypeVar> List<TypeVar> genericOverloaded(List<TypeVar>typeVariable, List<String> strings) {
			return new ArrayList<>();
		}
		
		public <TypeVar> List<TypeVar> genericOverloaded(List<TypeVar>typeVariable, String string, int i) {
			return new ArrayList<>(i);
		}
		
		public <TypeVar> List<TypeVar> genericOverloaded(List<TypeVar>typeVariable, String s) {
			return new ArrayList<TypeVar>();
		}
		
		public <TypeVar> List<TypeVar> genericOverloaded() {
			return new ArrayList<TypeVar>();
		}
		
		public void resetValue() {
			this.field = new ArrayList<T>();
			arrayList[0] = new ArrayList<String>();
			for(List<String>list = new ArrayList<String>(); !list.isEmpty(); ) {
				list.add("");
			}
		}
		
		/**
		 * SIM-820
		 */
		public void useOverloadedMethodOnParameterizedTypes(String input) {
			/*
			 * Should not be changed
			 */
			List<GenericSample> result = genericOverloaded(new ArrayList<GenericSample>());
			List<GenericSample> result2 = genericOverloaded(new ArrayList<GenericSample>(), 0);
			
			/*
			 * Should be changed in java 8
			 */
			List<GenericSample> result3 = genericOverloaded(new ArrayList<GenericSample>(), input);
			List<GenericSample> result4 = genericOverloaded(new ArrayList<GenericSample>(), input, 0);
		}
	}
	
	private String concatRawTypeList(List objects) {
		objects.add(new Object());
		Object val = objects.stream().map(o -> o.toString()).collect(Collectors.joining(", "));
		return val.toString();
	}
	
	public List<String> sampleGenericReturnTypeMethod(String input) {
		return new ArrayList<String>();
	}
	
	private String concatTypedList(List<String>foo, int i, Map<String, List<String>>map) {
		return foo.stream().collect(Collectors.joining(","));
	}
	
	public void inferListType() {
		List<String> list = new ArrayList<String>();
		list.add(BigDecimal.TEN.toString());
	}
	
	public void inferMapTypes() {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer ten = Integer.valueOf(10);
		map.put(BigDecimal.TEN.toString(), ten);
	}
	
	public void inferGenericTypes() {
		List<? extends Number> list = new ArrayList<Number>();
	}
	
	public void multipleDeclarationStatement(String input) {
		List<String> list1 = new ArrayList<String>(), list2 = new ArrayList<String>(), list3 = new ArrayList<>();
		list1.add(input);
		list2.add(input);
		list3.add(input);
	}
	
	public void lasyInstatiation(String input) {
		List<String> list;
		Map<String, GenericSample<String>> map;
		Integer ten = Integer.valueOf(10);
		if(ten > 0) {			
			list = new ArrayList<String>();
			map = new HashMap<String, GenericSample<String>>();
			list.add(ten.toString());
			map.put(input, new GenericSample<String>(ten.toString()));
		}
	}
	
	public void multipleMapDeclarationStatement(String input) {
		Map<String, Number> map1 = new HashMap<String, Number>(), map2 = new HashMap<String, Number>(), map3 = new HashMap<String, Number>();
		map1.put(input, 10);
		map2.put(input, 11);
		map3.put(input, 12);
	}
	
	public void blockWithMapAndLists(String input) {
		List<String> list = new ArrayList<String>();
		List<Integer> numList = new ArrayList<Integer>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		Set<String> set = new HashSet<String>();
		if(list.isEmpty()) {
			Set<Integer> numSet = new HashSet<Integer>();
			list.add(input);
			numSet.add(input.hashCode());
			numList.add(input.length());
			map.put(input, input.hashCode());
			set.add(input);
		}
	}
	
	public String userDefinedGeneric(String input) {
		GenericSample<String> userDefinedGeneric = new GenericSample<String>(input);
		
		return userDefinedGeneric.getValue();
	}
	
	public void nestedGenericTypes(String input) {
		GenericSample<String> userDefined = new GenericSample<String>(input);
		List<GenericSample<String>> list = new ArrayList<GenericSample<String>>();
		list.add(userDefined);
		
		Map<String, GenericSample<String>> map = new HashMap<String, GenericSample<String>>();
		map.put(input, userDefined);
	}
	
	public void diamondRuleOnMethodInvocation(String input) {
		concatRawTypeList(new ArrayList<String>());
		concatTypedList(new ArrayList<String>(), 1, new HashMap<String, List<String>>());
	}
	
	public void collectionArgumentInConstructor(String input) {
		ArrayList<String> ndCollection = new ArrayList<String>(new ArrayList<String>(1));
	}
	
	public String anonymousGenericInstatiation(String input) {
		Foo<String> foo = new Foo<String>(input){};
		return foo.getValue();
	}
	
	public void testOverloadWithTypeVariables_shouldNotChange(String input) {
		Foo<String> foo = new Foo<String>(input){};
		List<GenericSample> result = foo.genericOverloaded(new ArrayList<GenericSample>());
		List<GenericSample> result2 = foo.genericOverloaded(new ArrayList<GenericSample>(), 0);
	}
	
	public void testNormalOverloading_shouldChangeInJava8(String input) {
		Foo<String> foo = new Foo<String>(input){};
		List<GenericSample> result = foo.genericOverloaded(new ArrayList<GenericSample>(), input);
		List<GenericSample> result2 = foo.genericOverloaded(new ArrayList<GenericSample>(), input, 0);
	}
	
	public void savingComments() {
		List<String> myList = new ArrayList</* Some comment here */String>();
	}
	
	/**
	 * SIM-820 - if a diamond operator is used, eclipse will not indicate a compile error but the code will not compile.  
	 */
	CtorExpectingLambdas<String> collection = new CtorExpectingLambdas<String>(ArrayList::new, IdentityHashMap::new);
	
	class CtorExpectingLambdas<E> {
		Collection<E> collection;
		IdentityHashMap<E, Integer> map;
		public CtorExpectingLambdas (Supplier<List<E>> collectionFactory, Supplier<IdentityHashMap<E,Integer>> map) {
			this.collection = collectionFactory.get();
			this.map = map.get();
		}
	}
}