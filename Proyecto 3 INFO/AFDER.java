import java.util.*;
import java.io.*;

class Node{
  protected String regex;
  protected Node operador, derivado1, derivado2;
  protected HashSet<Integer> firstPos, lastPos;
  protected boolean kleene;
  protected int num;

  public Node(String regex){
    this.regex = regex;
    
    this.operador = null;
    this.derivado1 = null;
    this.derivado2 = null;

    firstPos = new HashSet<Integer>();
    lastPos = new HashSet<Integer>();

    this.kleene = false;
  }

  public Node(String regex, int num){
    this(regex);
    this.num = num;
  }

  public int getNum(){
    return this.num;
  }

  public void setNum(int num){
    this.num = num;
  }

  public String getRegex(){
    return this.regex;
  }

  public void SetRegex(String regex){
    this.regex = regex;
  }

  public Node getOperador(){
    return this.operador;
  }

  public void setOperador(Node operador){
    this.operador = operador;
  }

  public Node getDerivado1(){
    return this.derivado1;
  }

  public void setDerivado1(Node derivado1){
    this.derivado1 = derivado1;
  }

  public Node getDerivado2(){
    return this.derivado2;
  }

  public void setDerivado2(Node derivado2){
    this.derivado2 = derivado2;
  }

  public boolean getKleene(){
    return this.kleene;
  }

  public void setKleene(boolean kleene){
    this.kleene = kleene;
  }

  public HashSet<Integer> getfirstPos(){
    return this.firstPos;
  }

  public void addFirst(int num){
    this.firstPos.add(num);
  }

  public HashSet<Integer> getlastPos(){
    return this.lastPos;
  }

  public void addLast(int num){
    this.lastPos.add(num);
  }
}

public class AFDER{
  private static char[] operadores = {'|','*','.','+'};
  private static char[] alphabet = {'a','b','c','d','#'};

  private static int[] regExpNum;
  private static int contRegExp;

  private static HashSet<Integer> firstPos = new HashSet<Integer>();
  private static HashSet<Integer> lastPos = new HashSet<Integer>();
  private static HashMap<Integer, HashSet<Integer>> followPos = new HashMap<Integer, HashSet<Integer>>();


  public static boolean inAlfabeto(char r){
    for(int l = 0; l<alphabet.length; l++){
      if(r == alphabet[l]){
        return true;
      } 
    }
    return false;
  }

  public static String extendRE(String re){
    //ER -> ER# 
    StringBuilder extendedRE = new StringBuilder();
    boolean foundParentesis = false;
    StringBuilder aux = new StringBuilder();
    int cont = 0;
    ArrayList<Integer> start = new ArrayList<Integer>();
    ArrayList<Integer> end = new ArrayList<Integer>();
    String sub = "";
    
    for(int i = 0; i<re.length(); i++){
      
      char reActual = re.charAt(i);
      char reNext = (i+1<re.length()) ? re.charAt(i+1):'\0';
      char reAnt = (i-1>= 0) ? re.charAt(i-1):'\0';
      
      if(reActual == '('){
        foundParentesis = true;
        cont++;
        start.add(i);
        
      } else if (reActual == ')'){
        foundParentesis = false;
        cont--;
        end.add(i);
  
      } else if(reActual == '|' && foundParentesis == false){
        extendedRE.append('.');
        extendedRE.append("#");
      } 
      
      if(reActual == '+'){
        if (inAlfabeto(reAnt)) {
          extendedRE.deleteCharAt(extendedRE.length()-1);
          extendedRE.append(reAnt);
          extendedRE.append('.');
          extendedRE.append(reAnt);
          extendedRE.append('*');
        } else if(reAnt == ')'){
          if( cont == 0 || cont > 0){
            aux.setLength(0); //borra lo que habia en aux
            aux.append('.');
            aux.append('(');
            sub = extendRE(re.substring(start.remove(start.size()-1), end.remove(end.size()-1)));
            aux.append(sub.substring(0,sub.length()-2));
            aux.append(')');
            aux.append('*');
            if(inAlfabeto(reNext) || reNext == '('){
              aux.append('.');
            }
            extendedRE.append(aux);
          } 
        }
      } 
      
      if(reActual != '+'){
        extendedRE.append(reActual);
      }

      if(i+1<re.length()){
        if(inAlfabeto(reActual) && inAlfabeto(reNext)){
          extendedRE.append('.');
        } else if(inAlfabeto(reActual) && (reNext == '(')){
          extendedRE.append('.');
        } else if( (reActual == ')') && inAlfabeto(reNext)){
          extendedRE.append('.');
        } else if((reActual == '*') && inAlfabeto(reNext)){
          extendedRE.append('.');
        } else if((reActual == '*') && (reNext == '(') ){
          extendedRE.append('.');
        } else if((reActual == ')') && (reNext == '(')){
          extendedRE.append('.');
        }
      }
    }
    extendedRE.append('.');
    extendedRE.append("#");
    return extendedRE.toString();
  }

