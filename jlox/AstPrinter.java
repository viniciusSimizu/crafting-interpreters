public class AstPrinter implements ExprVisitor<String> {
	public String print(Expr expr) {
		return expr.accept(this);
	}

	private String parenthesize(String name, Expr... exprs) {
		StringBuilder builder = new StringBuilder();

		builder.append("(").append(name);
		for (Expr expr : exprs) {
			builder.append(" ");
			builder.append(expr.accept(this));
		}
		builder.append(")");

		return builder.toString();
	}

	@Override
	public String visitBinary(Expr.Binary expr) {
		return this.parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	@Override
	public String visitUnary(Expr.Unary expr) {
		return this.parenthesize(expr.operator.lexeme, expr.right);
	}

	@Override
	public String visitLiteral(Expr.Literal expr) {
		if (expr.value == null) return "nil";
		if (expr.value instanceof String) return "\"" + expr.value.toString() + "\"";
		return expr.value.toString();
	}

	@Override
	public String visitGrouping(Expr.Grouping expr) {
		return this.parenthesize("group", expr.expression);
	}
}
