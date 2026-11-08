package com.iaLab.consola;

import java.util.List;

/** Menú principal: lista los comandos y ejecuta el elegido hasta que el usuario sale. */
public final class Menu {

    private final Consola consola;
    private final List<Comando> comandos;

    /**
     * Crea un menú con la consola y los comandos indicados.
     *
     * @param consola consola para mostrar opciones y recibir la selección
     * @param comandos comandos disponibles en el menú
     */
    public Menu(Consola consola, List<Comando> comandos) {
        this.consola = consola;
        this.comandos = List.copyOf(comandos);
    }

    /**
     * Muestra el menú y ejecuta los comandos seleccionados hasta que el usuario sale o termina la entrada.
     */
    public void run() {
        try {
            while (true) {
                mostrarOpciones();
                int choice = consola.pedirOpcionDeMapa("Elige una opción: ", 0, comandos.size());
                if (choice == 0) {
                    consola.println("¡Hasta luego!");
                    return;
                }
                comandos.get(choice - 1).execute(consola);
            }
        } catch (Consola.InputClosedException e) {
            consola.println();
        }
    }

    private void mostrarOpciones() {
        consola.println();
        consola.println("=== Algoritmos de búsqueda ===");
        for (int i = 0; i < comandos.size(); i++) {
            consola.println((i + 1) + ". " + comandos.get(i).titulo());
        }
        consola.println("0. Salir");
    }
}
