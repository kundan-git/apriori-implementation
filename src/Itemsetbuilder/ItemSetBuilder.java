package Itemsetbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import Interfaces.ItemSetGenerator;

public class ItemSetBuilder implements ItemSetGenerator{

	private float mMinSup;
	private float mMinConf;
	private List<Set<Float>> mEncodedTransactions=null;
	private HashMap<Integer,List<Float>> mColHeaderIdxToColsEncodingVals= null;
	private HashMap<Set<Float>, Double> mOneItemSetToSup =null;
	private HashMap<Set<Float>, Double> mOneItemPrunedSetToSup =null;
	private float mTxnsCount;



	public ItemSetBuilder(HashMap<Integer,List<Float>> colHeaderIdxToColsEncodingVals,
			List<Set<Float>> encodedTransactions, float minSup,float minConf){
		mColHeaderIdxToColsEncodingVals = colHeaderIdxToColsEncodingVals;
		mEncodedTransactions = encodedTransactions;
		mMinSup = minSup;
		mMinConf=minConf;
		mTxnsCount = mEncodedTransactions.size();
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
	public HashMap<Set<Float>, Double> getOneFrequentItemsSet() {
		return mOneItemPrunedSetToSup;
	}
	
	

	@Override
	public HashMap<Set<Float>, Double> getKCandidateItemsSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Set<Float>, Double> getKFrequentItemsSet() {
		// TODO Auto-generated method stub
		return null;
	}



	public void printItemSetAndSupport(HashMap<Set<Float>, Double> itemSetToSupport){
		System.out.println("\n*********ItemSet with Support Values*********");
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





	private List<Set<Float>> getPrunedItemset(List<Set<Float>> itemSet) {
		double txnsCnt = mEncodedTransactions.size();
		List<Set<Float>> prunedItemSet = new ArrayList<Set<Float>>();
		for(int idx=0; idx<itemSet.size();idx++){
			Set<Float> item = itemSet.get(idx);
			//TOOD
		}
		return prunedItemSet;
	}

	/**
	 * Gets the support value for item set.
	 *
	 * @param itemSet the item set
	 * @return the support value for item set
	 */
	@SuppressWarnings("unchecked")
	private double getSupportValueForItemSet(Set<Float> itemSet){
		Double sup = (double) 0;
		for(int idx=0;idx<mEncodedTransactions.size();idx++){			
			Set<Float> txnSet = mEncodedTransactions.get(idx);
			@SuppressWarnings({ "rawtypes" })
			Set intersect = new TreeSet(txnSet);
			intersect.retainAll(itemSet);
			if(intersect.size() >0){
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



}
