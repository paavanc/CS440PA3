import java.io.File; 
import java.io.IOException; 
import java.io.FileReader; 
import java.io.BufferedReader; 
import java.util.Arrays; 
import java.io.*; 
import java.util.*; 
import java.text.DecimalFormat; 
import java.util.Collections; 

public class CS440PA3 
{ 
  
  public static void main(String[] args) throws IOException { 
    
    System.out.println("Enter in the format:");
    System.out.println("Recognize:  ./recognize hmm_file obs_file");
    System.out.println("Statepath:  ./statepath hmm_file obs_file");
    System.out.println("Optimize:  ./optimize hmm_file obs_file new_hmm_file");
    System.out.println("To quit, type in quotes: "+ "quit or q");
    
    
    Scanner console = new Scanner(System.in);   // user inputs in command line
    String input[] = console.nextLine().split(" "); //parses the input into array of string, each index contains the words passed
    
    while(!input[0].equalsIgnoreCase("q") && !input[0].equalsIgnoreCase("quit")){
      
      try
      {
        
        // If the user selects the 'recognize' function (forward algorithm)
        if(input[0].equalsIgnoreCase("./recognize")){
          if(input.length != 3){
            System.out.println("Incorrect number of arguments for recognize. \nPlease use format:  ./recognize hmm_file obs_file ");
          }
          else{
            patternRecognition(input[1], input[2]);
            System.out.println();
          }
        }
        else if(input[0].equalsIgnoreCase("./statepath")){
          if(input.length != 3){
            System.out.println("Incorrect number of arguments for statepath. \nPlease use format:  ./statepath hmm_file obs_file ");
          }
          else{
            statePathDetermination(input[1], input[2]);
          }
        }
        else if(input[0].equalsIgnoreCase("./optimize")){
          if(input.length != 4){
            System.out.println("Incorrect number of arguments for optimize. \nPlease use format:  ./optimize hmm_file obs_file new_hmm_file ");
          }
          
          FileReader readobs = new FileReader(input[2]);
          BufferedReader obslines = new BufferedReader(readobs);
          
          String tempNumberOfDataSets = obslines.readLine();
          int dataset = Integer.parseInt(tempNumberOfDataSets);
          
          if(dataset!=1){
            System.out.println("Incorrect number of datasets, please try again");
          }
          
          else{
            
            modelOptimization(input[1], input[2], input[3]); // Apply the Baum-Welch algorithm 
          }
        }
        else{
          System.out.println("\nIncorrect command. Please use format:");
          System.out.println("Recognize:  ./recognize hmm_file obs_file");
          System.out.println("Statepath:  ./statepath hmm_file obs_file");
          System.out.println("Optimize:  ./optimize hmm_file obs_file new_hmm_file");
          System.out.println();
        }
      }
      catch(IOException e){
        System.out.println("File(s) not found");
      }
      
      // Print out username like a real command prompt
      
      input = console.nextLine().split(" ");
    }  
    
    System.out.println("HMM WILL MISS YOU :CCCCCC");
    
    
    
    
    // patternRecognition("sentence.hmm", "example1.obs"); 
    //statePathDetermination("sentence.hmm", "example1.obs"); 
    // modelOptimization("sentence.hmm", "example2.obs","sentence-opti.hmm"); 
    
  } 
  
  
  public static void patternRecognition(String fileOne, String fileTwo) throws IOException{ 
    //READS HMM FILE 
    FileReader hmm = new FileReader(fileOne);  //reads hmm file
    BufferedReader linesHmm = new BufferedReader(hmm); 
    String tempLine = linesHmm.readLine();  //reads the next line
    //System.out.println(states); 
    String [] lineOneArray = tempLine.split("\\s+");    //splits first line into array of string
    int [] states = new int[lineOneArray.length]; 
    for (int i =0; i<lineOneArray.length; i++){ 
      states[i]= Integer.parseInt(lineOneArray[i]); 
    } 
    int numberOfStates = states[0]; //gets the number of states 
    int numberOfSymbols = states[1];  //gets the number of symbols
    int numberOfTimeSteps = states[2];  //gets the time step
    tempLine=linesHmm.readLine(); 
    String [] english = tempLine.split("\\s+"); 
    //System.out.println(Arrays.toString(english)); 
    tempLine=linesHmm.readLine(); 
    String [] sentence = tempLine.split("\\s+"); 
    // System.out.println(Arrays.toString(sentence)); 
    tempLine=linesHmm.readLine(); 
    String [] [] tempA= new String [numberOfStates][numberOfStates]; 
    for (int i =0; i<numberOfStates; i++){ 
      tempLine=linesHmm.readLine(); 
      tempA[i]= tempLine.split("\\s+"); 
    } 
    
    Double [] [] a = new Double [numberOfStates][numberOfStates];  //transition matrix probabilities
    
    for (int row=0; row < numberOfStates; row++){ 
      for (int col=0; col < numberOfStates; col++){ 
        a[row][col] = Double.parseDouble(tempA[row][col]); 
      } 
    } 
    
    tempLine=linesHmm.readLine(); 
    
    String [] [] tempB= new String [numberOfStates][numberOfSymbols]; 
    for (int i =0; i<numberOfStates; i++){ 
      tempLine=linesHmm.readLine(); 
      tempB[i]= tempLine.split("\\s+"); 
      
    } 
    
    Double [][] b = new Double [numberOfStates][numberOfSymbols];  //observation probabilitiy matrix
    
    for (int row=0; row < numberOfStates; row++){ 
      for (int col=0; col < numberOfSymbols; col++){ 
        b[row][col] = Double.parseDouble(tempB[row][col]); 
      } 
    } 
    
    tempLine=linesHmm.readLine(); 
    tempLine=linesHmm.readLine(); 
    String [] piTemp = tempLine.split("\\s+"); 
    
    Double [] pi = new Double[piTemp.length]; //intial state probabilities
    for (int i =0; i<pi.length; i++){ 
      pi[i]= Double.parseDouble(piTemp[i]); 
      
    } 
    // System.out.println(Arrays.toString(pi));       
    //System.out.println(Arrays.deepToString(b)); 
    
    
    //READS OBJECT FILE 
    
    FileReader obj = new FileReader(fileTwo); 
    BufferedReader linesObj = new BufferedReader(obj); 
    
    String tempNumberOfDataSets = linesObj.readLine(); 
    int numberOfDataSets = Integer.parseInt(tempNumberOfDataSets); 
    
    
    //System.out.println(numberOfDataSets); 
    int counter= 2*numberOfDataSets; 
    int tracker=0; 
    int tracker2=0; 
    int [] numberOfWords= new int [numberOfDataSets]; 
    String [] listOfWords= new String[numberOfDataSets]; 
    for (int i=0; i<counter; i++){ 
      
      if (i%2==0){ 
        numberOfWords[tracker]=Integer.parseInt(linesObj.readLine()); 
        tracker++; 
      } 
      if (i%2==1){ 
        listOfWords[tracker2]=linesObj.readLine(); 
        tracker2++; 
      } 
      
    } 
    
    // System.out.println(Arrays.toString(numberOfWords)); 
    //System.out.println(Arrays.toString(listOfWords)); 
    
    //Start applying formula 
    
    for (int i = 0; i<numberOfWords.length; i++){  //loops from 0 to the number of words
      int lengthOfSequence = 0; 
      if (numberOfWords[i]>=numberOfTimeSteps){ 
        lengthOfSequence=numberOfWords[i]; 
      } 
      else { 
        lengthOfSequence=numberOfWords[i]; 
      } 
      
      forwardRecursion(numberOfWords[i], listOfWords[i], sentence , lengthOfSequence, numberOfStates, a,b, pi ); //applies the forward algorithm
    } 
  } 
  
