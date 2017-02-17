package Itemsetbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import Inputreader.InputReaderEncoder;
import Interfaces.ItemSetGenerator;

public class ItemSetBuilder implements ItemSetGenerator{

	private float mTxnsCount;
	private float mMinSup;
	private List<Set<Float>> mEncodedTransactions=null;
	private HashMap<Set<Float>, Double> mOneItemSetToSup =null;
	private HashMap<Set<Float>, Double> mOneItemPrunedSetToSup =null;
	private HashMap<Integer,List<Float>> mColHeaderIdxToColsEncodingVals= null;
	private HashMap<Set<Float>, Integer> subsetsMap = new HashMap<Set<Float>, Integer>();


	public ItemSetBuilder(InputReaderEncoder encodedDataObject, float minSup){
		mColHeaderIdxToColsEncodingVals = encodedDataObject.getColHeaderIdxToEncodedDistinctVals();//colHeaderIdxToColsEncodingVals;
		mEncodedTransactions = encodedDataObject.getEncodedTransactions();
		mMinSup = minSup;
		mTxnsCount = mEncodedTransactions.size();
		initializeItemSets();
	}

	public int initializeItemSets(){
		/* Generate 1-item candidate set*/
		HashMap<Set<Float>, Double> ciSet= setOneCandidateItemsSet();
		/* Generate 1-item frequent set*/
		HashMap<Set<Float>, Double> fiSet=setOneFrequentItemsSet();
		if((ciSet!=null)&& (fiSet!=null)){
			return 0;
		}
		return -1;
	}
	
	
	@Override
	public HashMap<Set<Float>, Double> getOneCandidateItemsSet() {
		return mOneItemSetToSup;
	}
	

	@Override
	public HashMap<Set<Float>, Double> getFrequentOneItemsSet() {
		return mOneItemPrunedSetToSup;
	}
	
	
	private Set<Float> generateKPlus1CandidateSet(Set<Float> itemSet1,
			Set<Float> itemSet2,List<Set<Float>> prevFreqSets){
		Set<Float> kPlusOneSet = null;
		
		Set<Float> intersection = new HashSet<Float>(itemSet1);
		
		//System.out.println("\nPre-intersection:"+intersection+" Set2:"+itemSet2);
		intersection.retainAll(itemSet2);
		//System.out.println("Post-intersection:"+intersection);
		
		/* Intersection size will be 2 if both sets have k-1 identical items*/
		if(intersection.size()==itemSet1.size()-1){
			
			/* Check if the intersection leaves with item from the same class.
			 * Note that two k-itemset p and q are joinable iff p and q have k-1 identical 
			 * attribute-value pairs and different attributes in one attribute-value pair.
			 */	
			Set<Float> newItems = new HashSet<Float>(itemSet1);
			newItems.addAll(itemSet2);
			newItems.removeAll(intersection);
			List<Float> newItemsList = new ArrayList<Float>(newItems);
			//System.out.println(newItems);
			
			if(newItemsList.get(0).intValue() != newItemsList.get(1).intValue()){
				kPlusOneSet = new HashSet<Float>();
				kPlusOneSet.addAll(itemSet1);
				kPlusOneSet.removeAll(itemSet2);
				kPlusOneSet.addAll(itemSet2);
			}
		}
		//System.out.println(kPlusOneSet);
		return kPlusOneSet;
	}
	
	/**
	 * Gets the all subsets.
	 *
	 * @param superSetList the super set list
	 * @param subSetSize the sub set size
	 * @return the all subsets
	 */
	private List<Set<Float>> getAllSubsets(List<Float> superSetList, int subSetSize){
		subsetGenerator(superSetList, subSetSize);
		List<Set<Float>>  ret = new ArrayList<Set<Float>>(subsetsMap.keySet());
		subsetsMap.clear();
		return ret;
	}
	
	
	/**
	 * Subset generator.
	 *
	 * @param superSetList the super set list
	 * @param subSetSize the sub set size
	 */
	private void subsetGenerator(List<Float> superSetList, int subSetSize){
		if(superSetList.size() == subSetSize){
			subsetsMap.put(new TreeSet<Float>(superSetList), 0);	
		}
		for(int idx=0; idx<superSetList.size();idx++){
			List<Float> newSuperSetList = new ArrayList<Float>(superSetList);
			newSuperSetList.remove(idx);
			if(!newSuperSetList.isEmpty()){
				subsetGenerator(newSuperSetList, subSetSize);
			}
		}
	}
	
	@Override
	public HashMap<Set<Float>, Double> getKCandidateItemsSet(HashMap<Set<Float>, Double> ipSetMap) {
		HashMap<Set<Float>, Double> opSetMap = new HashMap<Set<Float>, Double>();
		
		List<Set<Float>> unprunedCandidateSets = new ArrayList<Set<Float>>();
		List<Set<Float>> prevFreqSets = new ArrayList<>(ipSetMap.keySet());
		
		/*
		 * For each pair of sets, check if they can be joined.
		 * If join-able get the joined set. 
		 */
		for(int i=0;i<prevFreqSets.size();i++ ){
			Set<Float> itemSet1 = prevFreqSets.get(i);
			for(int j=i+1;j<prevFreqSets.size();j++ ){
				Set<Float> itemSet2 = prevFreqSets.get(j);
				Set<Float> newCKItemSet = generateKPlus1CandidateSet(itemSet1,itemSet2,prevFreqSets);
				if(newCKItemSet != null){
					unprunedCandidateSets.add(newCKItemSet);
				}
			}
		}
		
		
		/*
		 * Prune candidate item-set
		 */
		for(int i=0;i<unprunedCandidateSets.size();i++){
			Set<Float> testSet = unprunedCandidateSets.get(i);
			List<Float> superSetList = new ArrayList<>(testSet);
			List<Set<Float>> subSets= getAllSubsets(superSetList, superSetList.size()-1);
			boolean isSubSetMissing = false;
			
			for(int j=0;j<subSets.size();j++){
				if(prevFreqSets.contains(subSets.get(j))){
					continue;
				}else{
					isSubSetMissing = true;
					break;
				}
			}
			if(!isSubSetMissing){
				opSetMap.put(testSet, getSupportValueForItemSet(testSet));
			}
		}
		return opSetMap;
	}

