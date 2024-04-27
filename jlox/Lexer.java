import java.util.ArrayList;
import java.util.List;

class Lexer {
	private int start, curr, line = 1;
	private final String source;
	private final List<Token> tokens = new ArrayList<>();

	public Lexer(String source) {
		this.source = source;
	}

	private List<Token> scanTokens() {
		while (!this.isEOF()) {
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
				if (this.peek() == '=') {
					this.addToken(TokenType.EQUAL_EQUAL);
				} else {
					this.addToken(TokenType.EQUAL);
				};
				break;
			case '!':
				if (this.peek() == '=') {
					this.addToken(TokenType.BANG_EQUAL);
				} else {
					this.addToken(TokenType.BANG);
				};
				break;
			case '>':
				if (this.peek() == '=') {
					this.addToken(TokenType.GREATER_EQUAL);
				} else {
					this.addToken(TokenType.GREATER);
				};
				break;
			case '<':
				if (this.peek() == '=') {
					this.addToken(TokenType.LESS_EQUAL);
				} else {
					this.addToken(TokenType.LESS);
				};
				break;

			default:
				System.exit(201);
				break;
		}
	}

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

	private char peek() {
		if (this.isEOF()) {
			return '\0';
		};
		return this.source.charAt(this.curr);
	}

	private boolean isEOF() {
		return this.curr >= this.source.length();
	}
};
