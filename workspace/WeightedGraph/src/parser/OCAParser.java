package parser;

import java.io.InputStream;
import java.io.InputStreamReader;

import automata.counter.OCA;

public class OCAParser {
	
	private InputStreamReader i;
	
	public OCAParser(InputStream is) {
		this.setI(new InputStreamReader(is));
	}
	
	public OCA parse(String ocd) {
		OCA oca = new OCA();
		String text = ocd;
		System.out.print(text);
	
		return null;
		System.out.println(text);
		
	}

	public InputStreamReader getI() {
		return i;
	}

	public void setI(InputStreamReader i) {
		this.i = i;
	}
}
