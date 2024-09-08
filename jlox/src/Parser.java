package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import src.datatype.Expr;
import src.datatype.Stmt;
import src.datatype.Token;
import src.datatype.TokenType;

public class Parser {
  private final List<Token> tokens;
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public List<Stmt> parse() {
    List<Stmt> expressions = new ArrayList<>();
    while (!isAtEnd()) {
      expressions.add(declaration());
    }

    return expressions;
  }

  private Stmt declaration() {
    try {
      if (match(TokenType.VAR)) {
	return varDeclaration();
      }
      return statement();

    } catch (ParserError error) {
      synchronize();
      return null;
    }
  }

  private Stmt varDeclaration() {
    Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
    Expr initializer = null;

    if (match(TokenType.EQUAL)) {
      initializer = expression();
    }
    consume(TokenType.SEMICOLON, "Expect ';' after value.");
    return new Stmt.Var(name, initializer);
  }

  private Stmt statement() {
    if (match(TokenType.PRINT)) return printStatement();
    if (match(TokenType.OPEN_BRACE)) return new Stmt.Block(block());
    if (match(TokenType.IF)) return ifStatement();;
    if (match(TokenType.WHILE)) return whileStatement();;
    if (match(TokenType.FOR)) return forStatement();;

    return expressionStatement();
  }

  private Stmt printStatement() {
    Expr expr = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(expr);
  }

  private Stmt ifStatement() {
    consume(TokenType.OPEN_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(TokenType.CLOSE_PAREN, "Expect ')' after 'if' condition.");

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(TokenType.ELSE)) {
      elseBranch = statement();
    }

    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  private Stmt whileStatement() {
    consume(TokenType.OPEN_PAREN, "Expect '(' after 'while'.");
    Expr condition = expression();
    consume(TokenType.CLOSE_PAREN, "Expect ')' after 'while' condition.");

    Stmt body = statement();
    return new Stmt.While(condition, body);
  }

  private Stmt forStatement() {
    consume(TokenType.OPEN_PAREN, "Expect '(' after 'for'.");

    Stmt initializer = null;
    if (check(TokenType.VAR)) {
      initializer = varDeclaration();
    } else if (!check(TokenType.SEMICOLON)) {
      initializer = expressionStatement();
    }
    consume(TokenType.SEMICOLON, "Expect ';' after loop initializer.");

    Expr condition = null;
    if (!check(TokenType.SEMICOLON)) {
      condition = expression();
    }
    consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

    Expr increment = null;
    if (!check(TokenType.CLOSE_PAREN)) {
      increment = expression();
    }
    consume(TokenType.CLOSE_PAREN, "Expect ')' after 'for' clauses.");

    Stmt body = statement();

    if (increment != null) {
      body = new Stmt.Block(Arrays.asList(
	    body,
	    new Stmt.Expression(increment)));
    }

    if (condition == null) condition = new Expr.Literal(true);
    body = new Stmt.While(condition, body);

    if (initializer != null) {
      body = new Stmt.Block(Arrays.asList(
	    initializer,
	    body));
    }

    return body;
  }

  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after value.");
    return new Stmt.Expression(expr);
  }

  private Expr expression() {
    return assignment();
  }

  private Expr assignment() {
    Expr expr = or();
    if (match(TokenType.EQUAL)) {
      Token equals = previous();
      Expr value = expression();

      if (!(expr instanceof Expr.Variable)) {
	error(equals, "Invalid assignment target.");
      }

      Expr.Variable var = (Expr.Variable) expr;
      return new Expr.Assign(var.name, value);
    }

    return expr;
  }

  private Expr or() {
    Expr expr = and();

    while (match(TokenType.OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr and() {
    Expr expr = equality();

    while (match(TokenType.AND)) {
      Token operator = previous();
      Expr right = equality();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr equality() {
    Expr expr = comparison();
    while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr comparison() {
    Expr expr = term();
    while (match(TokenType.LESS_EQUAL, TokenType.LESS, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr term() {
    Expr expr = factor();
    while (match(TokenType.PLUS, TokenType.MINUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr factor() {
    Expr expr = unary();
    while (match(TokenType.STAR, TokenType.SLASH)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr unary() {
    if (match(TokenType.BANG, TokenType.MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }
    return primary();
  }

  private Expr primary() {
    if (match(TokenType.TRUE)) return new Expr.Literal(true);
    if (match(TokenType.FALSE)) return new Expr.Literal(false);
    if (match(TokenType.NIL)) return new Expr.Literal(null);

    if (match(TokenType.NUMBER, TokenType.STRING)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(TokenType.VAR)) {
      return new Expr.Variable(previous());
    }

    if (match(TokenType.OPEN_PAREN)) {
      Expr expr = expression();
      consume(TokenType.CLOSE_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(TokenType.CLOSE_BRACE) && !isAtEnd()) {
      statements.add(statement());
    }

    consume(TokenType.CLOSE_BRACE, "Expect '}' after block.");
    return statements;
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == TokenType.SEMICOLON) return;

      switch (peek().type) {
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

      advance();
    }
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
	advance();
	return true;
      }
    }

    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd()) ++current;
    return previous();
  }

  private Token consume(TokenType type, String msg) {
    if (check(type)) return advance();
    throw error(peek(), msg);
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private boolean isAtEnd() {
    return peek().type == TokenType.EOF;
  }

  private ParserError error(Token token, String msg) {
    Lox.error(token, msg);
    return new ParserError();
  }

  private static class ParserError extends RuntimeException {}
}
