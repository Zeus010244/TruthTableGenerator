package TruthTableGenerator;
import java.util.*;
public class QuineMcCluskeyAlgo {
    static String[] variables;
    static int[] minterms;
    HashMap<Integer,ArrayList<String>> binTable,decTable;
    HashMap<String,String> decToBin;
    Set<String> UnmarkedTerms,UnmarkedTermsDecEquivalent;
    QuineMcCluskeyAlgo(int[] SOPterms ,int numOfInputs){
        String[] stringSOPTerms = SOPTermsToBinary(SOPterms ,numOfInputs);
        binTable = createBinaryTable(stringSOPTerms);
        decTable = createDecimalTable(stringSOPTerms);
        UnmarkedTerms = new HashSet<>();
        UnmarkedTermsDecEquivalent = new HashSet<>();
        decToBin = new HashMap<>();
        generateNextStep();
    }
    String[] SOPTermsToBinary(int[] SOPterms , int numOfInputs){
        String[] binTerms = new String[SOPterms.length];
        for(int i = 0 ; i<SOPterms.length ; i++){
            int lenOfCurrBinTerm = Integer.toBinaryString(SOPterms[i]).length();
            String currTerm = "";
            while(numOfInputs -lenOfCurrBinTerm!=0){
                currTerm += "0";
                lenOfCurrBinTerm++;
            }
            currTerm += Integer.toBinaryString(SOPterms[i]);
            binTerms[i] = currTerm;
        }
        return binTerms;
    }
    String binToDec(String s){
        int dec = 0, power = 0;
        for(int i = s.length()-1 ; i>=0 ; i--){
            dec+= (int) (((int)(s.charAt(i)-48)) * Math.pow(2,power++));
        }
        return String.valueOf(dec);
    }
    public HashMap<Integer,ArrayList<String>> createBinaryTable(String[] stringSOPTerms) {
        HashMap<Integer,ArrayList<String>> binTable = new HashMap<>();
        for(int i = 0 ; i<stringSOPTerms.length ; i++){
            int numberOfOnes = 0;
            String curr = stringSOPTerms[i];
            for(int j = 0 ; j<curr.length() ; j++){
                if(curr.charAt(j)=='1') numberOfOnes++;
            }
            binTable.putIfAbsent(numberOfOnes,new ArrayList<>());
            binTable.get(numberOfOnes).add(curr);
        }
        return binTable;
    }
    public HashMap<Integer,ArrayList<String>> createDecimalTable(String[] stringSOPTerms) {
        HashMap<Integer,ArrayList<String>> decTable = new HashMap<>();
        for(int i = 0 ; i<stringSOPTerms.length ; i++){
            int numberOfOnes = 0;
            String curr = stringSOPTerms[i];
            for(int j = 0 ; j<curr.length() ; j++){
                if(curr.charAt(j)=='1') numberOfOnes++;
            }
            decTable.putIfAbsent(numberOfOnes,new ArrayList<>());
            decTable.get(numberOfOnes).add(binToDec(curr));
        }
        return decTable;
    }
    int[] convertingDecTableKeysToArray(){
        int[] indices = new int[decTable.size()];
        int ctr = 0;
        for(Map.Entry<Integer,ArrayList<String>> curr : decTable.entrySet()) indices[ctr++] = curr.getKey();
        return indices;
    }
    int[] convertingBinTableKeysToArray() {
        int[] indices = new int[binTable.size()];
        int ctr = 0;
        for(Map.Entry<Integer,ArrayList<String>> curr : binTable.entrySet()) indices[ctr++] = curr.getKey();
        return indices;
    }
    Set<String> currDecTerms(){
        Set<String> allCurrDecTerms = new HashSet<>();
        for(Map.Entry<Integer,ArrayList<String>> curr : decTable.entrySet()){
            ArrayList<String> currGrp = curr.getValue();
            for (String s : currGrp) {
                allCurrDecTerms.add(sortString(s).trim());
            }
        }
        return allCurrDecTerms;
    }
    Set<String> currBinTerms(){
        Set<String> allCurrBinaryTerms = new HashSet<>();
        for(Map.Entry<Integer,ArrayList<String>> curr : binTable.entrySet()){
            allCurrBinaryTerms.addAll(curr.getValue());
        }
        return allCurrBinaryTerms;
    }
    String sortString(String s){
        StringTokenizer ofS = new StringTokenizer(s);
        String sortedVal = "";
        ArrayList<Integer> nums = new ArrayList<>();
        while(ofS.hasMoreTokens()){
            nums.add(Integer.parseInt(ofS.nextToken()));
        }
        Collections.sort(nums);
        for(int n : nums) sortedVal += ""+n+" ";
        return sortedVal;
    }
    HashMap<String,String> link(int[] indices){
        HashMap<String,String> map = new HashMap<>();
        for(int hashmapIndices = 0 ; hashmapIndices< decTable.size() ; hashmapIndices++){
            ArrayList<String> curr = decTable.get(indices[hashmapIndices]);
            for(int i = 0 ; i< curr.size() ; i++){
                map.put(curr.get(i),binTable.get(indices[hashmapIndices]).get(i));
            }
        }
        return map;
    }
    void generateNextStep() {
        boolean hasMatchedPair;
        while(true) {
            hasMatchedPair = false;
            int indices[] = convertingBinTableKeysToArray();
            Set<String> UnmarkedTermsChecker = currBinTerms();
            Set<String> DecUnmarkedTermsChecker = currDecTerms();
            HashMap<String,String> decToBin1 =  link(indices);
            HashMap<String,String> decToBinCopy =  link(indices);
            for (int hashmapIndices = 0; hashmapIndices < indices.length - 1; hashmapIndices++) {
                ArrayList<String> currGroup = binTable.get(indices[hashmapIndices]);
                ArrayList<String> nextGroup = binTable.get(indices[hashmapIndices + 1]);
                ArrayList<String> changedGroup = new ArrayList<>();
                ArrayList<String> changedDecGroup = new ArrayList<>();
                for (int i = 0; i < currGroup.size(); i++) {
                    for (int j = 0; j < nextGroup.size(); j++) {
                        int noOfDifferences = 0, indexOfDiff = 0;
                        String currEl = currGroup.get(i), nextEl = nextGroup.get(j);
                        for (int index = 0; index < currEl.length(); index++) {
                            if (currEl.charAt(index) != nextEl.charAt(index)) {
                                noOfDifferences++;
                                indexOfDiff = index;
                            }
                        }
                        if (noOfDifferences == 1) {
                            String changedString = currEl.substring(0, indexOfDiff) + "_" + currEl.substring(indexOfDiff + 1);
                            changedGroup.add(changedString);
                            String changedDecString = decTable.get(indices[hashmapIndices]).get(i) + " " + decTable.get(indices[hashmapIndices] + 1).get(j);
                            changedDecGroup.add(sortString(removeDuplicates(changedDecString)));
                            DecUnmarkedTermsChecker.remove(decTable.get(indices[hashmapIndices]).get(i).trim());
                            DecUnmarkedTermsChecker.remove(decTable.get(indices[hashmapIndices + 1]).get(j).trim());
                            UnmarkedTermsChecker.remove(currEl);
                            UnmarkedTermsChecker.remove(nextEl);
                            decToBin1.remove(decTable.get(indices[hashmapIndices]).get(i).trim());
                            hasMatchedPair = true;
                        }
                    }
                }
                if(!changedGroup.isEmpty())binTable.put(indices[hashmapIndices], changedGroup);
                if(!changedDecGroup.isEmpty())decTable.put(indices[hashmapIndices], changedDecGroup);
            }
            UnmarkedTermsDecEquivalent.addAll(DecUnmarkedTermsChecker);
            UnmarkedTerms.addAll(UnmarkedTermsChecker);
            decToBin.putAll(decToBin1);
            if(!hasMatchedPair) break;
            binTable.remove(indices[indices.length - 1]);
            decTable.remove(indices[indices.length - 1]);
        }
        for(Map.Entry<Integer,ArrayList<String>> curr : binTable.entrySet()){
            UnmarkedTerms.addAll(curr.getValue());
        }
        for(Map.Entry<Integer,ArrayList<String>> curr : decTable.entrySet()){
            ArrayList<String> currDecGrp = curr.getValue();
            for(int i = 0 ; i < currDecGrp.size() ; i++){
                UnmarkedTermsDecEquivalent.add(sortString(currDecGrp.get(i)).trim());
            }
        }
    }

