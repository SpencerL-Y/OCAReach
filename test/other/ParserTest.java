package other;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import automata.counter.OCA;
import parser.OCAParser;

public class ParserTest {
	public static void main(String[] args) throws IOException {
		int ch = 0;
		InputStream input = new FileInputStream(new File("/home/clexma/Desktop/a.ocd"));
		StringBuilder sb = new StringBuilder();
		while((ch = input.read()) != -1) {
			sb.append((char)ch);
		}
		String ocd = sb.toString();
		OCAParser p = new OCAParser(input);
		OCA oca = p.parse(ocd);
		oca.print();
	}
}
  