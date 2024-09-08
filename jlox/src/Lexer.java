package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.datatype.Token;
import src.datatype.TokenType;

class Lexer {
  private int start;
  private int curr;
  private int line = 1;
  private final String source;
  private final List<Token> tokens = new ArrayList<>();

  private static final Map<String, TokenType> keywords;
  static {
    keywords = new HashMap<>();
    keywords.put("print", TokenType.PRINT);
    keywords.put("and", TokenType.AND);
    keywords.put("or", TokenType.OR);
    keywords.put("if", TokenType.IF);
    keywords.put("else", TokenType.ELSE);
    keywords.put("for", TokenType.FOR);
    keywords.put("while", TokenType.WHILE);
    keywords.put("fun", TokenType.FUN);
    keywords.put("return", TokenType.RETURN);
    keywords.put("class", TokenType.CLASS);
    keywords.put("this", TokenType.THIS);
    keywords.put("super", TokenType.SUPER);
    keywords.put("true", TokenType.TRUE);
    keywords.put("false", TokenType.FALSE);
    keywords.put("var", TokenType.VAR);
    keywords.put("nil", TokenType.NIL);
  }

  public Lexer(String source) {
    this.source = source;
  }

  public List<Token> scanTokens() {
    while (!isEOF()) {
      start = curr;
      scanToken();
    }
    addToken(TokenType.EOF);
    return tokens;
  }

  private void scanToken() {
    char chr = advance();
    switch (chr) {
      case ' ':
      case '\r':
      case '\t': break;

      case '\n': ++line; break;

      case '(':	addToken(TokenType.OPEN_PAREN); break;
      case ')':	addToken(TokenType.CLOSE_PAREN); break;
      case '{':	addToken(TokenType.OPEN_BRACE); break;
      case '}':	addToken(TokenType.CLOSE_BRACE); break;
      case ',':	addToken(TokenType.COMMA); break;
      case '.':	addToken(TokenType.DOT); break;
      case '+':	addToken(TokenType.PLUS); break;
      case '-':	addToken(TokenType.MINUS); break;
      case '*':	addToken(TokenType.STAR); break;
      case '/':	addToken(TokenType.SLASH); break;
      case ';':	addToken(TokenType.SEMICOLON); break;

      case '=':
		if (match('=')) {
		  addToken(TokenType.EQUAL_EQUAL);
		} else {
		  addToken(TokenType.EQUAL);
		};
		break;
      case '!':
		if (match('=')) {
		  addToken(TokenType.BANG_EQUAL);
		} else {
		  addToken(TokenType.BANG);
		};
		break;
      case '>':
		if (match('=')) {
		  addToken(TokenType.GREATER_EQUAL);
		} else {
		  addToken(TokenType.GREATER);
		};
		break;
      case '<':
		if (match('=')) {
		  addToken(TokenType.LESS_EQUAL);
		} else {
		  addToken(TokenType.LESS);
		};
		break;

      case '"':	string(); break;

      default:
		if (isDigit(chr)) {
		  number();
		  break;
		};

		if (isAlpha(chr)) {
		  identifier();
		  break;
		};

		Lox.error(line, "Unexpected character.");
		break;
    }
  }

  private void string() {
    start = curr - 1;
    while (!isEOF() && peek() != '"') {
      if (peek() == '\n') ++line;
      advance();
    };

    if (isEOF()) {
      Lox.error(line, "Unterminated string.");
      return;
    };

    advance();

    String literal = source.substring(start + 1, curr - 1);
    addToken(TokenType.STRING, literal);
  };

  private void number() {
    start = curr - 1;
    while (isDigit(peek())) advance();

    if (peek() == '.' && isDigit(peekNext())) {
      advance();
      while (isDigit(peek())) advance();
    };

    String literal = source.substring(start, curr);
    addToken(TokenType.NUMBER, Double.parseDouble(literal));
  };

  private void identifier() {
    start = curr - 1;

    while (isAlphaNumeric(peek())) advance();
    String lexeme = source.substring(start, curr);
    TokenType token = Lexer.keywords.get(lexeme);

    if (token == null) token = TokenType.IDENTIFIER;
    addToken(token);
  };

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String lexeme = source.substring(start, curr);
    tokens.add(new Token(type, lexeme, literal, line));
  }

  private char advance() {
    return source.charAt(curr++);
  }

  private boolean match(char chr) {
    if (peek() == chr) {
      advance();
      return true;
    };
    return false;
  };

  private char peek() {
    if (isEOF()) {
      return '\0';
    };
    return source.charAt(curr);
  }

  private char peekNext() {
    if (curr + 1 >= source.length()) {
      return '\0';
    };
    return source.charAt(curr + 1);
  }

  private boolean isDigit(char chr) {
    if (chr >= '0' && chr <= '9') {
      return true;
    };
    return false;
  }

  private boolean isAlpha(char chr) {
    if (chr >= 'a' && chr <= 'z' || chr >= 'A' && chr <= 'Z' || chr == '_') {
      return true;
    };
    return false;
  }

  private boolean isAlphaNumeric(char chr) {
    return isAlpha(chr) || isDigit(chr);
  };

  private boolean isEOF() {
    return curr >= source.length();
  }
};
