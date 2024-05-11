import java.io.*;
import java.util.*;

/*class extends ER to ER#*/
public class ER#{

  private static int[] regExpNum;
  private static int contRegExp;

  public static String ExtendRE(String re){
    //ER -> ER# 
    StringBuilder extendedRE = new StringBuilder();
    boolean foundParentesis = false;

    for(int i = 0; i<re.length(); i++){
      char reActual = re.charAt(i);

      if(reActual == '('){
        foundParentesis = true;
      } else if (reActual == ')'){
        foundParentesis = false;
      } else if(reActual == '|' && foundParentesis == false){
        extendedRE.append("#");
      }

      extendedRE.append(reActual);
    }

    extendedRE.append("#");

    return extendedRE.toString();
  }

  public static int[] regNumerator(String re){
    //[0,1,..,n] posiciones de ER
    contRegExp = 0;
    for(char i : re.toCharArray()){
      if(!(i == '*' || i == '+' || i == '|' || i == '(' || i == ')' || i == '[' || i == ']')){
        contRegExp += 1;
      }
    }

    regExpNum = new int[contRegExp];

    for(int m = 0; m < contRegExp; m++){
      regExpNum[m] = m;
    }
    
    toStringList(regExpNum);

    return regExpNum;
  }

  public static boolean Operators(char i){
    return !(i == '*' || i == '+' || i == '|' || i == '(' || i == ')' || i == '[' || i == ']' || i == '{' || i == '}');
  }


  public static void main(String args[]) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("write: ");
    String prueba = teclado.readLine();
    System.out.println(ExtendRE(prueba));
    regNumerator(ExtendRE(prueba));
    System.out.print("\n");
  }

  public static void toStringList(int[] list){
    System.out.print("[ ");
    for(int k : list){
      if(k == list.length- 1){
        System.out.print(String.valueOf(k));
      } else {
        System.out.print(String.valueOf(k)+",");
      }
      
    }
    System.out.print(" ]");
  }

  public static void toStringLista(ArrayList<Integer> lista){
    System.out.print("[ ");
    for(int k = 0; k<lista.size(); k++){
      if(k == lista.size()- 1){
        System.out.print(String.valueOf(k));
      } else {
        System.out.print(String.valueOf(k)+",");
      }

    }
    System.out.print(" ]");
  }
  
}
