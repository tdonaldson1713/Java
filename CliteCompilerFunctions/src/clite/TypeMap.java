/*
 * TypeMap.java implements the type map for storing
 * the variables and array references declared in the program.
 */

package clite;

import java.util.*;

public class TypeMap extends HashMap<VariableRef, Type> {

  public void display () {
    System.out.print("{ ");
    String sep = "";
    for (VariableRef key : keySet() ) {
        System.out.print(sep + "<" + key + ", " + get(key).getId() + ">");
        sep = ", ";
    }
    System.out.println(" }");
  }
}