/*
 * TypeTransformer.java implements the Clite type system that allows
 * implicit type conversion. It transforms the parser generated 
 * abstract syntax tree by adding explicit type conversions as needed.
 * It is defined by the functions T, which use classes in the abstract
 * syntax of Clite and return the transformed syntax tree under that node.
 */

package clite_donaldson;

public class TypeTransformer {
	public static Program T(Program p, TypeMap map) {
		Block body = (Block) T(p.body, map);
		return new Program(p.decpart, body);
	}

	/*
	 * Value, Variable, ArrayRef, Binary, Unary
	 */
	public static Expression T(Expression e, TypeMap map) {
		if (e instanceof Value) {
			return e;
		}

		if (e instanceof Variable) {
			return e;
		}

		if (e instanceof ArrayRef) {
			return e;
		}

		if (e instanceof Binary) {
			Binary b = (Binary) e;

			Type type1 = StaticTypeCheck.typeOf(b.term1, map);
			Type type2 = StaticTypeCheck.typeOf(b.term2, map);

			Expression left = T(b.term1, map);
			Expression right = T(b.term2, map);
		
			if (type1 == Type.FLOAT || type2 == Type.FLOAT){ 
				if (type1 == Type.INT) {
					return new Binary(b.op.floatMap(b.op.val), new Unary(new Operator(Operator.I2F), left), right);
				} else if (type2 == Type.INT) { 
					return new Binary(b.op.floatMap(b.op.val), left, new Unary(new Operator(Operator.I2F), right));
				} else {
					return new Binary(b.op.floatMap(b.op.val), left, right);
				}
			} else if (type1 == Type.INT || type2 == Type.INT) { 
				if (type1 == Type.CHAR) {
					return new Binary(b.op.intMap(b.op.val), new Unary(new Operator(Operator.C2I), left), right);
				} else if (type2 == Type.CHAR) {
					return new Binary(b.op.intMap(b.op.val), left, new Unary(new Operator(Operator.C2I), right));
				} else {
					return new Binary(b.op.intMap(b.op.val), left, right);
				}
			} else if (type1 == Type.CHAR || type2 == Type.CHAR) {
				return new Binary(b.op.charMap(b.op.val), left, right);
			} else if (type1 == Type.BOOL || type2 == Type.BOOL) {
				return new Binary(b.op.boolMap(b.op.val), left, right);
			}
		}

		if (e instanceof Unary) {
			Unary u = (Unary) e;
			Expression term = T(u.term, map);
			Type type = StaticTypeCheck.typeOf(u.term, map);

			if (type == Type.INT) {
				return new Unary(u.op.intMap(u.op.val), term);
			} else if (type == Type.FLOAT) { 
				return new Unary(u.op.floatMap(u.op.val), term);	
			} else if (type == Type.CHAR) { 
				return new Unary(u.op.charMap(u.op.val), term);	
			} else if (type == Type.BOOL) {
				return new Unary(u.op.boolMap(u.op.val), term);	
			}
		}

		throw new IllegalArgumentException("should never reach here");
	}

	/*
	 * null, Skip, Assignment, Conditional, Loop, Block
	 */
	public static Statement T(Statement s, TypeMap map) {		
		if (s instanceof Skip) {
			return s;
		}

		if (s instanceof Assignment) {
			Assignment a = (Assignment) s;
			VariableRef target = (VariableRef) a.target;
			Expression src = T(a.source, map);

			Type tarType = (Type) map.get(a.target);
			Type srcType = StaticTypeCheck.typeOf(a.source, map);

			if (tarType == Type.FLOAT) { 
				if (srcType == Type.INT){ 
					src = new Unary(new Operator(Operator.I2F), src);
					srcType = Type.FLOAT;
				}
			} else if (tarType == Type.INT) {
				if (srcType == Type.CHAR) { 
					src = new Unary(new Operator(Operator.C2I), src);
					srcType = Type.INT;
				} else if (srcType == Type.FLOAT) {
					src = new Unary(new Operator(Operator.F2I), src);
					srcType = Type.INT;
				}
			} 
			
			StaticTypeCheck.check(tarType == srcType,  "type error in assignment to " + target);
			return new Assignment(target, src);
		}

		if (s instanceof Conditional) {
			Conditional c = (Conditional) s;

			Expression test = T(c.test, map);
			
			Statement thenbranch = T(c.thenbranch, map);
			Statement elsebranch = T(c.elsebranch, map);

			return new Conditional(test, thenbranch, elsebranch);
		}

		if (s instanceof Loop) {
			Loop l = (Loop) s;
		
			Expression test = T(l.test, map);
			Statement body = T(l.body, map);

			return new Loop(test, body);
		}

		if (s instanceof Block) {	
			Block b = (Block) s;

			Block newBlock = new Block();

			for (Statement st : b.members) {
				newBlock.members.add(T(st, map));
			}

			return newBlock;
		}

		throw new IllegalArgumentException("should never reach here");
	}
	
} // class TypeTransformer