	@Override
	public HashMap<Set<Float>, Double> getFrequentKItemsSet(HashMap<Set<Float>, Double> kCandidateSet) {
		return getPrunedItemset(kCandidateSet);
	}



	public void printItemSetAndSupport(HashMap<Set<Float>, Double> itemSetToSupport){
		System.out.println("*********ItemSet with Support Values*********");
		for(Set<Float> setAsKey: itemSetToSupport.keySet()){
			System.out.println(setAsKey+" ---> "+itemSetToSupport.get(setAsKey));
		}
	}

	private HashMap<Set<Float>, Double> setOneFrequentItemsSet() {
		mOneItemPrunedSetToSup = new HashMap<Set<Float>, Double> ();
		for(Set<Float> setAsKey: mOneItemSetToSup.keySet()){
			Double supVal = getSupportValueForItemSet(setAsKey);
			float support = (float) (supVal / mTxnsCount);
			if(support >= mMinSup){
				mOneItemPrunedSetToSup.put(setAsKey, supVal);
			}
		}
		return mOneItemPrunedSetToSup;
	}
	
	
	private HashMap<Set<Float>, Double> setOneCandidateItemsSet() {
		mOneItemSetToSup = new HashMap<Set<Float>, Double> ();
		List<Set<Float>> oneItemSet = getOneItemset();
		List<Double> oneItemSupSet = getSupportSet(oneItemSet);
		
		/* Return if mismatch in size of iteSet and its support set*/
		if(oneItemSet.size() != oneItemSupSet.size()){
			System.out.println("ERROR! Mismatch in size of itemSet and its supportSet");
			return null;
		}
		
		/* Build map for itemset and its support vaue*/
		for(int idx=0;idx<oneItemSet.size();idx++){
			mOneItemSetToSup.put(oneItemSet.get(idx), oneItemSupSet.get(idx));
		}
		return mOneItemSetToSup;
	}
	
	private List<Set<Float>> getOneItemset() {
		List<Set<Float>> oneItemsSet = new ArrayList<Set<Float>>();
		for(Integer key:mColHeaderIdxToColsEncodingVals.keySet()){
			List<Float> colDistinctVals = mColHeaderIdxToColsEncodingVals.get(key);
			for(Float oneColVal:colDistinctVals ){
				Set<Float> oneSet = new HashSet<Float>();
				oneSet.add(oneColVal);
				oneItemsSet.add(oneSet);
			}
		}
		return oneItemsSet;
	}


	private HashMap<Set<Float>, Double> getPrunedItemset(HashMap<Set<Float>, Double> ipSetToSupport) {
		HashMap<Set<Float>, Double> opSetToSupport = new HashMap<Set<Float>, Double>();
		for(Set<Float> setAsKey:ipSetToSupport.keySet()){
			Double supVal = ipSetToSupport.get(setAsKey);
			float support = (float) (supVal / mTxnsCount);
			if(support >= mMinSup){
				opSetToSupport.put(setAsKey,supVal);
			}
		}
		return opSetToSupport;
	}

	/**
	 * Gets the support value for item set.
	 *
	 * @param itemSet the item set
	 * @return the support value for item set
	 */
	public double getSupportValueForItemSet(Set<Float> itemSet){
		Double sup = (double) 0;
		for(int idx=0;idx<mEncodedTransactions.size();idx++){			
			Set<Float> txnSet = mEncodedTransactions.get(idx);
			@SuppressWarnings({ })
			Set<Float> intersect = new TreeSet<Float>(txnSet);
			intersect.retainAll(itemSet);
			if(intersect.size() == itemSet.size()){
				sup++;
			}
		}
		return sup;
	}

	private List<Double> getSupportSet(List<Set<Float>> itemSet) {
		List<Double> supSet = new ArrayList<Double>();

		for(int idx = 0; idx<itemSet.size();idx++){
			Set<Float> itSet= itemSet.get(idx);
			supSet.add(getSupportValueForItemSet(itSet));
		}
		return supSet;
	}

	@Override
	public HashMap<Set<Float>, Double> getTwoItemsCandidateSet(HashMap<Set<Float>, Double> oneItemFrequentSet) {
		List<Set<Float>> oneItemSetKeys = new ArrayList<Set<Float>>(oneItemFrequentSet.keySet());
		HashMap<Set<Float>, Double> twoItemCandidateSet = new HashMap<Set<Float>, Double>(); 
		for(int i=0;i<oneItemSetKeys.size();i++){
			for(int j=i+1;j<oneItemSetKeys.size();j++){
				
				Set<Float> set1 = new TreeSet<Float>(oneItemSetKeys.get(i));
				set1.addAll(oneItemSetKeys.get(j));
				List<Float> keyPairs = new ArrayList<Float>(set1);
				if (keyPairs.size()>1){
					if( keyPairs.get(0).intValue() != keyPairs.get(1).intValue()){
						twoItemCandidateSet.put(set1, getSupportValueForItemSet(set1));	
					}	
				}
			}
		}
		return twoItemCandidateSet;
	}

}
