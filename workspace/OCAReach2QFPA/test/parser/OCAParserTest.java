package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import automata.counter.OCA;
import junit.framework.TestCase;
import parser.OCAParser;
import parser.OCDGenerator;

public class OCAParserTest extends TestCase {

	private OCDGenerator ocdg;
	private OCAParser parser;
	
	public void setUp() {
		this.ocdg = new OCDGenerator();
	}
	
	@SuppressWarnings("resource")
	public final void testParse() throws IOException {
		for(int i = 0; i < 100; i++) {
			int ch = 0;
			FileOutputStream output = new FileOutputStream(new File("/home/clexma/Desktop/test.ocd"));
			Random r = new Random();
			output.write(ocdg.generateRandomOcd(r.nextInt(10)+1).getBytes());
			InputStream input = new FileInputStream(new File("/home/clexma/Desktop/test.ocd"));
			StringBuilder sb = new StringBuilder();
			while((ch = input.read()) != -1) {
				sb.append((char)ch);
			}
			String ocd = sb.toString();
			parser = new OCAParser(input);
			OCA oca = parser.parse(ocd);
			oca.print();
		}
	}

	public void tearDown() {
		
	}
}
