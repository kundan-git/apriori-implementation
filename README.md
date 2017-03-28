Summary
==========================================
- Implementation of Apriori-Algorithm in Java.
- Provides Java Swing based user interface. 
- Includes pruning of candidate-set and search pruning for 
  rule gneration (bonus-part).


How to Run
==========================================
- Step 1: Double click the  Apriori.jar
- Step 2: On launched UI, enter path to data file, 
            minimum support and confidence.
- Step 3: Click on "Generate Rules" button.
- Step 4: It would generate 'Rules.txt' in the same directory. 
          It would also print the rules in the application console.
            
    Note: Acceptable Parameter Values:
    1.    The application can handle input-dataset with either space/comma delimiter.
    2.    File must exist at entered path.
    3.    Support and Confidence values must be between 0 and 1.


How to Build
==========================================
- Step 1: Install Maven
  -  Step 1.1: Download Maven 'apache-maven-3.3.9-bin.zip' 
        from https://maven.apache.org/download.cgi
  -  Step 1.2: Unzip and set 'apache-maven-3.3.9\bin' in PATH.
- Step 2: Run "mvn package" in apriori-implementation directory. 
    This would generate Apriori.jar in 'apriori-implementation\target\' folder.
    
Architecture Overview:
==========================================
    InputreaderEncoder
        1.    Load data
        2.    Encode headers and columns

    ItemsetBuilder
        3.    Build candidate1ItemSet
        4.    Build candidate2ItemSet --> Prune candidate set
        5.    Build frequent2ItemSet  --> Prune frequent item set using support
        In while loop, till empty frequent(K|1)ItemSet
            6.    Build candidateKItemSet --> Prune candidate set
            7.  Build frequentKItemSet--> Prune using support .
            
    RuleBuilder
        8.    Generate rule for all frequent item sets. 
            Use pruning for efficient rule generation.
        9.    Write rules to a file.
        

Code Overview:
==========================================
The program is divided into following submodules(packages):

| Sl |    Package  |        Functionality Overvie      |               |
| -- | ----------- | --------------------------------- | ------------- |
| 1  | Interfaces  | Defines interfaces to be implemented by packages  |
| 2  | Exceptions        | Defines application specific exceptions.    |
| 3  | Inputreader        | Reads data from the input file line by line and |
|    |                    | encodes headers and column values.         |
| 4  | ItemsetBuilder    | Generates candidateKItemsSet & frequentKItemSets |
|    |                    | Implements pruning of candidateItemSets          |
| 5  | RulesBuilder        | Generates rules using all frequentKItemSets and |
|    |                    | configured confidence. Implements pruning for   |
|    |                    | optimized rule generation.                      |
| 6  | Core                | Implements the driver which uses Inputreader   |
|    |                    | ItemsetBuilder and RulesBuilder to run Apriori. |
| 7  | Ui                | Implements the UI which uses Core to run Apriori |


Code Details
==========================================
InputReaderEncoder.java: 
            
            Reads input file and encodes input header and column values
            in numbers like 1.1,1.2,2.1,2.2,2.3 etc.    
            
            Public Methods:
            public InputReaderEncoder(String filepath, InputDataDelimiters delimiter, 
                boolean hasHeader) throws FileNotFoundException InputReaderAndEncoderException 
            public String getFilepath() 
            public double getColumnsCount() 
            public String[] getColumnHeaders() 
            public double getTransactionsCount()
            public void printEncodedTransactions()
            public void loadAndEncodeDataFromFile()
            public void printHeadersAndUniqueColVals()
            public List<Set<Float>> getEncodedTransactions()
            public HashMap<Float, String> getEncodeColsToName() 
            public HashMap<Integer, String> getEncodeHeaderToName() 
            public HashMap<Integer, List<String>> getColHeaderIdxToColsUnqVals() 
            public HashMap<Integer, List<Float>> getColHeaderIdxToEncodedDistinctVals() 
            
            Private Methods:
            private void encodeColumns()
            private boolean isValidRow(int tokensCnt)
            private String preProcessLine(String line)
            private void setColHeadersAndDistinctColVals()
            private void generateColumnHeaderAndDistinctValueMap()
            private Float getTokenEncodeValue(int idx, String token) 
            private String getDelimiter(InputDataDelimiters enumDelim)
            private void encodeTransactions() throws FileNotFoundException, IOException
    

    
