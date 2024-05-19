import java.io.*;
import java.util.*;


public class ER{

  private static String regExp;
  public static final int ESTADOERROR = 0;
  public static final int ESTADOINICIAL = 1;

  public ER(String er){
    regExp = er;
  }

  public ArrayList<String> toGLD(char[] alfabeto, int[][] afdTransiciones, int cantEstados, int[] estadosFinales, boolean archivo){
    ArrayList<String> GLD = new ArrayList<String>();

    // Se crea un array de caracteres con los nombres de los simbolos no terminales
    char[] noTerminales = new char[cantEstados];

    for (int i = 1; i <= cantEstados; i++) {
      if (i == ESTADOERROR){
        noTerminales[i] = (char) ('A');
      } else if(i == ESTADOINICIAL){
        noTerminales[i] = 'S';
      } else if(!((char) ('A' + i) == 'S')){
        noTerminales[i] = (char) ('A' + i);
      } else {
        noTerminales[i] = (char) ('A' + i + 1);
      }
    }

    for (int col=0; col<cantEstados; col++){
      for (int row=0; row<alfabeto.length; row++){
        if (col == ESTADOERROR){
          GLD.add(noTerminales[col] + " -> " + alfabeto[row] + noTerminales[ESTADOERROR]);
        } else if (col == ESTADOINICIAL){
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
      genArchivoGLD(GLD, noTerminales);
    }
    return GLD;
  }

  private boolean isFinal(int estado, int[] estadosFinales){
    for (int i = 0; i < estadosFinales.length; i++) {
      if (estado == estadosFinales[i]) {
        return true;
      }
    }
    return false;
  }

  private void genArchivoGLD(ArrayList<String> GLD, char[] simbolosNT) {
    try {
      FileWriter writer = new FileWriter("GLD.gld");

      for (int i = 0; i < simbolosNT.length; i++) {
        writer.write(simbolosNT[i]);
        if (i < simbolosNT.length - 1) {
          writer.write(", ");
        }
      }
      writer.write(System.lineSeparator());
      writer.write("a, b, c" + System.lineSeparator()); 
      for (String str : GLD) {
        writer.write(str + System.lineSeparator());
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void minimizarAFD(int[][] afdTransiciones, char[] alfabeto, int cantEstados, int[] estadosFinales, boolean archivo){
    ArrayList<ArrayList<Integer>> pi = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> piNoFinales = new ArrayList<>();
    ArrayList<Integer> piFinales  = new ArrayList<>();
    for (int i = 0; i < cantEstados; i++) {
      if (isFinal(i, estadosFinales)) {
        piFinales.add(i);
      } else {
        piNoFinales.add(i);
      }
    }
    pi.add(piNoFinales);
    pi.add(piFinales);
    ArrayList<ArrayList<Integer>> siguiente = new ArrayList<ArrayList<Integer>>();
    while (true) {
      siguiente = new ArrayList<ArrayList<Integer>>(pi);
      // Recorre cada subconjunto
      for (ArrayList<Integer> subconjunto : siguiente) {
        HashMap<ArrayList<Integer>, ArrayList<Integer>> nuevosSubConjuntos = new HashMap<>();
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

    int cantEstadosNuevos = pi.size();

    int[][] transicionesMinimizadas = new int[alfabeto.length][pi.size()];

    for (int i = 0; i < pi.size(); i++) {
      // Como todas las transiciones son iguales, se toma el primer estado del subconjunto
      int estado = pi.get(i).get(0);
      
      for (int j = 0; j < alfabeto.length; j++) {
        int transicion = afdTransiciones[j][estado];
        // Estado al que corresponde la transición
        int nuevoEstado = encontrarConjunto(pi, transicion);
        // Guarda la transición en el nuevo AFD
        transicionesMinimizadas[j][i] = nuevoEstado;
      }
    }

    ArrayList<Integer> estadosFinalesNuevosA = new ArrayList<>();
    for (int i = 0; i < pi.size(); i++) {
      // Como todas las transiciones son iguales, se toma el primer estado del subconjunto
      int estado = pi.get(i).get(0);
      
      if (isFinal(estado, estadosFinales)) {
        estadosFinalesNuevosA.add(i);
      }
    }
    int[] estadosFinalesNuevos = new int[estadosFinalesNuevosA.size()];
    for (int i = 0; i < estadosFinalesNuevos.length; i++) {
      estadosFinalesNuevos[i] = estadosFinalesNuevosA.get(i);
    }


    if (archivo){
      genArchivoMinimizacion(transicionesMinimizadas, cantEstadosNuevos, alfabeto, estadosFinalesNuevos);
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

  private void genArchivoMinimizacion(int[][] transicionesMinimizadas, int cantEstados, char[] alfabeto, int[] estadosFinales) {
    try {
      FileWriter writer = new FileWriter("AFDmin.txt");
      for (int i = 0; i < alfabeto.length; i++) {
        writer.write(alfabeto[i]);
        if (i < alfabeto.length - 1) {
          writer.write(", ");
        }
      }
      writer.write(System.lineSeparator());
      writer.write(cantEstados + System.lineSeparator());
      for (int i = 0; i < estadosFinales.length; i++) {
        writer.write(estadosFinales[i]);
        if (i < estadosFinales.length - 1) {
          writer.write(", ");
        }
      }
      writer.write(System.lineSeparator());
      for (int fila = 0; fila < alfabeto.length; fila++) {
        for (int col = 0; col < transicionesMinimizadas[fila].length; col++) {
          writer.write(transicionesMinimizadas[fila][col]);
          if (col < transicionesMinimizadas[fila].length - 1) {
            writer.write(", ");
          }
        }
        writer.write(System.lineSeparator());
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public boolean parsingAFD(String cuerda, int[][] AFD, int[] estadosFinales){
    int estadoActual = ESTADOINICIAL;
    for (char c: cuerda.toCharArray()){
      if (c != 'a' && c != 'b' && c != 'c'){
        return false;
      }
      int cIndex = getCharIndex(c);
      if (cIndex == -1){
        return false;
      }
      estadoActual = AFD[cIndex][estadoActual];
    }
    if (isFinal(estadoActual, estadosFinales)){
      return true;
    } 
    return false;
  }

  private int getCharIndex(char c){
    switch (c){
      case 'a':
        return 0;
      case 'b':
        return 1;
      case 'c':
        return 2;
      default:
        return -1; // Error
    }
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
    switch (opcion) {
      case 1:
        // Implementar conversion a AFD
        break;
        /*
      case 2:
        int[][] afdTransiciones = getAFDTransiciones();
        int cantEstados = getCantEstados();
        int[] estadosFinales = getEstadosFinales();
        boolean imprimir = true;
        toGLD(alfabeto, afdTransiciones, cantEstados, estadosFinales, imprimir);
        // Implementar conversion a GLD
        break;
      case 3:
        // Implementar conversion a AFD
        // Implementar conversion a GLD
        int[][] afdTransiciones = getAFDTransiciones();
        int cantEstados = getCantEstados();
        int[] estadosFinales = getEstadosFinales();
        boolean imprimir = true;
        toGLD(alfabeto, afdTransiciones, cantEstados, estadosFinales, imprimir);
        break;
      default:
        System.out.println("Opcion no valida");
        break;
      */
    }
  }
}