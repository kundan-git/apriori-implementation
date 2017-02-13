package Interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Interface ItemSetGenerator.
 */
public interface ItemSetGenerator {

	/**
	 * Gets the one item-set.
	 *
	 * @return the one item-set
	 */
	public List<Set<Float>> getOneItemset();

	/**
	 * Gets the support set.
	 *
	 * @param itemSet the item set
	 * @param encodedTransactions the encoded transactions
	 * @return the support set
	 */
	public List<Double> getSupportSet(List<Set<Float>> itemSet, List<Set<Float>> encodedTransactions);
	
	/**
	 * Gets the pruned itemset.
	 *
	 * @param itemSet the item set
	 * @param encodedTransactions the encoded transactions
	 * @param minSup the min sup
	 * @return the pruned itemset
	 */
	public List<Set<Float>> getPrunedItemset(List<Set<Float>> itemSet,List<Set<Float>> encodedTransactions,float minSup);

}
