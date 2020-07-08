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
import parser.OCDGenerator;

public class CLI {
	public static void main(String[] args) throws ParseException, IOException {
		/*String[] testargs = new String[4];
		testargs[0] = "-gen";
		testargs[1] = "/home/clexma/Desktop/my.ocd";
		testargs[2] = "10";
		testargs[3] = "0.5";
		CLI.handleArgs(testargs);*/
		System.out.println("OCAReach Version 1.0 ------ Author: CLEXMA");
		CLI.handleArgs(args);
	}
	
	public static void handleArgs(String[] args) throws ParseException, IOException {
		Options options = new Options();
		Option helpOpt = new Option("h", "help", false, "Print help infos");
		helpOpt.setRequired(false);
		options.addOption(helpOpt);
		
		Option convertOpt = Option.builder("con").numberOfArgs(2).argName("src> <dst").desc("convert src OCA textual description into PA formula to dst").build();
		convertOpt.setRequired(false);
		options.addOption(convertOpt);
		
		Option genOpt = Option.builder("gen").numberOfArgs(3).argName("dst> <stateNum> <eta").desc("generate a OCA randomly with number of state and edge sparsity").build();
		genOpt.setRequired(false);
		options.addOption(genOpt);
		
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
		
		} else if(commandLine.hasOption("gen")) {
			
			for(String s : args) {
				System.out.print(s + '\t');
			}
			String[] searchArgs = commandLine.getOptionValues("gen");
			if(searchArgs.length != 3) {
				System.out.println("OCDGenerator arguments error, src path and dst path needed.");
				return;
			} else {
				String dst = searchArgs[0];
				int stateNum = Integer.parseInt(searchArgs[1]);
				double eta = Double.parseDouble(searchArgs[2]);
				OCDGenerator ocdg = new OCDGenerator();
				String result = ocdg.generateRandomOcdEta(stateNum, eta);
				FileOutputStream fo = new FileOutputStream(searchArgs[0]);
				fo.write(result.getBytes());
				System.out.println("DONE");
			}
		} else {
			hf.printHelp("java OCAReach.jar ", options, true);
		}
		Option[] opts = commandLine.getOptions();
		
	}
}

