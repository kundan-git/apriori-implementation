package RulesBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Inputreader.InputReaderEncoder;


/**
 * @author kundan kumar
 *
 */
public class RuleBuilder {

	public RuleBuilder(InputReaderEncoder encodedDataObj, float minConfidence){
		mIdxToColName = encodedDataObj.getEncodeHeaderToName();
		mEncodedColsToName = encodedDataObj.getEncodeColsToName();
		mEncodedTransactions= encodedDataObj.getEncodedTransactions();
		mInputFilePath = encodedDataObj.getFilepath();
		mMinConfidence=minConfidence;
		mTotalTxns = mEncodedTransactions.size();
		mAllRules = new ArrayList<Rule>();
		mAllFreqSetToSupport = new HashMap<Set<Float>, Double>();
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

		if(setLength > mMaxSizeOfFreqSet){
			mMaxSizeOfFreqSet = setLength;
		}

		for(Set<Float> aKey: frequentItemSet.keySet()){
			mAllFreqSetToSupport.put(aKey,frequentItemSet.get(aKey));
		}
	}

	private String decodeSet(Set<Float> ipSet){
		String decodedString= "";
		List<Float> asList = new ArrayList<>(ipSet);
		for(int idx=0;idx<asList.size();idx++){
			decodedString=decodedString+"  "+mIdxToColName.get(asList.get(idx).intValue())+
					"="+mEncodedColsToName.get(asList.get(idx));
		}

		return decodedString;
	}


	public void printGeneratedRules(float minSup,long executionTime){
		System.out.println(getRulesAndSummary(minSup,executionTime));
	}

	private String getRulesAndSummary(float minSup,long executionTime){
		String rulesAndSummary = "";

		/* Generate summary as String*/
		rulesAndSummary="************************************************************\n";
		rulesAndSummary=rulesAndSummary+"Association Rules Mining Using Apriori Algorithm\n";
		rulesAndSummary=rulesAndSummary+"************************************************************\n";
		rulesAndSummary=rulesAndSummary+"\nSummary:\n";
		rulesAndSummary=rulesAndSummary+"\tInput File:\t\t"+mInputFilePath+"\n";
		rulesAndSummary=rulesAndSummary+"\tMeasures:  \t\t"+"Support:"+minSup+"\n";
		rulesAndSummary=rulesAndSummary+"\t           \t\t"+"Confidence:"+mMinConfidence+"\n";
		rulesAndSummary=rulesAndSummary+"\tTotal Rows in input:    "+(int)mTotalTxns+"\n";
		rulesAndSummary=rulesAndSummary+"\tTotal Rules discovered: "+mAllRules.size()+"\n";
		rulesAndSummary=rulesAndSummary+"\tProgram Execution Time: "+executionTime+" msec\n";
		rulesAndSummary=rulesAndSummary+"\n************************************************************\n\n";
		rulesAndSummary=rulesAndSummary+"Rules:\n";



		/* Generate rules as String*/
		for(int idx=0;idx<mAllRules.size();idx++){
			Rule aRule = mAllRules.get(idx);
			rulesAndSummary = rulesAndSummary+"\nRule#"+(idx+1)+": "+
					"(Support="+roundToTwoDecimalPlaces(aRule.getSupportVal())+
					" Confidence="+roundToTwoDecimalPlaces(aRule.getConfidenceVal())+")";
			rulesAndSummary = rulesAndSummary+"\n{"+decodeSet(aRule.getCausationSet())+" }";
			rulesAndSummary = rulesAndSummary+"\n\t----> {"+decodeSet(aRule.getEffectSet())+" }\n";

		}	
		return rulesAndSummary;
	}

	public String getRulesAndSummary(String resultFilepath,float minSup,long executionTime){
		String result = getRulesAndSummary(minSup,executionTime);
		return result;
	}