  public static Node kleeneOperador(Node regex){
    Node operador = new Node("*");
    regex.setOperador(operador);
    operador.setDerivado1(regex);
    return operador;
  }

  public static Node concatOperador(Node regex1, Node regex2){
    Node operador = new Node(".");
    regex1.setOperador(operador);
    regex2.setOperador(operador);
    operador.setDerivado1(regex1);
    operador.setDerivado2(regex2);
    return operador;
  }

  public static Node orOperador(Node regex1, Node regex2){
    Node operador = new Node("|");
    regex1.setOperador(operador);
    regex2.setOperador(operador);
    operador.setDerivado1(regex1);
    operador.setDerivado2(regex2);
    return operador;
  }

  /*public static Node positivaOperador(Node regex){
    Node kleene = kleeneOperador(regex);
    Node kleeneConcat = concatOperador(regex, kleene);
    return kleeneConcat;
  }*/

  public static Node regex(String expresion, int num){
    Node regex0 = new Node(expresion,num);
    regex0.addFirst(regex0.getNum());
    regex0.addLast(regex0.getNum());
    System.out.println(regex0.getRegex()+regex0.getfirstPos());
    System.out.println(regex0.getRegex()+regex0.getlastPos());
    return regex0;
  }

  public static int jerarquia(char op){
    switch(op){
        case('*'):
        return 3;

        case('.'):
        return 2;

        case('|'):
        return 1;

        default:
        return 0;
    }
  }

  public static Node sintaxis(String regex){
    regex = extendRE(regex);
    ArrayList<Character> operators = new ArrayList<Character>();
    ArrayList<Node> silabas = new ArrayList<Node>();
    int posicion = 0;

    for(int i = 0; i<regex.length(); i++){
      char reActual = regex.charAt(i); 
      if(inAlfabeto(reActual)){
        silabas.add(regex(String.valueOf(reActual),++posicion));
      }else if(operators.isEmpty() || reActual == '('){
        operators.add(reActual);
      } else if(reActual == ')'){
        while(operators.get(operators.size()-1) != '('){
          char x = operators.get(operators.size()-1);
          
          if(x=='*'){
            Node regex0 = silabas.remove(silabas.size()-1);
            silabas.add(kleeneOperador(regex0));
            
          }else if(x=='.'){
            Node regex1 = silabas.remove(silabas.size()-1);
            Node regex2 = silabas.remove(silabas.size()-1);
            silabas.add(concatOperador(regex1,regex2));
            
          }else if(x=='|'){
            Node regex1 = silabas.remove(silabas.size()-1);
            Node regex2 = silabas.remove(silabas.size()-1);
            silabas.add(orOperador(regex1,regex2));
          }
          operators.remove(operators.size()-1);
        }
        operators.remove(operators.size()-1);
      } else {
        while(!operators.isEmpty() && jerarquia(operators.get(operators.size()-1))>=jerarquia(reActual)){
          
          char x = operators.get(operators.size()-1);
          if(x=='*'){
            Node regex0 = silabas.remove(silabas.size()-1);
            silabas.add(kleeneOperador(regex0));

          }else if(x=='.'){
            Node regex1 = silabas.remove(silabas.size()-1);
            Node regex2 = silabas.remove(silabas.size()-1);
            silabas.add(concatOperador(regex1,regex2));

          }else if(x=='|'){
            Node regex1 = silabas.remove(silabas.size()-1);
            Node regex2 = silabas.remove(silabas.size()-1);
            silabas.add(orOperador(regex1,regex2));
          }
          operators.remove(operators.size()-1);
        }
        operators.add(reActual);
      }
    }
    while(!operators.isEmpty()){
      char x = operators.get(operators.size()-1);
      if(x=='*'){
        Node regex0 = silabas.remove(silabas.size()-1);
        silabas.add(kleeneOperador(regex0));
  
      }else if(x=='.'){
        Node regex1 = silabas.remove(silabas.size()-1);
        Node regex2 = silabas.remove(silabas.size()-1);
        silabas.add(concatOperador(regex1,regex2));
  
      }else if(x=='|'){
        Node regex1 = silabas.remove(silabas.size()-1);
        Node regex2 = silabas.remove(silabas.size()-1);
        silabas.add(orOperador(regex1,regex2));
      }
      operators.remove(operators.size()-1);
    }

    Node sintax = silabas.get(0);
    return sintax;
  }

