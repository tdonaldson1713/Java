/*
 * TypeTransformer.java implements the Clite type system that allows
 * implicit type conversion. It transforms the parser generated 
 * abstract syntax tree by adding explicit type conversions as needed.
 * It is defined by the functions T, which use classes in the abstract
 * syntax of Clite and return the transformed syntax tree under that node.
 */

package clite_donaldson;

public class TypeTransformer {

    public static Program T (Program p, TypeMap tm) {
        Block body = (Block)T(p.body, tm);
        return new Program(p.decpart, body);
    }

    public static Expression T (Expression e, TypeMap tm) {
        if (e instanceof Value)
            return e;
        if (e instanceof ArrayRef) {
        	ArrayRef a = (ArrayRef) e;
        	return new ArrayRef(a.id, T(a.index, tm));
        }
        if (e instanceof VariableRef)
            return e;
        if (e instanceof Binary) {
            Binary b = (Binary)e;
            Type typ1 = StaticTypeCheck.typeOf(b.term1, tm);
            Type typ2 = StaticTypeCheck.typeOf(b.term2, tm);
            Expression t1 = T (b.term1, tm);
            Expression t2 = T (b.term2, tm);
//            if (typ1 == Type.INT)
//                return new Binary(b.op.intMap(b.op.val), t1,t2);
//            else if (typ1 == Type.FLOAT)
//                return new Binary(b.op.floatMap(b.op.val), t1,t2);
//            else if (typ1 == Type.CHAR)
//                return new Binary(b.op.charMap(b.op.val), t1,t2);
//            else if (typ1 == Type.BOOL)
//                return new Binary(b.op.boolMap(b.op.val), t1,t2);
            if (typ1 == Type.FLOAT || typ2 == Type.FLOAT)
                if (typ1 == Type.INT)
                    return new Binary(b.op.floatMap(b.op.val), new Unary(new Operator(Operator.I2F), t1), t2);
                else if (typ2 == Type.INT)
                    return new Binary(b.op.floatMap(b.op.val), t1, new Unary(new Operator(Operator.I2F), t2));
                else   
                    return new Binary(b.op.floatMap(b.op.val), t1,t2);
            else if (typ1 == Type.INT || typ2 == Type.INT)
                if (typ1 == Type.CHAR)
                    return new Binary(b.op.intMap(b.op.val), new Unary(new Operator(Operator.C2I), t1), t2);
                else if (typ2 == Type.CHAR)
                    return new Binary(b.op.intMap(b.op.val), t1, new Unary(new Operator(Operator.C2I), t2));
                else   
                    return new Binary(b.op.intMap(b.op.val), t1,t2);
            else if (typ1 == Type.CHAR || typ2 == Type.CHAR)
                return new Binary(b.op.charMap(b.op.val), t1,t2);
            else if (typ1 == Type.BOOL || typ2 == Type.BOOL)
                return new Binary(b.op.boolMap(b.op.val), t1,t2);
            throw new IllegalArgumentException("should never reach here");
        }
        if (e instanceof Unary) {
            Unary u = (Unary) e;
            Type typ1 = StaticTypeCheck.typeOf(u.term, tm);
            Expression term = T(u.term, tm);
            Operator op = u.op;
            if (u.op.equals(Operator.NOT))
                ;
            else if (u.op.equals(Operator.NEG)) {
                if (typ1== Type.INT)
                    op = op.intMap(op.val);
                else if (typ1== Type.FLOAT)
                    op = op.floatMap(op.val);
            }
            else if (u.op.equals(Operator.FLOAT))
                op = op.intMap(op.val);
            else if (u.op.equals(Operator.CHAR))
                op = op.intMap(op.val);
            else if (u.op.equals(Operator.INT)) {
                if (typ1== Type.FLOAT)
                    op = op.floatMap(op.val);
                else if (typ1== Type.CHAR)
                    op = op.charMap(op.val);
            }
            else {
                throw new IllegalArgumentException("should never reach here");
            }
            return new Unary(op, term);
        }
        throw new IllegalArgumentException("should never reach here");
    }

    public static Statement T (Statement s, TypeMap tm) {
        if (s instanceof Skip) return s;
        if (s instanceof Assignment) {
            Assignment a = (Assignment)s;
            VariableRef target = a.target;
            Expression src = T (a.source, tm);
            Type ttype = (Type)tm.get(a.target);
            Type srctype = StaticTypeCheck.typeOf(a.source, tm);
            if (ttype == Type.FLOAT) {
                if (srctype == Type.INT) {
                    src = new Unary(new Operator(Operator.I2F), src);
                    srctype = Type.FLOAT;
                }
            }
            else if (ttype == Type.INT) {
                if (srctype == Type.CHAR) {
                    src = new Unary(new Operator(Operator.C2I), src);
                    srctype = Type.INT;
                }
            }
            StaticTypeCheck.check( ttype == srctype,
                      "bug in assignment to " + target);
            return new Assignment(target, src);
        }
        if (s instanceof Conditional) {
            Conditional c = (Conditional)s;
            Expression test = T (c.test, tm);
            Statement tbr = T (c.thenbranch, tm);
            Statement ebr = T (c.elsebranch, tm);
            return new Conditional(test,  tbr, ebr);
        }
        if (s instanceof Loop) {
            Loop l = (Loop)s;
            Expression test = T (l.test, tm);
            Statement body = T (l.body, tm);
            return new Loop(test, body);
        }
        if (s instanceof Block) {
            Block b = (Block)s;
            Block out = new Block();
            for (Statement stmt : b.members)
                out.members.add(T(stmt, tm));
            return out;
        }
        throw new IllegalArgumentException("should never reach here");
    }


    public static void main(String args[]) {
    	System.out.println("Begin parsing... " + args[0]);
    	Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display();           // display abstract syntax tree
        System.out.println("\nBegin type checking...");
        System.out.println("\nType map:");
        TypeMap map = StaticTypeCheck.typing(prog.decpart);
        map.display();
        StaticTypeCheck.V(prog);
        Program out = T(prog, map);
        System.out.println("\nTransformed Abstract Syntax Tree");
        out.display();
    } //main

    } // class TypeTransformer
