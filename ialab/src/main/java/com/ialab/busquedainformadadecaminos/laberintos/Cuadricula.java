package com.ialab.busquedainformadadecaminos.laberintos;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Mapa inmutable de una cuadrícula con terrenos de distinto costo.
 * <pre>
 *   S inicio   G meta   # pared   . costo 1   1-9 costo del terreno
 * </pre>
 */
public final class Cuadricula {
  
  private static final char PARED = '#';
  private static final char INICIO = 'S';
  private static final char META = 'G';
  private static final char ABIERTO = '.';
  private static final int BLOQUEADO = 0;
  
  private final List<String> filas;
  private final int[][] costos;
  private final Posicion inicio;
  private final Posicion meta;
  
  private Cuadricula(List<String> filas, int[][] costos, Posicion inicio, Posicion meta) {
    this.filas = filas;
    this.costos = costos;
    this.inicio = inicio;
    this.meta = meta;
  }
  
  /**
   * Construye una cuadrícula a partir de sus filas de texto.
   *
   * @param lineas filas del mapa, con exactamente un inicio y una meta
   * @return cuadrícula analizada e inmutable
   * @throws IllegalArgumentException si el mapa está vacío, no es rectangular, contiene símbolos inválidos o no tiene exactamente un inicio y una meta
   */
  public static Cuadricula parse(List<String> lineas) {
    List<String> filas = lineas.stream().map(String::strip).filter(linea -> !linea.isEmpty()).toList();
    if (filas.isEmpty()) {
      throw new IllegalArgumentException("el mapa está vacío");
    }
    int ancho = filas.get(0).length();
    int[][] costos = new int[filas.size()][ancho];
    Posicion inicio = null;
    Posicion meta = null;
    
    for (int r = 0; r < filas.size(); r++) {
      String linea = filas.get(r);
      if (linea.length() != ancho) {
        throw new IllegalArgumentException("la fila " + (r + 1) + " no tiene el mismo ancho que la primera");
      }
      for (int c = 0; c < ancho; c++) {
        char simbolo = linea.charAt(c);
        Posicion position = new Posicion(r, c);
        switch (simbolo) {
          case PARED -> costos[r][c] = BLOQUEADO;
          case ABIERTO -> costos[r][c] = 1;
          case INICIO -> {
            if (inicio != null) {
              throw new IllegalArgumentException("hay más de un inicio (S)");
            }
            inicio = position;
            costos[r][c] = 1;
          }
          case META -> {
            if (meta != null) {
              throw new IllegalArgumentException("hay más de una meta (G)");
            }
            meta = position;
            costos[r][c] = 1;
          }
          default -> {
            if (simbolo < '1' || simbolo > '9') {
              throw new IllegalArgumentException(
                  "símbolo no válido '" + simbolo + "' en fila " + (r + 1) + ", columna " + (c + 1));
            }
            costos[r][c] = simbolo - '0';
          }
        }
      }
    }
    if (inicio == null || meta == null) {
      throw new IllegalArgumentException("el mapa necesita un inicio (S) y una meta (G)");
    }
    return new Cuadricula(filas, costos, inicio, meta);
  }
  
  /**
   * Devuelve la posición inicial de la cuadrícula.
   *
   * @return posición del inicio
   */
  public Posicion inicio() {
    return inicio;
  }
  
  /**
   * Devuelve la posición objetivo de la cuadrícula.
   *
   * @return posición de la meta
   */
  public Posicion meta() {
    return meta;
  }
  
  /**
   * Indica si una posición está dentro de la cuadrícula y no corresponde a una pared.
   *
   * @param posicion posición que se comprueba
   * @return {@code true} si la posición es transitable
   */
  public boolean esTransitable(Posicion posicion) {
    return posicion.fila() >= 0 && posicion.fila() < filas.size()
        && posicion.columna() >= 0 && posicion.columna() < costos[0].length
        && costos[posicion.fila()][posicion.columna()] != BLOQUEADO;
  }
  
  /**
   * Devuelve el costo de entrar a una celda transitable.
   *
   * @param posicion posición de la celda
   * @return costo de entrada a la celda
   * @throws NullPointerException     si {@code posicion} es {@code null}
   * @throws IllegalArgumentException si la posición está fuera de la cuadrícula o es una pared
   */
  public int costoDe(Posicion posicion) {
    Objects.requireNonNull(posicion, "posicion");
    if (!esTransitable(posicion)) {
      throw new IllegalArgumentException("la posición no es transitable: " + posicion);
    }
    return costos[posicion.fila()][posicion.columna()];
  }
  
  /**
   * Dibuja el mapa marcando con {@code *} las celdas del camino, sin tapar el inicio ni la meta.
   *
   * @param camino posiciones que se marcarán en el mapa
   * @return representación textual de la cuadrícula
   */
  public String renderizar(Collection<Posicion> camino) {
    Set<Posicion> enCamino = Set.copyOf(camino);
    StringBuilder salida = new StringBuilder();
    for (int r = 0; r < filas.size(); r++) {
      for (int c = 0; c < costos[0].length; c++) {
        char simbolo = filas.get(r).charAt(c);
        boolean marcado = enCamino.contains(new Posicion(r, c)) && simbolo != INICIO && simbolo != META;
        salida.append(marcado ? '*' : simbolo);
      }
      if (r < filas.size() - 1) {
        salida.append(System.lineSeparator());
      }
    }
    return salida.toString();
  }
}
