/*
 * StaticTypeCheck.java implements the Clite type system for detecting 
 * type errors in Clite programs at compile-time. It is defined by
 * the functions V and the auxiliary functions typing and typeOf.
 * These functions use classes in the abstract syntax of Clite.
 */

package clite_donaldson;


public class StaticTypeCheck {
	public static void V(Program p) {
		V(p.decpart);
		V(p.body, typing(p.decpart));
		System.out.println("\nFinished Type Checking...");
	}

	public static void V(Declarations d) {
		for (int a = 0; a < d.size() - 1; a++) {
			for (int b = a+1; b < d.size(); b++) {
				Declaration first = d.get(a);
				Declaration second = d.get(b);
				check(!(first.v.equals(second.v)), "Duplicate declaration: " + first.v);				
			}
		}
	}

	public static void V(Statement s, TypeMap map) {
		if (s == null) {
			throw new IllegalArgumentException("AST error: null statement");
		}

		if (s instanceof Skip) {
			return;
		} 

		if (s instanceof Block) {
			Block b = (Block) s;

			for (int a = 0; a < b.members.size(); a++) {
				V(b.members.get(a), map);
			}

			return;
		} 

		if (s instanceof Assignment) {
			Assignment a = (Assignment) s;
			VariableRef target;
			
			if (a.target instanceof ArrayRef) {
				target = (ArrayRef) a.target;
			} else {
				target = (Variable) a.target;
			}

			check(map.containsKey(target), " undefined target in assignment: " + a.target);
			V(a.source, map);

			Type tarType = (Type) map.get(target);
			Type srcType = typeOf(a.source, map);

			if (tarType != srcType) {
				if (tarType == Type.FLOAT){ 
					check(srcType == Type.INT, " mixed mode assignment to " + target);
				} else if (tarType == Type.INT) { 
					check(srcType == Type.CHAR, " mixed mode assignment to " + target);
				} else {
					check(false, " mixed mode assignment to " + target);
				}
			}

			return;
		} 

		if (s instanceof Conditional) {
			Conditional c = (Conditional) s;

			V(c.test, map); // Check that the expression is valid
			V(c.thenbranch, map); // Check that the thenbranch is valid
			V(c.elsebranch, map); // Check that the elsebranch is valid

			return;
		} 

		if (s instanceof Loop) {
			Loop l = (Loop) s;
			V(l.test, map);
			V(l.body, map);

			return;
		} 

		throw new IllegalArgumentException("should never reach here");
	}

	public static void V(Expression exp, TypeMap map) {
		// Value | Variable | Binary | Unary
		if (exp instanceof Value) {
			return;
		}

		if (exp instanceof Variable) {
			Variable v = (Variable) exp;
			check(map.containsKey(v), " undeclared variable: " + v);
			return;
		}

		if (exp instanceof ArrayRef) {
			ArrayRef ar = (ArrayRef) exp;
			Variable v = new Variable(ar.id);
			check(map.containsKey(ar), " undeclared variable: " + ar);
			Type type = typeOf(ar.getIndex(), map);			
			check(type == Type.INT, "Non-integer for index into array: " + ar);
			return;
		}

		if (exp instanceof Binary) {
			Binary b = (Binary) exp;
			Type type1 = typeOf(b.term1, map);
			Type type2 = typeOf(b.term2, map);

			V(b.term1, map);
			V(b.term2, map);

			if (b.op.ArithmeticOp()) {
				check(type1 == type2 && (type1 == Type.INT || type1 == Type.FLOAT), "Type error: " + b.op);
			} else if (b.op.RelationalOp()) {
				check(type1 == type2, "type error for " + b.op);
			} else if (b.op.BooleanOp()) {
				check(type1 == Type.BOOL && type2 == Type.BOOL, b.op + ": non-bool operand");
			} else {
				throw new IllegalArgumentException("should never reach here");
			}

			return;
		} 

		if (exp instanceof Unary) {
			Unary u = (Unary) exp;
			Type type = typeOf(u.term, map);
			V(u.term, map);

			if (u.op.NotOp()) {
				check(type == Type.BOOL, "Type error: " + u.op);
			} else if (u.op.NegateOp()) {
				check(type == Type.INT || type == Type.FLOAT, "Type error: " + u.op);
			} else if (u.op.floatOp() || u.op.charOp()) {
				check(type == Type.INT, "Type error: " + u.op);
			} else if (u.op.intOp()) { 
				check(type == Type.CHAR || type == Type.FLOAT, "Type error: " + u.op);
			} else {	
				throw new IllegalArgumentException("should never reach here");
			}

			return;
		}

		throw new IllegalArgumentException("should never reach here");
	}

	public static Type typeOf(Expression e, TypeMap map) {
		// Value | Variable | ArrayRef | Binary | Unary

		if (e instanceof Value) {
			return ((Value) e).type;
		} 

		if (e instanceof Variable) {
			Variable v = (Variable) e;
			check(map.containsKey(v), "Undefined variable: " + v);
			return (Type) map.get(v);
		}

		if (e instanceof ArrayRef) {
			ArrayRef ar = (ArrayRef) e;
			Variable v = new Variable(ar.id);
			check(map.containsKey(ar), "Undefined array: " + ar);
			return (Type) map.get(ar);
		}

		if (e instanceof Binary) {
			Binary b = (Binary) e;
			if (b.op.ArithmeticOp()) {
				if (typeOf(b.term1, map) == Type.FLOAT){ 
					return Type.FLOAT;
				} else {
					return Type.INT;
				}
			}

			if (b.op.RelationalOp() || b.op.BooleanOp()) {
				return Type.BOOL;
			}
		}

		if (e instanceof Unary) {
			Unary u = (Unary) e;

			if (u.op.NotOp()) {
				if (typeOf(u.term, map) == Type.BOOL){ 
					return Type.BOOL;
				} else {
					check(false, "Type error: " + u);
				}
			}

			if (u.op.NegateOp()) {
				return typeOf(u.term, map);
			} 

			if (u.op.charOp()) {
				return Type.CHAR;
			}

			if (u.op.intOp()) {
				return Type.INT;
			}

			if (u.op.floatOp()) {
				return Type.FLOAT;
			}
		}

		throw new IllegalArgumentException("should never reach here");
	}

	public static TypeMap typing(Declarations decpart) {
		TypeMap map = new TypeMap();
		for (Declaration d : decpart) {	
			if (d instanceof ArrayDecl) {
				ArrayRef ar = new ArrayRef(d.v.id, ((ArrayDecl) d).s);
				map.put(ar, d.t);
			} else {
				map.put(d.v, d.t);
			}
		}

		return map;
	}

	public static void check(boolean test, String msg) {
		if (test)
			return;
		System.err.println(msg);
		System.err.println("Java Result: 1");
		System.exit(1);
	}
} // class StaticTypeCheck
