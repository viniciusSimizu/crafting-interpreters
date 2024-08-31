package src.datatype;

public abstract class Stmt {
	public abstract <T> T accept(Visitor<T> visitor);

	public static class Expression extends Stmt {
		public Expr expression;

		public Expression(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitExpressionStmt(this);
		}
	}

	public static class Print extends Stmt {
		public Expr expression;

		public Print(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitPrintStmt(this);
		}
	}

	public static interface Visitor<T> {
		T visitExpressionStmt(Expression stmt);
		T visitPrintStmt(Print stmt);
	}
}