    String generateImplicantTable(int[] SOPterms){
        char[][] table = new char[UnmarkedTermsDecEquivalent.size()][SOPterms.length];
        String[] UnmarkedDecTerms = UnmarkedTermsDecEquivalent.toArray(new String[UnmarkedTermsDecEquivalent.size()]);
        int indexCtr=0;
        HashMap<Integer,Integer> IndexForSOPterms = new HashMap<>();
        for(int el : SOPterms) IndexForSOPterms.put(el,indexCtr++);
        for(int i = 0 ; i< table.length ; i++){
            StringTokenizer extractNums = new StringTokenizer(UnmarkedDecTerms[i]," ");
            while(extractNums.hasMoreTokens()){
                table[i][IndexForSOPterms.get(Integer.parseInt(extractNums.nextToken()))] = 'X';
            }
        }
        return PetricksMethod(table,UnmarkedDecTerms);
    }
    String removeDuplicates(String changedStr){
        SortedSet<String> set = new TreeSet<>();
        StringTokenizer ofChangedStr = new StringTokenizer(changedStr);
        String noDoop = "";
        while(ofChangedStr.hasMoreTokens()){
            set.add(ofChangedStr.nextToken());
        }
        for(String el : set) noDoop += el + " ";
        return noDoop.trim();
    }
    String PetricksMethod(char[][] table, String[] UnmarkedDecTerms){
        ArrayList<ArrayList<String>> reduction = new ArrayList<>();
        for(int j = 0 ; j<table[0].length ; j++){
            ArrayList<String> curr = new ArrayList<>();
            for(int i = 0 ; i<table.length ; i++){
                if(table[i][j] == 'X') curr.add(""+i);
            }
            if(!curr.isEmpty() && !reduction.contains(curr))reduction.add(curr);
        }
        for(int i = 0 ; i<reduction.size()-1 ; i++){
            ArrayList<String> curr = reduction.get(i);
            ArrayList<String> next = reduction.get(i+1);
            ArrayList<String> changedStr = new ArrayList<>();
            for(int j = 0 ; j<curr.size() ; j++){
                for(int k = 0 ; k< next.size() ; k++){
                    changedStr.add(removeDuplicates(next.get(k)+" "+curr.get(j)));
                }
            }
            reduction.set(i+1,changedStr);
        }
        Set<String> removeDuplicates = new HashSet<>();
        removeDuplicates.addAll(reduction.get(reduction.size()-1));
        ArrayList<String> distributedTerms = new ArrayList<>(); for(String el : removeDuplicates) distributedTerms.add(el);
        Collections.sort(distributedTerms, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return Integer.compare(s1.length(), s2.length());
            }
        });
        StringTokenizer essentialTermsGetter = new StringTokenizer(distributedTerms.get(0));
        ArrayList<String> essentialTerms = new ArrayList<>();
        while(essentialTermsGetter.hasMoreTokens()){
            essentialTerms.add(UnmarkedDecTerms[Integer.parseInt(essentialTermsGetter.nextToken())]);
        }
        String ans = "";
        int indices[] = convertingDecTableKeysToArray();
        HashMap<String, String> sortedDecToBin = new HashMap<>();
        for (Map.Entry<String,String> curr : decToBin.entrySet()) {
            sortedDecToBin.put(sortString(curr.getKey()).trim(), curr.getValue());
        }
        for(String el : essentialTerms) {
            ans += sortedDecToBin.get(el)+" ";
        }
        return ans;
    }
    public void replacingBinaryWithVariables(){
        String ans = generateImplicantTable(minterms);
        StringTokenizer replacingBinaryWithVariables = new StringTokenizer(ans);
        boolean isTrue = true;
        ans = "";
        while(replacingBinaryWithVariables.hasMoreTokens()){
            String currToken = replacingBinaryWithVariables.nextToken();
            for(int i = 0 ; i <currToken.length() ; i++){
                char currentChar = currToken.charAt(i);
                if(currentChar == '1'){ ans += variables[i]; isTrue = false; }
                else if(currentChar == '0'){ans += variables[i]+"'"; isTrue = false;}
            }
            ans += " + ";
        }
        if(isTrue) System.out.println("The Most Simplified Expression Is: 1");
        else System.out.println("The Most Simplified Expression Is: "+ans.substring(0,ans.lastIndexOf('+')));
    }
    public static boolean processVariableInput(String input) {
        StringTokenizer varExtractor = new StringTokenizer(input, " ,");
        int index = 0;
        String vars[] = new String[varExtractor.countTokens()];
        if (vars.length == 0){
            System.out.println("There Can't Be 0 Variables");
            return false;
        }
        Set<String> duplicateChecker = new HashSet<>();
        while (varExtractor.hasMoreTokens()) {
            String currVar = varExtractor.nextToken();
            for (int i = 0; i < currVar.length(); i++) {
                if (!Character.isLetter(currVar.charAt(i))) {
                    System.out.println("Don't Use Numbers Or Special Characters In The Variables");
                    return false;
                }
            }
            duplicateChecker.add(currVar);
        }
        for (String curr : duplicateChecker) vars[index++] = curr;
        variables = vars;
        return true;
    }
    private static boolean processMintermInput(String input) {
        StringTokenizer mintermExtractor = new StringTokenizer(input, ", ");
        minterms = new int[mintermExtractor.countTokens()];
        if (minterms.length == 0){
            System.out.println("The Most Simplified Expression is: 0");
            System.exit(0);
        }
        int indexer = 0;
        long currMinterm = 0;
        while (mintermExtractor.hasMoreTokens()) {
            try {
                currMinterm = Long.parseLong(mintermExtractor.nextToken());
                if (currMinterm > 2147483647 || currMinterm < 0){
                    System.out.println("Minterms Must Be In The Range 0<=X<=2147483647");
                    return false;
                }
                minterms[indexer++] = (int) currMinterm;
            } catch (NumberFormatException e) {
                System.out.println( "The Minterms Must Not Include Letters");
                return false;
            }
        }
        return true;
    }
    public static void main(String[] Args){
        Scanner sc = new Scanner(System.in);
        String vars = "", mintermsInput = "";
        do{
            System.out.println("Enter Your Variables Seperated By Commas");
            vars = sc.nextLine();
        }while(!processVariableInput(vars));
        do{
            System.out.println("Enter Your Minterms Seperated By Commas");
            mintermsInput = sc.nextLine();
        }while(!processMintermInput(mintermsInput));
        QuineMcCluskeyAlgo obj = new QuineMcCluskeyAlgo(minterms , variables.length);
        obj.replacingBinaryWithVariables();
    }
}