	public void writeRulesAndSummaryToFile(String resultFilepath,float minSup,long executionTime) throws IOException{
		String result = getRulesAndSummary(minSup,executionTime);
		File file = new File(resultFilepath);
		BufferedWriter bufWriter = null;

		bufWriter = new BufferedWriter(new FileWriter(file));
		bufWriter.write(result);
		bufWriter.close();
	}

	public void generateRules(){
		Set<Set<Float>> allFreqSets = mAllFreqSetToSupport.keySet();
		for(int idx=2;idx<(mMaxSizeOfFreqSet+1);idx++){
			for(Set<Float> oneFreqSet: allFreqSets){
				if(oneFreqSet.size() != idx){
					continue;
				}

				int k = oneFreqSet.size();
				double supportCount =  mAllFreqSetToSupport.get(oneFreqSet);
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
							Rule newRule = 
									new Rule((float)(supportCount/mTotalTxns), confidence, causationSet, effectSet);
							mAllRules.add(newRule);
						}
					}
				}
			}
		}
	}


	/**
	 * Gets the all rules.
	 *
	 * @return the all rules
	 */
	public List<Rule> getAllRules() {
		return mAllRules;
	}

	private boolean isCausationSetSubsetOfDiscardedSets(Set<Float> causationSet,
			List<Set<Float>> setsBelowMinConfidence) {
		boolean ret = false;
		for(int idx=0;idx<setsBelowMinConfidence.size();idx++){
			Set<Float> belowMinConfSet = new HashSet<Float>(setsBelowMinConfidence.get(idx));
			belowMinConfSet.retainAll(causationSet);
			if(belowMinConfSet.size() == causationSet.size()){
				ret=true;
				break;
			}
		}
		return ret;
	}


	private String roundToTwoDecimalPlaces(float val)
	{
		return String.format("%.2f", val);
	}

	private Set<Float> getEffectSet(Set<Float> oneFreqSet, Set<Float> causationSet) {
		Set<Float> newOneFreqSet = new HashSet<Float>(oneFreqSet);
		newOneFreqSet.removeAll(causationSet);
		return newOneFreqSet;
	}

	private float getConfidenceValue(Set<Float> causation, double supportCount){
		double causeSupportVal = mAllFreqSetToSupport.get(causation);
		float conf = (float)(supportCount/causeSupportVal);
		return conf;
	} 





	/**
	 * Subset generator.
	 *
	 * @param superSetList the super set list
	 * @param subSetSize the sub set size
	 */
	private void subsetGenerator(List<Float> superSetList, int subSetSize){
		if(superSetList.size() == subSetSize){
			mSubsetsMap.put(new HashSet<Float>(superSetList), 0);	
		}
		for(int idx=0; idx<superSetList.size();idx++){
			List<Float> newSuperSetList = new ArrayList<Float>(superSetList);
			newSuperSetList.remove(idx);
			if(!newSuperSetList.isEmpty()){
				subsetGenerator(newSuperSetList, subSetSize);
			}
		}
	}

	/**
	 * Generate all subsets.
	 *
	 * @param oneFreqSet the one freq set
	 * @param subSetSize the sub set size
	 * @return the list
	 */
	private List<Set<Float>> generateAllSubsets(Set<Float> oneFreqSet, int subSetSize){
		subsetGenerator(new ArrayList<>(oneFreqSet), subSetSize);
		List<Set<Float>>  setPermutations = new ArrayList<Set<Float>>(mSubsetsMap.keySet());
		mSubsetsMap.clear();
		return setPermutations;
	}

	private double mTotalTxns;
	private float mMinConfidence;
	private int mMaxSizeOfFreqSet = 0;
	private List<Rule> mAllRules= null;
	private String mInputFilePath=null;
	private Map<Integer,String> mIdxToColName;
	private Map<Float,String> mEncodedColsToName;
	private List<Set<Float>> mEncodedTransactions;
	private HashMap<Set<Float>, Integer> mSubsetsMap = new HashMap<Set<Float>, Integer>();


	/* Stores frequent sets, ordered by size of the set */
	private HashMap<Set<Float>, Double> mAllFreqSetToSupport = null;

}
