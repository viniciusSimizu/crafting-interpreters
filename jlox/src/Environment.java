package src;

import java.util.HashMap;
import java.util.Map;

import src.datatype.Token;

public class Environment {
  public final Environment enclosing;
  private final Map<String, Object> values = new HashMap<>();

  public Environment() {
    this(null);
  }

  public Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  public void define(String name, Object value) {
    this.values.put(name, value);
  }

  public Object get(Token name) {
    if (this.values.containsKey(name.lexeme)) {
      return this.values.get(name.lexeme);
    }

    if (enclosing != null) return enclosing.get(name);

    throw new RuntimeError(name,
	String.format("Undefined variable '%s'.", name.lexeme));
  }

  public void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
    }

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }

    throw new RuntimeError(name,
	String.format("Undefined variable '%s'.", name.lexeme));
  }
}
