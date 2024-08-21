import java.util.List;

public class Parser {
	private final List<Token> tokens;
	private int current = 0;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	public Expr parse() {
		try {
			return this.expression();
		} catch (ParserError err) {
			this.synchronize();
			return null;
		}
	}

	private Expr expression() {
		return this.equality();
	}

	private Expr equality() {
		Expr expr = this.comparison();
		while (this.match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
			Token operator = this.previous();
			Expr right = this.comparison();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr comparison() {
		Expr expr = this.term();
		while (this.match(TokenType.LESS_EQUAL, TokenType.LESS, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
			Token operator = this.previous();
			Expr right = this.term();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr term() {
		Expr expr = this.factor();
		while (this.match(TokenType.PLUS, TokenType.MINUS)) {
			Token operator = this.previous();
			Expr right = this.factor();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr factor() {
		Expr expr = this.unary();
		while (this.match(TokenType.STAR, TokenType.SLASH)) {
			Token operator = this.previous();
			Expr right = this.unary();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr unary() {
		if (this.match(TokenType.BANG, TokenType.MINUS)) {
			Token operator = this.previous();
			Expr right = this.unary();
			return new Expr.Unary(operator, right);
		}
		return this.primary();
	}

	private Expr primary() {
		if (this.match(TokenType.TRUE)) return new Expr.Literal(true);
		if (this.match(TokenType.FALSE)) return new Expr.Literal(false);
		if (this.match(TokenType.NIL)) return new Expr.Literal(null);

		if (this.match(TokenType.NUMBER, TokenType.STRING)) {
			return new Expr.Literal(this.previous().literal);
		}

		if (this.match(TokenType.OPEN_PAREN)) {
			Expr expr = this.expression();
			consume(TokenType.CLOSE_PAREN, "Expect ')' after expression.");
			return new Expr.Grouping(expr);
		}

		throw	this.error(this.peek(), "Expect expression.");
	}

	private void synchronize() {
		this.advance();

		while (!this.isAtEnd()) {
			if (this.previous().type == TokenType.SEMICOLON) return;

			switch (this.peek().type) {
				case CLASS:
				case FUN:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					return;
				default:
					break;
			}

			this.advance();
		}
	}

	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (this.check(type)) {
				this.advance();
				return true;
			}
		}

		return false;
	}

	private boolean check(TokenType type) {
		if (this.isAtEnd()) return false;
		return this.peek().type == type;
	}

	private Token advance() {
		if (this.isAtEnd()) ++this.current;
		return this.previous();
	}

	private Token consume(TokenType type, String msg) {
		if (this.check(type)) return this.advance();
		throw this.error(this.peek(), msg);
	}

	private Token peek() {
		return this.tokens.get(this.current);
	}

	private Token previous() {
		return this.tokens.get(this.current - 1);
	}

	private boolean isAtEnd() {
		return this.peek().type == TokenType.EOF;
	}

	private ParserError error(Token token, String msg) {
		Lox.error(token, msg);
		return new ParserError();
	}

	private static class ParserError extends RuntimeException {}
}
