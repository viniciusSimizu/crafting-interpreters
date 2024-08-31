package src.datatype;

public abstract class Expr {
	public abstract <T> T accept(Visitor<T> visitor);

	public static class Binary extends Expr {
		public final Expr left;
		public final Token operator;
		public final Expr right;

		public Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitBinaryExpr(this);
		}
	}

	public static class Unary extends Expr {
		public final Token operator;
		public final Expr right;

		public Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitUnaryExpr(this);
		}
	}

	public static class Literal extends Expr {
		public final Object value;

		public Literal(Object value) {
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitLiteralExpr(this);
		}
	}

	public static class Grouping extends Expr {
		public final Expr expression;

		public Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitGroupingExpr(this);
		}
	}

	public static interface Visitor<T> {
		T visitBinaryExpr(Binary expr);
		T visitUnaryExpr(Unary expr);
		T visitLiteralExpr(Literal expr);
		T visitGroupingExpr(Grouping expr);
	}
}
