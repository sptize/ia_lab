package com.ia_lab.consola;


import java.util.Random;

import com.ia_lab.busquedainformadalocal.core.HillClimbing;
import com.ia_lab.busquedainformadalocal.core.ResultadoBusquedaLocal;
import com.ia_lab.busquedainformadalocal.ochoreinas.ProblemaOchoReinas;
import com.ia_lab.busquedainformadalocal.ochoreinas.TableroOchoReinas;

/** Resuelve las 8 reinas con Hill Climbing, con o sin reinicios aleatorios. */
public final class ComandoOchoReinas implements Comando {

    private static final int MAX_REINICIOS = 1000;

    /**
     * {@inheritDoc}
     */
    @Override
    public String titulo() {
        return "Hill Climbing - problema de las 8 reinas";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Consola consola) {
        consola.println();
        consola.println("Sin reinicios, Hill Climbing suele quedarse atascado en un óptimo local.");
        int reinicios = consola.pedirOpcionDeMapa(
                "Reinicios aleatorios permitidos (0 a " + MAX_REINICIOS + "): ", 0, MAX_REINICIOS);

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
