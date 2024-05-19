import java.util.*;
import java.io.*;

class lP{
  //Esta clase es un apoyo, sirve para distinguir con que caracter de la expresion regular estamos tratando, si se trata de una letra, "a", o un operador, "*" .
  
  protected Character regex;
  protected boolean kleene,or,concat,er;
  protected ArrayList<Integer> posActual, posNext;
  protected int num;
  protected lP letraAnt, letraNext;

  //contructor general usando principalmente para los operadores.
  public lP(char regex){
    this.regex = regex;
    this.kleene = false;
    this.or = false;
    this.concat = false;
    posActual = new  ArrayList<Integer>();
    posNext = new  ArrayList<Integer>();
  }
  
 //Constructor sobrecargado para las letas
  public lP(char regex, int num){
    this(regex);
    this.posActual.add(num);
    this.posNext.add(num);
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

  @Override
  public String toString(){
    StringBuilder oStr = new StringBuilder();
    oStr.append("{ "+this.getRegex() + " -> "+ "[ " + this.posActual.toString() + ", "+ this.posNext+ " ]"+" }");
    return oStr.toString();
  }

}

public class ea{
  private static char[] operadores = {'|','*','.','+','(',')'};
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

    /*System.out.println("letras: ");
    System.out.println(letras.toString());
    System.out.println("\n");*/
  
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
  
    /*System.out.println("\n");
    System.out.println("ops: ");
    System.out.println(ops.toString());*/
    return ops;
  }

  

  public static HashMap<lP, ArrayList<lP>> relaciones(ArrayList<lP> auxPosiciones){
    
    HashMap<lP, ArrayList<lP> > funcion = new HashMap<lP, ArrayList<lP> >();
    ArrayList<lP> dominio = new ArrayList<lP>();
    ArrayList<lP> dominioOPs = new ArrayList<lP>();

    for(int m = 0; m<auxPosiciones.size(); m++){
      lP punt1 = auxPosiciones.get(m);
      
      if(inAlfabeto(punt1.getRegex())){
        dominio.add(punt1);
        
      }else if(inOps(punt1.getRegex()){
        dominioOPs.add(punt1);
        
      }else if(punt1.getRegex() == ')'){
        
        while(dominioOPs.get(dominioOPs.size()-1).getRegex() != '('){
          
          ArrayList<lP> contradominio = new ArrayList<lP>();

          lP punt2 = 

          if(punt1.getKleene() && !dominio.isEmpty()){
            lP letter = dominio.remove(dominio.size()-1);
            contradominio.add(letter);

          } else if(punt1.getOr() && !dominio.isEmpty()){
            lP letter1 = dominio.remove(dominio.size()-1);
            lP letter2 = dominio.remove(dominio.size()-1);
            contradominio.add(letter2);
            contradominio.add(letter1);

          } else if(punt1.getConcat() && !dominio.isEmpty()){
            lP letter1 = dominio.remove(dominio.size()-1);
            lP letter2 = dominio.remove(dominio.size()-1);
            contradominio.add(letter2);
            contradominio.add(letter1);
          }
          
        }
        
      }else {
        dominioOPs.add(punt1);
      }
      
      if(inOps(punt1.getRegex())){
        ArrayList<lP> contradominio = new ArrayList<lP>();
        
        if(punt1.getKleene() && !dominio.isEmpty()){
          lP letter = dominio.remove(dominio.size()-1);
          contradominio.add(letter);
          
        } else if(punt1.getOr() && !dominio.isEmpty()){
          lP letter1 = dominio.remove(dominio.size()-1);
          lP letter2 = dominio.remove(dominio.size()-1);
          contradominio.add(letter2);
          contradominio.add(letter1);
          
        } else if(punt1.getConcat() && !dominio.isEmpty()){
          lP letter1 = dominio.remove(dominio.size()-1);
          lP letter2 = dominio.remove(dominio.size()-1);
          contradominio.add(letter2);
          contradominio.add(letter1);
        }

        funcion.put(punt1,contradominio);
      }
    }
    System.out.println(funcion.toString());
    return funcion;
  }

  
  public static String toHashString(HashMap<lP,ArrayList<lP>> vaina){
    StringBuilder str = new StringBuilder();
    str.append("{ ");
    
    str.append(" }");
    return str.toString();
  }

  public static int jerarquia(char op){
//Definimos la jerarquia de operaciones
    
    switch(op){
        case('*'):
        return 3;

        case('.'):
        return 2;

        case('|'):
        return 1;

        default:
        return 0;
    }
  }


  



  public static void main(String[] args) throws Exception{
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("write: ");
    String prueba = teclado.readLine();
    System.out.println(extendRE(prueba));
    auxPosiciones(extendRE(prueba));
    ArrayList<lP> puntL = punterosLetras(auxPosiciones(extendRE(prueba)));
    ArrayList<lP> puntO = punterosOps(auxPosiciones(extendRE(prueba)));
    relaciones(auxPosiciones(extendRE(prueba)));
  }
  
}