import java.io.*;
import java.util.*;


public class ER{

  private static String regExp;
  private static int[] regExpNum;
  private static int contRegExp;
  private static HashMap<Integer, int[]> firstPos;

  public static String ExtendRE(String re){
    //ER -> ER# 
    StringBuilder extendedRE = new StringBuilder();

    extendedRE.append(re);
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

  public static HashMap<Integer, int[]> firstPosition(){
    
    return firstPos;
  }

  public static void main(String args[]) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("write: ");
    String prueba = teclado.readLine();
    System.out.println(ExtendRE(prueba));
    regNumerator(ExtendRE(prueba));
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
  
}