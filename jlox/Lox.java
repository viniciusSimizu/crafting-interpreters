import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
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
	};

	private static void runRepl() throws IOException {
		InputStreamReader stream = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(stream);

		while (true) {
			System.out.print("input: ");
			String line = reader.readLine();
			if (line == null) break;
			run(line);
		};
	};

	private static void run(String source) {
		Lexer lexer = new Lexer(source);
		List<Token> tokens = lexer.scanTokens();

		for (Token token : tokens) {
			System.out.println(token);
		};
	};
};