  //function that returns probability of each obvserved sequence
  public static void forwardRecursion(int numbderOfWords, String listOfWords, String [] sentence,int lengthOfSequence,  int numberOfStates, Double [][] a, Double [][]b, Double [] pi){ 
    String [] observedSentence= listOfWords.split("\\s+"); //parses the sentence into an array of strings, each index stores each word
    double probabilities [] [] =new double [numberOfStates][lengthOfSequence +1]; 
    //t1 -initialize probability array
    for(int i=0; i<numberOfStates; i++){ 
      probabilities[i][0] =  pi[i] * helper(sentence, i, observedSentence[0], b[i]); 
    } 
    //System.out.println(Arrays.deepToString(probabilities)); 
    
//t2 -- Carry out function, apply alphas and update probability matrix
    
    //System.out.println(Arrays.deepToString(a)); 
    for(int i=1; i<numbderOfWords; i++){ 
      for(int j=0; j<numberOfStates; j++){ 
        
        double total = 0; 
        for(int z=0; z<numberOfStates; z++){ 
          total = total + probabilities[z][i-1] * a[z][j]; 
        } 
        //update probability matrix with total and observational probabilities
        probabilities[j][i] = helper( sentence, j, observedSentence[i], b[j]) *total ;  
        
      } 
    } 
    //System.out.println(Arrays.deepToString(probabilities)); 
    // System.out.println(lengthOfSequence);
    double finalProbability= 0; 

    
    
    //Calculate final probability and print it out
    for (int i=0; i< numberOfStates; i++){ 
      //System.out.println(i);
      finalProbability= finalProbability + probabilities[i][lengthOfSequence -1 ]; 
      //System.out.println(probabilities[i][lengthOfSequence -1 ]);
    } 
    
    DecimalFormat niceFormat = new DecimalFormat("#.######");
    System.out.print(niceFormat.format(finalProbability)+ " "); 
    
    
    System.out.println(); 
    
  } 
  
  
  //function that finds the obervational probabilty based on the oberserved word and the hmm sentence
  public static double helper( String[] sentence,int i, String observedSentence,java.lang.Double[] b) { 
    for (int j = 0; j < sentence.length; j++) { 
      if (observedSentence.equals(sentence[j])) {    
        return b[j]; 
      } 
    } 
    return 0; 
  } 
  
  
  public static void statePathDetermination(String fileOne, String fileTwo) throws IOException{ 
    //READS HMM FILE 
    
    FileReader hmm = new FileReader(fileOne); 
    BufferedReader linesHmm = new BufferedReader(hmm); 
    
    String tempLine = linesHmm.readLine(); 
    //System.out.println(states); 
    String [] lineOneArray = tempLine.split("\\s+"); 
    int [] states = new int[lineOneArray.length]; 
    for (int i =0; i<lineOneArray.length; i++){ 
      states[i]= Integer.parseInt(lineOneArray[i]); 
      
    } 
    
    int numberOfStates = states[0]; 
    int numberOfSymbols = states[1]; 
    int numberOfTimeSteps = states[2]; 
    
    tempLine=linesHmm.readLine(); 
    String [] english = tempLine.split("\\s+"); 
    
    //System.out.println(Arrays.toString(english)); 
    tempLine=linesHmm.readLine(); 
    String [] sentence = tempLine.split("\\s+"); 
    //System.out.println(Arrays.toString(sentence)); 
    
    tempLine=linesHmm.readLine(); 
    String [] [] tempA= new String [numberOfStates][numberOfStates]; 
    for (int i =0; i<numberOfStates; i++){ 
      tempLine=linesHmm.readLine(); 
      tempA[i]= tempLine.split("\\s+"); 
      
    } 
    
    Double [][] a = new Double [numberOfStates][numberOfStates];  
    
    for (int row=0; row < numberOfStates; row++) 
    { 
      for (int col=0; col < numberOfStates; col++) 
      { 
        a[row][col] = Double.parseDouble(tempA[row][col]); 
        
      } 
    } 
    
    tempLine=linesHmm.readLine(); 
    
    String [][] tempB= new String [numberOfStates][numberOfSymbols]; 
    for (int i =0; i<numberOfStates; i++){ 
      tempLine=linesHmm.readLine(); 
      tempB[i]= tempLine.split("\\s+"); 
      
    } 
    
    Double [][] b = new Double [numberOfStates][numberOfSymbols];  
    
    for (int row=0; row < numberOfStates; row++) 
    { 
      for (int col=0; col < numberOfSymbols; col++) 
      { 
        b[row][col] = Double.parseDouble(tempB[row][col]); 
        
      } 
    } 
    
    tempLine=linesHmm.readLine(); 
    tempLine=linesHmm.readLine(); 
    String [] piTemp = tempLine.split("\\s+"); 
    
    Double [] pi = new Double[piTemp.length]; 
    for (int i =0; i<pi.length; i++){ 
      pi[i]= Double.parseDouble(piTemp[i]); 
      
    } 
    //System.out.println(Arrays.toString(pi));       
    //System.out.println(Arrays.deepToString(b)); 
    
    
    //READS OBJECT FILE 
    FileReader obj = new FileReader(fileTwo); 
    BufferedReader linesObj = new BufferedReader(obj); 
    
    String tempNumberOfDataSets = linesObj.readLine(); 
    int numberOfDataSets = Integer.parseInt(tempNumberOfDataSets); 
    
    
    //System.out.println(numberOfDataSets); 
    int counter= 2*numberOfDataSets; 
    int tracker=0; 
    int tracker2=0; 
    int [] numberOfWords= new int [numberOfDataSets]; 
    String [] listOfWords= new String[numberOfDataSets]; 
    for (int i=0; i<counter; i++){ 
      
      if (i%2==0){ 
        numberOfWords[tracker]=Integer.parseInt(linesObj.readLine()); 
        tracker++; 
      } 
      if (i%2==1){ 
        listOfWords[tracker2]=linesObj.readLine(); 
        tracker2++; 
      } 
      
    } 
    
    //System.out.println("statePathDetermination"); 
    for (int i = 0; i<numberOfWords.length; i++){ 
      viterbiAlgorithm(numberOfWords[i], listOfWords[i], sentence , numberOfWords[i], numberOfStates, a,b, pi, english ); 
    } 
  } 
  
  
  //Finds the optimal path for each data set and the chance of reaching that path
  public static void viterbiAlgorithm(int numberOfWords, String listOfWords, String [] sentence,int lengthOfSequence,  int numberOfStates, Double [][] a, Double [][]b, Double [] pi, String [] english){ 
    
    //parse observed sentence into individual words
    String[] obvwords=listOfWords.split("\\s+"); 
    
    //fill up begin matrix (probability matrix) with intial  values
    double begin[][]= new double[lengthOfSequence+1][numberOfStates]; 
    
    for(int i=0; i<numberOfStates;i++){ 
      begin[0][i]= pi[i]* helper(sentence, i, obvwords[0],b[i]);  
    } 
    
    // System.out.println("1st loops"); 
    // System.out.println(Arrays.deepToString(begin)); 
    
    for( int i=0; i<numberOfStates;i++){ 
      begin[1][i]= begin[0][i]*helper(sentence, i, obvwords[0],b[i]);  
      
    } 
    
    //System.out.println("2nd loops"); 
    //System.out.println(Arrays.deepToString(begin)); 
    //System.out.println(Arrays.toString(sentence)); 
    //System.out.println(numberOfStates); 
    
    
    //We calcuate the maximum path or the path with the highest probability
    for (int i=0; i< lengthOfSequence -1; i++){ 
      double[] max =new double[numberOfStates]; 
      for (int j=0; j<numberOfStates; j++){ 
        for (int z=0; z<numberOfStates; z++){ 
          max[z]= begin[i][z]*helper(sentence, j, obvwords[i+1], b[j])*a[z][j]; 
          // System.out.println(begin[i][z]); 
          //System.out.println(Arrays.toString(max)); 
        } 
        double maxInt=getMax(max); 
        //System.out.println("The maximum is = "+ maxInt); 
        /// System.out.println("Index is = "+locationOfMax); 
        //System.out.println(Arrays.toString(max)); 
        begin[i+1][j]=maxInt; 
      } 
    } 
    
    //Determine the bestPath and most likely paths based on observations
    double bestPath[] = new double[lengthOfSequence+1];   
    int mostLiklyPath[] = new int[lengthOfSequence+1]; 
    
    for(int i=0; i<lengthOfSequence; i++){  
      
      bestPath[i] = begin[i][0];  
      mostLiklyPath[i] = 0;  
      
      for(int j=0; j<numberOfStates; j++){  
        if(begin[i][j] > bestPath[i]){  
          bestPath[i] = begin[i][j];  
          mostLiklyPath[i] = j;  
        }  
      }  
    } 
    //Print out chose path and probability
    DecimalFormat niceFormat = new DecimalFormat("#.######"); 
    System.out.print(niceFormat.format(bestPath[lengthOfSequence-1]) + " ");  
    
    if(bestPath[lengthOfSequence -1] > 0.0){  
      for(int i=0; i<lengthOfSequence; i++){  
        System.out.print(english[mostLiklyPath[i]] + " ");  
      }  
    }  
    System.out.println();  
    
  } 
  
  
  //Finds the maximum element in an array
  public static Double getMax(double [] array){ 
    double max=0.0; 
    for(int i=0; i<array.length;i++){ 
      if(array[i]>max){ 
        max=array[i]; 
      } 
    } 
    return max; 
  } 
  
  
  
  
  
