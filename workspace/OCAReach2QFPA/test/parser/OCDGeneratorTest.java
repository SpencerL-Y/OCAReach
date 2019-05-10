package parser;

import java.util.Random;

import junit.framework.TestCase;
import parser.OCDGenerator;

public class OCDGeneratorTest extends TestCase {

	private OCDGenerator ocdg;
	public void setUp() {
		this.ocdg =  new OCDGenerator();
	}
	
	public final void testGenerateRandomOcd() {
		Random r = new Random();
		for(int i = 0; i < 100; i++) {
			String result = ocdg.generateRandomOcd(r.nextInt(20)+1);
			System.out.println(result);
		}
	}

	public final void testGenerateOcd() {
		for(int i = 0; i < 10; i++) {
			Random r = new Random();
			ocdg.genRandomOca(r.nextInt(100)+1);
			String result = ocdg.generateOcd();
			System.out.println(result);
		}
	}

	public final void testGenRandomOca() {
		for(int i = 0; i < 100; i++) {
			Random r = new Random();
			ocdg.genRandomOca(r.nextInt(100)+1);
		}
	}
	
	public void tearDown() {
		
	}
}
