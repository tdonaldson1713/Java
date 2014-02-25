/*
 * The driver code for the project
 */

package clite_donaldson;

public class Main {

    public static void main(String[] args) {
    	/*
    	 * hello
    	 * factorial
    	 * castimplicit
    	 * cast
    	 * array
    	 */
        String filename = "programs/array.cpp";
    	System.out.println("Begin parsing... " + filename);
    	Parser parser  = new Parser(new Lexer(filename));
        Program prog = parser.program();
        prog.display();      // display abstract syntax tree

        System.out.println("\nBegin type checking...");
        System.out.println("\nType map:");
        TypeMap map = StaticTypeCheck.typing(prog.decpart);
        map.display();
        //StaticTypeCheck.V(prog);
        Program out = TypeTransformer.T(prog, map);
        System.out.println("\nTransformed Abstract Syntax Tree");
        out.display();
    }
}
