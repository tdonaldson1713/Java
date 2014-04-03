/*
 * Parser.java is a recursive descent parser that inputs a CLite program
 * and generates its abstract syntax.  Each method corresponds to a
 * concrete syntax grammar rule, which appears as a comment at the
 * beginning of the method.
 */

package clite;

public class Parser {

    Token token;          // current token from the input stream
	Variable cFunction;
    Lexer lexer;
    String funcId = "main";

    public Parser(Lexer ts) { // Open the Clite source program
        lexer = ts;           // as a token stream, and
        token = lexer.next(); // retrieve its first Token
    }

    private String match (TokenType t) {
        String value = token.value();
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }

    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok
                           + "; saw: " + token);
        System.exit(1);
    }

    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok
                           + "; saw: " + token);
        System.exit(1);
    }

    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
        TokenType[ ] header = {TokenType.Int, TokenType.Main,
                          TokenType.LeftParen, TokenType.RightParen};
        for (int i=0; i<header.length; i++)   // bypass "int main ( )"
            match(header[i]);
        match(TokenType.LeftBrace);
        Declarations decpart = declarations();
        Block body = statements();
        match(TokenType.RightBrace);
        return new Program(decpart, body);
    }
	
	private Program program() {
		Declarations globals = new Declarations();
		Functions functions = new Functions();
		globals.putAll(functionOrGlobal(functions));
		return new Program(globals, functions);
	}
	
    private Declarations declarations (Functions functions) {
        // Declarations --> { Declaration }
        Declarations ds = new Declarations ();
        
		while (isType()) {
            declaration(ds, functions);
        }
		
        return ds;
    }

	private void Declaration declaration(Declarations d, Functions f) {
		Type t = type();
		
		while (!token.type().equals(TokenType.EOF)) {
			Variable v = new Variable(token.value);
			match(TokenType.Identifier);
			
			// Let's check to see if we have a function by checking if 
			// the next character is a paranethesis.
			if (token.type().equals(TokenType.LeftParan)) {
				functions(f, t,	v);
				if (isType()) {
					type = type();
				} else { 
					break;
				}
			} else { // Otherwise it is a variable.
				Declaration globalDec = d.put(v.toString(), new VariableDecl(v, t));
				if (globalDec != null) {
					System.err.println("Declarations of variable already exists.");
				}
				
				if (token.type().equals(TokenType.Comma)) {
					match(TokenType.Comma);
					} else if (token.type().equals(TokenType.Semicolon)) {
						match(TokenType.Semicolon);
						if (isType()) {
							t = type();
						} else {
							break;
						}
					} else {
						error(" ; or ,");
					}
				}
			}
		}
	}
	
	private void function(Functions f, Type t, Variable v) {
		currentFunction = v;
		match(TokenType.LeftParan);
		Declarations params = parameters();
		match(TokenType.RightParan);
		match(TokenType.LeftBrace);
		Declarations locals = declarations(f);
		Block body = statements();
		match(TokenType.RightBrace);
		
		Functions newFunctions = new Function(t, v, params, locals, body);
		if (newFunctions != null) {
			System.err.println("Declarations of variable already exists.");
		}
	}
	
	private Declarations parameters() {
		Declarations params = new Declarations();
		
		// We simply need to declare a new variable for each parameter.
		// This is simply a rehash of what we do for local variables inside a function
		while (!token.type().equals(TokenType.RightParan) {
			Type t = type();
			Variable v = new Variable(token.value());
			match(TokenType.Identifier);
			params.put(v.toString(), new VariableDecl(v, t));
			
			if (token.type().equals(TokenType.Comma)) {
				match(TokenType.Comma);
			}
		}
		
		return params;
	}

    private Type type () {
        // Type  -->  int | bool | float | char
        Type t = null;
        if (token.type().equals(TokenType.Int))
            t = Type.INT;
        else if (token.type().equals(TokenType.Bool))
            t = Type.BOOL;
        else if (token.type().equals(TokenType.Float))
            t = Type.FLOAT;
        else if (token.type().equals(TokenType.Char))
            t = Type.CHAR;
        else error("int | bool | float | char");
        token = lexer.next(); // pass over the type
        return t;
    }

    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
        Statement s = new Skip();
        if (token.type().equals(TokenType.Semicolon))    // Skip
            match(TokenType.Semicolon);
        else if (token.type().equals(TokenType.LeftBrace)) { // Block
            token = lexer.next();
            s = statements();
            match(TokenType.RightBrace);
        }
        else if (token.type().equals(TokenType.If))         // IfStatement
            s = ifStatement();
        else if (token.type().equals(TokenType.While))      // WhileStatement
            s = whileStatement();
        else if (token.type().equals(TokenType.Identifier))  // Assignment
            s = assignment();
        else error("Illegal statement");
        return s;
    }

    private Block statements () {
        // Block --> '{' Statements '}'
        Block b = new Block();
        while (! token.type().equals(TokenType.RightBrace)) {
            b.members.add(statement());
        }
        return b;
    }

