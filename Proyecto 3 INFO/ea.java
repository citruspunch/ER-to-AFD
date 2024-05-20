import java.util.*;
import java.io.*;

class lP{
  //Esta clase es un apoyo, sirve para distinguir con que caracter de la expresion regular estamos tratando, si se trata de una letra, "a", o un operador, "*" .
  
  protected Character regex;
  protected boolean kleene,or,concat,er;
  protected ArrayList<Integer> posLetraActual, posLetraNext;
  protected ArrayList<lP> op1, op2;
  protected int num;
  protected lP letraAnt, letraNext;

  //contructor general usando principalmente para los operadores.
  public lP(char regex){
    this.regex = regex;
    this.kleene = false;
    this.or = false;
    this.concat = false;
    op1 = new  ArrayList<lP>();
    op2 = new  ArrayList<lP>();
  }
  
 //Constructor sobrecargado para las letas
  public lP(char regex, int num){
    this(regex);
    posLetraActual = new ArrayList<Integer>();
    posLetraNext = new ArrayList<Integer>();
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

  public ArrayList<lP> getOp1(){
    return this.op1;
  }

  public ArrayList<lP> getOp2(){
    return this.op2;
  }

  public void addLetra1(lP letra1){
    this.op1.add(letra1);
  }

  public void addLetra2(lP letra2){
    this.op2.add(letra2);
  }

  
  public String toString(){
    StringBuilder oStr = new StringBuilder();
    if(this.getRegex() == '*' || this.getRegex()=='.' || this.getRegex()== '+' || this.getRegex()== '|' || this.getRegex()== ')' || this.getRegex()== '(' ){
      oStr.append("{ "+this.getRegex() + " -> "+  this.op1.toString() + ", "+ this.op2 +" }");
      return oStr.toString();
    } else {
      oStr.append("{ "+this.getRegex() + " -> "+  this.posLetraActual.toString() + ", "+ this.posLetraNext +" }");
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

  //Este metodo hace un arraylist de objetos lP los cuales representan a los caracterres de la expresion regular junto a su posicion asocidada, basicamente sirve para tener un orden en la expresion regular.

  public static ArrayList<lP> auxPosiciones(String regex){
    
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
    System.out.println("Positions: ");
    System.out.println(posAux.toString());
    System.out.println("\n");
    return posAux;
  }

  public static ArrayList<lP> punterosLetras(ArrayList<lP> auxPosiciones){
  //Este metodo va inmediatamente despues de auxPosiciones pues recibe el resultado de este, ahora con el arrayList de auxPosiciones que nos da en orden la expresion regular, podemos separar cada tipo de objeto lP, letras y operadores.

    ArrayList<lP> letras = new ArrayList<lP>();
    
    for(int k = 0; k<auxPosiciones.size(); k++){
      lP actual = auxPosiciones.get(k);
//-------------------------------------------------------------------------
//metemos las letras a el Array letras
      if(inAlfabeto(actual.getRegex())){
        letras.add(actual);
      } 
    } 

    System.out.println("letras: ");
    System.out.println(letras.toString());
    System.out.println("\n");
  
    return letras;
  }

  public static ArrayList<lP> punterosOps(ArrayList<lP> auxPosiciones){
//Continuacion de punterosLetras, este solo toma los operadores.

    ArrayList<lP> ops = new ArrayList<lP>();

    for(int k = 0; k<auxPosiciones.size(); k++){
      lP actual = auxPosiciones.get(k);
//-------------------------------------------------------------------------
//metemos los operadores al array de operadores
      if(inOps(actual.getRegex())){
        ops.add(actual);
      }
    } 
  
    System.out.println("\n");
    System.out.println("ops: ");
    System.out.println(ops.toString());
    return ops;
  }
  
  /*funcion: 
[{ . -> [{ . -> [{ . -> [{ a -> [1], [1] }], [{ b -> [2], [2] }] }], [{ a -> [3], [3] }] }], [{ # -> [4], [4] }] }]*/

  public static ArrayList<lP> relacionesParentesis(ArrayList<lP> auxPosiciones){
    
    ArrayList<lP> funcion = new ArrayList<lP>(auxPosiciones);
    ArrayList<Integer> start = new ArrayList<Integer>();
    ArrayList<Integer> end = new ArrayList<Integer>();
    /*  */

    for(int a = 0; a<funcion.size(); a++){
      lP opActPar = funcion.get(a);
      if(opActPar.getRegex() == '('){
        start.add(a);
        //funcion.remove(a);
      } else if( opActPar.getRegex() == ')'){
        end.add(a);
        //funcion.remove(a);
        if(!start.isEmpty() && !end.isEmpty()){
          ArrayList<lP> auxAux = new ArrayList<lP>();
          int starte = start.remove(start.size()-1);
          int ende = end.remove(end.size()-1);

          for(int i = starte +1 ; i< ende; i=i+1){
            auxAux.add(funcion.get(i));
          }
          System.out.println("auxAux: "+auxAux.toString());
          
          ArrayList<lP> auxFuncion = relacionesSINparentesis(auxAux);
          
          System.out.println("AuxF: "+auxFuncion.toString());
          
          for(int o = ende; o>starte -1; o--){
            funcion.remove(o);
          }
          System.out.println("funcion1: "+funcion.toString());

          for(int g = 0; g<auxFuncion.size(); g++){
            funcion.add(starte , auxFuncion.get(g));
          }
          System.out.println("funcionAdd: "+funcion.toString());
          System.out.println(starte + "" );
          System.out.println(ende + "" );
          System.out.print("recur");
          a = starte + auxFuncion.size();
        }
      }
    }
    return funcion;
  }

  public static ArrayList<lP> relacionesSINparentesis(ArrayList<lP> auxPosiciones){
    
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
    ArrayList<lP> sinpar = relacionesParentesis(auxPosiciones);
    ArrayList<lP> fin = relacionesSINparentesis(sinpar);
    return fin;
  }

  
  public static String toHashString(HashMap<lP,ArrayList<lP>> vaina){
    StringBuilder str = new StringBuilder();
    str.append("{ ");
    
    str.append(" }");
    return str.toString();
  }


  public static void main(String[] args) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("write: ");
    String prueba = teclado.readLine();
    System.out.println(extendRE(prueba));
    auxPosiciones(extendRE(prueba));
    //punterosLetras(auxPosiciones(extendRE(prueba)));
    //punterosOps(auxPosiciones(extendRE(prueba)));
    relaciones(auxPosiciones(extendRE(prueba)));
  }
  
}