import java.util.*;
import java.io.*;

class Node{
  protected String regex;
  protected Node operador, derivado1, derivado2;
  protected HashSet<Integer> firstPos, lastPos, followPos;
  protected boolean kleene;
  protected int num;

  public Node(String regex){
    this.regex = regex;
    
    this.operador = null;
    this.derivado1 = null;
    this.derivado2 = null;

    firstPos = new HashSet<Integer>();
    lastPos = new HashSet<Integer>();
    followPos = new HashSet<Integer>();

    this.kleene = false;
  }

  public Node(String regex, int num){
    this(regex);
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

  public HashSet<Integer> getlastPos(){
    return this.lastPos;
  }

  public HashSet<Integer> getfollowPos(){
    return this.followPos;
  }
}

public class AFDER{
  private static char[] operadores = {'|','*','.','+'};
  private static char[] alphabet = {'a','b','c','d'};

  public static boolean inAlfabeto(char r){
    for(int l = 0; l<alphabet.length; l++){
      if(r == alphabet[l]){
        return true;
      } 
    }
    return false;
  }

  public static String ExtendRE(String re){
    //ER -> ER# 
    StringBuilder extendedRE = new StringBuilder();
    boolean foundParentesis = false;

    for(int i = 0; i<re.length(); i++){
      char reActual = re.charAt(i);
      char reNext = (i+1<re.length()) ? re.charAt(i+1):'\0';

      if(reActual == '('){
        foundParentesis = true;
      } else if (reActual == ')'){
        foundParentesis = false;
      } else if(reActual == '|' && foundParentesis == false){
        extendedRE.append("#");
      } 
      extendedRE.append(reActual);

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

    extendedRE.append("#");

    return extendedRE.toString();
  }

  public void kleeneOperador(Node regex){
    Node operador = new Node("*");
    regex.setOperador(operador);
    operador.setDerivado1(regex);
  }

  public void concatOperador(Node regex1, Node regex2){
    Node operador = new Node(".");
    regex1.setOperador(operador);
    regex2.setOperador(operador);
    operador.setDerivado1(regex1);
    operador.setDerivado2(regex2);
  }

  public void or(Node regex1, Node regex2){
    Node operador = new Node(".");
    regex1.setOperador(operador);
    regex2.setOperador(operador);
    operador.setDerivado1(regex1);
    operador.setDerivado2(regex2);
  }

  public void regex(String expresion, int num){
    Node regex = new Node(expresion,num);
  }









  
  public static void main(String args[]) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("write: ");
    String prueba = teclado.readLine();
    System.out.println(ExtendRE(prueba));
    //regNumerator(ExtendRE(prueba));
    System.out.print("\n");
  }

}
