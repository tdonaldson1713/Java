/*
 * State.java represents a program state. It defines the set of variables
 * and their associated values that are active during interpretation.
 */

package clite_donaldson;

import java.util.*;

public class State extends HashMap<VariableRef, ArrayList<Value>> {

	public State( ) { }

	public State update(VariableRef key, Value val) {
		if (key instanceof Variable) {
			ArrayList<Value> value = get(key);
			value.set(0, val);
		}
		else 
			throw new IllegalArgumentException("not a variable reference");
		return this;
	}

	public State update(VariableRef key, int id, Value val) {
		if (key instanceof ArrayRef) {
			ArrayList<Value> values = get(key);
			if (id >= values.size()) {
				values.add(val);
			} else {
				values.set(id, val);
			}
		}
		else 
			throw new IllegalArgumentException("not an array reference");
		return this;
	}

	public State update(VariableRef key, ArrayList<Value> val) {
		if (key instanceof ArrayRef) {
			put(key, val);
		}
		else if (key instanceof Variable) {
			put(key, val);
		} else 
			throw new IllegalArgumentException("not an array reference");
		return this;    	
	}

	public State update (State t) {
		for (VariableRef key : t.keySet( ))
			put(key, t.get(key));
		return this;
	}

	public void display( ) {
		System.out.print("{ ");
		String sep = "";
		for (VariableRef key : keySet( )) {
			if (key instanceof Variable)
				System.out.print(sep + "<" + key + ", " + get(key).get(0) + ">");
			else
				System.out.print(sep + "<" + key + "[" + get(key).size() + "]" + ", " + get(key) + ">");
			sep = ", ";
		}
		System.out.println(" }");
	}
}