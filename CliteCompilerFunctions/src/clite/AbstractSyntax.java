/*
 * AbstractSyntax.java implements the abstract syntax for CLite
 */

package clite;
import java.util.ArrayList;
import java.util.Stack;

class Indenter {
	public int level;
	public Indenter(int nextlevel) { level = nextlevel; }

	public void display(String message) {
		// displays a message on the next line at the current level
		String tab = "";
		System.out.println();
		for (int i=0; i<level; i++)
			tab = tab + "  ";
		System.out.print(tab + message);
	}
} // Indenter


class Program {
	// Program = Declarations decpart ; Block body
	Declarations globals;
	Functions functions;

	Program (Declarations d, Functions f) {
		globals = d;
		functions = f;
	}

	public void display () {
		int level = 0;
		Indenter indent = new Indenter(level);
		indent.display("Program (abstract syntax): ");
		globals.display(level+1);
		functions.display(level+1);
		System.out.println();
	}
}

class Declarations extends ArrayList<Declaration> {
	// Declarations = Declaration*
	// (a list of declarations d1, d2, ..., dn)

	public void display (int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": ");
		indent.display("  Globals = {");
		String sep = "";
		for (Declaration dcl : this) {
			System.out.print(sep);
			dcl.display();
			sep = ", ";
		}
		System.out.print("}");
	}
}

class Functions extends ArrayList<Function> {
	public void display(int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": ");
		indent.display("  Function:");
		String sep = "";
		
		for (Function fcn : this) {
			fcn.display();
		}
	}
}

class Function {
	Type t;
	Variable id;
	Declarations params, locals;
	Block body;
	
	public Function(Type t, Variable id, Declarations params, Declarations locals, Block body) {
		this.t = t;
		this.id = id;
		this.params = params;
		this.locals = locals;
		this.body = body;
	}
	
	public void display() {
		int level = 1;
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + " = ");
		System.out.print(id + ";" + "Return type = " + t);
		System.out.println("params = ");
		params.display(level+1);
		System.out.println("locals = ");
		locals.display(level+1);
		System.out.println("Block:");
		body.display(level);
	}
}

class Call implements Expression, Statement {
	Variable v;
	Stack<Expression> arguments;
	
	public Call(Variable v, Stack<Expression> arguments) {
		this.v = v;
		this.arguments = arguments;
	}
	
	@Override
	public void display(int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": " + v.id);
		
		if (arguments.size() > 0) {
			System.out.println("args =");
			for (int a = 0; a < arguments.size(); a++) {
				arguments.get(a).display(level);
			}
		}
	}
}

class Return {
	Expression result;
	Variable target;
	
	public Return(Variable v, Expression e) {
		result = e;
		target = v;
	}
	
	public void display(int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + " Return:");
		target.display(level + 1);
		result.display(level + 1);
	}
}

//class Declaration {
//    // Declaration = Variable v; Type t
//    Variable v;
//    Type t;
//
//    Declaration( ) { }
//    Declaration (Variable var, Type type) {
//        v = var;
//        t = type;
//    }
//    Declaration (String id, Type type) {
//        v = new Variable(id); t = type;
//    } // declaration
//
//    public void display () {
//       System.out.print("<" + v + ", " + t.getId() + ">");
//    }
//}

abstract class Declaration {
	// Declaration = VariableDecl | ArrayDecl
	Variable v;
	Type t;

	public void display () {
		//System.out.print("<" + v + ", " + t.getId() + ">");
	}
}

class VariableDecl extends Declaration {
	// VariableDecl = Variable v; Type t
	//    Variable v;
	//    Type t;

	VariableDecl( ) { }
	VariableDecl (Variable var, Type type) {
		v = var;
		t = type;
	}
	VariableDecl (String id, Type type) {
		v = new Variable(id); t = type;
	}

	public void display () {
		System.out.print("<" + v + ", " + t.getId() + ">");
	}
}

class ArrayDecl extends Declaration {
	// ArrayDecl = Variable v; Type t; Integer size
	//    Variable v;
	//    Type t;
	IntValue s;

	ArrayDecl( ) { }
	ArrayDecl (Variable var, Type type, IntValue ival) {
		v = var;
		t = type;
		s = ival;
	}
	ArrayDecl (String id, Type type, IntValue ival) {
		v = new Variable(id); t = type; s = ival;
	}

	public void display () {
		System.out.print("<" + v + "[" + s.intValue() + "]" + ", " + t.getId() + ">");
	}
}

class Type {
	// Type = int | bool | char | float
	final static Type INT = new Type("int");
	final static Type BOOL = new Type("bool");
	final static Type FLOAT = new Type("float");
	final static Type CHAR = new Type("char");
	final static Type VOID = new Type("void");
	final static Type UNDEFINED = new Type("undefined");
	final static Type UNUSED = new Type("unused");

