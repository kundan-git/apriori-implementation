package Core;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
		
		HashMap<Set<Float>, Double> allFrequentItemSetToSupport = new HashMap<Set<Float>, Double>();  
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
						colHeaderIdxToDistEncodedVals,encodedTransactions,(float)0);

		itemSetBuilder.initializeItemSets();
		HashMap<Set<Float>, Double> oneItemFrequentSet = itemSetBuilder.getOneFrequentItemsSet();
		
		ruleBuilder.addFrequentItemSet(oneItemFrequentSet);
		HashMap<Set<Float>, Double> twoItemCandidateSet = itemSetBuilder.getTwoItemsCandidateSet(oneItemFrequentSet);


		HashMap<Set<Float>, Double> frequentKItemSet = itemSetBuilder.getKFrequentItemsSet(twoItemCandidateSet);

		itemSetBuilder.printItemSetAndSupport(frequentKItemSet);
		while(frequentKItemSet.size()!=0){
			ruleBuilder.addFrequentItemSet(frequentKItemSet);
			HashMap<Set<Float>, Double> candidateSet = itemSetBuilder.getKCandidateItemsSet(frequentKItemSet);	
			frequentKItemSet = itemSetBuilder.getKFrequentItemsSet(candidateSet);
			System.out.println("\n\nCandidate Set");
			itemSetBuilder.printItemSetAndSupport(candidateSet);
			System.out.println("\nFrequent Set");
			itemSetBuilder.printItemSetAndSupport(frequentKItemSet);

		}

		ruleBuilder.generateRules();
		List<Rule>  allRules= ruleBuilder.getAllRules();
		System.out.println("Found "+allRules.size()+" rules\n");



	}

}

