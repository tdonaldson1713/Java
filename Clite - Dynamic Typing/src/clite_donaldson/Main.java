/*
 * The driver code for the project
 */

package clite_donaldson;

public class Main {

	public static void main(String[] args) {
		String filename = "programs/newton.cpp";
    	System.out.println("Begin parsing... " + filename);
    	Parser parser  = new Parser(new Lexer(filename));
        Program prog = parser.program();
        prog.display();

//        System.out.println("\nBegin type checking..." + filename);
//        System.out.println("\nType map:");
//        TypeMap map = StaticTypeCheck.typing(prog.decpart);
//        map.display();
// 
//        StaticTypeCheck.V(prog);
//        Program out = TypeTransformer.T(prog, map);
//        System.out.println("\nTransformed Abstract Syntax Tree");
//        out.display();
//
//        System.out.println("\nBegin interpreting..." + filename);
//        Semantics semantics = new Semantics( );
//        State state = semantics.M(out);
//        System.out.println("\nFinal State");
//        state.display( );

        DynamicTyping dynamic = new DynamicTyping( );
        State state = dynamic.M(prog);
        System.out.println("\nFinal State");
        state.display( );
	}
}