	protected String id;
	protected Type (String t) { id = t; }
	public String getId ( ) { return id; }
}

interface Statement {
	// Statement = Skip | Block | Assignment | Conditional | Loop
	void display (int level);
}

class Skip implements Statement {
	public void display (int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": ");
	}
}

class Block implements Statement {
	// Block = Statement*
	//         (a Vector of members)
	public ArrayList<Statement> members = new ArrayList<Statement>();

	public void display(int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": ");

		for (Statement s : members)
			s.display(level+1);
	}
}
//
//class Assignment extends Statement {
//    // Assignment = Variable target; Expression source
//    Variable target;
//    Expression source;
//
//    Assignment (Variable t, Expression e) {
//        target = t;
//        source = e;
//    }
//
//    public void display (int level) {
//       super.display(level);
//       target.display(level+1);
//       source.display(level+1);
//    }
//}

class Assignment implements Statement {
	// Assignment = VariableRef target; Expression source
	VariableRef target;
	Expression source;

	Assignment (VariableRef t, Expression e) {
		target = t;
		source = e;
	}

	public void display (int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": ");
		target.display(level+1);
		source.display(level+1);
	}
}

class Conditional implements Statement {
	// Conditional = Expression test; Statement thenbranch, elsebranch
	Expression test;
	Statement thenbranch, elsebranch;
	// elsebranch == null means "if... then"

	Conditional (Expression t, Statement tp) {
		test = t; thenbranch = tp; elsebranch = new Skip( );
	}

	Conditional (Expression t, Statement tp, Statement ep) {
		test = t; thenbranch = tp; elsebranch = ep;
	}

	public void display (int level) {
		Indenter indent = new Indenter(level);
        indent.display(getClass().toString().substring(12) + ": ");
        
		test.display(level+1);
		thenbranch.display(level+1);
		assert elsebranch != null : "else branch cannot be null";
		elsebranch.display(level+1);
	}
}

class Loop implements Statement {
	// Loop = Expression test; Statement body
	Expression test;
	Statement body;

	Loop (Expression t, Statement b) {
		test = t; body = b;
	}

	public void display (int level) {
		Indenter indent = new Indenter(level);
        indent.display(getClass().toString().substring(12) + ": ");
        
		test.display(level+1);
		body.display(level+1);
	}
}
//
//abstract class Expression {
//    // Expression = Variable | Value | Binary | Unary
//
//    public void display (int level) {
//         Indenter indent = new Indenter(level);
//         indent.display(getClass().toString().substring(12) + ": ");
//    }
//}

interface Expression {
	// Expression = VariableRef | Value | Binary | Unary

	void display (int level);
}

abstract class VariableRef implements Expression {
	// VariableRef = Variable | ArrayRef
	String id;

	public String id( )  { return id; }

	public void display (int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": ");
	}
}

//class Variable extends Expression {
class Variable extends VariableRef {
	// Variable = String id
	//    private String id;

	Variable (String s) { id = s; }
	//
	//    public String id( )  { return id; }

	public String toString( ) { return id; }

	public boolean equals (Object obj) {
		String s = ((Variable) obj).id;
		return id.equals(s); // case-sensitive identifiers
	}

	public int hashCode ( ) { return id.hashCode( ); }

	public void display (int level) {
		super.display(level);
		System.out.print(id);
	}
}

class ArrayRef extends VariableRef {
	// ArrayRef = String id; Expression index
	//    private String id;
	public Expression index;

	ArrayRef (String s, Expression e) { id = s; index = e; }
	//
	//    public String id( )  { return id; }

	public String toString( ) { return id + "[" + index.toString() + "]"; }

	public boolean equals (Object obj) {
		String s = ((ArrayRef) obj).id;
		return id.equals(s); // case-sensitive identifiers
	}

	public int hashCode ( ) { return id.hashCode( ); }

	public void display (int level) {
		super.display(level);
		System.out.print(id + "[" + index.toString() + "]");
	}
}

abstract class Value implements Expression {
	// Value = IntValue | BoolValue | CharValue | FloatValue
	protected Type type;
	protected boolean undef = true;

	int intValue ( ) {
		assert false : "should never reach here";
	return 0;
	}

	boolean boolValue ( ) {
		assert false : "should never reach here";
	return false;
	}

	char charValue ( ) {
		assert false : "should never reach here";
	return ' ';
	}

	float floatValue ( ) {
		assert false : "should never reach here";
	return 0.0f;
	}

	boolean isUndef( ) { return undef; }

