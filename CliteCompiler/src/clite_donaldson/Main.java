/*
 * The driver code for the project
 */

package clite_donaldson;

import java.util.ArrayList;

public class Main {

	/* File names:
	 * array
	 * binarydigits /
	 * cast /
	 * hello /
	 * highest /
	 * newton /
	 */
	public static void main(String[] args) {
		ArrayList<String> files = new ArrayList<String>();
		files.add("programs/hello.cpp");
		files.add("programs/cast.cpp");
		files.add("programs/castimplicit.cpp");
		files.add("programs/factorial.cpp");
		files.add("programs/array.cpp");

		String filename = "programs/array.cpp";
    	System.out.println("Begin parsing... " + filename);
    	Parser parser  = new Parser(new Lexer(filename));
        Program prog = parser.program();
        prog.display();      // display abstract syntax tree

        System.out.println("\nBegin type checking...");
        System.out.println("\nType map:");
        TypeMap map = StaticTypeCheck.typing(prog.decpart);
        map.display();
        StaticTypeCheck.V(prog);
	}
}
