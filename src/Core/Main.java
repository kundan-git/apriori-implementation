package Core;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Inputreader.InputDataDelimiters;
import Inputreader.InputReaderEncoder;
import Itemsetbuilder.ItemSetBuilder;
import RulesBuilder.Rule;
import RulesBuilder.RuleBuilder;
import Exceptions.InputReaderAndEncoderException;

/**
 * The Class Main.
 */
public class Main {

	public static void main(String[] argv) throws FileNotFoundException, IOException, InputReaderAndEncoderException{
		
		String filepath="C:\\Users\\6910P\\Google Drive\\Dalhousie\\term_1\\data_mining\\assignment_3\\Ass3-Demo\\data1";

		/*
		 * Step 1: Load the data and encode it for Apriori algorithm
		 */
		InputReaderEncoder data= new InputReaderEncoder(filepath,InputDataDelimiters.SPACE,true);
		HashMap<Integer,List<Float>> colHeaderIdxToDistEncodedVals = data.getColHeaderIdxToEncodedDistinctVals();
		List<Set<Float>> encodedTransactions = data.getEncodedTransactions();
		Map<Integer,String> headerIdxToName= data.getEncodeHeaderToName();
		Map<Float,String> encodedColsToName= data.getEncodeColsToName();
		data.printHeadersAndUniqueColVals();


		/*
		 * Step 2: Get the 1-ItemSet and its support value
		 */
		ItemSetBuilder itemSetBuilder = 
				new ItemSetBuilder(colHeaderIdxToDistEncodedVals,encodedTransactions,(float)0.2);

		RuleBuilder ruleBuilder =
				new RuleBuilder(headerIdxToName,encodedColsToName,
						colHeaderIdxToDistEncodedVals,encodedTransactions,(float)0.2);

		itemSetBuilder.initializeItemSets();
		HashMap<Set<Float>, Double> oneItemFrequentSet = itemSetBuilder.getOneFrequentItemsSet();
		
		ruleBuilder.addFrequentItemSet(oneItemFrequentSet);
		HashMap<Set<Float>, Double> twoItemCandidateSet = itemSetBuilder.getTwoItemsCandidateSet(oneItemFrequentSet);
		HashMap<Set<Float>, Double> frequentKItemSet = itemSetBuilder.getKFrequentItemsSet(twoItemCandidateSet);

		while(frequentKItemSet.size()!=0){
			ruleBuilder.addFrequentItemSet(frequentKItemSet);
			HashMap<Set<Float>, Double> candidateSet = itemSetBuilder.getKCandidateItemsSet(frequentKItemSet);	
			frequentKItemSet = itemSetBuilder.getKFrequentItemsSet(candidateSet);
		}

		ruleBuilder.generateRules();
		List<Rule>  allRules= ruleBuilder.getAllRules();
		System.out.println("Found "+allRules.size()+" rules\n");
		ruleBuilder.printGeneratedRules();

	}

}

