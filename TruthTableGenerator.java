package TruthTableGenerator;


import java.util.*;

public class TruthTableGenerator {
    boolean output[];
    HashMap<String,Boolean> input = new HashMap<>();
    String expression;
    int variablesLength;
    TruthTableGenerator(String exp){
        expression = exp;
        variablesLength = 1;
        for(int i = 0 ; i<expression.length() ; i++){ // Iterating through the expression and adding the variables to the hashmap
            if(Character.isLetter(exp.charAt(i))){
                String token = "";
                while(i<expression.length() && Character.isLetter(exp.charAt(i))){
                    token += exp.charAt(i);
                    ++i;
                }
                if(!input.containsKey(token)){
                    variablesLength += token.length()+3;
                    input.putIfAbsent(token,false);
                }
            }
        }
        output = new boolean[(int)Math.pow(2, input.size())];   //Generating the output column as per the number of variables present
        TreeMap<String,Boolean> treeMap = new TreeMap<>(input); //Sorting The Map Using A TreeMap So All Terms Are Placed In Alphabetical Order.
        input.clear();
        input.putAll(treeMap);
        treeMap.clear();
    }
    static String spaceRemover(String s){
        String o = "";
        for(int i = 0 ; i<s.length() ; i++){
            if(s.charAt(i)==' ')continue;
            o+=s.charAt(i);
        }
        return o;
    }
    static boolean isValidExpression(String exp) {
        if (exp.isEmpty()) return false;
        Stack<Character> ParenthesesChecker = new Stack<>();
        char prev = 0;
        boolean isAlphabetPresent = false;
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if(!Character.isLetter(c)) {
                switch (c) {
                    case '-': {
                        if(prev=='<')break;
                        if ((i == 0 || i > exp.length() - 3) || (exp.charAt(i + 1) != '>') || (!Character.isLetter(prev) && prev!='\'' && prev!=')') || (!Character.isLetter(exp.charAt(i + 2)) && exp.charAt(i+2)!='(')) {
                            System.out.println("Error At Term At Index: "+i);
                            for(int spacePrinter = 0; spacePrinter<i ; spacePrinter++) System.out.print(" ");
                            System.out.print("v\n");
                            System.out.println(exp);
                            return false;
                        }
                        break;
                    }
                    case '<': {
                        if ((i == 0 || i > exp.length() - 4) || (exp.charAt(i + 2) != '>' && exp.charAt(i + 1) != '-') || (!Character.isLetter(prev) && prev!='\'' && prev!=')') || (!Character.isLetter(exp.charAt(i + 3)) && exp.charAt(i+3)!='(')) {
                            System.out.println("Error At Term At Index: "+i);
                            for(int spacePrinter = 0; spacePrinter<i ; spacePrinter++) System.out.print(" ");
                            System.out.print("v\n");
                            System.out.println(exp);
                            return false;
                        }
                        break;
                    }
                    case '\'': {
                        if (i == 0 || (!Character.isLetter(prev) && prev!=')')) {
                            System.out.println("Error At Term At Index: "+i);
                            for(int spacePrinter = 0; spacePrinter<i ; spacePrinter++) System.out.print(" ");
                            System.out.print("v\n");
                            System.out.println(exp);
                            return false;
                        }
                        break;
                    }
                    case '+','.','@': {
                        if ((i == 0 || i == exp.length() - 1) || ((!Character.isLetter(exp.charAt(i + 1)) && exp.charAt(i+1)!='(') || (!Character.isLetter(prev) && prev!='\'' && prev!=')'))) {
                            System.out.println("Error At Term At Index: "+i);
                            for(int spacePrinter = 0; spacePrinter<i ; spacePrinter++) System.out.print(" ");
                            System.out.print("v\n");
                            System.out.println(exp);
                            return false;
                        }
                        break;
                    }
                    case '(': {
                        if(i!=0 && (Character.isLetter(prev) || prev == ')')){
                            System.out.println("Error At Term At Index: "+i);
                            for(int spacePrinter = 0; spacePrinter<i ; spacePrinter++) System.out.print(" ");
                            System.out.print("v\n");
                            System.out.println(exp);
                            return false;
                        }
                        ParenthesesChecker.push(')');
                        break;
                    }
                    case ')': {
                        if (ParenthesesChecker.isEmpty()){
                            System.out.println("Error At Term At Index: "+i);
                            for(int spacePrinter = 0; spacePrinter<i ; spacePrinter++) System.out.print(" ");
                            System.out.print("v\n");
                            System.out.println(exp);
                            return false;
                        }
                        ParenthesesChecker.pop();
                        break;
                    }
                    default:
                        if(c!='>'){
                            System.out.println("Error At Term At Index: "+i);
                            for(int spacePrinter = 0; spacePrinter<i ; spacePrinter++) System.out.print(" ");
                            System.out.print("v\n");
                            System.out.println(exp);
                            return false;
                        }
                }
            }
            else{ isAlphabetPresent = true; }
            prev = c;
        }
        if(!ParenthesesChecker.isEmpty()){
            System.out.println("Uneven Or Incorrectly Placed Brackets");
            return false;
        }
        else if(!isAlphabetPresent){
            System.out.println("No Alphabets Detected");
            return false;
        }
        return true;
    }
    void displayHeadOfTable(){
        for(int i = 1 ; i<variablesLength+ expression.length()+7 ; i++)System.out.print("_");
        System.out.print("\n|");
        for (Map.Entry<String,Boolean> mapElement : input.entrySet()){
            System.out.print(" "+mapElement.getKey()+" |");
        }
        System.out.print("   "+expression+"   \n");
        TableGenerator();
        MintermMaxtermGenerator();
    }
    String[] expressionGenerator(){
        int size = 0 ,i =0 ;
        while(i<expression.length()){ // finding the number of terms to separate, so we can create the expression array.
            char curr = expression.charAt(i);
            if(!Character.isLetter(curr)){
                switch(curr){
                    case '.', '+','\'','(',')','@':
                        size++;
                        i++;
                        continue;
                    case '-':
                        size++;
                        i+=2;
                        continue;
                    case '<':
                        size++;
                        i+=3;
                        continue;
                }
            }
            while(i<expression.length() && Character.isLetter(expression.charAt(i))){
                ++i;
            }
            ++size;
        }
        String exp[] = new String[size];
        size = 0;
        for(i = 0; i<expression.length() ; ++i){
            if(i<expression.length() && !Character.isLetter(expression.charAt(i))){
                switch(expression.charAt(i)){
                    case '.', '+','\'','(',')','@':
                        exp[size++] = ""+expression.charAt(i);
                        continue;
                    case '-':
                        exp[size++] = ">";
                        i+=1;
                        continue;
                    case '<':
                        exp[size++] = "<";
                        i+=2;
                        continue;
                }
            }
            String curr = "";
            int j = i;
            for(; j<expression.length() && Character.isLetter(expression.charAt(j)); j++){
                curr += expression.charAt(j);
            }
            exp[size++] = curr;
            i=j-1;
        }
        return exp;
    }
    char[] expressionFiller(String exp[]){
        char currExp[] = new char[exp.length];
        for(int i = 0 ; i<exp.length ; i++){
            if(input.containsKey(exp[i])){
               currExp[i] = input.get(exp[i])?'1':'0';
               continue;
            }
            currExp[i] = exp[i].charAt(0);
        }
        return currExp;
    }
    void hashMapValueUpdater(){
        int[] binaryNum = new int[input.size()];
        int ctr = 0;
        for (Map.Entry<String,Boolean> mapElement : input.entrySet()){
            int curr = mapElement.getValue() ? 1 : 0;
            binaryNum[ctr++] = curr;
        }
        int carry = 1;
        for(int i = input.size()-1 ; i>=0  && carry==1 ; i--){
           binaryNum[i] = (binaryNum[i] + carry)%2;
           carry = binaryNum[i]==0 ? 1 : 0;
        }
        ctr =0;
        for (Map.Entry<String,Boolean> mapElement : input.entrySet()){
            int curr = mapElement.setValue(binaryNum[ctr++]==1) ? 1 : 0;
        }
    }
    void TableGenerator(){
        String exp[] = expressionGenerator();
        for(int i = 0 ; i < output.length ; i++) {
            System.out.print("|");
            for (Map.Entry<String, Boolean> mapElement : input.entrySet()) {
                char inputVariableState = (mapElement.getValue())?'1':'0';
                System.out.print(" " +inputVariableState+"  ");
            }
            char currExp[] = expressionFiller(exp);
            ShuntingYardAlgo obj2 = new ShuntingYardAlgo(currExp);
            obj2.InfixToPostfix();
            output[i] = obj2.solver();
            System.out.print("   " + output[i] + "    \n");
            hashMapValueUpdater();
        }
    }
    void MintermMaxtermGenerator(){
        String[] vars = new String[input.size()]; int index = 0;
        for (Map.Entry<String,Boolean> mapElement : input.entrySet()){
            vars[index++] = mapElement.getKey();
        }
        String canonicalSOPExp = "Σ(";
        String mintermSOPExp = "";
        String canonicalPOSExp = "π(";
        String maxtermPOSExp = "";
        int numOfOnes = 0,ctr=0;
        for(boolean ans : output) if(ans) numOfOnes++;
        int[] SOPterms = new int[numOfOnes];
        for(int i = 0 ; i< output.length ; i++){
            if(output[i]){
                SOPterms[ctr++] = i;
                canonicalSOPExp += " "+(""+i)+" ,";
                String binaryNum = Integer.toBinaryString(i) , currMinterm = "";
                index = 0; int diffBetweenNumAndVar = vars.length - binaryNum.length() , binCtr =0;
                while(diffBetweenNumAndVar-->0){ // Since the binaryNum will not contain the preceding 0s we have to manually input them till the current binary number
                    vars[index++] = "0";
                }
                for( ; index<vars.length  ; index++ , binCtr++){ // filling in the current binary number
                    vars[index] = ""+binaryNum.charAt(binCtr);
                }
                index = 0;
                for (Map.Entry<String,Boolean> mapElement : input.entrySet()){
                   if(vars[index++].equals("1"))  mintermSOPExp += (mapElement.getKey()+".");
                   else                           mintermSOPExp += (mapElement.getKey()+"'.");
                }
                mintermSOPExp = mintermSOPExp.substring(0,mintermSOPExp.length()-1); // Removing the last unnecessary and operator
                mintermSOPExp += " + ";
            }
            else{
                canonicalPOSExp += " "+(""+i)+" ,";
                String binaryNum = Integer.toBinaryString(i) , currMaxterm = "";
                index = 0; int diffBetweenNumAndVar = vars.length - binaryNum.length() , binCtr =0;
                while(diffBetweenNumAndVar-->0){                                 // Since the binaryNum will not contain the preceding 0s we have to manually input them till the current binary number
                    vars[index++] = "0";
                }
                for( ; index<vars.length  ; index++ , binCtr++){                 // filling in the current binary number
                    vars[index] = ""+binaryNum.charAt(binCtr);
                }
                index = 0;
                for (Map.Entry<String,Boolean> mapElement : input.entrySet()){
                    if(vars[index].equals("0") && 1==vars.length)          maxtermPOSExp += ("( "+mapElement.getKey()+" )");
                    else if(vars[index].equals("1") && 1==vars.length)     maxtermPOSExp += ("( "+mapElement.getKey()+"' )");
                    else if(vars[index].equals("0") && index==0)                      maxtermPOSExp += ("( "+mapElement.getKey()+" + ");
                    else if (vars[index].equals("0") && index == input.size()-1) maxtermPOSExp += (mapElement.getKey()+")");
                    else if (vars[index].equals("0"))                            maxtermPOSExp += (mapElement.getKey()+" + ");
                    else if (index==0)                                           maxtermPOSExp += ("( "+mapElement.getKey()+"' + ");
                    else if(index==input.size()-1)                               maxtermPOSExp += (mapElement.getKey()+"')");
                    else                                                         maxtermPOSExp += (mapElement.getKey()+"' + ");
                    ++index;
                }
                maxtermPOSExp += " . ";
            }
        }
        canonicalSOPExp = canonicalSOPExp.lastIndexOf(',') == -1 ? canonicalSOPExp : canonicalSOPExp.substring(0,canonicalSOPExp.length()-1);
        mintermSOPExp   = mintermSOPExp.lastIndexOf('+') == -1 ? mintermSOPExp : mintermSOPExp.substring(0,mintermSOPExp.length()-2);
        canonicalPOSExp = canonicalPOSExp.lastIndexOf(',') == -1 ? canonicalPOSExp : canonicalPOSExp.substring(0,canonicalPOSExp.length()-1);
        maxtermPOSExp   = maxtermPOSExp.lastIndexOf('.') == -1 ? maxtermPOSExp : maxtermPOSExp.substring(0,maxtermPOSExp.length()-2);
        System.out.println("\nCanonical S.O.P Expression: "+canonicalSOPExp+")");
        System.out.println("Minterm S.O.P Expression: "    +mintermSOPExp);
        System.out.println("Canonical P.O.S Expression: "  +canonicalPOSExp+" )");
        System.out.println("Maxterm P.O.S Expression: "+ maxtermPOSExp);
        generateMinimizedExpression(SOPterms);
    }
    void generateMinimizedExpression(int[] SOPterms){
        if(SOPterms.length == 0){ System.out.println("The Most Simplified Expression Is: 0"); return; }
        String vars[] = input.keySet().toArray(new String[0]);
        boolean expressionIsNotTrue = true;
        QuineMcCluskeyAlgo caller = new QuineMcCluskeyAlgo(SOPterms, input.size());
        String ans = caller.generateImplicantTable(SOPterms);
        StringTokenizer replacingBinaryWithVariables = new StringTokenizer(ans);
        ans = "";
        while(replacingBinaryWithVariables.hasMoreTokens()){
            String currToken = replacingBinaryWithVariables.nextToken();
            for(int i = 0 ; i <currToken.length() ; i++){
                char currentChar = currToken.charAt(i);
                if(currentChar == '1'){ ans += vars[i]; expressionIsNotTrue = false;}
                else if(currentChar == '0'){ans += vars[i]+"'"; expressionIsNotTrue = false;}
            }
            ans += " + ";
        }
        if(expressionIsNotTrue){ System.out.println("The Most Simplified Expression Is: 1"); return; }
        System.out.println("The Most Simplified Expression Is: "+ans.substring(0,ans.lastIndexOf('+')));
    }
    public static void main(String[] Args){
        Scanner sc = new Scanner(System.in);
        System.out.println("\t\tTruth Table Generator\t\t\nRules:\nUse '.' for AND\nUse '+' For OR\nUse '@' For XOR\nUse ''' after the term for NEGATION\nUse \"->\" for IMPLICATION\nUse \"<->\" for BI-CONDITIONAL IMPLICATION\nUse '(' and ')' for PARENTHESES\nVariables Aren't Case Sensitive\nEnter Your Expression");
        String exp = sc.nextLine();
        exp = exp.toLowerCase();
        exp = spaceRemover(exp);
        while (!isValidExpression(exp)) {
            System.out.println("Enter Your Expression");
            exp = sc.nextLine();
        }
        TruthTableGenerator obj = new TruthTableGenerator(exp);
        obj.displayHeadOfTable();
    }
}