	Type type ( ) { return type; }

	public void display(int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": ");
	}
	
	static Value mkValue (Type type) {
		if (type == Type.INT) return new IntValue( );
		if (type == Type.BOOL) return new BoolValue( );
		if (type == Type.CHAR) return new CharValue( );
		if (type == Type.FLOAT) return new FloatValue( );
		throw new IllegalArgumentException("Illegal type in mkValue");
	}
}

class IntValue extends Value {
	private int value = 0;

	IntValue ( ) { type = Type.INT; }

	IntValue (int v) { this( ); value = v; undef = false; }

	int intValue ( ) {
		assert !undef : "reference to undefined int value";
	return value;
	}

	public String toString( ) {
		if (undef)  return "undef";
		return "" + value;
	}

	public void display (int level) {
		super.display(level);
		System.out.print(value);
	}
}

class BoolValue extends Value {
	private boolean value = false;

	BoolValue ( ) { type = Type.BOOL; }

	BoolValue (boolean v) { this( ); value = v; undef = false; }

	boolean boolValue ( ) {
		assert !undef : "reference to undefined bool value";
	return value;
	}

	int intValue ( ) {
		assert !undef : "reference to undefined bool value";
	return value ? 1 : 0;
	}

	public String toString( ) {
		if (undef)  return "undef";
		return "" + value;
	}

	public void display (int level) {
		super.display(level);
		System.out.print(value);
	}
}

class CharValue extends Value {
	private char value = ' ';

	CharValue ( ) { type = Type.CHAR; }

	CharValue (char v) { this( ); value = v; undef = false; }

	char charValue ( ) {
		assert !undef : "reference to undefined char value";
	return value;
	}

	public String toString( ) {
		if (undef)  return "undef";
		return "" + value;
	}

	public void display (int level) {
		super.display(level);
		System.out.print(value);
	}
}

class FloatValue extends Value {
	private float value = 0;

	FloatValue ( ) { type = Type.FLOAT; }

	FloatValue (float v) { this( ); value = v; undef = false; }

	float floatValue ( ) {
		assert !undef : "reference to undefined float value";
	return value;
	}

	public String toString( ) {
		if (undef)  return "undef";
		return "" + value;
	}

	public void display (int level) {
		super.display(level);
		System.out.print(value);
	}
}

class Binary implements Expression {
	// Binary = Operator op; Expression term1, term2
	Operator op;
	Expression term1, term2;

	Binary (Operator o, Expression l, Expression r) {
		op = o; term1 = l; term2 = r;
	} // binary

	public void display (int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": ");
		
		op.display(level+1);
		term1.display(level+1);
		term2.display(level+1);
	}
}

class Unary implements Expression {
	// Unary = Operator op; Expression term
	Operator op;
	Expression term;

	Unary (Operator o, Expression e) {
		op = o.val.equals("-") ? new Operator("neg"): o;
		term = e;
	} // unary

	public void display (int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": ");
		
		op.display(level+1);
		term.display(level+1);
	}
}

class Operator {
	// Operator = BooleanOp | RelationalOp | ArithmeticOp | UnaryOp
	// BooleanOp = && | ||
	final static String AND = "&&";
	final static String OR = "||";
	// RelationalOp = < | <= | == | != | >= | >
	final static String LT = "<";
	final static String LE = "<=";
	final static String EQ = "==";
	final static String NE = "!=";
	final static String GT = ">";
	final static String GE = ">=";
	// ArithmeticOp = + | - | * | /
	final static String PLUS = "+";
	final static String MINUS = "-";
	final static String TIMES = "*";
	final static String DIV = "/";
	// UnaryOp = !
	final static String NOT = "!";
	final static String NEG = "neg";
	// CastOp = int | float | char
	final static String INT = "int";
	final static String FLOAT = "float";
	final static String CHAR = "char";
	// Typed Operators
	// RelationalOp = < | <= | == | != | >= | >
	final static String INT_LT = "INT<";
	final static String INT_LE = "INT<=";
	final static String INT_EQ = "INT==";
	final static String INT_NE = "INT!=";
	final static String INT_GT = "INT>";
	final static String INT_GE = "INT>=";
	// ArithmeticOp = + | - | * | /
	final static String INT_PLUS = "INT+";
	final static String INT_MINUS = "INT-";
	final static String INT_TIMES = "INT*";
	final static String INT_DIV = "INT/";
	// UnaryOp = !
	final static String INT_NEG = "INTNEG";
	// RelationalOp = < | <= | == | != | >= | >
	final static String FLOAT_LT = "FLOAT<";
	final static String FLOAT_LE = "FLOAT<=";
	final static String FLOAT_EQ = "FLOAT==";
	final static String FLOAT_NE = "FLOAT!=";
	final static String FLOAT_GT = "FLOAT>";
	final static String FLOAT_GE = "FLOAT>=";
	// ArithmeticOp = + | - | * | /
	final static String FLOAT_PLUS = "FLOAT+";
	final static String FLOAT_MINUS = "FLOAT-";
	final static String FLOAT_TIMES = "FLOAT*";
	final static String FLOAT_DIV = "FLOAT/";
	// UnaryOp = !
	final static String FLOAT_NEG = "FLOATNEG";
	// RelationalOp = < | <= | == | != | >= | >
	final static String CHAR_LT = "CHAR<";
	final static String CHAR_LE = "CHAR<=";
	final static String CHAR_EQ = "CHAR==";
	final static String CHAR_NE = "CHAR!=";
	final static String CHAR_GT = "CHAR>";
	final static String CHAR_GE = "CHAR>=";
	// RelationalOp = < | <= | == | != | >= | >
	final static String BOOL_LT = "BOOL<";
	final static String BOOL_LE = "BOOL<=";
	final static String BOOL_EQ = "BOOL==";
	final static String BOOL_NE = "BOOL!=";
	final static String BOOL_GT = "BOOL>";
	final static String BOOL_GE = "BOOL>=";
	// Type specific cast
	final static String I2F = "I2F";
	final static String F2I = "F2I";
	final static String C2I = "C2I";
	final static String I2C = "I2C";

