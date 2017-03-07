Summary:
Implementation of Apriori-Algorithm in Java.

How to Build:
  Step 1: Install Maven
		1.1: Download Maven 'apache-maven-3.3.9-bin.zip' from 
			 https://maven.apache.org/download.cgi
		1.2: Unzip and set 'apache-maven-3.3.9\bin' to PATH.
  Step 2: Run "mvn package" in apriori-implementation directory. This would 
		  generate Apriori.jar in 'apriori-implementation\target\' folder.

How to Run:
	Step 1:	Double click the above generated Apriori.jar
	Step 2: Enter path to data file, minimum support and confidence.
	Step 3: Click on "Generate Rules" button.
	Step 4: It would generate 'Rules.txt' in the same directory. 
			It would also print the rules in the application console.
			
	Note: Acceptable Parameter Values:
	1.	The system can handle input-dataset with either space/comma delimiter.
	2.	File must exist at entered path.
	3.	Support and Confidence values must be between 0 and 1.

Code Overview:
	The program is divided into following submodules(packages):
	+----+------------------+-------------------------------------------------+
	| Sl |	Package			|		Functionality Overview		   			  |
	+----+------------------+-------------------------------------------------+
	| 1  | Interfaces		| Defines interfaces to be implemented by packages|
	+----+------------------+-------------------------------------------------+	
	| 2  | Exceptions		| Defines application specific exceptions.		  |
	+----+------------------+-------------------------------------------------+	
	| 3  | Inputreader		| Reads data from the input file line by line and |
	|	 |					| encodes headers and column values.   			  |
	+----+------------------+-------------------------------------------------+
	| 4  | ItemsetBuilder	| Generates candidateKItemsSet & frequentKItemSets|
	|	 |					| Implements pruning of candidateItemSets		  |
	+----+------------------+-------------------------------------------------+
	| 5  | RulesBuilder		| Generates rules using all frequentKItemSets and |
	|	 |					| configured confidence. Implements pruning for   |
	|	 |					| optimized rule generation.				      |
	+----+------------------+-------------------------------------------------+
	| 6  | Core				| Implements the driver which uses Inputreader	  |
	|	 |					| ItemsetBuilder and RulesBuilder to run Apriori. |
	+----+------------------+-------------------------------------------------+	
	| 7  | Ui				| Implements the UI which uses Core to run Apriori|
	+----+------------------+-------------------------------------------------+	

	