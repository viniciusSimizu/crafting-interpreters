abstract class Expr {
	public abstract <T> T accept(ExprVisitor<T> visitor);

	static class Binary extends Expr {
		public final Expr left;
		public final Token operator;
		public final Expr right;

		Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <T> T accept(ExprVisitor<T> visitor) {
			return visitor.visitBinary(this);
		}
	}

	static class Unary extends Expr {
		public final Token operator;
		public final Expr right;

		Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <T> T accept(ExprVisitor<T> visitor) {
			return visitor.visitUnary(this);
		}
	}

	static class Literal extends Expr {
		public final Object value;

		Literal(Object value) {
			this.value = value;
		}

		@Override
		public <T> T accept(ExprVisitor<T> visitor) {
			return visitor.visitLiteral(this);
		}
	}

	static class Grouping extends Expr {
		public final Expr expression;

		Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <T> T accept(ExprVisitor<T> visitor) {
			return visitor.visitGrouping(this);
		}
	}
}
