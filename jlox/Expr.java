abstract class Expr {
	static class Binary extends Expr {
		private final Expr left;
		private final Token operator;
		private final Expr right;

		Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}
	}

	static class Unary extends Expr {
		private final Token operator;
		private final Expr right;

		Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}
	}

	static class Literal extends Expr {
		private final Object value;

		Literal(Object value) {
			this.value = value;
		}
	}

	static class Grouping extends Expr {
		private final Expr expression;

		Grouping(Expr expression) {
			this.expression = expression;
		}
	}
}
