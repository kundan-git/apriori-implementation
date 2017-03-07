package Core;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import Inputreader.InputDataDelimiters;
import Inputreader.InputReaderEncoder;
import Itemsetbuilder.ItemSetBuilder;
import RulesBuilder.RuleBuilder;
import Exceptions.InputReaderAndEncoderException;

/**
 * The Class Main.
 */
public class AprioriDriver {

	public String RunApriori(String inputFilePath,InputDataDelimiters delimiter, boolean hasHeader, 
			float minSup,float minConf,String resultFilePath) 
					throws FileNotFoundException, IOException, InputReaderAndEncoderException{
		final long startTime = System.currentTimeMillis();

		/* Step 1: Load the data and encode it.*/
		InputReaderEncoder encodedDataObj= new InputReaderEncoder(inputFilePath,delimiter,true);
		encodedDataObj.printEncodedTransactions();

		/* Step 2: Initialize Item-set Builder. */
		ItemSetBuilder itemSetBuilder = new ItemSetBuilder(encodedDataObj,minSup);

		/* Step 3: Initialize Rule Builder*/
		RuleBuilder ruleBuilder=new RuleBuilder(encodedDataObj,minConf);

		/* Step 4: Generate candidate-set and frequent-item-sets. Add each frequent item-set to Rule builder*/
		/* Step 4.1: Create frequent 1-items set*/
		HashMap<Set<Float>, Double> frequentOneItemSet = itemSetBuilder.getFrequentOneItemsSet();
		ruleBuilder.addFrequentItemSet(frequentOneItemSet);

		/* Step 4.2: Create frequent 2-items set.*/
		HashMap<Set<Float>, Double> twoItemCandidateSet = itemSetBuilder.getTwoItemsCandidateSet(frequentOneItemSet);
		HashMap<Set<Float>, Double> frequentKItemSet = itemSetBuilder.getFrequentKItemsSet(twoItemCandidateSet);

		int cnt =0;
		/* Step 4.3: Generate candidate,frequent k-items sets until we get empty frequent set.*/
		while(frequentKItemSet.size()!=0){
			cnt =cnt+1;
			System.out.println("iteration:"+cnt);
			ruleBuilder.addFrequentItemSet(frequentKItemSet);

			System.out.println("Generating CS");
			
			/* N.O.T.E: Candidate set pruning is done internally by getKCandidateItemsSet() method.*/
			HashMap<Set<Float>, Double> candidateSet = itemSetBuilder.getKCandidateItemsSet(frequentKItemSet);
			System.out.println("Generating FS");
			frequentKItemSet = itemSetBuilder.getFrequentKItemsSet(candidateSet);
		}

		/* Step 5: Generate all rules.*/
		ruleBuilder.generateRules();

		
		final long endTime = System.currentTimeMillis();
		ruleBuilder.writeRulesAndSummaryToFile(resultFilePath,minSup,(endTime-startTime));
		String rules =ruleBuilder.getRulesAndSummary(resultFilePath,minSup,(endTime-startTime));
		return rules;
	}

	public static void main(String[] argv) {

		
		float minSup = 0;
		float minConf =0;

		String outFilePath = System.getProperty("user.dir")+File.separator+"Rules.txt";
		Scanner reader = new Scanner(System.in);

		/* Read input file path.*/
		System.out.println("Enter the filepath: ");
		String filepath = reader.nextLine(); 


		/* Read minimum support Value.*/
		try{
			System.out.println("Please select the minimum support rate(0.00-1.00):");
			minSup = reader.nextFloat();	
			if((minSup>1) || (minSup<0)){
				System.out.println("ERROR >> Invalid value for support rate! Please retry.");
				reader.close();
				return;
			}
		}catch(Exception e){
			System.out.println("ERROR >> Invalid value for support rate! Please retry.");
			reader.close();
			return;
		}

		/* Read minimum confidence Value.*/
		try{
			System.out.println("Please select the minimum confidence rate(0.00-1.00):");;
			minConf = reader.nextFloat();	
			if((minConf>1) || (minConf<0)){
				System.out.println("ERROR >> Invalid value for confidence! Please retry.");
				reader.close();
				return;
			}
		}catch(Exception e){
			System.out.println("ERROR >> Invalid value for confidence! Please retry.");
			reader.close();
			return;
		}

		reader.close();

		AprioriDriver mainObj= new AprioriDriver();
		try {
			mainObj.RunApriori(filepath,InputDataDelimiters.SPACE,true,minSup,minConf,outFilePath);
		} catch (IOException e) {
			System.out.println("ERROR >> Unable to read/write file.\nCheck file path and permissions.");
			return;
		}catch( InputReaderAndEncoderException ireE){
			System.out.println("ERROR >> "+ireE.getMessage());
			return;
		}
		System.out.println("The result is in the "+outFilePath+" file.");
		System.out.println("\n*** Algorithm Finished ***");


	}

}

