import java.util.*;
import java.io.*;

class lP{
  //Esta clase es un apoyo, sirve para distinguir con que caracter de la expresion regular estamos tratando, si se trata de una letra, "a", o un operador, "*" y asi poder asignarle una posicion concreta y estática a cada letra para que en el resto de métodos siempre tengamos consistencia con las posiciones.
  
  protected Character regex;
  protected boolean kleene,or,concat,er;
  protected ArrayList<Integer> posLetraActual, posLetraNext, posFP;
  protected ArrayList<lP> op1, op2;
  protected int num;

  //contructor general usando principalmente para los operadores.
  public lP(char regex){
    this.regex = regex;
    this.kleene = false;
    this.or = false;
    this.concat = false;
    this.op1 = new ArrayList<lP>();
    this.op2 = new ArrayList<lP>();
    this.posLetraActual = new ArrayList<Integer>();
    this.posLetraNext = new ArrayList<Integer>();
  }
  
 //Constructor sobrecargado para las letas
  public lP(char regex, int num){
    this(regex);
    this.posLetraActual = new ArrayList<Integer>();
    this.posLetraNext = new ArrayList<Integer>();
    this.posLetraActual.add(num);
    this.posLetraNext.add(num);
    this.er = true;
    
  }

  //Getters y setters 
  public Character getRegex(){
    return this.regex;
  }

  public boolean getKleene(){
    return this.kleene;
  }

  public boolean getOr(){
    return this.or;
  }

  public boolean getConcat(){
    return this.concat;
  }

  public void setKleene(boolean kleene){
    this.kleene = kleene;
  }

  public void setOr(boolean or){
    this.or = or;
  }

  public void setConcat(boolean concat){
    this.concat = concat;
  }

  public void addLetra1(lP letra1){
    this.op1.add(letra1);
  }

  public lP devolverLetra1(int num){
    return this.op1.get(num);
  }

  public void addLetra2(lP letra2){
    this.op2.add(letra2);
  }

  public lP devolverLetra2(int num){
    return this.op2.get(num);
  }

  
  public String toString(){
    StringBuilder oStr = new StringBuilder();
    if(this.getRegex() == '*' || this.getRegex()=='.' || this.getRegex()== '+' || this.getRegex()== '|' || this.getRegex()== ')' || this.getRegex()== '(' ){
      oStr.append("{ "+this.getRegex() + " -> "+  this.op1.toString() + ", "+ this.op2.toString()+ "posAnt: "+this.posLetraActual+","+"letraNxt:"+this.posLetraNext+" }");
      return oStr.toString();
    } else {
      oStr.append("{ "+this.getRegex() + " -> "+  this.posLetraActual.toString()+","+this.posLetraNext +" }");
      return oStr.toString();
    }
  }

}

public class ea{
  private static char[] operadores = {'|','*','.','+'};
  private static char[] alfabeto = {'a','b','c','d','#'};

  public static boolean inAlfabeto(char r){
  //Este metodo nos dice si un carcater pertenece al alfabeto con el que estamos trabajando.
    
    for(int l = 0; l<alfabeto.length; l++){
      if(r == alfabeto[l]){
        return true;
      } 
    }
    return false;
  }

  public static boolean inOps(char r){
    //Este metodo nos dice si un carcater pertenece al conjunto de operadores con el que estamos trabajando.

      for(int l = 0; l<operadores.length; l++){
        if(r == operadores[l]){
          return true;
        } 
      }
      return false;
    }

  public static void test(){
    //lo hice solo para saber si un addAll sobreescribia las cosas de un array o no
    ArrayList<Integer> test = new ArrayList<Integer>();
    test.add(1);
    test.add(2);
    System.out.println("test1:"+test.toString());
    ArrayList<Integer> test2 = new ArrayList<Integer>();
    test2.add(3);
    test2.add(4);
    System.out.println("test2:"+test2.toString());
    test.addAll(test2);
    System.out.println("testFinal:"+test.toString());
  }

