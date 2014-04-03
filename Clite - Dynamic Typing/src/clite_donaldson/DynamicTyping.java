/*
 * DynamicTyping.java implements the semantics for CliteD - the dynamically
 * typed version of Clite. It accepts the abstract syntax tree (produced by
 * the parser) as input and checks whether it is semantically correct with
 * appropriate M functions.
 */

package clite_donaldson;

import java.util.ArrayList;

public class DynamicTyping {
	public State M(Program p) {
		return M(p.body, new State());
	}

	State M (Statement s, State state) {
		System.out.println();
		if (s instanceof Skip) 
			return M((Skip)s, state);

		if (s instanceof Assignment)  {
			Assignment a = (Assignment) s;

			if (a.target instanceof ArrayRef) {
				if (state.containsKey(a.target)) {
					ArrayList<Value> value = state.get(a.target);
					
					if ((M(((ArrayRef)a.target).index, state).intValue()) >= value.size()) {
						value.add(M(a.source, state));
					} else {
						value.set((M(((ArrayRef)a.target).index, state)).intValue(), M(a.source, state));
					}
					
					return state.update(a.target, value);
				} else {
					ArrayList<Value> value = new ArrayList<Value>();
					value.add(M(a.source, state));
					state.put(new ArrayRef(a.target.id, ((ArrayRef)a.target).index), value);
				}
			} else if (a.target instanceof Variable) {
				if (state.containsKey(a.target)) { 
					ArrayList<Value> value = state.get(a.target);
					value.set(0, M(a.source, state));
					return state.update(a.target, value);
				} else {
					ArrayList<Value> value = new ArrayList<Value>();
					value.add(M(a.source, state));
					state.put(a.target, value);
				}
			}

			return M(a, state);
		}

		if (s instanceof Conditional)  
			return M((Conditional)s, state);

		if (s instanceof Loop)  
			return M((Loop)s, state);

		if (s instanceof Block) 
			return M((Block)s, state);
		throw new IllegalArgumentException("should never reach here");
	}

	State M (Skip s, State state) {
		return state;
	}

	State M (Assignment a, State state) {
		if (a.target instanceof Variable) {
			return state.update(a.target, M (a.source, state));	
		} else if (a.target instanceof ArrayRef) {
			System.out.println(a.target + " " + ((IntValue)M(((ArrayRef)a.target).index, state)).intValue() + " " +  M(a.source, state));
			return state.update(a.target, ((IntValue)M(((ArrayRef)a.target).index, state)).intValue(), M(a.source, state));
		} else
			return state;
	}

	State M (Block b, State state) {
		for (Statement s : b.members)
			state = M (s, state);
		return state;
	}

	State M (Conditional c, State state) {
		if (M(c.test, state).boolValue( ))
			return M (c.thenbranch, state);
		else
			return M (c.elsebranch, state);
	}

	State M (Loop l, State state) {
		if (M (l.test, state).boolValue( ))
			return M(l, M (l.body, state));
		else 
			return state;
	}

	Value M (Expression e, State state) {
		if (e instanceof Value)
			return (Value)e;
		if (e instanceof Variable) {
			Semantics.check(state.containsKey(e), "reference to undefined variable");
			return (Value)(state.get(e).get(0));
		} if (e instanceof ArrayRef) {
			//            return (Value)(state.get(e));
			int id = ((IntValue)M(((ArrayRef)e).index, state)).intValue();
			return (Value)(state.get(e).get(id));
		}
		if (e instanceof Binary) {
			Binary b = (Binary)e;
			//System.out.println(b.term1 + " " + (M(b.term1, state)));
			return applyBinary (b.op, M(b.term1, state), M(b.term2, state));
		}
		if (e instanceof Unary) {
			Unary u = (Unary)e;
			return applyUnary(u.op, M(u.term, state));
		}
		throw new IllegalArgumentException("should never reach here");
	}

