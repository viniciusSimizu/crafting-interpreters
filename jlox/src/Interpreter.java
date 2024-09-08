package src;

import java.util.List;

import src.datatype.Expr;
import src.datatype.Stmt;
import src.datatype.Token;
import src.datatype.TokenType;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  private Environment environment = new Environment();

  public void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
	execute(statement);
      }
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }

  private void execute(Stmt statement) {
    statement.accept(this);
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);
    return null;
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    Object value = evaluate(stmt.expression);
    System.out.println(stringfy(value));
    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    Object value = null;
    if (stmt.initializer != null) {
      value = evaluate(stmt.initializer);
    }

    environment.define(stmt.name.lexeme, value);
    return null;
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    executeBlock(stmt.statements, new Environment(environment));
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    if (isTruthy(evaluate(stmt.condition))) {
      execute(stmt.thenBranch);
    }
    else execute(stmt.elseBranch);

    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    while (isTruthy(evaluate(stmt.condition))) {
      execute(stmt.body);
    }

    return null;
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case LESS_EQUAL:
	checkNumberOperands(expr.operator, left, right);
	return (double)left <= (double)right;
      case LESS:
	checkNumberOperands(expr.operator, left, right);
	return (double)left < (double)right;
      case GREATER:
	checkNumberOperands(expr.operator, left, right);
	return (double)left > (double)right;
      case GREATER_EQUAL:
	checkNumberOperands(expr.operator, left, right);
	return (double)left >= (double)right;

      case EQUAL_EQUAL: return isEqual(left, right);
      case BANG_EQUAL: return !isEqual(left, right);

      case PLUS:
	if (left instanceof Double && right instanceof Double) {
	  return (double)left + (double)right;
	}

	if (left instanceof String) {
	  return (String)left + concatValue(right);
	}

	throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");

      case MINUS:
	checkNumberOperands(expr.operator, left, right);
	return (double)left - (double)right;

      case STAR:
	checkNumberOperands(expr.operator, left, right);
	return (double)left * (double)right;

      case SLASH:
	checkNumberOperands(expr.operator, left, right);
	if ((double)right == 0) {
	  throw new RuntimeError(expr.operator, "Number cannot be divided by zero.");
	}

	return (double)left / (double)right;

      default:
	break;
    }

    // Unreacheable
    return null;
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case MINUS:
	checkNumberOperand(expr.operator, right);
	return -(double)right;
      case BANG: return !isTruthy(right);
      default: break;
    }

    // Unreacheable
    return null;
  }

  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr);
  }

  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    return environment.get(expr.name);
  }

  @Override
  public Object visitAssignExpr(Expr.Assign expr) {
    Object value = evaluate(expr);
    environment.assign(expr.name, value);
    return value;
  }

  private void executeBlock(List<Stmt> statements, Environment environment) {
    this.environment = environment;

    try {
      for (Stmt stmt : statements) {
	execute(stmt);
      }
    } finally {
      this.environment = environment.enclosing;
    }
  }

  @Override
  public Object visitLogicalExpr(Expr.Logical expr) {
    Object left = evaluate(expr.left);

    if (expr.operator.type == TokenType.OR) {
      if (isTruthy(left)) return true;
    } else {
      if (!isTruthy(left)) return false;
    }

    return isTruthy(evaluate(expr.right));
  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  private boolean isTruthy(Object value) {
    if (value == null) return false;
    if (value instanceof Boolean) return (boolean)value;
    return true;
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) return true;
    if (a == null) return false;
    return a.equals(b);
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;
    throw new RuntimeError(operator, "Operands must be a number.");
  }

  private String stringfy(Object value) {
    if (value == null) return "nil";

    if (value instanceof Double) {
      String text = value.toString();
      if (text.endsWith(".0")) {
	text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return value.toString();
  }

  private String concatValue(Object value) {
    if (value == null) return "";
    return stringfy(value);
  }
}
