abstract class Stmt {
	abstract <T> T accept(Visitor<T> visitor);

	static class Expression extends Stmt {
		public Expr expression;

		Expression(Expr expression) {
			this.expression = expression;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitExpressionStmt(this);
		}
	}

	static class Print extends Stmt {
		public Expr expression;

		Print(Expr expression) {
			this.expression = expression;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitPrintStmt(this);
		}
	}

	static interface Visitor<T> {
		T visitExpressionStmt(Expression stmt);
		T visitPrintStmt(Print stmt);
	}
}
