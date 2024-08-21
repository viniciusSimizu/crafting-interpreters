public class Interpreter implements ExprVisitor<Object> {
	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		Object left = this.evaluate(expr.left);
		Object right = this.evaluate(expr.right);

		switch (expr.operator.type) {
			case LESS_EQUAL:
				return (double)left <= (double)right;
			case LESS:
				return (double)left < (double)right;
			case GREATER:
				return (double)left > (double)right;
			case GREATER_EQUAL:
				return (double)left >= (double)right;

			case EQUAL_EQUAL: return this.isEqual(left, right);
			case BANG_EQUAL: return !this.isEqual(left, right);

			case PLUS:
				if (left instanceof Double && right instanceof Double) {
					return (double)left + (double)right;
				}

				if (left instanceof String && right instanceof String) {
					return (String)left + (String)right;
				}

				break;
			case MINUS:
				return (double)left - (double)right;
			case STAR:
				return (double)left * (double)right;
			case SLASH:
				return (double)left / (double)right;
			default:
				break;
		}

		return null;
	}

	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
		Object right = this.evaluate(expr.right);

		switch (expr.operator.type) {
			case MINUS: return -(double)right;
			case BANG: return !this.isTruthy(right);
			default: break;
		}
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
}
