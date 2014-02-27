/*
 * Semantics.java implements the semantic interpreter for Clite. It is defined
 * by the functions M that use the classes in the Abstract Syntax of Clite.
 */

package clite_donaldson;

public class Semantics {
	public State M(Program p) {
		return M(p.body, initialState(p.decpart));
	}

	public State initialState(Declarations d) {
		State state = new State();
		for (Declaration decl : d) {
			if (decl instanceof VariableDecl) {
				VariableDecl vd = (VariableDecl) decl;
				state.put(vd.v, Value.mkValue(vd.t));
			} else if (decl instanceof ArrayDecl) {
				ArrayDecl ad = (ArrayDecl) decl;
				state.put(new ArrayRef(ad.v.id, ad.s), Value.mkValue(ad.t));
			}
		}
		
		return state;
	}

	public State M(Statement s, State state) {
		if (s instanceof Skip) {
			return M((Skip)s, state);
		}

		if (s instanceof Assignment) {
			return M((Assignment)s, state);
		}

		if (s instanceof Conditional) {
			return M((Conditional)s, state);
		}

		if (s instanceof Loop) {
			return M((Loop)s, state);
		}

		if (s instanceof Block) {
			return M((Block)s, state);
		}

		throw new IllegalArgumentException("Error");
	}

	public State M(Skip s, State state) {
		return state;
	}

	public State M(Assignment a, State state) {
		if (a.target instanceof ArrayRef) {
			ArrayRef ar = (ArrayRef) a.target;
			ArrayRef newAR = new ArrayRef(ar.id, M(ar.index, state));
			return state.update(newAR, M(a.source, state));
		}
		
		return state.update(a.target, M(a.source, state));
	}

	public State M(Conditional c, State state) {
		if (M(c.test, state).boolValue()) {
			return M(c.thenbranch, state);
		} else {
			return M(c.elsebranch, state);
		}
	}

	public State M(Loop l, State state) {
		if (M(l.test, state).boolValue()) {
			return M(l, M(l.body, state));
		} else {
			return state;
		}
	}

	public State M(Block b, State state) {
		for (Statement s : b.members) { 
			state = M(s, state);
		}

		return state;
	}

	public Value M(Expression e, State state) {
		if (e instanceof Value) {
			return (Value) e;
		}
		
		if (e instanceof Variable) {
			return (Value) state.get(e);
		}

		if (e instanceof ArrayRef) {
		    ArrayRef a = (ArrayRef) e;
		    ArrayRef key = new ArrayRef(a.id, M(a.index, state));
		    return (Value)(state.get(key));
		}
		
		if (e instanceof Binary) {
			Binary b = (Binary) e;
			return applyBinary(b.op, M(b.term1, state), M(b.term2, state));
		}

		if (e instanceof Unary) {
			Unary u = (Unary) e;
			return applyUnary(u.op, M(u.term, state));
		}

		throw new IllegalArgumentException("Error in Expression");
	}

