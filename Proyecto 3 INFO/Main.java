public void minimizarAFD(boolean archivo, int cantEstados, int[][] afdTransiciones, int[] estadosFinales, char[] alfabeto){
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
      ArrayList<ArrayList<Integer>> copiaDePi = new ArrayList<ArrayList<Integer>>(pi);
      siguiente = copiaDePi;
      // Recorre cada subconjunto
      for (ArrayList<Integer> subconjunto : siguiente) {
        HashMap<ArrayList<Integer>, ArrayList<Integer>> nuevosSubConjuntos = new HashMap<>();
        // Recorre cada estado del subconjunto
        for (int estado : subconjunto) {
          ArrayList<Integer> transitions = new ArrayList<>();
          // Recorre cada letra del alfabeto
          int letra = 0;
          while (letra < alfabeto.length){
            int transicion = afdTransiciones[letra][estado];
            int groupIndex = encontrarConjunto(pi, transicion);
            transitions.add(groupIndex);
            letra++;
          }
          if (!nuevosSubConjuntos.containsKey(transitions)) {
            ArrayList<Integer> nuevoSubConjunto = new ArrayList<>();
            nuevosSubConjuntos.put(transitions, nuevoSubConjunto);
          }
          ArrayList<Integer> subConjuntoDeEstado = nuevosSubConjuntos.get(transitions);
          subConjuntoDeEstado.add(estado);
        }
        pi.remove(subconjunto);
        for (ArrayList<Integer> nuevos : nuevosSubConjuntos.values()) {
          pi.add(nuevos);
        }
      }
      if (siguiente.size() > 0 && pi.size() > 0){
        // Si tiene las mismas particiones terminamos
        if (siguiente.size() == pi.size()) {
          break;
        }
      }
    }

    int cantEstadosNuevos = pi.size();

    // Determinar en que arraylist se encuentra el estado de error
    int estadoErrorIndex = encontrarEstadoError(pi);

    ArrayList<Integer> estadoError = pi.get(estadoErrorIndex);
    pi.remove(estadoErrorIndex);
    pi.add(0, estadoError);
    
    int estadoInicialIndex = encontrarEstadoInicial(pi);
    ArrayList<Integer> estadoInicial = pi.get(estadoInicialIndex);
    pi.remove(estadoInicialIndex);
    pi.add(1, estadoInicial);
    
    for(int i=2; i<cantEstadosNuevos; i++){
      int estadoIndex = encontrarConjunto(pi, i);
      ArrayList<Integer> estado = pi.get(estadoIndex);
      pi.remove(estadoIndex);
      pi.add(i, estado);
    }
    


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
      genArchivoMinimizacion(transicionesMinimizadas, cantEstadosNuevos, estadosFinalesNuevos, alfabeto);
    }
  }

  private int encontrarEstadoError(ArrayList<ArrayList<Integer>> conjunto) {
    for (int i = 0; i < conjunto.size(); i++) {
      if (conjunto.get(i).contains(ESTADOERROR)) {
        return i;
      }
    }
    return -1;
  }

  private int encontrarEstadoInicial(ArrayList<ArrayList<Integer>> conjunto){
    for (int i = 0; i < conjunto.size(); i++) {
        if (conjunto.get(i).contains(ESTADOINICIAL)) {
        return i;
      }
    }
    return -1;
  }

  private int encontrarConjunto(ArrayList<ArrayList<Integer>> conjunto, int estado) {
    for (int i = 0; i < conjunto.size(); i++) {
      if (conjunto.get(i).contains(estado)) {
        return i;
      }
    }
    return -1;
  }

  private void genArchivoMinimizacion(int[][] transicionesMinimizadas, int cantEstados, int[] estadosFinales, char[] alfabeto) {
    try {
      FileWriter writer = new FileWriter("AFDmin.afd");
      // Imprime el alfabeto
      for (int i = 0; i < alfabeto.length; i++) {
        writer.write(alfabeto[i]);
        if (i < alfabeto.length - 1) {
          writer.write(",");
        }
      }
      writer.write(System.lineSeparator());
      // Imprime la cantidad de estados
      writer.write(cantEstados + System.lineSeparator());
      for (int i = 0; i < estadosFinales.length; i++) {
        writer.write(Integer.toString(estadosFinales[i]));
        if (i < estadosFinales.length - 1) {
          writer.write(",");
        }
      }
      writer.write(System.lineSeparator());
      // Imprime las transiciones
      for (int fila = 0; fila < alfabeto.length; fila++) {
        for (int col = 0; col < transicionesMinimizadas[fila].length; col++) {
          writer.write(Integer.toString(transicionesMinimizadas[fila][col]));
          if (col < transicionesMinimizadas[fila].length - 1) {
            writer.write(",");
          }
        }
        writer.write(System.lineSeparator());
      }
      writer.close();
      System.out.println("Archivo Creado Exitosamente en: AFDmin.afd");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public ArrayList<String> toGLD(boolean archivo, int cantEstados, int[][] afdTransiciones, int[] estadosFinales, char[] alfabeto){
    ArrayList<String> GLD = new ArrayList<String>();
    // Se crea un array de caracteres con los nombres de los simbolos no terminales
    ArrayList<Character> noTerminales = new ArrayList<Character>();

    for(int i = 0; i < cantEstados; i++){
      if (i == ESTADOERROR){
        noTerminales.add((char) ('A'));
      } else if(i == ESTADOINICIAL){
        noTerminales.add('S');
      } else if(!((char) ('A' + i) == 'S')){
        noTerminales.add((char) ('A' + i));
      } else {
        noTerminales.add((char) ('A' + i + 1));
      }
    }

    for (int col=0; col<cantEstados; col++){
      for (int row=0; row<alfabeto.length; row++){
          if (col == ESTADOERROR){
            GLD.add(noTerminales.get(col) + " -> " + alfabeto[row] + noTerminales.get(ESTADOERROR));
          } else if (col == ESTADOINICIAL){
            GLD.add(noTerminales.get(col) + " -> " + alfabeto[row] + noTerminales.get(afdTransiciones[row][col]));
          } else {
            GLD.add(noTerminales.get(col) + " -> " + alfabeto[row] + noTerminales.get(afdTransiciones[row][col]));
          }
      }
    }
    for (int i = 0; i < estadosFinales.length; i++) {
      GLD.add(noTerminales.get(estadosFinales[i]) + " -> λ");
    }

    if (archivo){
      genArchivoGLD(GLD, noTerminales);
    }
    return GLD;
  }
  
  private void genArchivoGLD(ArrayList<String> GLD, ArrayList<Character> simbolosNT) {
    try {
      FileWriter writer = new FileWriter("GLD.gld");

      for (int i = 0; i < simbolosNT.size(); i++) {
        writer.write(simbolosNT.get(i));
        if (i < simbolosNT.size() - 1) {
          writer.write(",");
        }
      }
      writer.write(System.lineSeparator());
      writer.write("a,b,c,d" + System.lineSeparator()); 
      for (String str : GLD) {
        writer.write(str + System.lineSeparator());
      }
      writer.close();
      System.out.println("Archivo Creado Exitosamente en: GLD.gld");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean parsingAFD(String cuerda, int[][] AFD, int[] estadosFinales){
    int estadoActual = ESTADOINICIAL;
    for (char c: cuerda.toCharArray()){
      boolean encontrado = false;
      for (char letra : alfabeto){
        if (c == letra){
          encontrado = true;
        }
      }
      if (!encontrado){
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
      case 'd':
        return 3;
      default:
        return -1; // Error
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
  
  public static void main(String args[]) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("*********  MENU  *********\n");
    System.out.println("1. Convertir ER a AFD");
    System.out.println("2. Convertir ER a GLD");
    System.out.println("3. Convertir ER a AFD Minimo");
    System.out.println("4. Aceptación de cuerda");
    System.out.println("Ingrese la opcion: ");
    int opcion = Integer.parseInt(teclado.readLine());
    char[] alfabeto = {'a', 'b', 'c', 'd'};
    System.out.println("Alfabeto a usar: " + Arrays.toString(alfabeto));
    ea er = new ea();
    if (opcion == 1){
      er.toAFD(true);
    } else if (opcion == 2){
      int[][] afd = er.toAFD(false);
      int[] estadosFinales = er.toAFDestadosFinales();
      int cantEstados = afd[0].length;
      er.toGLD(true, cantEstados, afd, estadosFinales);
    } else if (opcion == 3){
      int[][] afd = er.toAFD(false);
      int[] estadosFinales = er.toAFDestadosFinales();
      int cantEstados = afd[0].length;
      er.minimizarAFD(true, afd, cantEstados, estadosFinales, alfabeto);
    } else if (opcion == 4){
      int[][] afd = er.toAFD(false);
      int[] estadosFinales = er.toAFDestadosFinales();
      while(true){
        System.out.println("Ingrese la cuerda a evaluar: ");
        String cuerda = teclado.readLine();
        if (cuerda.isEmpty()){
          break;
        }
        boolean aceptado = er.parsingAFD(cuerda, afd, estadosFinales);
        if (aceptado){
          System.out.println("La cuerda es aceptada");
        } else {
          System.out.println("La cuerda no es aceptada");
        }
      }
    }
  }