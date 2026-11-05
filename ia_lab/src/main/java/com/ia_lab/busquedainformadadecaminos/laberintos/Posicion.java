package com.ia_lab.busquedainformadadecaminos.laberintos;


/** Celda de la cuadrícula (fila, columna). */
public record Posicion(int fila, int columna) {

    /**
     * Devuelve la posición resultante de aplicar una dirección.
     *
     * @param direccion dirección del movimiento
     * @return nueva posición después del movimiento
     */
    public Posicion mover(Direccion direccion) {
        return new Posicion(fila + direccion.getFilaDelta(), columna + direccion.getColDelta());
    }

    /**
     * Calcula la distancia Manhattan hasta otra posición.
     *
     * @param otra posición de destino
     * @return distancia Manhattan entre ambas posiciones
     */
    public int manhattanDistanciaA(Posicion otra) {
        return Math.abs(fila - otra.fila()) + Math.abs(columna - otra.columna());
    }
}