  public static String extendRE(String re){
//Se encarga de agregar el caracter # donde debe finalizar la expresion regular, asi como agregar un "." donde hay concatenaciones y cambiar la clausura positiva + a una expresion de la forma a.a* .
    
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
      
//---------------------------------------------------------------
      //Buscamos los lugares correctos en los casos de Or donde poner el #.
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
//---------------------------------------------------------------
//Hacemos la conversion de b+ a b.b* la idea es que el caso inmediado de b+ b.b* se evalua viendo el caracter anterior si es del alfabeto y para el caso de los parentesis en un array metimos todas las posiciones donde encontramos un parentesis entpnces evalua si el caracter actual es + y si el anterior es un parentesis ) saca a el parentesis de cierre del array y hace recursion con la expresion de dentro. 
      
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
//-------------------------------------------------------------------
//Evaluamos las concatenaciones, basicamente tendremos concatenaciones en casos como aa, a(, )a, *(, *a, )( entonces agregamos un punto entre estos para decir que ahi hay una concatenacion, esta separacion nos servirá despues.

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
//---------------------------------------------------------------------
    }
    extendedRE.append('.');
    extendedRE.append("#");
    return extendedRE.toString();
  }

  public static ArrayList<lP> auxPosiciones(String regex){
    //Este metodo hace un arraylist de objetos lP los cuales representan a los caracterres de la expresion regular junto a su posicion asocidada, basicamente sirve para tener un orden en la expresion regular y que las posiciones de cada letra no se pierdan sino que se queden adheridas a cada letra siempre. 
    
    ArrayList<lP> posAux = new ArrayList<lP>();
    int positionen = 1;
    
    for(int i = 0; i<regex.length(); i++){
      char charAct = regex.charAt(i);
      if(inAlfabeto(charAct)){
        lP letraActual = new lP(charAct,positionen++);
        posAux.add(letraActual);
      } else{
        lP operadorActual = new lP(charAct);
        switch(charAct){
            case('*'):
            operadorActual.setKleene(true);
            break;

            case('|'):
            operadorActual.setOr(true);
            break;

            case('.'):
            operadorActual.setConcat(true);
            break;

          default:
            break;
        }
        posAux.add(operadorActual);
      }
    }
    /*System.out.println("Positions: ");
    System.out.println(posAux.toString());
    System.out.println("\n");*/
    return posAux;
  }
  
  /*funcion: 
[{ . -> [{ . -> [{ . -> [{ a -> [1], [1] }], [{ b -> [2], [2] }] }], [{ a -> [3], [3] }] }], [{ # -> [4], [4] }] }]*/

  public static ArrayList<lP> relacionesParentesis(ArrayList<lP> auxPosiciones){
  /*Esta funcion sirve para evaluar las expresiones dentro de parentesis, basicamente va agregando a un array las posiciones donde encuentre paréntesis y cuando estos se cierren crea un array auxiliar para meter los objetos del array original que estaban en parentesis a el array auxiliar, luego llamamos a ka funcion relacionesSINparentesis que es el mismo caso de esta funcion pero para expresiones que no llevan parentesis, se procesa y se mete a un nuevo array luego la expresion ya procesada se mete de nuevo en el array original y se continua el ciclo hasta que ya no hayan mas exoresiones en oarentesis por evaluar*/
    
    ArrayList<lP> funcion = new ArrayList<lP>(auxPosiciones);
    ArrayList<Integer> start = new ArrayList<Integer>();
    ArrayList<Integer> end = new ArrayList<Integer>();
    boolean falta = false;
    /*  */

    for(int a = 0; a<funcion.size(); a++){
      lP opActPar = funcion.get(a);
      if(opActPar.getRegex() == '('){
        start.add(a);
        //funcion.remove(a);
      } else if( opActPar.getRegex() == ')'){
        end.add(a);
        //funcion.remove(a);
        if(!start.isEmpty() /*&& !end.isEmpty()*/){
          ArrayList<lP> auxAux = new ArrayList<lP>();
          int starte = start.remove(start.size()-1);
          int ende = end.remove(end.size()-1);

          for(int i = starte +1 ; i< ende; i=i+1){
            auxAux.add(funcion.get(i));
          }
          //System.out.println("auxAux: "+auxAux.toString());
          
          ArrayList<lP> auxFuncion = relacionesSINparentesis(auxAux);
          
          //System.out.println("AuxF: "+auxFuncion.toString());
          
          for(int o = ende; o>starte -1; o--){
            funcion.remove(o);
          }
          //System.out.println("funcion1: "+funcion.toString());

          for(int g = 0; g<auxFuncion.size(); g++){
            funcion.add(starte , auxFuncion.get(g));
          }
          /*System.out.println("funcionAdd: "+funcion.toString());
          System.out.println(starte + "" );
          System.out.println(ende + "" );
          System.out.print("recur");*/
          a = starte + auxFuncion.size();
        }
      } 
    }
    for(int k = 0; k<funcion.size(); k++){
      lP act = funcion.get(k);
      if(act.getRegex() == ')' || act.getRegex()=='('){
        falta = true;
        break;
      }
    }
    if(falta){
      return relacionesParentesis(funcion);
    } else {
      return funcion;
    }
  }

  public static ArrayList<lP> relacionesSINparentesis(ArrayList<lP> auxPosiciones){
  /*Esta funcion crea las relaciones entre operadores y letras, es decir, el propópsito de esta funcion es determinar a que cosas afecta cierto operador, por ejemplo a|b esta funcion nos dice a que operandos esta afectando el operador or, en este caso a,b y el resultado de eso lo devuelve como un array, utilizamos 3 fors para recorrer la expresion y haciendo las operaciones requeridas, el primer for se encarga de hacer la relacion para los operadores kleene, el segundo for para los operadores concatenacion y el tercero para los operadores or y en dichos fors se evalua cada caso posible*/
    
    ArrayList<lP> funcion = new ArrayList<lP>(auxPosiciones);
    
    for(int m = 0; m<funcion.size(); m++){
      lP opActKleene = funcion.get(m);

      if(opActKleene.getKleene() || opActKleene.getRegex() == '*'){
        if(m > 0 && m<funcion.size()){
          if(funcion.get(m-1) != null && inAlfabeto(funcion.get(m-1).getRegex())){
            lP letra1 = funcion.remove(m-1);
            opActKleene.addLetra1(letra1);
            m--;
          } else if( funcion.get(m-1) != null && inOps(funcion.get(m-1).getRegex())){
            lP letra1 = funcion.remove(m-1);
            opActKleene.addLetra1(letra1);
            m--;
          }
        } 
      }
    }

    for(int n = 0; n<funcion.size(); n++){
      lP opActConcat = funcion.get(n);

      if(opActConcat.getConcat()){
        if(n > 0 && n<funcion.size()-1){
          if((funcion.get(n-1) != null && inAlfabeto(funcion.get(n-1).getRegex())) && (funcion.get(n+1) != null && inAlfabeto(funcion.get(n+1).getRegex())) ){
            lP letra1 = funcion.remove(n-1);
            lP letra2 = funcion.remove(n);
            opActConcat.addLetra1(letra1);
            opActConcat.addLetra2(letra2);
            n--;

          } else if( (funcion.get(n-1) != null && inOps(funcion.get(n-1).getRegex())) && (funcion.get(n+1) != null && inOps(funcion.get(n+1).getRegex())) ){
            lP letra1 = funcion.remove(n-1);
            lP letra2 = funcion.remove(n);
            opActConcat.addLetra1(letra1);
            opActConcat.addLetra2(letra2);
            n--;

          } else if( (funcion.get(n-1) != null && inOps(funcion.get(n-1).getRegex())) &&  (funcion.get(n+1) != null && inAlfabeto(funcion.get(n+1).getRegex()))){
            lP letra1 = funcion.remove(n-1);
            lP letra2 = funcion.remove(n);
            opActConcat.addLetra1(letra1);
            opActConcat.addLetra2(letra2);
            n--;

          } else if ( (funcion.get(n-1) != null && inAlfabeto(funcion.get(n-1).getRegex())) && (funcion.get(n+1) != null && inOps(funcion.get(n+1).getRegex()))){
            lP letra1 = funcion.remove(n-1);
            lP letra2 = funcion.remove(n);
            opActConcat.addLetra1(letra1);
            opActConcat.addLetra2(letra2);
            n--;
          }
        }
      } 
    }
    for(int l = 0; l<funcion.size(); l++){
      lP opActOr = funcion.get(l);

      if(opActOr.getOr()){
        if(l > 0 && l<funcion.size()-1){
          if((funcion.get(l-1) != null && inAlfabeto(funcion.get(l-1).getRegex())) && (funcion.get(l+1) != null && inAlfabeto(funcion.get(l+1).getRegex())) ){
            lP letra1 = funcion.remove(l-1);
            lP letra2 = funcion.remove(l);
            opActOr.addLetra1(letra1);
            opActOr.addLetra2(letra2);
            l--;

          } else if( (funcion.get(l-1) != null && inOps(funcion.get(l-1).getRegex())) && (funcion.get(l+1) != null && inOps(funcion.get(l+1).getRegex())) ){
            lP letra1 = funcion.remove(l-1);
            lP letra2 = funcion.remove(l);
            opActOr.addLetra1(letra1);
            opActOr.addLetra2(letra2);
            l--;

          } else if( (funcion.get(l-1) != null && inOps(funcion.get(l-1).getRegex())) &&  (funcion.get(l+1) != null && inAlfabeto(funcion.get(l+1).getRegex()))){
            lP letra1 = funcion.remove(l-1);
            lP letra2 = funcion.remove(l);
            opActOr.addLetra1(letra1);
            opActOr.addLetra2(letra2);
            l--;

          } else if ( (funcion.get(l-1) != null && inAlfabeto(funcion.get(l-1).getRegex())) && (funcion.get(l+1) != null && inOps(funcion.get(l+1).getRegex()))){
            lP letra1 = funcion.remove(l-1);
            lP letra2 = funcion.remove(l);
            opActOr.addLetra1(letra1);
            opActOr.addLetra2(letra2);
            l--;
          }
        }
      }
    }
    System.out.println("\n");
    System.out.println("funcion: ");
    System.out.println(funcion.toString());
    return funcion;
  }

  public static ArrayList<lP> relaciones(ArrayList<lP> auxPosiciones){
  /*esta ya es la implementacion de los dos metodos anteriores para crear un array definitivo*/
    ArrayList<lP> sinpar = relacionesParentesis(auxPosiciones);
    ArrayList<lP> fin = relacionesSINparentesis(sinpar);
    return fin;
  }

  public static ArrayList<Integer> firstPos(ArrayList<lP> fin){
    /*Una vez obtenido el array definitivo con las funciones anteriores hacemos un for para recorrer a todos los elementos de este y en cada iteracion iremos evaluando lo siguiente, si estamos en una concatenacion su primera posicion será lo que tenga a la izquierda siempre y cuando esta expresion no sea un operador, si lo es, metemos ese operador al array posiblefirstPos y llamamos recursivamente a firstPos() con el array de posibles. Luego para los ors operamos de manera similar, si a sus lados tiene solo letras estas seran posibles posiciones, si no, de nuevo metemos el operador a el array de posibles y hacemos recursividad con este, ahora para el caso de kleene, este en el 99% de los casos estara metido entre una concatenacion por lo que lo evaluamos dentro de los ifs de las concatenaciones diciendo que si la concatenacion tiene un kleene en su lado izquierdo debemos agregar a firstPos el lado izquiero y derecho de la expresion por aquello que kleene puede ser lambda, de lo contrario hacemos el if especifico de kleene y agreamos su posicion correspondiente a firstpos*/
    
    ArrayList<lP> posiblefirstPos = new ArrayList<lP>();
    ArrayList<Integer> firstPosA = new ArrayList<Integer>();
    
    for(int a = 0; a<fin.size(); a++){
      lP analisis = fin.get(a);
      if(analisis.getConcat()){
        lP letra1 = analisis.devolverLetra1(0);
        lP letra2 = analisis.devolverLetra2(0);
        
        if(inAlfabeto(letra1.getRegex()) ){
          firstPosA.add( letra1.posLetraActual.get(0) );
          
        } else if(inAlfabeto(letra1.getRegex()) && inOps(letra2.getRegex()) ){
          firstPosA.add( letra1.posLetraActual.get(0) );
          
        } else if( inOps(letra1.getRegex())  ){
          if(letra1.getKleene()){
            
            if(inAlfabeto(letra2.getRegex())){
              firstPosA.add( letra2.posLetraActual.get(0) );
              posiblefirstPos.add(letra1);
              firstPosA.addAll(firstPos(posiblefirstPos));
              
            } else if(inOps(letra2.getRegex())){
              posiblefirstPos.add(letra1);
              posiblefirstPos.add(letra2);
              firstPosA.addAll(firstPos(posiblefirstPos));
            }
          } else {
            posiblefirstPos.add(letra1);
            firstPosA.addAll(firstPos(posiblefirstPos));
          }
        }
        
      } else if(analisis.getOr()){
        lP letra1 = analisis.devolverLetra1(0);
        lP letra2 = analisis.devolverLetra2(0);

        if( inAlfabeto(letra1.getRegex()) && inAlfabeto(letra2.getRegex()) ){
          firstPosA.add( letra1.posLetraActual.get(0) );
          firstPosA.add( letra2.posLetraActual.get(0) );
          
        } else if( inAlfabeto(letra1.getRegex()) && inOps(letra2.getRegex()) ){
          firstPosA.add( letra1.posLetraActual.get(0) );
          posiblefirstPos.add(letra2);
          firstPosA.addAll(firstPos(posiblefirstPos));
          
        } else if( inOps(letra1.getRegex()) && inAlfabeto(letra2.getRegex()) ){
          posiblefirstPos.add(letra1);
          firstPosA.addAll(firstPos(posiblefirstPos));
          firstPosA.add( letra2.posLetraActual.get(0) );
        } else if( inOps(letra1.getRegex()) && inOps(letra2.getRegex()) ){
          posiblefirstPos.add(letra1);
          posiblefirstPos.add(letra2);
          firstPosA.addAll(firstPos(posiblefirstPos));
        }
        
      } else if(analisis.getKleene()){
        lP letra1 = analisis.devolverLetra1(0);
        if(inAlfabeto(letra1.getRegex())){
          firstPosA.add( letra1.posLetraActual.get(0) );
        } else if( inOps(letra1.getRegex()) ){
          posiblefirstPos.add(letra1);
          firstPosA.addAll(firstPos(posiblefirstPos));
        }
      }
      
    }
//Quitamos posiciones que se hayan agregado repetidas
    HashSet<Integer> transitorio = new HashSet<Integer>();
    for(int q=0; q<firstPosA.size(); q++){
      transitorio.add(firstPosA.remove(q));
      q--;
    }

    firstPosA.addAll(transitorio);
    
    /*System.out.println(posiblefirstPos.toString());*/
    System.out.println(firstPosA.toString());
    return firstPosA; 
  }

  public static lP numAletra(ArrayList<lP> array, int num){
  /*El proposito de esta funcion es saber que numero tiene cierta letra asociada, como en los metodos de firstPos y followPos estamos trabajando solo con las posiciones, despues necesitamos saber a que letra corresponde esa posicion.*/
    for(int p = 0; p<array.size(); p++){
      lP letraBuscada = array.get(0);
      if(letraBuscada.posLetraActual.contains(num)){
        System.out.println("letra: "+letraBuscada.getRegex());
        return letraBuscada;
      } 
    }
    return null;
  }

  public static void followPs(ArrayList<lP> fin){
    for(int g = 0; g<fin.size(); g++){
      lP inicio = fin.get(g);
      followPs(inicio.op1);
      followPs(inicio.op2);
      if(inicio.getConcat()){
        ArrayList<Integer> recursion = new ArrayList<Integer>();
        ArrayList<Integer> recursion2 = new ArrayList<Integer>();
        lP letra1 = inicio.devolverLetra1(0);
        lP letra2 = inicio.devolverLetra2(0);
        System.out.println("CASO CONCATENACION");
        System.out.println("letra:"+inicio.getRegex());
        System.out.println("derivado1:"+letra1.getRegex());
        System.out.println("derivado2:"+letra2.getRegex());
        inicio.setKleene(letra1.getKleene() && letra2.getKleene());
        if(letra1.getRegex() == '*'){
          recursion.addAll(letra1.posLetraNext);
          recursion.addAll(letra2.posLetraNext);
        } else{
          recursion.addAll(letra2.posLetraNext);
        }
        ArrayList<Integer> noDuplicados = quitarDuplicados(recursion);
        if(!noDuplicados.isEmpty()) {
          inicio.posLetraNext.addAll(noDuplicados);
        }
        
        if(letra2.getRegex()=='*'){
          recursion2.addAll(letra1.posLetraActual);
          recursion2.addAll(letra2.posLetraActual);
        } else{
          recursion2.addAll(letra2.posLetraActual);
        }
        ArrayList<Integer> noDuplicados2 = quitarDuplicados(recursion);
        if(!noDuplicados2.isEmpty()){
          inicio.posLetraActual.addAll(noDuplicados2);
        }
        
      } else if(inicio.getOr()){
        ArrayList<Integer> recursion = new ArrayList<Integer>();
        ArrayList<Integer> recursion2 = new ArrayList<Integer>();
        lP letra1 = inicio.devolverLetra1(0);
        lP letra2 = inicio.devolverLetra2(0);
        System.out.println("CASO OR");
        System.out.println("letra:"+inicio.getRegex());
        System.out.println("derivado1:"+letra1.getRegex());
        System.out.println("derivado2:"+letra2.getRegex());
        inicio.setKleene(letra1.getKleene() || letra2.getKleene());
        recursion.addAll(letra1.posLetraNext);
        recursion.addAll(letra2.posLetraNext);
        recursion2.addAll(letra1.posLetraActual);
        recursion2.addAll(letra2.posLetraActual);
        ArrayList<Integer> noDuplicados = quitarDuplicados(recursion);
        if(!noDuplicados.isEmpty()){
          inicio.posLetraNext.addAll(noDuplicados);
        }
        ArrayList<Integer> noDuplicados2 = quitarDuplicados(recursion);
        if(!noDuplicados2.isEmpty()){
          inicio.posLetraActual.addAll(noDuplicados2);
        }
        
      } else if(inicio.getRegex() == '*'){
        ArrayList<Integer> recursion = new ArrayList<Integer>();
        ArrayList<Integer> recursion2 = new ArrayList<Integer>();
        lP letra1 = inicio.devolverLetra1(0);
        System.out.println("CASO KLEENE");
        System.out.println("letra:"+inicio.getRegex());
        System.out.println("derivado1:"+letra1.getRegex());
        recursion.addAll(letra1.posLetraNext);
        recursion2.addAll(letra1.posLetraNext);
        
        ArrayList<Integer> noDuplicados = quitarDuplicados(recursion);
        if(!noDuplicados.isEmpty()){
          inicio.posLetraNext.addAll(noDuplicados);
          ArrayList<Integer> arrayPrueba = inicio.posLetraNext;
        }
        ArrayList<Integer> noDuplicados2 = quitarDuplicados(recursion);
        if(!noDuplicados2.isEmpty()){
          inicio.posLetraActual.addAll(noDuplicados2);
        }
      }
      inicio.posLetraActual = quitarDuplicados(inicio.posLetraActual);
      inicio.posLetraNext = quitarDuplicados(inicio.posLetraNext);
      System.out.println("letra next:"+inicio.posLetraNext.toString());
      System.out.println("letra ant:"+inicio.posLetraActual.toString());
    }
  }
  public static ArrayList<Integer> quitarDuplicados(ArrayList<Integer> duplicados){
    ArrayList<Integer> noDuplicados =  new ArrayList<Integer>();
    for(int i = 0; i<duplicados.size(); i++){
      int comp = duplicados.get(i);
      if(!noDuplicados.contains(comp)){
        noDuplicados.add(comp);
      }
    }
    return noDuplicados;
  }
  public static void correcionfolowpos(ArrayList<lP> posfin){
    followPs(posfin);
    
    /*for(int y =0; y<posfin.size(); y++){
      lP inicio = posfin.get(y);
      
      correcionfolowpos(inicio.op1);
      correcionfolowpos(inicio.op2);
      if(inicio.getConcat()){
        lP letra1 = inicio.devolverLetra1(0);
        lP letra2 = inicio.devolverLetra2(0);
        
      }
      HashSet<Integer> transitorio = new HashSet<Integer>();
      for(int q=0; q<inicio.posLetraNext.size(); q++){
        transitorio.add(inicio.posLetraNext.remove(q));
        q--;
      }
      inicio.posLetraNext.addAll(transitorio);

      HashSet<Integer> transitorio2 = new HashSet<Integer>();
      for(int q=0; q<inicio.posLetraActual.size(); q++){
        transitorio.add(inicio.posLetraActual.remove(q));
        q--;
      }
      inicio.posLetraActual.addAll(transitorio2);
    }*/
    
    System.out.println(posfin.toString());
  }

  public static void main(String[] args) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("write: ");
    String prueba = teclado.readLine();
    System.out.println(extendRE(prueba));
    auxPosiciones(extendRE(prueba));
    relaciones(auxPosiciones(extendRE(prueba)));
    firstPos(relaciones(auxPosiciones(extendRE(prueba))));
    //ArrayList<Integer> fp = new ArrayList<Integer>();
    //casoBasefollowPos( relaciones(auxPosiciones(extendRE(prueba))));
    //followPos(relaciones(auxPosiciones(extendRE(prueba))));
    ArrayList<lP> inicio = relaciones(auxPosiciones(extendRE(prueba)));
    //lP fin = inicio.get(0);
    followPs( inicio );
    correcionfolowpos(inicio);
    //analisisfolowpos(inicio);
    //HashMap<Integer, ArrayList<Integer>> follow = new HashMap<Integer, ArrayList<Integer>>();
    //foPos(inicio, follow);
    //follow(fin);
    //test();
  }
  
}