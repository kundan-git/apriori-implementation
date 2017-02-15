package Core;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import Inputreader.InputDataDelimiters;
import Inputreader.InputReaderEncoder;
import Itemsetbuilder.ItemSetBuilder;
import Exceptions.InputReaderAndEncoderException;

/**
 * The Class Main.
 */
public class Main {


	public static void main(String[] argv) throws FileNotFoundException, IOException, InputReaderAndEncoderException{


		String filepath="C:\\Users\\6910P\\Google Drive\\Dalhousie\\term_1\\data_mining\\assignment_3\\Ass3-Demo\\data3";

		/*
		 * Step 1: Load the data and encode it for Aprioi algorithm
		 */
		InputReaderEncoder data= new InputReaderEncoder(filepath,InputDataDelimiters.SPACE,true);
		HashMap<Integer,List<Float>> colHeaderIdxToColsEncodingVals = data.getColHeaderIdxToColsEncodingVals();
		List<Set<Float>> encodedTransactions = data.getEncodedTransactions();
		data.printHeadersAndUniqueColVals();
		data.printEncodedTransactions();

		/*
		 * Step 2: Get the 1-ItemSet and its support value
		 */
		ItemSetBuilder itemSetBuilder = new ItemSetBuilder(colHeaderIdxToColsEncodingVals,encodedTransactions,
				(float)0.3, 0);
		itemSetBuilder.initializeItemSets();

		itemSetBuilder.printItemSetAndSupport(itemSetBuilder.getOneCandidateItemsSet());

		System.out.println("\nFrequent Item Set");
		itemSetBuilder.printItemSetAndSupport(itemSetBuilder.getOneFrequentItemsSet());

		System.out.println("\nCandidate Item Set");
		HashMap<Set<Float>, Double> ipSetMap = itemSetBuilder.getOneFrequentItemsSet();

		List<Set<Float>> ipSetKeys = new ArrayList<Set<Float>>(ipSetMap.keySet());

		System.out.println(ipSetKeys);
		HashMap<Set<Float>, Double> twoItemSetMap = new HashMap<Set<Float>, Double>(); 
		for(int i=0;i<ipSetKeys.size();i++){
			for(int j=i+1;j<ipSetKeys.size();j++){
				
				Set<Float> set1 = new TreeSet<Float>(ipSetKeys.get(i));
				set1.addAll(ipSetKeys.get(j));
				List<Float> keyPairs = new ArrayList<Float>(set1);
				if (keyPairs.size()>1){
					if( keyPairs.get(0).intValue() != keyPairs.get(1).intValue()){
						twoItemSetMap.put(set1, itemSetBuilder.getSupportValueForItemSet(set1));	
					}	
				}
			}
		}

		System.out.println("\n\nTwo Items Set");
		for(Set<Float> setKey:twoItemSetMap.keySet()){
			System.out.println(setKey+"-->"+twoItemSetMap.get(setKey));
		}
		
		
		HashMap<Set<Float>, Double> frequentTwoItemSetMap = itemSetBuilder.getKFrequentItemsSet(twoItemSetMap);
		System.out.println("\n\nPruned Two Items Set");
		for(Set<Float> setKey:frequentTwoItemSetMap.keySet()){
			System.out.println(setKey+"-->"+frequentTwoItemSetMap.get(setKey));
		}
		
				
		System.out.println("\n\n\n---------------------------\n\n");
		HashMap<Set<Float>, Double> frequentKItemSet = frequentTwoItemSetMap;
		while(frequentKItemSet.size()!=0){
			HashMap<Set<Float>, Double> candidateSet = itemSetBuilder.getKCandidateItemsSet(frequentKItemSet);	
			System.out.println("\n\nCandidate Set");
			itemSetBuilder.printItemSetAndSupport(candidateSet);
			frequentKItemSet = itemSetBuilder.getKFrequentItemsSet(candidateSet);
			System.out.println("\nFrequent Set");
			itemSetBuilder.printItemSetAndSupport(frequentKItemSet);
			
		}
		
	}

}

