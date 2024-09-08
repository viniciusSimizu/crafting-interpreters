package src.datatype;

import java.util.List;

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

  public static class Variable extends Expr {
    public Token name;

    public Variable(Token name) {
      this.name = name;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visitVariableExpr(this);
    }
  }

  public static class Assign extends Expr {
    public Token name;
    public Expr value;

    public Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visitAssignExpr(this);
    }
  }

  public static class Logical extends Expr {
    public Expr left;
    public Token operator;
    public Expr right;

    public Logical(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visitLogicalExpr(this);
    }
  }

  public static class Call extends Expr {
    public Expr callee;
    public Token paren;
    public List<Expr> args;

    public Call(Expr callee, Token paren, List<Expr> args) {
      this.callee = callee;
      this.paren = paren;
      this.args = args;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visitCallExpr(this);
    }
  }

  public static interface Visitor<T> {
    T visitBinaryExpr(Binary expr);
    T visitUnaryExpr(Unary expr);
    T visitLiteralExpr(Literal expr);
    T visitGroupingExpr(Grouping expr);
    T visitVariableExpr(Variable expr);
    T visitAssignExpr(Assign expr);
    T visitLogicalExpr(Logical expr);
    T visitCallExpr(Call expr);
  }
}
