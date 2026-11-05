package com.ia_lab.busquedainformadadecaminos.grafos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Representa un grafo ponderado con una heurística para cada nodo, un inicio y una meta.
 * La dirección de las aristas se determina al analizar su representación textual.
 */
public final class Grafo {
    /**
     * Arista saliente hacia otro nodo.
     *
     * @param destino nodo al que llega la arista
     * @param costo costo no negativo y finito de recorrer la arista
     */
    public record Arista(String destino, double costo) {}

    private final String inicio;
    private final String meta;
    private final Map<String, List<Arista>> adyacencias;
    private final Map<String, Double> heuristicas;

    private Grafo(String inicio, String meta, Map<String, List<Arista>> adyacencias, Map<String, Double> heuristicas) {
        this.inicio = inicio;
        this.meta = meta;
        this.adyacencias = adyacencias;
        this.heuristicas = heuristicas;
    }

    /**
     * Devuelve el nodo inicial del grafo.
     *
     * @return nombre del nodo inicial
     */
    public String getInicio() {
        return this.inicio;
    }

    /**
     * Devuelve el nodo meta del grafo.
     *
     * @return nombre del nodo meta
     */
    public String getMeta() {
        return this.meta;
    }

    /**
     * Devuelve la heurística asociada al nodo.
     *
     * @param nodo nombre del nodo
     * @return valor heurístico del nodo
     * @throws NullPointerException si el nodo no pertenece al grafo
     */
    public double getHeuristica(String nodo) {
        return heuristicas.get(nodo);
    }

    /**
     * Devuelve las aristas que salen del nodo indicado.
     *
     * @param nodo nombre del nodo de origen
     * @return lista inmutable de aristas salientes, o {@code null} si el nodo no existe
     */
    public List<Arista> aristasDesde(String nodo) {
        return adyacencias.get(nodo);
    }

    /**
     * Devuelve los nombres de todos los nodos del grafo.
     *
     * @return conjunto inmutable de nodos
     */
    public Set<String> nodos() {
        return adyacencias.keySet();
    }

    /**
     * Construye un grafo a partir de sus filas de texto.
     *
     * <p>Las directivas se indican con {@code :}. Por ello, los nombres de nodo
     * no pueden contener dos puntos, ya que el texto se interpretaría como una
     * directiva. Las secciones de nodos y aristas pueden aparecer en cualquier
     * orden.</p>
     *
     * <p>El análisis no valida la admisibilidad de las heurísticas. Para
     * comprobarla explícitamente en pruebas o experimentos, se puede invocar
     * {@link ValidadorHeuristica#validar(Grafo)}.</p>
     *
     * @param lineas líneas de entrada, con exactamente un inicio y una meta
     * @return grafo analizado e inmutable
     * @throws IllegalArgumentException si la lista es nula, el grafo no contiene nodos,
     *         contiene valores inválidos o no tiene exactamente un inicio y una meta
     */
    public static Grafo parsear(List<String> lineas) {
        if (lineas == null) {
            throw new IllegalArgumentException("La lista de líneas no puede ser nula");
        }
        Parser parser = new Parser();
        for (int i = 0; i < lineas.size(); i++) {
            int numeroLinea = i + 1;
            try {
                String entrada = lineas.get(i);
                if (entrada == null) {
                    throw new IllegalArgumentException("la línea no puede ser nula");
                }
                String linea = entrada.strip();
                parser.actualizarNumeroLinea(numeroLinea);
                parser.aceptar(linea);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("línea " + numeroLinea + ": " + e.getMessage(), e);
            }
        }
        Grafo grafo = parser.construir();
        return grafo;
    }

    private enum Seccion{ NINGUNA, NODOS, ARISTAS}
    record AristaSuelta(String origen, String destino, double costo, int numeroLinea) {}

    private static final class Parser {
        private boolean dirigido = true; // por defecto el grafo es dirigido
        private Seccion seccionActual = Seccion.NINGUNA;
        private int numeroLineaActual;
        private String inicio;
        private String meta;
        private final Map<String, Double> heuristicas = new LinkedHashMap<>();
        private final List<AristaSuelta> aristasSueltas = new ArrayList<>();

        void actualizarNumeroLinea(int numeroLinea) {
            numeroLineaActual = numeroLinea;
        }

        void aceptar(String linea) {
            //si esta vacia o empieza con # ignorar
            if (linea.isBlank() || linea.startsWith("#")) {
                return;
            }
            //si contiene ':' -> separar clave y valor, llamar a directiva(...)
            if (linea.contains(":")) {
                String[] partes = linea.split(":", 2);
                String clave = partes[0].trim();
                String valor = partes[1].trim();
                directiva(clave, valor);
            } else {
                //si no contiene ':' -> llamar segun requiera a nodo(...) o arista(...) segun seccionActual
                switch (seccionActual) {
                    case NODOS -> nodo(linea);
                    case ARISTAS -> arista(linea);
                    case NINGUNA -> throw new IllegalArgumentException("se esperaba una directiva o una sección de nodos");
                    
                }
            }
        }
        private void directiva(String clave, String valor) {
            switch (clave.toLowerCase(Locale.ROOT)) {
                case "inicio" -> {
                    if (inicio != null) {
                        throw new IllegalArgumentException("la directiva inicio no se puede repetir");
                    }
                    inicio = validarNombreDirectiva("inicio", valor);
                }
                case "meta" -> {
                    if (meta != null) {
                        throw new IllegalArgumentException("la directiva meta no se puede repetir");
                    }
                    meta = validarNombreDirectiva("meta", valor);
                }
                case "nodos" -> {
                    validarSeccionSinContenido("nodos", valor);
                    seccionActual = Seccion.NODOS;
                }
                case "aristas" -> {
                    validarSeccionSinContenido("aristas", valor);
                    seccionActual = Seccion.ARISTAS;
                }
                case "dirigido" -> dirigido = aBooleano(valor);
                default -> throw new IllegalArgumentException("directiva desconocida: " + clave);
            }
        }

