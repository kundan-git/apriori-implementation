package RulesBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.util.resources.cldr.sah.CalendarData_sah_RU;

/**
 * @author kundan kumar
 *
 */
public class RuleBuilder {
	public RuleBuilder(Map<Integer,String> idxToColName,
			Map<Float,String> encodedColsToName,
			Map<Integer,List<Float>> headerIdxToDistEncodedVals,
			List<Set<Float>> encodedTransactions, float minConfidence){
		mIdxToColName = idxToColName;
		mEncodedColsToName = encodedColsToName;
		mHeaderIdxToDistEncodedVals = headerIdxToDistEncodedVals;
		mEncodedTransactions= encodedTransactions;
		mMinConfidence=minConfidence;
		allFrequentItemSets = new ArrayList<HashMap<Set<Float>, Double>>();
		mTotalTxns = mEncodedTransactions.size();
		mAllRules = new ArrayList<Rule>(); 
	}

	public void addFrequentItemSet(HashMap<Set<Float>, Double> frequentItemSet){
		int setLength = 0;
		/* Get the size of the keys in map.
		 * As one frequent set contains all keys of same length, 
		 * break after getting the length of the first key*/
		for(Set<Float> keys : frequentItemSet.keySet()){
			setLength = keys.size();
			break;
		}

		allFrequentItemSets.add(setLength,frequentItemSet);
	}


	private void generateRulesForFrequentItemSet(HashMap<Set<Float>, Double> freqSetToSupport){
		// TODO
	}
	
	
	public void generateRules(){
		System.out.println("Count of freqtent sets:"+allFrequentItemSets.size());

		if(allFrequentItemSets.size() < 2){
			//TODO: Check what to return
			return;
		}

		for(int idx=0;idx<allFrequentItemSets.size();idx++){
			HashMap<Set<Float>, Double> allFreqSets = allFrequentItemSets.get(idx);

			for(Set<Float> oneFreqSet: allFreqSets.keySet()){

				int k = oneFreqSet.size();
				double supportCount =  allFreqSets.get(oneFreqSet);
				List<Set<Float>> belowConfSets = new ArrayList<Set<Float>>();
				while(k>1){
					k = k-1;
					List<Set<Float>> kMinus1Sets= generateAllSubsets(oneFreqSet,k);
					for(int cnt=0;cnt<kMinus1Sets.size();cnt++){
						/* Get the causation and effect sets*/
						Set<Float> causationSet = kMinus1Sets.get(cnt); 
						Set<Float> effectSet = getEffectSet(oneFreqSet,causationSet);

						/* Step 1: P.R.U.N.I.N.G
						 * Check if the causation is a subset of any set
						 * in the below minimum confidence threshold list 
						 * sets. If yes, continue as its confidence would 
						 * be again lesser than confidence threshold.  
						 * */
						if(isCausationSetSubsetOfDiscardedSets(causationSet,belowConfSets)){
							continue;
						}

						/* Step 2: Get the confidence of the causation, effect pair*/
						float confidence = getConfidenceValue(causationSet,supportCount);

						/* If below confidence threshold add to below threshold 
						 * list else create a Rule Object.*/
						if(confidence < mMinConfidence){
							belowConfSets.add(causationSet);
						}else{
							Rule newRule = new Rule((float)(supportCount/mTotalTxns), confidence, causationSet, effectSet);
							mAllRules.add(newRule);
						}
					}
				}
			}
		}
	}

	private boolean isCausationSetSubsetOfDiscardedSets(Set<Float> causationSet,
			List<Set<Float>> setsBelowMinConfidence) {
		boolean ret = false;
		for(int idx=0;idx<setsBelowMinConfidence.size();idx++){
			if(setsBelowMinConfidence.get(idx).contains(causationSet)){
				ret=true;
				break;
			}
		}
		return false;
	}


	private Set<Float> getEffectSet(Set<Float> oneFreqSet, Set<Float> causationSet) {
		oneFreqSet.removeAll(causationSet);
		return oneFreqSet;
	}

	private float getConfidenceValue(Set<Float> causation, double supportCount){
		//TODO
		float conf = (float)0;
		return conf;
	} 


	private List<Set<Float>> generateAllSubsets(Set<Float> oneFreqSet, int subSetSize){
		// TODO
		List<Set<Float>> setPermutations = new ArrayList<Set<Float>>();
		return setPermutations;
	}

	private float mMinConfidence;
	private List<Rule> mAllRules= null;
	private Map<Integer,String> mIdxToColName;
	private Map<Float,String> mEncodedColsToName;
	private List<Set<Float>> mEncodedTransactions;
	private Map<Integer,List<Float>> mHeaderIdxToDistEncodedVals;
	private double mTotalTxns;

	/* Stores frequent sets, ordered by size of the set */
	private List<HashMap<Set<Float>, Double>> allFrequentItemSets = null;
	private Map<Set<Float>,Set<Float>> mCauseToEffectSet = new HashMap<Set<Float>,Set<Float>>();




}
