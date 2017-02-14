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

	//	Map<Set<Float>,Integer> mSetToCount = new HashMap<Set<Float>,Integer>(); 
	//	public Map<Set<Float>,Integer> getSubSets(){
	//		return mSetToCount;
	//	}
	//    public void getSubsets(List<Float> superSetList, int subSetSize)
	//    {
	//        int size = superSetList.size();
	//        for(int i = 0; i < (1<<size); i++)
	//        {
	//        	Set<Float> oneSet = new HashSet<Float>();
	//            for (int j = 0; j < size; j++){
	//                if ((i & (1 << j)) > 0){
	//                	oneSet.add(superSetList.get(j));
	//                }
	//            }
	//            if(oneSet.size() == subSetSize){
	//            	mSetToCount.put(oneSet, 0);
	//            }
	//        }
	//    }


	public static void main(String[] argv) throws FileNotFoundException, IOException, InputReaderAndEncoderException{
		//		Main mainObj = new Main();
		//		
		//		List<Float> superSetList= new ArrayList<Float>();
		//		superSetList.add((float) 1);
		//		superSetList.add((float) 2);
		//		superSetList.add((float) 3);
		//		superSetList.add((float) 4);
		//		
		//		mainObj.getSubsets(superSetList,superSetList.size()-1);
		//		Map<Set<Float>,Integer> subsets = mainObj.getSubSets(); 
		//		System.out.println("Printing Keys..\n");
		//		for(Set<Float> key:subsets.keySet()){
		//			System.out.println(key);
		//		}


		String filepath="C:\\Users\\6910P\\Google Drive\\Dalhousie\\term_1\\data_mining\\assignment_3\\Ass3-Demo\\data1";

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
				(float)0.2, 0);
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
		
		
		HashMap<Set<Float>, Double> prunedTwoItemSetMap = itemSetBuilder.getPrunedItemset(twoItemSetMap);
		System.out.println("\n\nPruned Two Items Set");
		for(Set<Float> setKey:prunedTwoItemSetMap.keySet()){
			System.out.println(setKey+"-->"+prunedTwoItemSetMap.get(setKey));
		}
		
		

		HashMap<Set<Float>, Double> candidateSet = itemSetBuilder.getKCandidateItemsSet(prunedTwoItemSetMap);
		System.out.println("\n\nK+1 Set");
		itemSetBuilder.printItemSetAndSupport(candidateSet);
	}




}

