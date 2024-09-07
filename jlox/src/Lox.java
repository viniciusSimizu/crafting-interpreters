package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import src.datatype.Stmt;
import src.datatype.Token;
import src.datatype.TokenType;

public class Lox {
  private static final Interpreter interpreter = new Interpreter();

  private static boolean hadError;
  private static boolean hadRuntimeError;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.exit(64);

    } else if (args.length == 1) {
      runFile(args[0]);

    } else {
      runRepl();
    };
  };

  private static void runFile(String filepath) throws IOException {
    Path path = Paths.get(filepath);
    byte[] buff = Files.readAllBytes(path);
    String source = new String(buff, Charset.forName("UTF-8"));
    run(source);

    if (hadError) System.exit(65);
    if (hadRuntimeError) System.exit(70);
  };

  private static void runRepl() throws IOException {
    InputStreamReader stream = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(stream);

    while (true) {
      System.out.print("input: ");
      String line = reader.readLine();
      if (line == null) {
	System.out.println();
	break;
      }
      run(line);
      hadError = false;
    };
  };

  private static void run(String source) {
    Lexer lexer = new Lexer(source);
    List<Token> tokens = lexer.scanTokens();

    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    if (hadError) return;

    interpreter.interpret(statements);
  };

  public static void error(int line, String msg) {
    report(line, "", msg);
  }

  public static void error(Token token, String msg) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", msg);
    } else {
      report(token.line, String.format(" at '%s'", token.lexeme), msg);
    }
  }

  private static void report(int line, String where, String msg) {
    System.err.println(String.format("[line %d] Error %s: %s", line, where, msg));
    hadError = true;
  }

  public static void runtimeError(RuntimeError error) {
    System.err.println(String.format("%s\n[line %d]",
	  error.getMessage(),
	  error.token.literal));
    hadRuntimeError = true;
  }
};
