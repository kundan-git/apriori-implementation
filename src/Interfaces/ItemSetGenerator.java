package Interfaces;

import java.util.HashMap;
import java.util.Set;

public interface ItemSetGenerator {

	
	/**
	 * Gets the one candidate items set.
	 *
	 * @return the one candidate items set
	 */
	public HashMap<Set<Float>,Double> getOneCandidateItemsSet();
	
	
	/**
	 * Gets the one frequent items set.
	 *
	 * @return the one frequent items set
	 */
	public HashMap<Set<Float>,Double> getFrequentOneItemsSet();
	
	/**
	 * Gets the two items candidate set.
	 *
	 * @return the two items candidate set
	 */
	public HashMap<Set<Float>,Double> getTwoItemsCandidateSet(HashMap<Set<Float>, Double> oneItemFrequentSet);
	
	/**
	 * Gets the k candidate items set for k>=2.
	 * 
	 * @return the k candidate items set
	 */
	public HashMap<Set<Float>,Double> getKCandidateItemsSet(HashMap<Set<Float>, Double> ipSetMap);
	
	/**
	 * Gets the k frequent items set for k>=2.
	 *
	 * @return the k frequent items set
	 */
	public HashMap<Set<Float>,Double> getFrequentKItemsSet(HashMap<Set<Float>, Double> kCandidateSet);
	
}
