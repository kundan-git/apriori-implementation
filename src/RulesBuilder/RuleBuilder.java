package RulesBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public void generateRules(){
		System.out.println("Count of freqtent sets:"+allFrequentItemSets.size());
		for(int idx=0;idx<allFrequentItemSets.size();idx++){
			HashMap<Set<Float>, Double> freqSet = allFrequentItemSets.get(idx); 
			List<Set<Float>> setList= generateAllSubsets(freqSet);
		}
		
	}
	
	private List<Set<Float>> generateAllSubsets(HashMap<Set<Float>, Double> freqSet){
		List<Set<Float>> setPermutations = new ArrayList<Set<Float>>();
		return setPermutations;
	}
	
	private float mMinConfidence;
	private Map<Integer,String> mIdxToColName;
	private Map<Float,String> mEncodedColsToName;
	private List<Set<Float>> mEncodedTransactions;
	private Map<Integer,List<Float>> mHeaderIdxToDistEncodedVals;
	
	/* Stores frequent sets, ordered by size of the set */
	private List<HashMap<Set<Float>, Double>> allFrequentItemSets = null;
	private Map<Set<Float>,Set<Float>> mCauseToEffectSet = new HashMap<Set<Float>,Set<Float>>();
	
	
	
	
}