  public static void printN(Node nodo, int capa){
    StringBuilder space = new StringBuilder();
    for(int i = 0; i<capa; i++){
      space.append(" ");
    }
    
    if(nodo == null){
      System.out.print("");
    }

    System.out.print(space.toString()+nodo.getRegex());
    
    if(nodo.getRegex().equals(".")){
      System.out.println(space.toString()+"\n/ \\");
      printN(nodo.getDerivado1(),0);
      printN(nodo.getDerivado2(),1);
    } else if(nodo.getRegex().equals("|")){
      System.out.println(space.toString()+"\n/ \\");
      printN(nodo.getDerivado1(),0);
      printN(nodo.getDerivado2(),1);
    } else if(nodo.getRegex().equals("*")){
      System.out.println(space.toString()+"\n/ ");
      printN(nodo.getDerivado1(),0);
    }
  }

  public static void toStringList(int[] list){
    System.out.print("[ ");
    for(int k : list){
      if(k == list.length ){
        System.out.print(String.valueOf(k));
      } else {
        System.out.print(String.valueOf(k)+",");
      }

    }
    System.out.print(" ]");
  }

  public static int[] regNumerator(String re){
    //[0,1,..,n] posiciones de ER
    contRegExp = 0;
    for(char i : re.toCharArray()){
      if(!(i == '*' || i == '+' || i == '|' || i == '(' || i == ')' || i == '.' )){
        contRegExp += 1;
      }
    }

    regExpNum = new int[contRegExp];

    for(int m = 0; m < contRegExp; m++){
      regExpNum[m] = m + 1;
      followPos.put(m+1, new HashSet<Integer>());
    }

    toStringList(regExpNum);

    return regExpNum;
  }

  public static void fPlP(ArrayList<Node> nodos){
    /*[. -> [#], [. -> [b], [. -> [b], [. -> [a], [* -> [| -> [b], [a]]]]]]] Recorrerlo al reves de hoja a root*/
  }
  
  public static String toHashString(HashSet<Integer> s){
    StringBuilder hash = new StringBuilder();
    hash.append("[ ");
      for(int i : s){
        hash.append(i).append(", ");
      }
    hash.append(" ]");
    return hash.toString();
  }

  public static String nodesToString(Node nodo) {
    StringBuilder array = new StringBuilder();
    array.append("[");
    array.append(nodo.getRegex());
    if (nodo.getDerivado1() != null || nodo.getDerivado2() != null) {
        array.append(" -> ");
        if (nodo.getDerivado1() != null) {
            array.append(nodesToString(nodo.getDerivado1()));
        }
        if (nodo.getDerivado2() != null) {
            if (nodo.getDerivado1() != null) {
                array.append(", ");
            }
            array.append(nodesToString(nodo.getDerivado2()));
        }
    }
    array.append("]");
    return array.toString();
  }

  /*public static HashMap<Integer,HashSet<Integer>> followPosition(Node arbol){
    /*if(arbol.getRegex()){

    }
    return followPos;
  }*/
  
  public static void main(String args[]) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("write: ");
    String prueba = teclado.readLine();
    System.out.println(extendRE(prueba));
    regNumerator(extendRE(prueba));
    System.out.print("\n");
    Node test = sintaxis(prueba);
    printN(test,0); 
    System.out.println("\n");
    System.out.println(nodesToString(test));
    //fPlP(sintaxis(prueba), firstPos, lastPos);
  }

}
