public interface ExprVisitor<T> {
	public T visitBinaryExpr(Expr.Binary expr);
	public T visitUnaryExpr(Expr.Unary expr);
	public T visitLiteralExpr(Expr.Literal expr);
	public T visitGroupingExpr(Expr.Grouping expr);
}
