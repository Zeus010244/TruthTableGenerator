
//Algo to convert the expression to post fix notation for evaluation.

package TruthTableGenerator;
import java.sql.SQLOutput;
import java.util.*;
public class ShuntingYardAlgo {
    char exp[];
    HashMap<Character, Integer> precedence = new HashMap<Character, Integer>();
    Stack<Character> operators = new Stack<>();
    ArrayList<Character> result = new ArrayList<>();
    ShuntingYardAlgo(char[] expression){
        exp = expression;
        precedence.put('\'',6);
        precedence.put('.',5);
        precedence.put('@',4);
        precedence.put('+',3);
        precedence.put('>',2);
        precedence.put('<',1);
        InfixToPostfix();
    }
    void InfixToPostfix(){
        for(int i = 0 ; i< exp.length ; i++){
            char curr = exp[i];
            if(Character.isDigit(curr)){
                result.add(curr);
            }
            else if(precedence.containsKey(curr) || curr == '(' || curr == ')'){
                if(operators.isEmpty() || operators.peek()=='(' || curr =='('){
                    operators.push(curr);
                    continue;
                }
                else if(curr==')'){
                    while(operators.peek()!='('){
                        result.add(operators.pop());
                    }
                    operators.pop();
                    continue;
                }
                else if(precedence.get(operators.peek()) >= precedence.get(curr) && !operators.isEmpty()) {
                    while ((!operators.isEmpty() && (operators.peek() != '(')) && (precedence.get(operators.peek()) >= precedence.get(curr))) {
                        result.add(operators.pop());
                    }
                    operators.push(curr);
                }
                else{
                    operators.push(curr);
                }
            }
        }
        while(!operators.isEmpty()){
            result.add(operators.pop());
        }
        solver();
    }
    boolean performOperation(char operator , char first , char second){
        boolean one = first == '1';
        boolean two = second == '1';
        switch (operator){
            case '\'':
                return !two;
            case '.':
                return one&&two;
            case '+':
                return one||two;
            case '@':
                return !one&&two || one&&!two;
            case '>':
                return !one||two;
            case '<':
                return !one&&!two || one&&two;
        }
        return false;
    }
    boolean solver(){
        for(int i = 0 ; i< result.size() ; i++){
            char curr = result.get(i);
            if(precedence.containsKey(curr)){
                if(curr=='\''){
                    result.add(i, ((performOperation(curr, ' ', result.get(i - 1)))?'1':'0'));
                    result.remove(i+1);
                    result.remove(i-1);
                    i-=2;
                }
                else {
                    char currAns = performOperation(curr, result.get(i - 2), result.get(i - 1))?'1':'0';
                    result.add(i, currAns);
                    result.remove(i+1);
                    result.remove(i-1);
                    result.remove(i-2);
                    i-=3;
                }
            }
        }
        return (result.get(0)=='1');
    }
    public static void main(String[] Args){
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        ShuntingYardAlgo obj = new ShuntingYardAlgo(s.toCharArray());
    }
}
