package cli;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.*;

import automata.counter.OCA;
import ocareach.ConverterZero;
import parser.OCAParser;

public class CLI {
	public static void main(String[] args) throws ParseException, IOException {
		String[] testargs = new String[3];
		testargs[0] = "-con";
		testargs[1] = "/home/clexma/Desktop/test.ocd";
		testargs[2] = "/home/clexma/Desktop/testFormula.txt";
		CLI.handleArgs(testargs);
	}
	
	public static void handleArgs(String[] args) throws ParseException, IOException {
		Options options = new Options();
		Option helpOpt = new Option("h", "help", false, "Print help infos");
		helpOpt.setRequired(false);
		options.addOption(helpOpt);
		
		Option convertOpt = Option.builder("con").numberOfArgs(2).argName("src> <dst").desc("convert src OCA textual description into PA formula to dst").build();
		convertOpt.setRequired(false);
		options.addOption(convertOpt);
		
		
		HelpFormatter hf = new HelpFormatter();
		hf.setWidth(110);
		CommandLine commandLine = null;
		CommandLineParser parser = new DefaultParser();
		commandLine = parser.parse(options, args);
		if(commandLine.hasOption('h')){
			hf.printHelp("java -jar OCAReach.jar ", options, true);
		} else if(commandLine.hasOption("con")) {
			for(String s : args) {
				System.out.print(s + '\t');
			}
			String[] searchArgs = commandLine.getOptionValues("con");
			if(searchArgs.length != 2) {
				System.out.println("Converter arguments error, src path and dst path needed.");
				return;
			} else {
				InputStream input = new FileInputStream(new File(searchArgs[0]));
				StringBuilder sb = new StringBuilder();
				int ch = 0;
				while((ch = input.read()) != -1) {
					sb.append((char)ch);
				}
				String ocd = sb.toString();
				OCAParser ocaParser = new OCAParser(input);
				OCA oca = ocaParser.parse(ocd);
				
				ConverterZero cz = new ConverterZero(oca);
				String result = cz.convert();
				FileOutputStream fo = new FileOutputStream(new File(searchArgs[1]));
				fo.write(result.getBytes());
				System.out.println("DONE");
			}
			
		} else {
			hf.printHelp("java OCAReach.jar ", options, true);
		}
		Option[] opts = commandLine.getOptions();
		
	}
}