//    private Assignment assignment () {
//        // Assignment --> Identifier = Expression ;
//        Variable target = new Variable(match(TokenType.Identifier));
//        match(TokenType.Assign);
//        Expression source = expression();
//        match(TokenType.Semicolon);
//        return new Assignment(target, source);
//    }

    private Assignment assignment () {
        // Assignment --> Identifier [ [ Expression ] ] = Expression ;
        VariableRef target = null;
        String s = match(TokenType.Identifier);
        if (token.type().equals(TokenType.LeftBracket))
        {
            match(TokenType.LeftBracket);
            Expression index = expression();
            target = new ArrayRef(s, index);
            match(TokenType.RightBracket);
        }
        else
            target = new Variable(s);
        match(TokenType.Assign);
        Expression source = expression();
        match(TokenType.Semicolon);
        return new Assignment(target, source);
    }

    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
        match(TokenType.If);
        match(TokenType.LeftParen);
        Expression test = expression();
        match(TokenType.RightParen);
        Statement thenbranch = statement();
        Statement elsebranch = new Skip();
        if (token.type().equals(TokenType.Else)){
            match(TokenType.Else);
            elsebranch = statement();
        }
        return new Conditional(test, thenbranch, elsebranch);
    }

    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
        match(TokenType.While);
        match(TokenType.LeftParen);
        Expression test = expression();
        match(TokenType.RightParen);
        Statement body = statement();
        return new Loop(test, body);
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
        Expression e = conjunction();
        while (token.type().equals(TokenType.Or)) {
            Operator op = new Operator(match(TokenType.Or));
            Expression term2 = conjunction();
            e = new Binary(op, e, term2);
        }
        return e;
    }

    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
        Expression e = equality();
        while (token.type().equals(TokenType.And)) {
            Operator op = new Operator(match(TokenType.And));
            Expression term2 = equality();
            e = new Binary(op, e, term2);
        }
        return e;
    }

    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
        Expression e = relation();
        while (isEqualityOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = relation();
            e = new Binary(op, e, term2);
        }
        return e;
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition]
        Expression e = addition();
        while (isRelationalOp()){
            Operator op = new Operator(match(token.type()));
            Expression term2 = addition();
            e = new Binary(op, e, term2);
        }
        return e;
    }

    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }

    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }

    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }

//    private Expression primary () {
//        // Primary --> Identifier | Literal | ( Expression )
//        //             | Type ( Expression )
//        Expression e = null;
//        if (token.type().equals(TokenType.Identifier)) {
//            Variable v = new Variable(match(TokenType.Identifier));
//            e = v;
//        } else if (isLiteral()) {
//            e = literal();
//        } else if (token.type().equals(TokenType.LeftParen)) {
//            match(TokenType.LeftParen);
//            e = expression();
//            match(TokenType.RightParen);
//        } else if (isType( )) {
//            Operator op = new Operator(match(token.type()));
//            match(TokenType.LeftParen);
//            Expression term = expression();
//            match(TokenType.RightParen);
//            e = new Unary(op, term);
//        } else error("Identifier | Literal | ( | Type");
//        return e;
//    }
    
    private Expression primary () {
        // Primary --> Identifier [ [ Expression ] ] | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            String s = match(TokenType.Identifier);
            if (token.type().equals(TokenType.LeftBracket)) {
                match(TokenType.LeftBracket);
                Expression index = expression();
                e = new ArrayRef(s, index);
                match(TokenType.RightBracket);
            } else {
                e = new Variable(s);
            }
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            match(TokenType.LeftParen);
            e = expression();
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
        String s = null;
        switch (token.type()) {
        case IntLiteral:
            s = match(TokenType.IntLiteral);
            return new IntValue(Integer.parseInt(s));
        case CharLiteral:
            s = match(TokenType.CharLiteral);
            return new CharValue(s.charAt(0));
        case True:
            s = match(TokenType.True);
            return new BoolValue(true);
        case False:
            s = match(TokenType.False);
            return new BoolValue(false);
        case FloatLiteral:
            s = match(TokenType.FloatLiteral);
            return new FloatValue(Float.parseFloat(s));
        }
        throw new IllegalArgumentException( "should not reach here");
    }


    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }

    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }

    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }

    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }

    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) ||
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }

    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool)
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }

    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }

    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }

    public static void main(String args[]) {
        String filename = "programs/cast.cpp";
    	System.out.println("Begin parsing... " + filename);
    	Parser parser  = new Parser(new Lexer(filename));
        Program prog = parser.program();
        prog.display();      // display abstract syntax tree
    } //main

} // Parser