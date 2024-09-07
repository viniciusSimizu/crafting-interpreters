package src;

import java.util.HashMap;
import java.util.Map;

import src.datatype.Token;

public class Environment {
  private final Map<String, Object> values = new HashMap<>();

  public void define(String name, Object value) {
    this.values.put(name, value);
  }

  public Object get(Token name) {
    if (!this.values.containsKey(name.lexeme)) {
      throw new RuntimeError(name,
	  String.format("Undefined variable '%s'.", name.lexeme));
    }

    return this.values.get(name.lexeme);
  }

  public void assign(Token name, Object value) {
    if (!values.containsKey(name.lexeme)) {
      throw new RuntimeError(name,
	  String.format("Undefined variable '%s'.", name.lexeme));
    }

    values.put(name.lexeme, value);
  }
}
