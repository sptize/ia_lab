package com.busquedainformada.ia_lab.algoritmos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import com.ia_lab.busquedainformadadecaminos.core.BusquedaAEstrella;
import com.ia_lab.busquedainformadadecaminos.core.IProblemaBusqueda;
import com.ia_lab.busquedainformadadecaminos.core.Paso;
import com.ia_lab.busquedainformadadecaminos.core.ResultadoBusqueda;
import com.ia_lab.busquedainformadadecaminos.core.BusquedaPrimeroMejor;

class BusquedaPrimeroMejorTest {

    @Test
    void claseBaseYHooksNoSonParteDeLaApiPublica() throws ReflectiveOperationException {
        assertFalse(Modifier.isPublic(BusquedaPrimeroMejor.class.getModifiers()));
        int prioridadModifiers = BusquedaPrimeroMejor.class
                .getDeclaredMethod("prioridad", double.class, double.class).getModifiers();
        int encolarModifiers = BusquedaPrimeroMejor.class
                .getDeclaredMethod("encolar", Double.class, double.class).getModifiers();
        assertFalse(Modifier.isPublic(prioridadModifiers) || Modifier.isProtected(prioridadModifiers));
        assertFalse(Modifier.isPublic(encolarModifiers) || Modifier.isProtected(encolarModifiers));
    }

    @Test
    void pasoRechazaCostosNegativosONoFinitos() {
        assertThrows(IllegalArgumentException.class, () -> new Paso<>("avanzar", "A", -1));
        assertThrows(IllegalArgumentException.class, () -> new Paso<>("avanzar", "A", Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new Paso<>("avanzar", "A", Double.POSITIVE_INFINITY));
    }

    @Test
    void aEstrellaEncuentraElCaminoOptimoConHeuristicaAdmisibleInconsistente() {
        IProblemaBusqueda<String> problema = new ProblemaDePrueba(
                Map.of(
                        "S", List.of(new Paso<>("a", "A", 3), new Paso<>("b", "B", 1)),
                        "A", List.of(new Paso<>("meta", "G", 2)),
                        "B", List.of(new Paso<>("mejorar", "A", 1), new Paso<>("alternativa", "G", 100)),
                        "G", List.of()),
                Map.of("S", 4.0, "A", 0.0, "B", 2.0, "G", 0.0));

        ResultadoBusqueda<String> resultado = new BusquedaAEstrella().solucionar(problema).orElseThrow();

        assertEquals(4.0, resultado.costoTotal());
        assertEquals(List.of("S", "B", "A", "G"), resultado.estados());
    }

    @Test
    void rechazaHeuristicasNegativasONoFinitas() {
        IProblemaBusqueda<String> noFinita = problemaConHeuristica(Double.NaN);
        IProblemaBusqueda<String> negativa = problemaConHeuristica(-1);

        assertThrows(IllegalArgumentException.class, () -> new BusquedaAEstrella().solucionar(noFinita));
        assertThrows(IllegalArgumentException.class, () -> new BusquedaAEstrella().solucionar(negativa));
    }

    @Test
    void rechazaPrioridadYCostoAcumuladoNoFinitos() {
        IProblemaBusqueda<String> prioridadNoFinita = new ProblemaDePrueba(
                Map.of("S", List.of(new Paso<>("llegar", "A", Double.MAX_VALUE)), "A", List.of()),
                Map.of("S", 0.0, "A", Double.MAX_VALUE));
        IProblemaBusqueda<String> costoAcumuladoNoFinito = new ProblemaDePrueba(
                Map.of(
                        "S", List.of(new Paso<>("primero", "A", Double.MAX_VALUE)),
                        "A", List.of(new Paso<>("desbordar", "G", Double.MAX_VALUE)),
                        "G", List.of()),
                Map.of("S", 0.0, "A", 0.0, "G", 0.0));

        assertThrows(IllegalArgumentException.class,
                () -> new BusquedaAEstrella().solucionar(prioridadNoFinita));
        assertThrows(IllegalArgumentException.class,
                () -> new BusquedaAEstrella().solucionar(costoAcumuladoNoFinito));
    }

    @Test
    void resultadoBusquedaValidaLaEstructuraYLosContadores() {
        assertEquals(1, new ResultadoBusqueda<>(List.of("S", "G"), List.of("avanzar"), 2, 1).acciones().size());
        assertThrows(IllegalArgumentException.class,
                () -> new ResultadoBusqueda<>(List.of("S", "G"), List.of(), 2, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ResultadoBusqueda<>(List.of(), List.of(), 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new ResultadoBusqueda<>(List.of("S"), List.of(), -1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new ResultadoBusqueda<>(List.of("S"), List.of(), 0, -1));
    }

    private static IProblemaBusqueda<String> problemaConHeuristica(double heuristica) {
        return new ProblemaDePrueba(
                Map.of("S", List.of()),
                Map.of("S", heuristica));
    }

    private record ProblemaDePrueba(
            Map<String, List<Paso<String>>> transiciones,
            Map<String, Double> heuristicas) implements IProblemaBusqueda<String> {

        @Override
        public String estadoInicial() {
            return "S";
        }

        @Override
        public boolean esMeta(String estado) {
            return estado.equals("G");
        }

        @Override
        public List<Paso<String>> sucesores(String estado) {
            return transiciones.getOrDefault(estado, List.of());
        }

        @Override
        public double heuristica(String estado) {
            return heuristicas.getOrDefault(estado, 0.0);
        }
    }
}
