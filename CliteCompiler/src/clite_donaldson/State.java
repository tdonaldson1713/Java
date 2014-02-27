/*
 * State.java represents a program state. It defines the set of variables
 * and their associated values that are active during interpretation.
 */

package clite_donaldson;

import java.util.HashMap;

public class State extends HashMap<VariableRef, Value> {
	public void display () {
		System.out.print("{ ");
		String sep = "";
		for (VariableRef key : keySet() ) {
			System.out.print(sep + "<" + key + ", " + get(key) + ">");
			sep = ", ";
		}
		System.out.println(" }");
	}

	public State update(VariableRef target, Value v) {
		this.put(target, v);	
		return this;
	}
}

