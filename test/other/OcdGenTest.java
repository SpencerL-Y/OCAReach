package other;

import parser.OCDGenerator;

public class OcdGenTest {
	public static void main(String[] argsf) {
		OCDGenerator og = new OCDGenerator();
		System.out.println(og.generateRandomOcd(6));
	}
}
