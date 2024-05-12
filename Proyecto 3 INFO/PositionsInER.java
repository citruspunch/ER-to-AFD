import java.io.*;
import java.util.*;

public class PositionsInER {

  private Character letter;
  private PositionsInER father, sonIZQ, sonDER;
  private HashSet<Integer> firstPos, lastPos, followPos;
  private int num;
  private boolean posAnidada;
  private boolean kleene;

  public PositionsInER(Character letra, int num) {
    this.letter = letra;
    this.num = num;
    this.father = null;
    this.sonIZQ = null;
    this.sonDER = null;
    this.firstPos = new HashSet<Integer>();
    this.lastPos = new HashSet<Integer>();
    this.followPos = new HashSet<Integer>();
    this.kleene = false;
    this.posAnidada = false;
  }

  /* getters setter para la firstPosition de la ER */
  public void foundFirstPos(int position) {
    this.firstPos.add(position);
  }

  public void reachLastPos(int position) {
    this.lastPos.add(position);
  }

  public void addFollowPos(int num) {
    this.followPos.add(num);
  }

  public HashSet<Integer> getFP() {
    return this.firstPos;
  }

  public HashSet<Integer> getLP() {
    return this.lastPos;
  }

  public HashSet<Integer> getFollowPos() {
    return this.followPos;
  }

  public int getNum() {
    return this.num;
  }

  public void setNum(int num) {
    this.num = num;
  }

  public boolean getPosAnidada() {
    return this.posAnidada;
  }

  public void setPosAnidada(boolean posAnidada) {
    this.posAnidada = posAnidada;
  }

  /*
   * kleene es el operador * entonces verifica si el nodo en el que estamos tiene
   * a kleene
   */
  public boolean foundKleene() {
    return this.kleene;
  }

  public void setKleene(boolean klin) {
    this.kleene = klin;
  }

  /* getter setter para padre e hijos */
  public PositionsInER foundFather() {
    return this.father;
  }

  public PositionsInER foundsonIZQ() {
    return this.sonIZQ;
  }

  public PositionsInER foundsonDER() {
    return this.sonDER;
  }

  public void setFather(PositionsInER padre) {
    this.father = padre;
  }

  public void setSonIZQ(PositionsInER izquierda) {
    this.sonIZQ = izquierda;
  }

  public void setSonDER(PositionsInER derecha) {
    this.sonDER = derecha;
  }

  public static void main(String[] args) throws Exception {
    BufferedReader tec = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Char: ");
    String write = tec.readLine();
    char wrote = write.charAt(0);

    PositionsInER prueba = new PositionsInER(wrote);
    System.out.println("character 'abcd'" + prueba.letter);
    System.out.println("Posicion inical FIRSTPOS" + prueba.getFP());
    System.out.println("en que termina el nodo analizado LASTPOS" + prueba.getLP());

    prueba.foundFirstPos(0);
    prueba.foundFirstPos(2);
    prueba.foundFirstPos(5);
    prueba.reachLastPos(3);
    prueba.reachLastPos(1);
    // Posicion repetida
    prueba.reachLastPos(3);

    System.out.println("FIRSTPOS" + prueba.getFP());
    System.out.println("LASTPOS" + prueba.getLP());

    System.out.println("Se encontró kleene?" + prueba.foundKleene());
    prueba.setKleene(true);
    System.out.println("Se encontró kleene?" + prueba.foundKleene());

    PositionsInER padre = new PositionsInER('|');
    PositionsInER hijoIZQ = new PositionsInER('a');
    PositionsInER hijoDER = new PositionsInER('b');

    padre.setSonIZQ(hijoIZQ);
    padre.setSonDER(hijoDER);

    System.out.println("\n PADRE: " + padre.letter);
    System.out.println("hijoIZQ: " + padre.foundsonIZQ().letter);
    System.out.println("hijoDER: " + padre.foundsonDER().letter);

    hijoIZQ.setFather(padre);
    hijoDER.setFather(padre);

    System.out
        .println("hijoIZQ padre: " + hijoIZQ.foundFather().letter + "HijoDER padre: " + hijoDER.foundFather().letter);
  }

}