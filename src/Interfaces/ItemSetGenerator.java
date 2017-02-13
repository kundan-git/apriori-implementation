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
	public HashMap<Set<Float>,Double> getOneFrequentItemsSet();
	
	/**
	 * Gets the k candidate items set.
	 *
	 * @return the k candidate items set
	 */
	public HashMap<Set<Float>,Double> getKCandidateItemsSet();
	
	/**
	 * Gets the k frequent items set.
	 *
	 * @return the k frequent items set
	 */
	public HashMap<Set<Float>,Double> getKFrequentItemsSet();
	
}
