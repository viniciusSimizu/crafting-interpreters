package src;

import java.util.List;

import src.datatype.Expr;
import src.datatype.Stmt;
import src.datatype.Token;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
	public void interpret(List<Stmt> statements) {
		try {
			for (Stmt statement : statements) {
				this.execute(statement);
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
		this.evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitPrintStmt(Stmt.Print stmt) {
		Object value = this.evaluate(stmt.expression);
		System.out.println(this.stringfy(value));
		return null;
	}

	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		Object left = this.evaluate(expr.left);
		Object right = this.evaluate(expr.right);

		switch (expr.operator.type) {
			case LESS_EQUAL:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left <= (double)right;
			case LESS:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left < (double)right;
			case GREATER:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left > (double)right;
			case GREATER_EQUAL:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left >= (double)right;

			case EQUAL_EQUAL: return this.isEqual(left, right);
			case BANG_EQUAL: return !this.isEqual(left, right);

			case PLUS:
				if (left instanceof Double && right instanceof Double) {
					return (double)left + (double)right;
				}

				if (left instanceof String) {
					return (String)left + this.concatValue(right);
				}

				throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");

			case MINUS:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left - (double)right;
			case STAR:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left * (double)right;
			case SLASH:
				this.checkNumberOperands(expr.operator, left, right);
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
		Object right = this.evaluate(expr.right);

		switch (expr.operator.type) {
			case MINUS: 
				this.checkNumberOperand(expr.operator, right);
				return -(double)right;
			case BANG: return !this.isTruthy(right);
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
		return this.evaluate(expr);
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
		return this.stringfy(value);
	}
}
