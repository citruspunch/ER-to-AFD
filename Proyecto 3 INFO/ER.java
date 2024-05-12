import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


public class ER{

  private static String regExp;

  public ER(String er){
    regExp = er;
  }

  public void toGLD(char[] alfabeto, int[][] afdTransiciones, int cantEstados, int[] estadosFinales, int estadoInicial, boolean archivo){
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
    if (archivo){
      writeToFile(GLD);
    }
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

  public void minimizarAFD(int[][] afdTransiciones, char[] alfabeto, int cantEstados, int[] estadosFinales, int estadoInicial){
    ArrayList<ArrayList<Integer>> pi = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> piNoFinales = new ArrayList<>();
    ArrayList<Integer> piFinales  = new ArrayList<>();
    for (int i = 0; i < cantEstados; i++) {
      if (isFinal(i, estadosFinales)) {
        piFinales.add(i);
      } else {
        piNoFinales .add(i);
      }
    }
    pi.add(piNoFinales);
    pi.add(piFinales);
    ArrayList<ArrayList<Integer>> siguiente = new ArrayList<ArrayList<Integer>>();
    while (true) {
      siguiente = new ArrayList<ArrayList<Integer>>(pi);
      // Recorre cada subconjunto
      for (ArrayList<Integer> subconjunto : siguiente) {
        Map<ArrayList<Integer>, ArrayList<Integer>> nuevosSubConjuntos = new HashMap<>();
        // Recorre cada estado del subconjunto
        for (int estado : subconjunto) {
          ArrayList<Integer> transitions = new ArrayList<>();
          // Recorre cada letra del alfabeto
          for (int j = 0; j < alfabeto.length; j++) {
            int transicion = afdTransiciones[j][estado];
            int groupIndex = encontrarConjunto(pi, transicion);
            transitions.add(groupIndex);
          }
          if (!nuevosSubConjuntos.containsKey(transitions)) {
            nuevosSubConjuntos.put(transitions, new ArrayList<>());
          }
          nuevosSubConjuntos.get(transitions).add(estado);
        }
        pi.remove(subconjunto);
        pi.addAll(nuevosSubConjuntos.values());
      }
      // Si tiene las mismas particiones terminamos
      if (siguiente.size() == pi.size()) {
        break;
      }
    }
  }

  private int encontrarConjunto(ArrayList<ArrayList<Integer>> conjunto, int estado) {
    for (int i = 0; i < conjunto.size(); i++) {
      if (conjunto.get(i).contains(estado)) {
        return i;
      }
    }
    return -1;
}

  public static void main(String args[]) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    
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
        int[][] afdTransiciones = getAFDTransiciones();
        int cantEstados = getCantEstados();
        int[] estadosFinales = getEstadosFinales();
        int estadoInicial = 1;
        boolean imprimir = true;
        toGLD(alfabeto, afdTransiciones, cantEstados, estadosFinales, estadoInicial, imprimir);
        // Implementar conversion a GLD
        break;
      case 3:
        // Implementar conversion a AFD
        // Implementar conversion a GLD
        int[][] afdTransiciones = getAFDTransiciones();
        int cantEstados = getCantEstados();
        int[] estadosFinales = getEstadosFinales();
        int estadoInicial = 1;
        boolean imprimir = true;
        toGLD(alfabeto, afdTransiciones, cantEstados, estadosFinales, estadoInicial, imprimir);
        break;
      default:
        System.out.println("Opcion no valida");
        break;
      */
    }
  }
}