  //optimizes our hmm file, creating a new one based on our observed file
  //only carries out one iteration
  public static void modelOptimization(String fileOne, String fileTwo, String fileThree)throws IOException{ 
    //READS HMM FILE 
    
    FileReader hmm = new FileReader(fileOne); 
    BufferedReader linesHmm = new BufferedReader(hmm); 
    
    String tempLine = linesHmm.readLine(); 
    //System.out.println(states); 
    String [] lineOneArray = tempLine.split("\\s+"); 
    int [] states = new int[lineOneArray.length]; 
    for (int i =0; i<lineOneArray.length; i++){ 
      states[i]= Integer.parseInt(lineOneArray[i]); 
      
    } 
    
    int numberOfStates = states[0]; 
    int numberOfSymbols = states[1]; 
    int numberOfTimeSteps = states[2]; 
    
    tempLine=linesHmm.readLine(); 
    String [] english = tempLine.split("\\s+"); 
    
    //System.out.println(Arrays.toString(english)); 
    tempLine=linesHmm.readLine(); 
    String [] sentence = tempLine.split("\\s+"); 
    //System.out.println(Arrays.toString(sentence)); 
    
    tempLine=linesHmm.readLine(); 
    String [] [] tempA= new String [numberOfStates][numberOfStates]; 
    for (int i =0; i<numberOfStates; i++){ 
      tempLine=linesHmm.readLine(); 
      tempA[i]= tempLine.split("\\s+"); 
      
    } 
    
    Double [][] a = new Double [numberOfStates][numberOfStates];  
    
    for (int row=0; row < numberOfStates; row++) 
    { 
      for (int col=0; col < numberOfStates; col++) 
      { 
        a[row][col] = Double.parseDouble(tempA[row][col]); 
        
      } 
    } 
    
    tempLine=linesHmm.readLine(); 
    
    String [][] tempB= new String [numberOfStates][numberOfSymbols]; 
    for (int i =0; i<numberOfStates; i++){ 
      tempLine=linesHmm.readLine(); 
      tempB[i]= tempLine.split("\\s+"); 
      
    } 
    
    Double [][] b = new Double [numberOfStates][numberOfSymbols];  
    
    for (int row=0; row < numberOfStates; row++) 
    { 
      for (int col=0; col < numberOfSymbols; col++) 
      { 
        b[row][col] = Double.parseDouble(tempB[row][col]); 
        
      } 
    } 
    
    tempLine=linesHmm.readLine(); 
    tempLine=linesHmm.readLine(); 
    String [] piTemp = tempLine.split("\\s+"); 
    
    Double [] pi = new Double[piTemp.length]; 
    for (int i =0; i<pi.length; i++){ 
      pi[i]= Double.parseDouble(piTemp[i]); 
      
    } 
    //System.out.println(Arrays.toString(pi));       
    //System.out.println(Arrays.deepToString(b)); 
    
    
    
    
    //READS OBJECT FILE 
    FileReader obj = new FileReader(fileTwo); 
    BufferedReader linesObj = new BufferedReader(obj); 
    
    String tempNumberOfDataSets = linesObj.readLine(); 
    int numberOfDataSets = Integer.parseInt(tempNumberOfDataSets); 
    
    
    //System.out.println(numberOfDataSets); 
    int counter= 2*numberOfDataSets; 
    int tracker=0; 
    int tracker2=0; 
    int [] numberOfWords= new int [numberOfDataSets]; 
    String [] listOfWords= new String[numberOfDataSets]; 
    for (int i=0; i<counter; i++){ 
      
      if (i%2==0){ 
        numberOfWords[tracker]=Integer.parseInt(linesObj.readLine()); 
        tracker++; 
      } 
      if (i%2==1){ 
        listOfWords[tracker2]=linesObj.readLine(); 
        tracker2++; 
      } 
      
    } 
    
    //System.out.println("modelOptimization"); 
    for (int i = 0; i<numberOfWords.length; i++){ 
      //start algorythm
      BaumWelch( numberOfSymbols,numberOfWords[i], listOfWords[i], sentence , numberOfWords[i], numberOfStates, a,b, pi, english, fileThree, fileTwo ); 
      
    } 
    
    
    
  } 
  
