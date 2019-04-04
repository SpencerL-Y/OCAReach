package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import parser.OCAParser;

public class parserTest {
	public static void main(String[] args) throws IOException {
		int ch = 0;
		InputStream input = new FileInputStream(new File("/home/clexma/Desktop/a.ocd"));
		StringBuilder sb = new StringBuilder();
		while((ch = input.read()) != -1) {
			sb.append((char)ch);
		}
		String ocd = sb.toString();
		OCAParser p = new OCAParser(input);
		p.parse(ocd);
	}
}
