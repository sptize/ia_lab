package com.ialab.consola;


import com.ialab.busquedainformadalocal.core.HillClimbing;
import com.ialab.busquedainformadalocal.core.ResultadoBusquedaLocal;
import com.ialab.busquedainformadalocal.ochoreinas.ProblemaOchoReinas;
import com.ialab.busquedainformadalocal.ochoreinas.TableroOchoReinas;

import java.util.Random;

/**
 * Resuelve las 8 reinas con Hill Climbing, con o sin reinicios aleatorios.
 */
public final class ComandoOchoReinas implements Comando {
  
  private static final int MAX_REINICIOS = 1000;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String titulo() {
    return "Hill Climbing - 8 reinas (con reinicios aleatorios)";
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(Consola consola) {
    consola.println();
    consola.println("Sin reinicios, Hill Climbing suele quedarse atascado en un óptimo local.");
    int reinicios = consola.pedirOpcionDeMapaConVolver(
        "Reinicios aleatorios permitidos (0 a " + MAX_REINICIOS + "; escribe 'volver' para regresar): ",
        0, MAX_REINICIOS);
    
    HillClimbing<TableroOchoReinas> hillClimbing = new HillClimbing<>(new Random());
    ResultadoBusquedaLocal<TableroOchoReinas> resultado = hillClimbing.solucionar(new ProblemaOchoReinas(), reinicios);
    
    consola.println();
    if (resultado.solucion()) {
      consola.println("Solución encontrada.");
    } else {
      int pairs = (int) resultado.costo();
      consola.println("Atascado en un óptimo local: "
          + (pairs == 1 ? "1 par de reinas se ataca." : pairs + " pares de reinas se atacan."));
    }
    consola.println("Pasos: " + resultado.pasos() + " | Reinicios: " + resultado.reinicios());
    consola.println(resultado.estado().toString());
  }
}