  //creats optimized hmm file based on series of inputs listed above
  public static void BaumWelch( int numberOfSymbols, int numberOfWords, String listOfWords, String [] sentence,int lengthOfSequence,  int numberOfStates, Double [][] a, Double [][]b, Double [] pi, String [] english, String fileThree, String fileTwo) throws IOException{ 
    String [] observedSentence= listOfWords.split("\\s+"); 
    
    //start creating hmm file
    BufferedWriter output = new BufferedWriter(new FileWriter(fileThree));
    //Write out the number of states, vocab length and observation count 
    output.write(String.format("%d %d %d\n", numberOfStates, numberOfSymbols, observedSentence.length)); 
    for (int i = 0; i < english.length; i++){
      output.write(english[i] + " "); //Write the names of each HMM state
    }
    output.write("\n");
    for (int i = 0; i < sentence.length; i++){
      output.write(sentence[i] + " ");  //Write the vocab words
    }
    output.write("\n");
    output.write("a:\n");
    
    
    
    //declare probabilities matrix 
    
    double probabilities [] [] =new double [numberOfStates][lengthOfSequence +1]; 
    
    
//t1  -initialize
    for(int i=0; i<numberOfStates; i++){ 
      probabilities[i][0] =  pi[i] * helper(sentence, i, observedSentence[0], b[i]); 
    } 
    // System.out.println(Arrays.deepToString(probabilities)); 
    
//t2   forward algorithym
    for(int i=1; i<numberOfWords; i++){ 
      for(int j=0; j<numberOfStates; j++){ 
        
        double total = 0; 
        for(int z=0; z<numberOfStates; z++){ 
          total = total + probabilities[z][i-1] * a[z][j]; 
        } 
        probabilities[j][i] =  helper( sentence, j, observedSentence[i], b[j])*total;  
        
      } 
    } 
    // System.out.println(Arrays.deepToString(probabilities)); 
    
    //print probaility before optimization
    double finalProbability= 0; 
    
    for (int i=0; i< numberOfStates; i++){ 
      finalProbability= finalProbability + probabilities[i][lengthOfSequence -1 ]; 
    } 
    DecimalFormat niceFormat = new DecimalFormat("#.######");
    System.out.print(niceFormat.format(finalProbability)+ " "); 
    
    

    
    //get back probabilities
    
    double backProbabilities[][] = new double[numberOfStates][lengthOfSequence+1]; 
    //initiliaze
    for(int state=0; state<numberOfStates; state++){ 
      backProbabilities[state][lengthOfSequence-1] = 1; 
    } 
    
    // System.out.println(Arrays.deepToString(backProbabilities)); 
    
    //update backProbabilities matrix 
    for(int i=lengthOfSequence-2; i>=0; i--){ 
      for(int j=0; j<numberOfStates; j++){ 
        double count = 0.0; 
        for(int z=0; z<numberOfStates; z++){ 
          //get variables for update
          double aij = a[j][z]; 
          
          double betaj = backProbabilities[z][i+1]; 
          
          double bj = helper(sentence, z, observedSentence[i+1],b[z]); 
          
          // System.out.println(bj); 
          
          count += aij * bj * betaj;  
        } 
        backProbabilities[j][i] = count; 
      } 
    }    
    
    //System.out.println(Arrays.deepToString(probabilities));
    
    //declare and update gamma matrix
    double gammaMatrix[][] = new double[numberOfStates][lengthOfSequence+1]; 
    for (int i = 0; i < lengthOfSequence+1; i++) {
      double count = 0; //not count is pr(o/lambda)
      for (int j = 0; j < numberOfStates; j++) {
        count += probabilities[j][i]*backProbabilities[j][i]; 
      }
      //get gammamatrix using count and aij/bij
      for (int z = 0; z < numberOfStates; z++) {    
        gammaMatrix[z][i] = (probabilities[z][i]*backProbabilities[z][i])/count; 
        
      }     
    }
    //System.out.println(Arrays.deepToString(gammaMatrix));
    
    
 //get xi state for each
    double[][][] xi = new double[lengthOfSequence-1][numberOfStates][numberOfStates]; 
    
    for (int x = 0; x < lengthOfSequence-1; x++) {
      double count = 0; 
      for (int y = 0; y < numberOfStates; y++) {
        for (int z = 0; z < numberOfStates; z++) {
          
          //update count based on aij, bj variables
          double bj = helper(sentence, z, observedSentence[x+1],b[z]); 
          double aij = a[y][z]; 
          
          
          
          double betaj = backProbabilities[z][x+1]; 
          
          count += bj*betaj*probabilities[y][x]*aij;
        }
      }
      for (int e = 0; e < numberOfStates; e++) {
        for (int f = 0; f < numberOfStates; f++) {
          
          //get xi based on variables
          
          double aij = a[e][f]; 
          double betaj = backProbabilities[f][x+1];
          
          double bj = helper(sentence, f, observedSentence[x+1],b[f]); 
          
           
          xi[x][e][f] = (aij*probabilities[e][x]*bj*betaj)/count;  
        }
      }
    }
    
    
    //System.out.println(Arrays.deepToString(xi)); 
    
    //Get updated a matrix---YAAY
    
    double[][] aOutput = new double[numberOfStates][numberOfStates]; 
    for (int x = 0; x < numberOfStates; x++) {
      double countg = 0;
      for (int y = 0; y < lengthOfSequence-1; y++) {
        //sum for gamma
        countg += gammaMatrix[x][y]; 
      }
      for (int z = 0; z < numberOfStates; z++) {
        double countxi = 0;
        for (int d = 0; d < lengthOfSequence-1; d++) {
          //sum for xi
          countxi += xi[d][x][z]; 
        }
        //update a matrix if state stat has changed
        if (countg == 0.0) {
          aOutput[x][z] = a[x][z]; 
        } else {
          //where summations come into play
          aOutput[x][z] = (countxi/countg);  
        }
      }
    }
    
    //System.out.println(Arrays.deepToString(aOutput)); 
    
    //preprocess new matrix for placement in file
    String [][]  atextOutput= new String [numberOfStates][numberOfStates];
    for (int i=0; i< aOutput.length; i++){
      for (int j=0; j<aOutput[i].length; j++){
        atextOutput[i][j]=niceFormat.format(aOutput[i][j]);
      }
      
    }
    //place a matrix in file
    for (int i = 0; i < atextOutput.length; i++) {
      for (int j = 0; j < atextOutput[i].length; j++) {
        output.write(String.format("%.6f ",Double.parseDouble(atextOutput[i][j]))); //Write the newly calculated As
      }
      output.write("\n");
    }
    output.write("b:\n");
    
    //get new b matrix
    double[][] bOutput = new double[numberOfStates][numberOfSymbols];
    for (int x = 0; x < numberOfStates; x++) {
      for (int y = 0; y < numberOfSymbols; y++) {
        //summs for gamma
        double countgq = 0;
        double countg = 0;
        for (int z = 0; z < lengthOfSequence; z++) {
          if (observedSentence[z].equals(sentence[y])) {
            //1st type of summations
            countgq += gammaMatrix[x][z]; 
          }
          //other
          countg += gammaMatrix[x][z]; 
        }
        //update b like in a earlier
        if (countg == 0.0) {
          bOutput[x][y] = b[x][y];  
        } else {
          bOutput[x][y] = (countgq/countg); 
        }
      }
    }
    //preprocess b
    String [][]  btextOutput= new String [numberOfStates][numberOfSymbols];
    for (int i=0; i< bOutput.length; i++){
      for (int j=0; j<bOutput[i].length; j++){
        btextOutput[i][j]=niceFormat.format(bOutput[i][j]);
      }
      
    }
    //put b in file
    for (int i = 0; i < btextOutput.length; i++) {
      for (int j = 0; j < btextOutput[i].length; j++) {
        output.write( String.format("%.6f ",Double.parseDouble(btextOutput[i][j]))); //Write the newly calculated Bs
      }
      output.write("\n");
    }
    output.write("pi:\n");
    
    
    double[] piOutput = new double[numberOfStates]; 
    
    //System.out.println(Arrays.deepToString(xi));
    
  //calculate pi from gamma
    for (int i = 0; i < numberOfStates; i++) {
      piOutput[i] = gammaMatrix[i][0]; 
    }
    //System.out.println(Arrays.toString(piOutput));
    
    //preprocess pi
    String [] pitextOutput= new String [piOutput.length];
    for (int i=0; i< piOutput.length; i++){
      pitextOutput[i]=niceFormat.format(piOutput[i]);
      
    }
    //stick pi in file
    for (int i = 0; i < pitextOutput.length; i++) {
      output.write(String.format("%.6f ", Double.parseDouble(pitextOutput[i]))); ////Write the newly calculated Pis
    }
    
    output.close();
    
    
    //System.out.println(Arrays.deepToString(bOutput));
    //System.out.println(fileThree);
    
    
    //do new pattern recognition to see if optimization helped!
    patternRecognition(fileThree, fileTwo); 
    
    
    
  } 
  
  
  
  
}