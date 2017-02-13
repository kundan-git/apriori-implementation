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
	
	
	@Override
	public List<Set<Float>> getOneItemset() {
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

	public ItemSetBuilder(HashMap<Integer,List<Float>> colHeaderIdxToColsEncodingVals,
			float minSup,float minConf){
		mColHeaderIdxToColsEncodingVals = colHeaderIdxToColsEncodingVals;
		mMinSup = minSup;
		mMinConf=minConf;
	}
	
	

	/* (non-Javadoc)
	 * @see Interfaces.ItemSetGenerator#getPrunedItemset(java.util.List, java.util.List, float)
	 */
	@Override
	public List<Set<Float>> getPrunedItemset(List<Set<Float>> itemSet, List<Set<Float>> encodedTransactions,
			float minSup) {
		mEncodedTransactions = encodedTransactions;
		double txnsCnt = encodedTransactions.size();
		List<Set<Float>> prunedItemSet = new ArrayList<Set<Float>>();
		for(int idx=0; idx<itemSet.size();idx++){
			Set<Float> item = itemSet.get(idx);
			item.size();
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

	/* (non-Javadoc)
	 * @see Interfaces.ItemSetGenerator#getSupportSet(java.util.List, java.util.List)
	 */
	@Override
	public List<Double> getSupportSet(List<Set<Float>> itemSet, List<Set<Float>> encodedTransactions) {
		List<Double> supSet = new ArrayList<Double>();
		mEncodedTransactions = encodedTransactions;
		for(int idx = 0; idx<itemSet.size();idx++){
			Set<Float> itSet= itemSet.get(idx);
			supSet.add(getSupportValueForItemSet(itSet));
		}
		return supSet;
	}
	
	public void printItemSetAndSupport(List<Set<Float>> itemSet, List<Double> supSet){
		/* Return if mismatch in size of iteSet and its support set*/
		if(itemSet.size() != supSet.size()){
			System.out.println("ERROR! Mismatch in size of itemSet and its supportSet");
			return;
		}
		
		System.out.println("\n************ItemSet with Support Values****************");
		for(int idx=0; idx<itemSet.size();idx++){
			System.out.println(itemSet.get(idx)+" ---> "+supSet.get(idx));
		}
		
	}
	
}
