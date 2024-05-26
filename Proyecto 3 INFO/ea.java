import java.util.*;
import java.io.*;

class lP{
  //Esta clase es un apoyo, sirve para distinguir con que caracter de la expresion regular estamos tratando, si se trata de una letra, "a", o un operador, "*" y asi poder asignarle una posicion concreta y estática a cada letra para que en el resto de métodos siempre tengamos consistencia con las posiciones.

  protected Character regex;
  protected boolean kleene,or,concat,er,hashtag;
  protected ArrayList<Integer> posLetraActual, posLetraNext, posEnFP, posConLetra;
  protected ArrayList<lP> op1, op2;
  protected int num;

  //contructor general usando para los operadores.
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

  //para las posiciones de followPos
  public lP(ArrayList<Integer> posEnFP, char letra){
    this.regex = letra;
    this.posEnFP = new ArrayList<Integer>();
    this.posEnFP.addAll(posEnFP);
    this.posConLetra = new ArrayList<Integer>();
    this.hashtag = false;
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

  @Override
    public boolean equals(Object otro) {
      if(otro instanceof lP){
        lP otro2 = (lP) otro;
        if(this.regex.equals(otro2.regex) && this.posEnFP.equals(otro2.posEnFP)){
          return true;
        }
      }
      return false; 
    }

  public String toString(){
    StringBuilder oStr = new StringBuilder();
    if(this.getRegex() == '*' || this.getRegex()=='.' || this.getRegex()== '+' || this.getRegex()== '|' || this.getRegex()== ')' || this.getRegex()== '(' ){
      oStr.append("{ "+this.getRegex() + " -> "+  this.op1.toString() + ", "+ this.op2.toString()+ "posAnt: "+this.posLetraActual+","+"letraNxt:"+this.posLetraNext+" }");
      return oStr.toString();
    } else if(this.posLetraActual == null && this.posLetraNext == null){
      oStr.append(toStringFP());
      return oStr.toString();
      } else {
      oStr.append("{ "+this.getRegex() + " -> "+  this.posLetraActual.toString()+","+this.posLetraNext +" }");
      return oStr.toString();
    }
  }

  public String toStringFP(){
    StringBuilder oStr = new StringBuilder();
    oStr.append(this.posEnFP.toString() + ","+this.regex);
    return oStr.toString();
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
    /*System.out.println("\n");
    System.out.println("funcion: ");
    System.out.println(funcion.toString());*/
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
    /*System.out.println(firstPosA.toString());*/
    return firstPosA; 
  }

  public static char numAletra(int num, String regex,ArrayList<lP> posiciones){
  /*El proposito de esta funcion es saber que numero tiene cierta letra asociada, como en los metodos de firstPos y followPos estamos trabajando solo con las posiciones, despues necesitamos saber a que letra corresponde esa posicion.*/
    //System.out.println(regex);
    //System.out.println(posiciones.toString());
    for(int p = 0; p<posiciones.size(); p++){
      lP letraBuscada = posiciones.get(p);
      if(letraBuscada.posLetraActual.contains(num)){
        //System.out.println("letra: "+letraBuscada.getRegex());
        return letraBuscada.getRegex();
      }
    }
    return 'e';
  }

  public static void followPs(ArrayList<lP> fin){
    for(int g = 0; g<fin.size(); g++){
      lP inicio = fin.get(g);
      if(inicio.getConcat()){
        ArrayList<Integer> recursion = new ArrayList<Integer>();
        ArrayList<Integer> recursion2 = new ArrayList<Integer>();
        lP letra1 = inicio.devolverLetra1(0);
        lP letra2 = inicio.devolverLetra2(0);
        followPs(inicio.op1);
        followPs(inicio.op2);
        /*System.out.println("CASO CONCATENACION");
        System.out.println("letra:"+inicio.getRegex());
        System.out.println("derivado1:"+letra1.getRegex());
        System.out.println("derivado2:"+letra2.getRegex());*/
        /*recursion -> siguiente -> posNetx
          recursion2 -> actual  -> posAct */
        inicio.setKleene(letra1.getKleene() & letra2.getKleene());
        if(letra1.getKleene() && !letra2.getKleene()){
          recursion.addAll(letra2.posLetraNext);
          recursion2.addAll(letra1.posLetraActual);
          recursion2.addAll(letra2.posLetraActual);

        }else if(!letra1.getKleene() && letra2.getKleene()){
          recursion2.addAll(letra1.posLetraActual);
          recursion.addAll(letra2.posLetraNext);
          recursion.addAll(letra1.posLetraNext);

        }else if(letra1.getKleene() && letra2.getKleene()){
          recursion.addAll(letra1.posLetraNext);
          recursion.addAll(letra2.posLetraNext);
          recursion2.addAll(letra1.posLetraActual);
          recursion2.addAll(letra2.posLetraActual);

        }else if(!letra1.getKleene() && !letra2.getKleene()){
          recursion.addAll(letra2.posLetraNext);
          recursion2.addAll(letra1.posLetraNext);
        }

        ArrayList<Integer> noDuplicados = quitarDuplicados(recursion);
        if(!noDuplicados.isEmpty()) {
          inicio.posLetraNext.addAll(noDuplicados);
        }

        ArrayList<Integer> noDuplicados2 = quitarDuplicados(recursion2);
        if(!noDuplicados2.isEmpty()){
          inicio.posLetraActual.addAll(noDuplicados2);
        }

      } else if(inicio.getOr()){
        followPs(inicio.op1);
        followPs(inicio.op2);
        ArrayList<Integer> recursion = new ArrayList<Integer>();
        ArrayList<Integer> recursion2 = new ArrayList<Integer>();
        lP letra1 = inicio.devolverLetra1(0);
        lP letra2 = inicio.devolverLetra2(0);
        /*System.out.println("CASO OR");
        System.out.println("letra:"+inicio.getRegex());
        System.out.println("derivado1:"+letra1.getRegex());
        System.out.println("derivado2:"+letra2.getRegex());*/
        inicio.setKleene(letra1.getKleene() | letra2.getKleene());
        recursion.addAll(letra1.posLetraNext);
        recursion.addAll(letra2.posLetraNext);
        recursion2.addAll(letra1.posLetraActual);
        recursion2.addAll(letra2.posLetraActual);
        ArrayList<Integer> noDuplicados = quitarDuplicados(recursion);
        if(!noDuplicados.isEmpty()){
          inicio.posLetraNext.addAll(noDuplicados);
        }
        ArrayList<Integer> noDuplicados2 = quitarDuplicados(recursion2);
        if(!noDuplicados2.isEmpty()){
          inicio.posLetraActual.addAll(noDuplicados2);
        }

      } else if(inicio.getKleene()){
        followPs(inicio.op1);
        ArrayList<Integer> recursion = new ArrayList<Integer>();
        ArrayList<Integer> recursion2 = new ArrayList<Integer>();
        lP letra1 = inicio.devolverLetra1(0);
        /*System.out.println("CASO KLEENE");
        System.out.println("letra:"+inicio.getRegex());
        System.out.println("derivado1:"+letra1.getRegex());*/
        inicio.setKleene(true);
        letra1.setKleene(true);
        recursion.addAll(letra1.posLetraNext);
        recursion2.addAll(letra1.posLetraActual);

        ArrayList<Integer> noDuplicados = quitarDuplicados(recursion);
        if(!noDuplicados.isEmpty()){
          inicio.posLetraNext.addAll(noDuplicados);
        }
        ArrayList<Integer> noDuplicados2 = quitarDuplicados(recursion2);
        if(!noDuplicados2.isEmpty()){
          inicio.posLetraActual.addAll(noDuplicados2);
        }
      }
      inicio.posLetraActual = quitarDuplicados(inicio.posLetraActual);
      inicio.posLetraNext = quitarDuplicados(inicio.posLetraNext);
      /*System.out.println("letra next:"+inicio.posLetraNext.toString());
      System.out.println("letra ant:"+inicio.posLetraActual.toString());*/
    }
    //System.out.println("FINAL:"+fin.toString());
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

  public static void conexiones(ArrayList<lP> folow, HashMap<Integer, ArrayList<Integer>> conex){
    try{
      for(int d = 0; d<folow.size(); d++){
        lP inicio = folow.get(d);
        if(inicio.getConcat()){
          lP letra1 = inicio.devolverLetra1(0);
          lP letra2 = inicio.devolverLetra2(0);
          for(int pos = 0; pos<letra1.posLetraNext.size(); pos++){
            int posicion = letra1.posLetraNext.get(pos);
            if(!conex.containsKey(posicion)){
              conex.put(posicion,new ArrayList<Integer>());
            }

            conex.get(posicion).addAll(letra2.posLetraActual);
            quitarDuplicados(conex.get(posicion));
          }
        } else if(inicio.getOr()){
          lP letra1 = inicio.devolverLetra1(0);
          lP letra2 = inicio.devolverLetra2(0);
        } else if(inicio.getKleene()){
          lP letra1 = inicio.devolverLetra1(0);
          for(int sop = 0; sop<letra1.posLetraNext.size(); sop++){
            int noicisop = letra1.posLetraNext.get(sop);
            if(!conex.containsKey(noicisop)){
              conex.put(noicisop,new ArrayList<Integer>());
            }
            conex.get(noicisop).addAll(letra1.posLetraActual);
            quitarDuplicados(conex.get(noicisop));
          }
        }
        conexiones(inicio.op1,conex);
        conexiones(inicio.op2,conex);
      }
      //System.out.println("conex:" + conex.toString());
    }catch(IndexOutOfBoundsException e){
      //na
    }
  }

  public static HashMap<Integer, ArrayList<Integer>> conexionesFULL(ArrayList<lP> folow){
    HashMap<Integer, ArrayList<Integer>> conex = new HashMap<Integer, ArrayList<Integer>>();
    conexiones(folow,conex);
    //System.out.println("TABLITA:" + conex.toString());
    return conex;
  }

  public static boolean contieneHashtag(ArrayList<Integer> estado, String regex, ArrayList<lP> aux){
    for (int indice=0; indice<estado.size(); indice++) {
      char at = numAletra(indice, regex, aux);
      if (at == '#') {
        return true;
      }
    }
    return false;
  }

  public static ArrayList<Integer> orden(ArrayList<Integer> ant){
    //es un bubble sort
    ArrayList<Integer> nuevo = new ArrayList<Integer>(ant);
    int i,j,change;
    boolean cambio;
    for(i = 0; i<nuevo.size()-1; i++){
      cambio = false;
      for(j=0; j<nuevo.size()-1; j++ ){
        if(nuevo.get(j) > nuevo.get(j+1)){
          change = nuevo.get(j);
          nuevo.set(j,nuevo.get(j+1));
          nuevo.set(j+1, change);
          cambio=true;
        }
      }
      if(cambio==false){
        break;
      }
    }
    return nuevo;
  }

  public static void transicionesInexistentes(HashMap<lP, ArrayList<Integer>>follow2Pos, ArrayList<Integer> analisis, char a, String regex, ArrayList<lP> aux){
    lP transInexistente = new lP(analisis,a);
    for(int l =0; l<analisis.size(); l++){
      int pos = analisis.get(l);
      if(numAletra(pos,regex, aux) == '#')
        transInexistente.hashtag=true;
    }
    if(!follow2Pos.containsKey(transInexistente)){
      follow2Pos.put(transInexistente, new ArrayList<Integer>(analisis));
    }
  }


  public static ArrayList<ArrayList<Integer>> follow2Pos(HashMap<Integer, ArrayList<Integer>> conex, HashMap<lP, ArrayList<Integer>>follow2Pos, ArrayList<Integer> firstPos, String regex, ArrayList<lP> aux){
    ArrayList<ArrayList<Integer>> estados = new ArrayList<ArrayList<Integer>>();
    //[[1,2,3],[2,3,4],..,[1,5,6]]
    //[1,2,3] con a -> [1,2]
    //lp[1,2,3],a - >[1,2]
    //hashmap([1,2,3],a)=[1,2]
    System.out.println(regex);
    estados.add(0,quitarDuplicados(orden(firstPos)));
    System.out.println("firstPos:"+estados.get(0).toString());

    for(int x = 0; x<estados.size(); x++){
      ArrayList<Integer> analisis = estados.get(x);
      ArrayList<Integer> letraA = new ArrayList<Integer>();
      ArrayList<Integer> letraB = new ArrayList<Integer>();
      ArrayList<Integer> letraC = new ArrayList<Integer>();
      ArrayList<Integer> letraD = new ArrayList<Integer>();
      ArrayList<Integer> hashtag = new ArrayList<Integer>();
      //System.out.println("analisis:"+analisis.toString());

      for(int w = 0; w<analisis.size(); w++){
        int pos = analisis.get(w);
        //System.out.println("pos: "+pos);
        if(numAletra(pos,regex,aux) == 'a'){
          letraA.add(pos);
          //System.out.println("A:"+letraA.toString());
        } else if(numAletra(pos,regex, aux) == 'b'){
          letraB.add(pos);
          //System.out.println("B:"+letraB.toString());
        } else if(numAletra(pos,regex,aux) == 'c'){
          letraC.add(pos);
          //System.out.println("C:"+letraC.toString());
        } else if(numAletra(pos,regex,aux) == 'd'){
          letraD.add(pos);
          //System.out.println("D:"+letraD.toString());
        } else if(numAletra(pos,regex,aux) == '#'){
          hashtag.add(pos);
          //System.out.println("D:"+hashtag.toString());
        } else {
          continue;
        }

      }
      ArrayList<Integer> nuevoA = new ArrayList<Integer>();
      if(!letraA.isEmpty()){
        for(int l = 0; l<letraA.size(); l++){
          int a = letraA.get(l);
          nuevoA.addAll(conex.get(a));
          //System.out.println("nuevoA: "+nuevoA.toString());
        }
        ArrayList<Integer> nuevoAorden = quitarDuplicados(orden(nuevoA));
        lP transicion = new lP(analisis,'a');
        for(int z = 0; z<hashtag.size(); z++){
          int hashAct = hashtag.get(z);
          for(int k = 0; k<nuevoAorden.size(); k++){
            if(nuevoAorden.contains(hashAct)){
              transicion.hashtag = true;
            }
          }
        }
        follow2Pos.put(transicion,nuevoAorden);
        if(!estados.contains(nuevoAorden)){
          estados.add(nuevoAorden);
        }
      }

      ArrayList<Integer> nuevoB = new ArrayList<Integer>();
      if(!letraB.isEmpty()){
        for(int l = 0; l<letraB.size(); l++){
          int b = letraB.get(l);
          nuevoB.addAll(conex.get(b));
          //System.out.println("nuevoB: "+nuevoB.toString());
        }
        ArrayList<Integer> nuevoBorden = quitarDuplicados(orden(nuevoB));
        lP transicionB = new lP(analisis,'b');
        for(int z = 0; z<hashtag.size(); z++){
          int hashAct = hashtag.get(z);
          for(int k = 0; k<nuevoBorden.size(); k++){
            if(nuevoBorden.contains(hashAct)){
              transicionB.hashtag = true;
            }
          }
        }
        follow2Pos.put(transicionB,nuevoBorden);
        if(!estados.contains(nuevoBorden)){
          estados.add(nuevoBorden);
        }
      }

      ArrayList<Integer> nuevoC = new ArrayList<Integer>();
      if(!letraC.isEmpty()){
        for(int l = 0; l<letraC.size(); l++){
          int c = letraC.get(l);
          nuevoC.addAll(conex.get(c));
          //System.out.println("nuevoC: "+nuevoC.toString());
        }
        ArrayList<Integer> nuevoCorden = quitarDuplicados(orden(nuevoC));
        lP transicionC = new lP(analisis,'c');
        for(int z = 0; z<hashtag.size(); z++){
          int hashAct = hashtag.get(z);
          for(int k = 0; k<nuevoCorden.size(); k++){
            if(nuevoCorden.contains(hashAct)){
              transicionC.hashtag = true;
            }
          }
        }
        follow2Pos.put(transicionC,nuevoCorden);
        if(!estados.contains(nuevoCorden)){
          estados.add(nuevoCorden);
        }
      }

      ArrayList<Integer> nuevoD = new ArrayList<Integer>();
      if(!letraD.isEmpty()){
        for(int l = 0; l<letraD.size(); l++){
          int d = letraD.get(l);
          nuevoD.addAll(conex.get(d));
          //System.out.println("nuevoD: "+nuevoD.toString());
        }
        ArrayList<Integer> nuevoDorden = quitarDuplicados(orden(nuevoD));
        lP transicionD = new lP(analisis,'d');
        for(int z = 0; z<hashtag.size(); z++){
          int hashAct = hashtag.get(z);
          for(int k = 0; k<nuevoDorden.size(); k++){
            if(nuevoDorden.contains(hashAct)){
              transicionD.hashtag = true;
            }
          }
        }
        follow2Pos.put(transicionD,nuevoDorden);
        if(!estados.contains(nuevoDorden)){
          estados.add(nuevoDorden);
        }
      }

    }
    //System.out.println("estados:"+estados.toString());
    //System.out.println(follow2Pos.toString());
    return estados;
  }

  public static void reconstruirHashFollow(HashMap<lP, ArrayList<Integer>>follow2Pos, ArrayList<ArrayList<Integer>> estados, HashMap<lP, ArrayList<Integer>>follow3Pos, String regex, ArrayList<lP> aux){

    for(int y = 0; y<estados.size(); y++){
      ArrayList<Integer> est = estados.get(y);
      for(lP keys : follow2Pos.keySet()){
        if(keys.posEnFP.equals(est)){
          ArrayList<Integer> nuevo = new ArrayList<Integer>();
          nuevo.add(y+1);
          lP neww = new lP(nuevo,keys.regex);
          follow3Pos.put(neww,follow2Pos.get(keys));
        }
      }
    }

    System.out.println(follow3Pos.toString());

  }

  public static int[][] transicionesAFD(HashMap<lP, ArrayList<Integer>> followPos, ArrayList<ArrayList<Integer>> estados){

      char[] alfabeto = {'a', 'b', 'c', 'd'};

      int[][] transiciones = new int[alfabeto.length][estados.size() + 1];

      HashMap<ArrayList<Integer>, Integer> posiciones = new HashMap<ArrayList<Integer>, Integer>();

      for (int i = 0; i < estados.size(); i++) {
        posiciones.put(estados.get(i), i + 1);
      }

      for (lP key : followPos.keySet()) {
        for (int i = 0; i < estados.size(); i++) {
          if (estados.get(i).equals(key.posEnFP)) {
            for (int j = 0; j < alfabeto.length; j++) {
              if (key.regex == alfabeto[j]) {
                transiciones[j][i+1] = posiciones.get(followPos.get(key));
              }
            }
          }
        }
      }  
      return transiciones;
  }

  public static void imprimirAFD(int[][]transiciones, ArrayList<Integer> estadosFinales){
    try {
      FileWriter writer = new FileWriter("AFDregex.txt");
      // Imprimir alfabeto
      char[] alfabeto = {'a', 'b', 'c', 'd'};
      for (int i = 0; i < alfabeto.length; i++) {
        writer.write(alfabeto[i]);
        if (i < alfabeto.length - 1) {
          writer.write(",");
        }
      }
      writer.write(System.lineSeparator());
      // Imprimir cantidad de estados
      writer.write(Integer.toString(transiciones[0].length));
      writer.write(System.lineSeparator());
      // Imprimir estados finales
      for (int i = 0; i < estadosFinales.size(); i++) {
        writer.write(estadosFinales.get(i).toString());
        if (i < estadosFinales.size() - 1) {
          writer.write(",");
        }
      }
      writer.write(System.lineSeparator());
      // Imprimir estados
      for (int i = 0; i < transiciones.length; i++) {
          for (int j = 0; j < transiciones[i].length; j++) {
              writer.write(Integer.toString(transiciones[i][j]));
              if (j < transiciones[i].length - 1) {
                  writer.write(",");
              }
          }
          writer.write(System.lineSeparator());
      }     
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }



  public static void main(String[] args) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("write: ");
    String prueba = teclado.readLine();
    //System.out.println(extendRE(prueba));
    String test = extendRE(prueba);
    ArrayList<lP> aux = auxPosiciones(extendRE(prueba));
    ArrayList<Integer> firstpos = firstPos(relaciones(auxPosiciones(extendRE(prueba))));
    //System.out.println("firstPos: "+firstpos.toString());
    ArrayList<lP> inicio = relaciones(auxPosiciones(extendRE(prueba)));
    followPs( inicio );
    HashMap<Integer, ArrayList<Integer>> follow = conexionesFULL(inicio);
    HashMap<lP, ArrayList<Integer>> hashnuevo = new HashMap<lP, ArrayList<Integer>>();
    HashMap<lP, ArrayList<Integer>> hashReconstruido = new HashMap<lP, ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> estado = follow2Pos(follow,hashnuevo,firstpos,test, aux);
    //System.out.println("TRANSICIONES: "+hashnuevo.toString());
    //System.out.println("ESTADOS"+follow2Pos(follow,hashnuevo,firstpos,test, aux).toString());
    //reconstruirHashFollow(hashnuevo,estado,hashReconstruido, test, aux);
    //System.out.println("hashReconstruido: "+hashReconstruido.toString());
    int[][] transiciones = transicionesAFD(hashnuevo,estado);
    HashMap<ArrayList<Integer>, Integer> posiciones = new HashMap<ArrayList<Integer>, Integer>();
    for (int i = 0; i < estado.size(); i++) {
      posiciones.put(estado.get(i), i + 1); 
    }
    ArrayList<Integer> estadosFinales = new ArrayList<Integer>();
    for (int i = 0; i < estado.size(); i++) {
        ArrayList<Integer> estadoActual = estado.get(i);
        for (Integer numero : estadoActual) {
            if (numAletra(numero, prueba, aux) == '#') {
                estadosFinales.add(i + 1);
                break;
            }
        }
    }
    imprimirAFD(transiciones, estadosFinales);
  }
}