	String val;

	Operator (String s) { val = s; }

	public String toString( ) { return val; }
	public boolean equals(Object obj) { return val.equals(obj); }

	boolean BooleanOp ( ) { return val.equals(AND) || val.equals(OR); }
	boolean RelationalOp ( ) {
		return val.equals(LT) || val.equals(LE) || val.equals(EQ)
				|| val.equals(NE) || val.equals(GT) || val.equals(GE);
	}
	boolean ArithmeticOp ( ) {
		return val.equals(PLUS) || val.equals(MINUS)
				|| val.equals(TIMES) || val.equals(DIV);
	}
	boolean NotOp ( ) { return val.equals(NOT) ; }
	boolean NegateOp ( ) { return val.equals(NEG) ; }
	boolean intOp ( ) { return val.equals(INT); }
	boolean floatOp ( ) { return val.equals(FLOAT); }
	boolean charOp ( ) { return val.equals(CHAR); }

	final static String intMap[ ] [ ] = {
		{PLUS, INT_PLUS}, {MINUS, INT_MINUS},
		{TIMES, INT_TIMES}, {DIV, INT_DIV},
		{EQ, INT_EQ}, {NE, INT_NE}, {LT, INT_LT},
		{LE, INT_LE}, {GT, INT_GT}, {GE, INT_GE},
		{NEG, INT_NEG}, {FLOAT, I2F}, {CHAR, I2C}
	};

	final static String floatMap[ ] [ ] = {
		{PLUS, FLOAT_PLUS}, {MINUS, FLOAT_MINUS},
		{TIMES, FLOAT_TIMES}, {DIV, FLOAT_DIV},
		{EQ, FLOAT_EQ}, {NE, FLOAT_NE}, {LT, FLOAT_LT},
		{LE, FLOAT_LE}, {GT, FLOAT_GT}, {GE, FLOAT_GE},
		{NEG, FLOAT_NEG}, {INT, F2I}
	};

	final static String charMap[ ] [ ] = {
		{EQ, CHAR_EQ}, {NE, CHAR_NE}, {LT, CHAR_LT},
		{LE, CHAR_LE}, {GT, CHAR_GT}, {GE, CHAR_GE},
		{INT, C2I}
	};

	final static String boolMap[ ] [ ] = {
		{AND, AND}, {OR, OR},
		{EQ, BOOL_EQ}, {NE, BOOL_NE}, {LT, BOOL_LT},
		{LE, BOOL_LE}, {GT, BOOL_GT}, {GE, BOOL_GE}
	};

	final static private Operator map (String[][] tmap, String op) {
		for (int i = 0; i < tmap.length; i++)
			if (tmap[i][0].equals(op))
				return new Operator(tmap[i][1]);
		assert false : "should never reach here";
		return null;
	}

	final static public Operator intMap (String op) {
		return map (intMap, op);
	}

	final static public Operator floatMap (String op) {
		return map (floatMap, op);
	}

	final static public Operator charMap (String op) {
		return map (charMap, op);
	}

	final static public Operator boolMap (String op) {
		return map (boolMap, op);
	}

	public void display (int level) {
		Indenter indent = new Indenter(level);
		indent.display(getClass().toString().substring(12) + ": " + val);
	}
}