	Value applyBinary (Operator op, Value v1, Value v2) {
		Semantics.check(v1.type() == v2.type(), 
				"mismatched types");

		if (op.ArithmeticOp()) {
			if (v1.type() == Type.INT){ 
				//System.out.println(op + " " + v1.intValue() + " " + v2.intValue());
				if (op.val.equals(Operator.PLUS)) {
					return new IntValue(v1.intValue( ) + v2.intValue( ));
				} if (op.val.equals(Operator.MINUS))
					return new IntValue(v1.intValue( ) - v2.intValue( ));
				if (op.val.equals(Operator.TIMES)) {
					return new IntValue(v1.intValue( ) * v2.intValue( ));
				} if (op.val.equals(Operator.DIV))
					return new IntValue(v1.intValue( ) / v2.intValue( ));


			} else if (v1.type() == Type.FLOAT) {
				if (op.val.equals(Operator.PLUS))
					return new FloatValue(v1.floatValue( ) + v2.floatValue( ));
				if (op.val.equals(Operator.MINUS))
					return new FloatValue(v1.floatValue( ) - v2.floatValue( ));
				if (op.val.equals(Operator.TIMES))
					return new FloatValue(v1.floatValue( ) * v2.floatValue( ));
				if (op.val.equals(Operator.DIV))
					return new FloatValue(v1.floatValue( ) / v2.floatValue( ));
			}
		}

		if (op.RelationalOp()) {
			if (v1.type() == Type.INT && v2.type() == Type.INT) {
				if (op.val.equals(Operator.LT)) {
					return new BoolValue(v1.intValue( ) < v2.intValue( )); }
				if (op.val.equals(Operator.LE))
					return new BoolValue(v1.intValue( ) <= v2.intValue( ));
				if (op.val.equals(Operator.EQ))
					return new BoolValue(v1.intValue( ) == v2.intValue( ));
				if (op.val.equals(Operator.NE))
					return new BoolValue(v1.intValue( ) != v2.intValue( ));
				if (op.val.equals(Operator.GE))
					return new BoolValue(v1.intValue( ) >= v2.intValue( ));
				if (op.val.equals(Operator.GT))
					return new BoolValue(v1.intValue( ) > v2.intValue( ));

			} else if (v1.type() == Type.FLOAT && v2.type == Type.FLOAT) {
				if (op.val.equals(Operator.LT)) 
					return new BoolValue(v1.floatValue( ) < v2.floatValue( ));
				if (op.val.equals(Operator.LE))
					return new BoolValue(v1.floatValue( ) <= v2.floatValue( ));
				if (op.val.equals(Operator.EQ))
					return new BoolValue(v1.floatValue( ) == v2.floatValue( ));
				if (op.val.equals(Operator.NE))
					return new BoolValue(v1.floatValue( ) != v2.floatValue( ));
				if (op.val.equals(Operator.GE))
					return new BoolValue(v1.floatValue( ) >= v2.floatValue( ));
				if (op.val.equals(Operator.GT))
					return new BoolValue(v1.floatValue( ) > v2.floatValue( ));

			} else if (v1.type() == Type.BOOL) { 
				if (op.val.equals(Operator.EQ))
					return new BoolValue(v1.boolValue( ) == v2.boolValue( ));
				if (op.val.equals(Operator.NE))
					return new BoolValue(v1.boolValue( ) != v2.boolValue( ));

			} else if (v1.type() == Type.CHAR) {
				if (op.val.equals(Operator.LT))
					return new BoolValue(v1.charValue( ) < v2.charValue( ));
				if (op.val.equals(Operator.LE))
					return new BoolValue(v1.charValue( ) <= v2.charValue( ));
				if (op.val.equals(Operator.EQ))
					return new BoolValue(v1.charValue( ) == v2.charValue( ));
				if (op.val.equals(Operator.NE))
					return new BoolValue(v1.charValue( ) != v2.charValue( ));
				if (op.val.equals(Operator.GE))
					return new BoolValue(v1.charValue( ) >= v2.charValue( ));
				if (op.val.equals(Operator.GT))
					return new BoolValue(v1.charValue( ) > v2.charValue( ));
			}
		}

		if (op.BooleanOp()) {
			if (op.val.equals(Operator.AND))
				return new BoolValue(v1.boolValue( ) && v2.boolValue( ));
			if (op.val.equals(Operator.OR))
				return new BoolValue(v1.boolValue( ) || v2.boolValue( ));
		}

		throw new IllegalArgumentException("should never reach here");
	}

	Value applyUnary (Operator op, Value v) {
		Semantics.check( ! v.isUndef( ),
				"reference to undef value");
		if (op.val.equals(Operator.NOT))
			return new BoolValue(!v.boolValue( ));

		if (v.type == Type.INT) {
			if (op.val.equals(Operator.INT_NEG))
				return new IntValue(-v.intValue( ));
		}

		if (v.type == Type.FLOAT) { 
			if (op.val.equals(Operator.FLOAT_NEG))
				return new FloatValue(-v.floatValue( ));
		}

		if (op.val.equals(Operator.INT)) {
			if (v.type == Type.INT) {
				return v;
			}

			if (v.type == Type.CHAR) {
				return new IntValue((int)v.charValue());
			}
			if (v.type == Type.FLOAT) { 
				return new IntValue((int)v.floatValue());
			}
		}

		if (op.val.equals(Operator.CHAR)) {
			if (v.type == Type.CHAR) {
				return v;
			}

			if (v.type == Type.INT) {
				if (v.intValue() >= 0 || v.intValue() <= 255) {
					return new CharValue((char)v.intValue());
				}
			}
		}

		if (op.val.equals(Operator.FLOAT)) { 
			if (v.type == Type.FLOAT) {
				return v;
			}

			if (v.type == Type.INT) {
				return new FloatValue((float) v.intValue());
			}
		}
		throw new IllegalArgumentException("should never reach here: "
				+ op.toString());
	}
}