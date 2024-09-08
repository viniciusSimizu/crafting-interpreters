package src.datatype;

import java.util.List;

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

  public static class Var extends Stmt {
    public Token name;
    public Expr initializer;

    public Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visitVarStmt(this);
    }
  }

  public static class Block extends Stmt {
    public List<Stmt> statements;

    public Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visitBlockStmt(this);
    }
  }

  public static interface Visitor<T> {
    T visitExpressionStmt(Expression stmt);
    T visitPrintStmt(Print stmt);
    T visitVarStmt(Var stmt);
    T visitBlockStmt(Block stmt);
  }
}