ItemSetBuilder.java: 
            
            Builds candidate and frequent itemsets. Implements pruning of candidate itemset.
            
            Public Methods:
            public int initializeItemSets()
            public double getSupportValueForItemSet(Set<Float> itemSet)
            public HashMap<Set<Float>, Double> getOneCandidateItemsSet() 
            public HashMap<Set<Float>, Double> getFrequentOneItemsSet() 
            public ItemSetBuilder(InputReaderEncoder encodedDataObject, float minSup)
            public void printItemSetAndSupport(HashMap<Set<Float>, Double> itemSetToSupport)
            public HashMap<Set<Float>, Double> getKCandidateItemsSet(HashMap<Set<Float>, Double> ipSetMap) 
            public HashMap<Set<Float>, Double> getFrequentKItemsSet(HashMap<Set<Float>, Double> kCandidateSet) 
            public HashMap<Set<Float>, Double> getTwoItemsCandidateSet(HashMap<Set<Float>, Double> oneItemFrequentSet) 

            Private Methods:
            private List<Set<Float>> getOneItemset() 
            private List<Double> getSupportSet(List<Set<Float>> itemSet) 
            private HashMap<Set<Float>, Double> setOneFrequentItemsSet() 
            private HashMap<Set<Float>, Double> setOneCandidateItemsSet() 
            private void subsetGenerator(List<Float> superSetList, int subSetSize)
            private List<Set<Float>> getAllSubsets(List<Float> superSetList, int subSetSize)
            private HashMap<Set<Float>, Double> getPrunedItemset(HashMap<Set<Float>, Double> ipSetToSupport) 
            private Set<Float> generateKPlus1CandidateSet(Set<Float> itemSet1,Set<Float> itemSet2,List<Set<Float>> prevFreqSets)
            
RuleBuilder.java: 
            
            Generates rules
            public void generateRules()
            public List<Rule> getAllRules() 
            public void printGeneratedRules(float minSup,long executionTime)
            public RuleBuilder(InputReaderEncoder encodedDataObj, float minConfidence)
            public void addFrequentItemSet(HashMap<Set<Float>, Double> frequentItemSet)
            public String getRulesAndSummary(String resultFilepath,float minSup,long executionTime)
            public void writeRulesAndSummaryToFile(String resultFilepath,float minSup,long executionTime) throws IOException
            
            private String decodeSet(Set<Float> ipSet)
            private String roundToTwoDecimalPlaces(float val)
            private String getRulesAndSummary(float minSup,long executionTime)
            private void subsetGenerator(List<Float> superSetList, int subSetSize)
            private float getConfidenceValue(Set<Float> causation, double supportCount)
            private Set<Float> getEffectSet(Set<Float> oneFreqSet, Set<Float> causationSet) 
            private List<Set<Float>> generateAllSubsets(Set<Float> oneFreqSet, int subSetSize)
            private boolean isCausationSetSubsetOfDiscardedSets(Set<Float> causationSet,List<Set<Float>> setsBelowMinConfidence) 
            
Rule.java:
            
            Class to hold each rule.
            public float getSupportVal() 
            public float getConfidenceVal() 
            public Set<Float> getEffectSet() 
            public Set<Float> getCausationSet() 
            public Rule(float supportVal,float confidenceVal,Set<Float> causationSet, Set<Float> effectset)
            
AprioriDriver.java:
            
            The driver program. Uses exposed interfaces of al modules to run Apriori
            
            public String RunApriori(String inputFilePath,InputDataDelimiters delimiter, boolean hasHeader, 
            float minSup,float minConf,String resultFilePath) 
            
AprioriUi.java:
            
            Implements the UI which uses the Apriori driver to run apriori.
            public static void createFrame()
            public static class AprioriCoreInvoker implements ActionListener
    
BONUS PART EXPLANATION
==========================================
1.    Candidate item-set pruning:
     L3={abc, abd, acd, ace, bcd}, Self-joining: L3*L3
        •abcd from abcand abd
        •acde from acdand ace
        •Pruning:
        •acde is removed because adeis not in L3
        •C4={abcd} 
        *Example taken from tutorial
    Implemented in:
        ItemSetBuilder.java
            --> getKCandidateItemsSet(HashMap<Set<Float>, Double> ipSetMap) : Line 150 to 167
                        
2.    Search pruning for rule generation
    Approach:
        Check if the causation is a subset of any set in the below minimum confidence threshold list sets.
        If yes, skip it as its confidence would be again lesser than confidence threshold.  
    Implemented in:
        RuleBuilder.java:    generateRules(): Line 130 to 157
