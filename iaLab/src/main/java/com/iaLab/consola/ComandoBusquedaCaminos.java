package com.iaLab.consola;


import com.iaLab.busquedainformadadecaminos.core.IAlgoritmoBusqueda;
import com.iaLab.busquedainformadadecaminos.core.IProblemaBusqueda;
import com.iaLab.busquedainformadadecaminos.core.ResultadoBusqueda;
import com.iaLab.busquedainformadadecaminos.laberintos.Cuadricula;
import com.iaLab.busquedainformadadecaminos.laberintos.CuadriculaProblema;
import com.iaLab.busquedainformadadecaminos.laberintos.Posicion;

import java.util.List;
import java.util.Optional;


/**
 * Busca un camino en una cuadrícula con uno o varios algoritmos (Strategy) y muestra el resultado.
 */
public final class ComandoBusquedaCaminos implements Comando {
  
  /**
   * Dos rutas: la directa cruza terreno caro (9); la larga rodea por terreno barato.
   */
  private static final List<String> MAPA_EJEMPLO = List.of(
      "S999999999G",
      ".#########.",
      "..3........",
      ".#.#.#.#.#.",
      ".....#.....");
  
  private final String titulo;
  private final List<IAlgoritmoBusqueda> algoritmos;
  
  /**
   * Crea un comando para resolver mapas con los algoritmos indicados.
   *
   * @param titulo     título que se muestra en el menú
   * @param algoritmos algoritmos que se ejecutarán sobre el mapa
   */
  public ComandoBusquedaCaminos(String titulo, List<IAlgoritmoBusqueda> algoritmos) {
    this.titulo = titulo;
    this.algoritmos = List.copyOf(algoritmos);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String titulo() {
    return titulo;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(Consola consola) {
    consola.println();
    consola.println("Leyenda:\nS inicio, G meta, # pared, . costo 1, 1-9 costo del terreno");
    Optional<Cuadricula> mapa = escogerMapa(consola);
    if (mapa.isEmpty()) {
      return;
    }
    IProblemaBusqueda<Posicion> problema = new CuadriculaProblema(mapa.get());
    for (IAlgoritmoBusqueda algoritmo : algoritmos) {
      consola.println();
      consola.println("--- " + algoritmo.nombre() + " ---");
      algoritmo.solucionar(problema).ifPresentOrElse(
          result -> imprimirSolucion(consola, mapa.get(), result),
          () -> consola.println("No existe camino entre S y G."));
    }
  }
  
  private Optional<Cuadricula> escogerMapa(Consola consola) {
    int opcion = consola.pedirOpcionDeMapaConVolver(
        "Mapa:\n 1) usar ejemplo\n 2) insertar mapa\n 0) volver\nSeleccione una opción: ",
        0, 2);
    if (opcion == 0) {
      throw new Consola.VolverAlMenuException();
    }
    List<String> lineas = opcion == 1
        ? MAPA_EJEMPLO
        : consola.pedirLineasConVolver(
        "Escribe el mapa, una fila por línea (línea vacía para terminar; escribe 'volver' para cancelar):");
    try {
      Cuadricula mapa = Cuadricula.parse(lineas);
      if (opcion == 1) {
        consola.println();
        consola.println(mapa.renderizar(List.of()));
      }
      return Optional.of(mapa);
    } catch (IllegalArgumentException e) {
      consola.println("Mapa inválido: " + e.getMessage());
      return Optional.empty();
    }
  }
  
  private void imprimirSolucion(Consola consola, Cuadricula mapa, ResultadoBusqueda<Posicion> resultado) {
    consola.println(
        String.format("Costo total: %.0f | Pasos: %d | Nodos expandidos: %d",
            resultado.costoTotal(), resultado.acciones().size(), resultado.nodosExpandidos()));
    consola.println(mapa.renderizar(resultado.estados()));
    consola.println("Movimientos: " + String.join(" -> ", resultado.acciones()));
  }
}
