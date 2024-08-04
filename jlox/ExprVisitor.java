public interface ExprVisitor<T> {
	public T visitBinary(Expr.Binary expr);
	public T visitUnary(Expr.Unary expr);
	public T visitLiteral(Expr.Literal expr);
	public T visitGrouping(Expr.Grouping expr);
}
