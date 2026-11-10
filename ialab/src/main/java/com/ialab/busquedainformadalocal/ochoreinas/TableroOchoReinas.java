package com.ialab.busquedainformadalocal.ochoreinas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tablero inmutable del problema de las 8 reinas.
 * Hay exactamente una reina por columna: {@code filas[columnas]} es la fila de la reina en esa columna.
 */
public final class TableroOchoReinas {
  
  public static final int TAMANIO = 8;
  
  private final int[] filas;
  
  private TableroOchoReinas(int[] filas) {
    this.filas = filas;
  }
  
  /**
   * Genera un tablero con una reina en una fila aleatoria de cada columna.
   *
   * @param aleatorio generador de números aleatorios
   * @return tablero generado
   */
  public static TableroOchoReinas aleatorio(Random aleatorio) {
    int[] filas = new int[TAMANIO];
    for (int columnas = 0; columnas < TAMANIO; columnas++) {
      filas[columnas] = aleatorio.nextInt(TAMANIO);
    }
    return new TableroOchoReinas(filas);
  }
  
  /**
   * Crea un tablero a partir de las filas ocupadas por las reinas en cada columna.
   *
   * @param filas fila de la reina de cada columna, en el rango {@code [0, TAMANIO)}
   * @return tablero inmutable con la distribución indicada
   * @throws IllegalArgumentException si no se proporcionan exactamente {@link #TAMANIO} filas o alguna está fuera de rango
   */
  public static TableroOchoReinas de(int... filas) {
    if (filas.length != TAMANIO) {
      throw new IllegalArgumentException("se esperaban " + TAMANIO + " filas");
    }
    for (int fila : filas) {
      if (fila < 0 || fila >= TAMANIO) {
        throw new IllegalArgumentException("fila fuera de rango: " + fila);
      }
    }
    return new TableroOchoReinas(filas.clone());
  }
  
  /**
   * Cuenta los pares de reinas que se atacan.
   *
   * @return número de pares de reinas que se atacan en la misma fila o diagonal; cero indica una solución
   */
  public int paresEnAtaque() {
    int pares = 0;
    for (int a = 0; a < TAMANIO; a++) {
      for (int b = a + 1; b < TAMANIO; b++) {
        boolean mismaFila = filas[a] == filas[b];
        boolean mismaDiagonal = Math.abs(filas[a] - filas[b]) == b - a;
        if (mismaFila || mismaDiagonal) {
          pares++;
        }
      }
    }
    return pares;
  }
  
  /**
   * Genera todos los tableros vecinos que resultan de mover una reina a otra fila de su columna.
   *
   * @return los 56 tableros vecinos
   */
  public List<TableroOchoReinas> vecinos() {
    List<TableroOchoReinas> vecinos = new ArrayList<>(TAMANIO * (TAMANIO - 1));
    for (int col = 0; col < TAMANIO; col++) {
      for (int fila = 0; fila < TAMANIO; fila++) {
        if (fila != filas[col]) {
          int[] mover = filas.clone();
          mover[col] = fila;
          vecinos.add(new TableroOchoReinas(mover));
        }
      }
    }
    return vecinos;
  }
  
  /**
   * Representa el tablero como una cuadrícula de caracteres.
   *
   * @return representación textual del tablero
   */
  @Override
  public String toString() {
    StringBuilder salida = new StringBuilder();
    for (int row = 0; row < TAMANIO; row++) {
      for (int col = 0; col < TAMANIO; col++) {
        salida.append(filas[col] == row ? 'Q' : '.');
        if (col < TAMANIO - 1) {
          salida.append(' ');
        }
      }
      if (row < TAMANIO - 1) {
        salida.append(System.lineSeparator());
      }
    }
    return salida.toString();
  }
}