	public Value applyBinary(Operator op, Value value1, Value value2) {
		StaticTypeCheck.check(!value1.isUndef() && !value2.isUndef(), 
				"reference to undefined value");

		// Integer Operations
		if (op.val.equals(Operator.INT_PLUS)) {
			return new IntValue(value1.intValue() + value2.intValue());
		} if (op.val.equals(Operator.INT_MINUS)) {
			return new IntValue(value1.intValue() - value2.intValue());
		} if (op.val.equals(Operator.INT_TIMES)) {
			return new IntValue(value1.intValue() * value2.intValue());
		} if (op.val.equals(Operator.INT_DIV)) {
			return new IntValue(value1.intValue() / value2.intValue());
		} if (op.val.equals(Operator.INT_EQ)) {
			return new BoolValue(value1.intValue() == value2.intValue());
		} if (op.val.equals(Operator.INT_LT)) {
			return new BoolValue(value1.intValue() < value2.intValue());
		} if (op.val.equals(Operator.INT_LE)) {
			return new BoolValue(value1.intValue() <= value2.intValue());
		} if (op.val.equals(Operator.INT_NE)) {
			return new BoolValue(value1.intValue() != value2.intValue());
		} if (op.val.equals(Operator.INT_GT)) {
			return new BoolValue(value1.intValue() > value2.intValue());
		} if (op.val.equals(Operator.INT_GE)) {
			return new BoolValue(value1.intValue() >= value2.intValue());
		}

		// Float Operations
		if (op.val.equals(Operator.FLOAT_PLUS)) {
			return new FloatValue(value1.floatValue() + value2.floatValue());
		} if (op.val.equals(Operator.FLOAT_MINUS)) {
			return new FloatValue(value1.floatValue() - value2.floatValue());
		} if (op.val.equals(Operator.FLOAT_TIMES)) {
			return new FloatValue(value1.floatValue() * value2.floatValue());
		} if (op.val.equals(Operator.FLOAT_DIV)) {
			return new FloatValue(value1.floatValue() / value2.floatValue());
		} if (op.val.equals(Operator.FLOAT_EQ)) {
			return new BoolValue(value1.floatValue() == value2.floatValue());
		} if (op.val.equals(Operator.FLOAT_LT)) {
			return new BoolValue(value1.floatValue() < value2.floatValue());
		} if (op.val.equals(Operator.FLOAT_LE)) {
			return new BoolValue(value1.floatValue() <= value2.floatValue());
		} if (op.val.equals(Operator.FLOAT_NE)) {
			return new BoolValue(value1.floatValue() != value2.floatValue());
		} if (op.val.equals(Operator.FLOAT_GT)) {
			return new BoolValue(value1.floatValue() > value2.floatValue());
		} if (op.val.equals(Operator.FLOAT_GE)) {
			return new BoolValue(value1.floatValue() >= value2.floatValue());
		}

		// Char Operations
		if (op.val.equals(Operator.CHAR_EQ)) {
			return new BoolValue(value1.charValue() == value2.charValue());
		} if (op.val.equals(Operator.CHAR_LT)) {
			return new BoolValue(value1.charValue() < value2.charValue());
		} if (op.val.equals(Operator.CHAR_LE)) {
			return new BoolValue(value1.charValue() <= value2.charValue());
		} if (op.val.equals(Operator.CHAR_NE)) {
			return new BoolValue(value1.charValue() != value2.charValue());
		} if (op.val.equals(Operator.CHAR_GT)) {
			return new BoolValue(value1.charValue() > value2.charValue());
		} if (op.val.equals(Operator.CHAR_GE)) {
			return new BoolValue(value1.charValue() >= value2.charValue());
		}

		// Boolean Operations
		if (op.val.equals(Operator.BOOL_EQ)) {
			return new BoolValue(value1.boolValue() == value2.boolValue());
		} if (op.val.equals(Operator.BOOL_NE)) {
			return new BoolValue(value1.boolValue() != value2.boolValue());
		}

		// BooleanOp && ||
		if (op.val.equals(Operator.AND)) { 
			return new BoolValue(value1.boolValue() && value2.boolValue());
		} if (op.val.equals(Operator.OR)) { 
			return new BoolValue(value1.boolValue() || value2.boolValue());
		}
		throw new IllegalArgumentException("Error in applyBinary");
	}

	public Value applyUnary(Operator op, Value value) {
		StaticTypeCheck.check(!value.isUndef(), "reference to undefined value");

		if (op.val.equals(Operator.NOT)) {
			return new BoolValue(!value.boolValue());
		}

		if (op.val.equals(Operator.INT_NEG)) {
			return new IntValue(-value.intValue());
		} if (op.val.equals(Operator.FLOAT_NEG)){
			return new FloatValue(-value.floatValue());
		}

		if (op.val.equals(Operator.I2F)) {
			return new FloatValue((float)value.intValue());
		} if (op.val.equals(Operator.C2I)) {
			return new IntValue((int)value.charValue());
		} if (op.val.equals(Operator.I2C)) { 
			if (value.intValue() >= 0 && value.intValue() <= 255) {
				return new CharValue((char)value.intValue());
			} else {
				throw new IllegalArgumentException("Error: Cannot convert " + value.intValue() + " to char");
			}
		} if (op.val.equals(Operator.F2I)) {
			return new IntValue((int)value.floatValue());
		}

		throw new IllegalArgumentException("Error in applyUnary");
	}

}