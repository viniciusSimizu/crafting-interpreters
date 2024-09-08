package src;

import java.util.List;

import src.datatype.Stmt;

public class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;
  private final Environment closure;

  public LoxFunction(Stmt.Function declaration, Environment closure) {
    this.declaration = declaration;
    this.closure = closure;
  }

  @Override
  public int arity() {
    return declaration.params.size();
  }

  @Override
  public Object call(List<Object> args) {
    Environment environment = new Environment(closure);
    for (int i = 0; i < arity(); ++i) {
      environment.define(
	  declaration.params.get(i).lexeme,
	  args.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      return returnValue.value;
    }

    // Unreacheable
    return null;
  }

  @Override
  public String toString() {
      return String.format("<fn %s>", declaration.name.lexeme);
  }
}