        private String validarNombreDirectiva(String directiva, String valor) {
            if (valor.isBlank()) {
                throw new IllegalArgumentException("el valor de " + directiva + " no puede estar vacío");
            }
            if (valor.chars().anyMatch(Character::isWhitespace)) {
                throw new IllegalArgumentException("el valor de " + directiva + " debe ser un solo nombre de nodo");
            }
            return valor;
        }

        private void validarSeccionSinContenido(String seccion, String valor) {
            if (!valor.isEmpty()) {
                throw new IllegalArgumentException("la directiva " + seccion + " no acepta texto en la misma línea");
            }
        }

        private boolean aBooleano(String valor) {
            switch (valor.toLowerCase(Locale.ROOT)) {
                case "true", "sí", "si", "1" -> { return true; }
                case "false", "no", "0" -> { return false; }
                default -> throw new IllegalArgumentException("valor booleano inválido (admite true/false o si/no): " + valor);
            }
        }
        private void nodo(String linea) {
            String[] partes = linea.split("\\s+");
            if (partes.length != 2) {
                throw new IllegalArgumentException("se esperaba un nodo con heurística, por ejemplo 'A 6'");
            }
            if (heuristicas.containsKey(partes[0])) {
                throw new IllegalArgumentException("el nodo '" + partes[0] + "' está repetido");
            }
            double heuristica = numero(partes[1], "la heurística");
            if (heuristica < 0) {
                throw new IllegalArgumentException("la heurística debe ser un valor no negativo");
            }
            heuristicas.put(partes[0], heuristica);
        }

        private void arista(String linea) {
            String[] partes = linea.split("\\s+");
            if (partes.length != 3) {
                throw new IllegalArgumentException("se esperaba una arista con costo, por ejemplo 'A B 6'");
            }
            double costo = numero(partes[2], "el costo de la arista");
            if (costo < 0) {
                throw new IllegalArgumentException("el costo de la arista debe ser un valor no negativo");
            }
            aristasSueltas.add(new AristaSuelta(partes[0], partes[1], costo, numeroLineaActual));
        }
        
        Grafo construir() {
            if (heuristicas.isEmpty()) {
                throw new IllegalArgumentException("el grafo debe tener al menos un nodo");
            }
            if (inicio == null || meta == null) {
                throw new IllegalArgumentException("Se debe especificar un nodo de inicio y un nodo meta");
            }
            if (!heuristicas.containsKey(inicio)) {
                throw new IllegalArgumentException("El nodo de inicio '" + inicio + "' no está definido en la sección de nodos");
            }
            if (!heuristicas.containsKey(meta)) {
                throw new IllegalArgumentException("El nodo meta '" + meta + "' no está definido en la sección de nodos");
            }
            Map<String, List<Arista>> adyacencias = new LinkedHashMap<>();
            for (String nodo : heuristicas.keySet()) {
                adyacencias.put(nodo, new ArrayList<>()); //inicializar lista vacía de aristas para cada nodo
            }
            //recorremos las aristas sueltas y las agregamos a la lista de adyacencias del nodo origen
            for (AristaSuelta arista : aristasSueltas) {
                if (!adyacencias.containsKey(arista.origen)) {
                    throw new IllegalArgumentException("línea " + arista.numeroLinea + ": el nodo origen '" + arista.origen + "' de la arista no está definido en la sección de nodos");
                }
                if (!adyacencias.containsKey(arista.destino)) {
                    throw new IllegalArgumentException("línea " + arista.numeroLinea + ": el nodo destino '" + arista.destino + "' de la arista no está definido en la sección de nodos");
                }
                adyacencias.get(arista.origen).add(new Arista(arista.destino, arista.costo));
                //si el grafo no es dirigido, agregamos la arista inversa
                if (!dirigido) {
                    adyacencias.get(arista.destino).add(new Arista(arista.origen, arista.costo));
                }

            }
           adyacencias.replaceAll((nodo, aristas) -> List.copyOf(aristas)); //hacer inmutable la lista de aristas de cada nodo
           
           
           
            return new Grafo(inicio, meta, Collections.unmodifiableMap(adyacencias), Collections.unmodifiableMap(heuristicas));
        }
    }

    private static double numero(String texto, String queEs) {
        try {
            double valor = Double.parseDouble(texto);
            if (!Double.isFinite(valor)) {
                throw new IllegalArgumentException(queEs + " debe ser un número finito: '" + texto + "'");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(queEs + " no es un número válido: '" + texto + "'", e);
        }
    }

}
