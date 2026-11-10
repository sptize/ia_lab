package com.ialab.consola;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Fachada de entrada/salida: aísla el uso de Scanner y System.out del resto de la aplicación.
 */
public final class Consola {
  
  private final Scanner entrada;
  private final PrintStream salida;
  
  /**
   * Crea una consola con los flujos de entrada y salida indicados.
   *
   * @param entrada flujo del que se leen las respuestas
   * @param salida  flujo en el que se muestran los mensajes
   */
  public Consola(InputStream entrada, PrintStream salida) {
    this.entrada = new Scanner(entrada);
    this.salida = salida;
  }
  
  public void println() {
    salida.println();
  }
  
  /**
   * Muestra una línea de texto.
   *
   * @param texto texto que se muestra
   */
  public void println(String texto) {
    salida.println(texto);
  }
  
  /**
   * Muestra el mensaje y lee una línea.
   *
   * @param prompt mensaje que se muestra antes de leer
   * @return línea leída, sin espacios al inicio ni al final
   * @throws InputClosedException si ya no hay entrada disponible
   */
  public String preguntar(String prompt) {
    salida.print(prompt);
    salida.flush();
    if (!entrada.hasNextLine()) {
      throw new InputClosedException();
    }
    return entrada.nextLine().strip();
  }
  
  /**
   * Muestra un mensaje y lee una línea; escribir {@code volver} cancela el comando actual.
   *
   * @param prompt mensaje que se muestra antes de leer
   * @return línea leída, sin espacios al inicio ni al final
   * @throws VolverAlMenuException si el usuario solicita volver al menú principal
   */
  public String preguntarConVolver(String prompt) {
    String respuesta = preguntar(prompt);
    if (respuesta.equalsIgnoreCase("volver")) {
      throw new VolverAlMenuException();
    }
    return respuesta;
  }
  
  /**
   * Repite la pregunta hasta recibir un entero dentro del intervalo indicado.
   *
   * @param prompt mensaje que se muestra antes de leer
   * @param min    valor mínimo permitido
   * @param max    valor máximo permitido
   * @return entero leído dentro del intervalo {@code [min, max]}
   */
  public int pedirOpcionDeMapa(String prompt, int min, int max) {
    while (true) {
      String respuesta = preguntar(prompt);
      try {
        int valor = Integer.parseInt(respuesta);
        if (valor >= min && valor <= max) {
          return valor;
        }
      } catch (NumberFormatException ignored) {
        // se informa abajo
      }
      println("Ingresa un número entre " + min + " y " + max + ".");
    }
  }
  
  /**
   * Solicita un entero dentro del intervalo indicado; escribir {@code volver} cancela el comando.
   *
   * @param prompt mensaje que se muestra antes de leer
   * @param min    valor mínimo permitido
   * @param max    valor máximo permitido
   * @return entero leído dentro del intervalo {@code [min, max]}
   */
  public int pedirOpcionDeMapaConVolver(String prompt, int min, int max) {
    while (true) {
      String respuesta = preguntarConVolver(prompt);
      try {
        int valor = Integer.parseInt(respuesta);
        if (valor >= min && valor <= max) {
          return valor;
        }
      } catch (NumberFormatException ignored) {
        // se informa abajo
      }
      println("Ingresa un número entre " + min + " y " + max + ", o escribe 'volver'.");
    }
  }
  
  /**
   * Muestra un mensaje y lee líneas hasta encontrar una vacía.
   *
   * @param prompt mensaje que se muestra antes de leer las líneas
   * @return líneas leídas, sin incluir la línea vacía que termina la entrada
   */
  public List<String> pedirLineas(String prompt) {
    println(prompt);
    List<String> lineas = new ArrayList<>();
    while (true) {
      String linea = preguntar("> ");
      if (linea.isEmpty()) {
        return lineas;
      }
      lineas.add(linea);
    }
  }
  
  /**
   * Lee líneas hasta encontrar una vacía; escribir {@code volver} en cualquier línea cancela.
   *
   * @param prompt mensaje que se muestra antes de leer las líneas
   * @return líneas leídas, sin incluir la línea vacía que termina la entrada
   */
  public List<String> pedirLineasConVolver(String prompt) {
    println(prompt);
    List<String> lineas = new ArrayList<>();
    while (true) {
      String linea = preguntarConVolver("> ");
      if (linea.isEmpty()) {
        return lineas;
      }
      lineas.add(linea);
    }
  }
  
  /**
   * Señala que el usuario desea cancelar el comando actual y volver al menú principal.
   */
  public static final class VolverAlMenuException extends RuntimeException {
    private static final long serialVersionUID = 1L;
  }
  
  /**
   * Señala que la entrada terminó (Ctrl+D / fin de archivo).
   */
  public static final class InputClosedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public InputClosedException() {
      super("fin de la entrada");
    }
  }
}
