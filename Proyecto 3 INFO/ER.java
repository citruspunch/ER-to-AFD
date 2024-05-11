import java.io.*;
import java.util.*;


public class ER{

  private static String regExp;
  private static int[] regExpNum;
  private static int contRegExp;
  private static HashMap<Integer, int[]> firstPos;

  public ER(String er){
    regExp = er;

  }

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






  public void toGLD(char[] alfabeto, int[][] afdTransiciones, int cantEstados, int[] estadosFinales, int estadoInicial){
    int estadoError = 0;
    ArrayList<String> GLD = new ArrayList<String>();

    // Crear un array de caracteres con los nombres de los simbolos no terminales
    char[] noTerminales = new char[cantEstados];

    for (int i = 0; i < cantEstados; i++) {
      if (i == estadoError){
        noTerminales[i] = (char) ('A' + cantEstados - 1);
      } else if(i == estadoInicial){
        noTerminales[i] = 'S';
      }else if(!((char) ('A' + i) == 'S')){
        noTerminales[i] = (char) ('A' + i);
      }
    }

    for (int col=0; col<cantEstados; col++){
      for (int row=0; row<alfabeto.length; row++){
        if (col == estadoError){
          GLD.add(noTerminales[col] + " -> " + alfabeto[row] + noTerminales[estadoError]);
        } else if (col == estadoInicial){
          GLD.add(noTerminales[col] + " -> " + alfabeto[row] + noTerminales[afdTransiciones[row][col]]);
        } else {
          boolean estadoFinal = isFinal(col, estadosFinales);
          GLD.add(noTerminales[col] + " -> " + alfabeto[row] + noTerminales[afdTransiciones[row][col]]);
          // Si es estado Final se agrega regla de produccion con lambda
          if (estadoFinal){
            GLD.add(noTerminales[col] + " -> λ");
          }
        }
      }
    }
    writeToFile(GLD);
  }

  private boolean isFinal(int estado, int[] estadosFinales){
    for (int i = 0; i < estadosFinales.length; i++) {
      if (estado == estadosFinales[i]) {
        return true;
      }
    }
    return false;
  }

  private void writeToFile(ArrayList<String> GLD) {
    try {
      FileWriter writer = new FileWriter("GLD.gld");
      for (String str : GLD) {
        writer.write(str + System.lineSeparator());
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("write: ");
    String prueba = teclado.readLine();
    System.out.println(ExtendRE(prueba));
    regNumerator(ExtendRE(prueba));

    
    char[] alfabeto = {'a', 'b', 'c'};
    System.out.println("ALFABETO: " + Arrays.toString(alfabeto));
    System.out.println("Ingrese la expresion regular: ");
    String re = teclado.readLine();
    System.out.println("Desea convertir a: ");
    System.out.println("1. AFD");
    System.out.println("2. GLD");
    System.out.println("3. Ambos");
    int opcion = Integer.parseInt(teclado.readLine());
    ER er = new ER(re);
    switch (opcion) {
      case 1:
        // Implementar conversion a AFD
        break;
        /*
      case 2:
        int[][] afdTransiciones = getAFDTransiciones(alfabeto, re);
        int cantEstados = getCantEstados(afdTransiciones);
        int[] estadosFinales = getEstadosFinales(afdTransiciones);
        int estadoInicial = 1;
        toGLD(alfabeto, afdTransiciones, cantEstados, estadosFinales, estadoInicial);
        // Implementar conversion a GLD
        break;
      case 3:
        // Implementar conversion a AFD
        // Implementar conversion a GLD
        int[][] afdTransiciones = getAFDTransiciones(alfabeto, re);
        int cantEstados = getCantEstados(afdTransiciones);
        int[] estadosFinales = getEstadosFinales(afdTransiciones);
        int estadoInicial = 1;
        toGLD(alfabeto, afdTransiciones, cantEstados, estadosFinales, estadoInicial);
        break;
      default:
        System.out.println("Opcion no valida");
        break;
      */
    }
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