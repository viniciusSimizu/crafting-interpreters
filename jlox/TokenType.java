public enum TokenType {
	// Single character token
	OPEN_PAREN, CLOSE_PAREN, OPEN_BRACE, CLOSE_BRACE,
	COMMA, DOT, PLUS, MINUS, STAR, SLASH, SEMICOLON,

	// Single or doubly character token
	EQUAL, EQUAL_EQUAL,
	BANG, BANG_EQUAL,
	GREATER, GREATER_EQUAL, 
	LESS, LESS_EQUAL, 

	// Literal
	IDENTIFIER, STRING, NUMBER,

	// Keywords
	PRINT, AND, OR, IF, ELSE, FOR, WHILE, FUN, RETURN,
	CLASS, THIS, SUPER, TRUE, FALSE, VAR, NIL,

	EOF;
};
