package src;

import src.datatype.Expr;
import src.datatype.Stmt;
import src.datatype.Stmt.Expression;
import src.datatype.Stmt.Print;

public class AstPrinter implements Stmt.Visitor<String>, Expr.Visitor<String> {
  public String print(Stmt stmt) {
    return stmt.accept(this);
  }

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
  public String visitExpressionStmt(Expression stmt) {
    return stmt.accept(this);
  }

  @Override
  public String visitPrintStmt(Print stmt) {
    return this.parenthesize("print", stmt.expression);
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    return this.parenthesize(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return this.parenthesize(expr.operator.lexeme, expr.right);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    if (expr.value instanceof String) return "\"" + expr.value.toString() + "\"";
    return expr.value.toString();
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    return this.parenthesize("group", expr.expression);
  }
}
