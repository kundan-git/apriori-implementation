package RulesBuilder;

import java.util.Set;

/**
 * The Class Rule.
 */
public class Rule {

	/**
	 * Instantiates a new rule.
	 *
	 * @param supportVal the support value
	 * @param confidenceVal the confidence value
	 * @param causationSet the causation set
	 * @param effectset the effect-set
	 */
	public Rule(float supportVal,float confidenceVal,Set<Float> causationSet, Set<Float> effectset){
		mSupportVal = supportVal;
		mConfidenceVal = confidenceVal;
		mCausationSet=causationSet;
		mEffectSet=effectset;
	}
	
	/**
	 * Gets the support value.
	 *
	 * @return the support value.
	 */
	public float getSupportVal() {
		return mSupportVal;
	}
	
	/**
	 * Gets the confidence value.
	 *
	 * @return the confidence value.
	 */
	public float getConfidenceVal() {
		return mConfidenceVal;
	}
	
	/**
	 * Gets the causation set.
	 *
	 * @return the causation set
	 */
	public Set<Float> getCausationSet() {
		return mCausationSet;
	}
	
	/**
	 * Gets the effect set.
	 *
	 * @return the effect set
	 */
	public Set<Float> getEffectSet() {
		return mEffectSet;
	}
	
	/** The support value. */
	private float mSupportVal;
	
	/** The confidence value. */
	private float mConfidenceVal;
	
	/** The causation set. */
	private Set<Float> mCausationSet;
	
	/** The effect set. */
	private Set<Float> mEffectSet;
	
}
