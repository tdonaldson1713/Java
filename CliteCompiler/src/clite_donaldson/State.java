/*
 * State.java represents a program state. It defines the set of variables
 * and their associated values that are active during interpretation.
 */

package clite_donaldson;

import java.util.ArrayList;
import java.util.HashMap;

public class State extends HashMap<VariableRef, Value> {
	public HashMap<ArrayRef, ArrayList<Integer>> intArray = new HashMap<ArrayRef, ArrayList<Integer>>();
	public HashMap<ArrayRef, ArrayList<Character>> charArray = new HashMap<ArrayRef, ArrayList<Character>>();
	public HashMap<ArrayRef, ArrayList<Boolean>> boolArray = new HashMap<ArrayRef, ArrayList<Boolean>>();
	public HashMap<ArrayRef, ArrayList<Float>> floatArray = new HashMap<ArrayRef, ArrayList<Float>>();

	public void display () {
		System.out.print("{ ");
		String sep = "";
		String sep2 = "";
		
		for (VariableRef key : keySet() ) {
			if (key instanceof Variable) {
				System.out.print(sep + "<" + key + ", " + get(key) + ">");
				sep = ", ";
			} else if (key instanceof ArrayRef) {
				ArrayRef ar = (ArrayRef) key;
				System.out.print(sep + "<" + key + ", [");
				
				if (intArray.containsKey(ar)) {
					for (Integer i : intArray.get(ar)) {
						System.out.print(sep2 + i);
						sep2 = ", ";
					}
				} else if (floatArray.containsKey(ar)) {
					for (Float f : floatArray.get(ar)) {
						System.out.print(sep2 + f);
						sep2 = ", ";
					}
				} else if (charArray.containsKey(ar)) {
					for (Character c : charArray.get(ar)) {
						System.out.print(sep2 + c);
						sep2 = ", ";
					}
				} else if (boolArray.containsKey(ar)) {
					for (Boolean b : boolArray.get(ar)) {
						System.out.print(sep2 + b);
						sep2 = ", ";
					}
				}
				
				sep2 = "";
				System.out.print("]>, ");
			}
		}

		System.out.println(" }");
	}

	public State update(Variable target, Value v) {
		this.put(target, v);	
		return this;
	}

	public State update(ArrayRef target, Value v) {
		if (intArray.containsKey(target)) {
			ArrayList<Integer> array = intArray.get(target);
			array.set(((Value) target.index).intValue(), ((IntValue) v).intValue());
			this.intArray.put(target, array);
		} else if (floatArray.containsKey(target)) {
			ArrayList<Float> array = floatArray.get(target);
			array.set(((Value) target.index).intValue(), ((FloatValue) v).floatValue());
			this.floatArray.put(target, array);
		} else if (charArray.containsKey(target)) {
			ArrayList<Character> array = charArray.get(target);
			array.set(((Value) target.index).intValue(), ((CharValue) v).charValue());
			this.charArray.put(target, array);
		} else if (boolArray.containsKey(target)) {
			ArrayList<Boolean> array = boolArray.get(target);
			array.set(((Value) target.index).intValue(), ((IntValue) v).boolValue());
			this.boolArray.put(target, array);
		}
		
		return this;
	}
}

