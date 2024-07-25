import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		while (!this.isEOF()) {
			this.start = this.curr;
			this.scanToken();
		}
		this.addToken(TokenType.EOF);
		return this.tokens;
	}

	private void scanToken() {
		char chr = this.advance();
		switch (chr) {
			case ' ':
			case '\r':
			case '\t': break;

			case '\n': ++this.line; break;

			case '(':	this.addToken(TokenType.OPEN_PAREN); break;
			case ')':	this.addToken(TokenType.CLOSE_PAREN); break;
			case '{':	this.addToken(TokenType.OPEN_BRACE); break;
			case '}':	this.addToken(TokenType.CLOSE_BRACE); break;
			case ',':	this.addToken(TokenType.COMMA); break;
			case '.':	this.addToken(TokenType.DOT); break;
			case '+':	this.addToken(TokenType.PLUS); break;
			case '-':	this.addToken(TokenType.MINUS); break;
			case '*':	this.addToken(TokenType.STAR); break;
			case '/':	this.addToken(TokenType.SLASH); break;
			case ';':	this.addToken(TokenType.SEMICOLON); break;

			case '=':
				if (this.match('=')) {
					this.addToken(TokenType.EQUAL_EQUAL);
				} else {
					this.addToken(TokenType.EQUAL);
				};
				break;
			case '!':
				if (this.match('=')) {
					this.addToken(TokenType.BANG_EQUAL);
				} else {
					this.addToken(TokenType.BANG);
				};
				break;
			case '>':
				if (this.match('=')) {
					this.addToken(TokenType.GREATER_EQUAL);
				} else {
					this.addToken(TokenType.GREATER);
				};
				break;
			case '<':
				if (this.match('=')) {
					this.addToken(TokenType.LESS_EQUAL);
				} else {
					this.addToken(TokenType.LESS);
				};
				break;

			case '"':	this.string(); break;

			default:
				if (this.isDigit(chr)) {
					this.number();
					break;
				};

				if (this.isAlpha(chr)) {
					this.identifier();
					break;
				};

				System.exit(201);
				break;
		}
	}

	private void string() {
		this.start = this.curr - 1;
		while (!this.isEOF() && this.peek() != '"') {
			if (this.peek() == '\n') ++this.line;
			this.advance();
		};

		if (this.isEOF()) {
			System.exit(201);
			return;
		};

		this.advance();

		String literal = this.source.substring(this.start + 1, this.curr - 1);
		this.addToken(TokenType.STRING, literal);
	};

	private void number() {
		this.start = this.curr - 1;
		while (this.isDigit(this.peek())) this.advance();

		if (this.peek() == '.' && this.isDigit(this.peekNext())) {
			this.advance();
			while (this.isDigit(this.peek())) this.advance();
		};

		String literal = this.source.substring(this.start, this.curr);
		this.addToken(TokenType.NUMBER, Double.parseDouble(literal));
	};

	private void identifier() {
		this.start = this.curr - 1;
		while (this.isAlphaNumeric(this.peek())) this.advance();
		String lexeme = this.source.substring(this.start, this.curr);
		TokenType token = Lexer.keywords.get(lexeme);
		if (token == null) token = TokenType.IDENTIFIER;
		this.addToken(token);
	};

	private void addToken(TokenType type) {
		this.addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String lexeme = this.source.substring(this.start, this.curr);
		this.tokens.add(new Token(type, lexeme, literal, this.line));
	}

	private char advance() {
		return this.source.charAt(this.curr++);
	}

	private boolean match(char chr) {
		if (this.peek() == chr) {
			this.advance();
			return true;
		};
		return false;
	};

	private char peek() {
		if (this.isEOF()) {
			return '\0';
		};
		return this.source.charAt(this.curr);
	}

	private char peekNext() {
		if (this.curr + 1 >= this.source.length()) {
			return '\0';
		};
		return this.source.charAt(this.curr + 1);
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
		return this.isAlpha(chr) || this.isDigit(chr);
	};

	private boolean isEOF() {
		return this.curr >= this.source.length();
	}
};
