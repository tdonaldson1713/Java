/*
 * State.java represents a program state. It defines the set of variables
 * and their associated values that are active during interpretation.
 */

package clite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class State extends HashMap<VariableRef, Value> {

	public State( ) { }

	public State(VariableRef key, Value val) {
		this.put(key, val);
	}

	public State update(VariableRef key, Value val) {
		this.put(key, val);
		return this;
	}

	public State update(VariableRef key, int id, Value val) {
		this.put(key, val);
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
			System.out.print(sep + "<" + key + ", " + get(key) + ">");
			sep = ", ";
		}
		System.out.println(" }");
	}
}

/*// Represents the program state as it executes and simulates its runtime behavior
class State {

    HashMap <Variable, Value> static_area;  // Static area of memory (for globals)
    Stack <ActivationRecord> runtime_stack; // Stack area of memory (holds a stack of activation records)
    ArrayList <Value> heap_area;            // Heap area of memory (holds dynamically allocated elements, e.g. arrays)

    final static int STACK_LIMIT = 512; // Sets the limits on how big the stack and heap areas
    final static int HEAP_LIMIT = 512;  // are allowed to grow; simulates the RAM size of a machine

    int a = 0;              // Next available memory address in stack area
    int h = STACK_LIMIT;    // Next available memory address in heap area

    HashMap <VariableRef, Value> visible_state;  // Set of variables visible to the active function (topmost AR)


}*/