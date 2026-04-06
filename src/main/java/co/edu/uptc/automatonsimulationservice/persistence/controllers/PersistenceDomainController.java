package co.edu.uptc.automatonsimulationservice.persistence.controllers;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.persistence.models.AutomatonFile;
import co.edu.uptc.automatonsimulationservice.persistence.models.StatePosition;
import co.edu.uptc.automatonsimulationservice.persistence.services.AutomatonJsonPersistenceService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class PersistenceDomainController {

    private final AutomatonJsonPersistenceService automatonJsonPersistenceService;

    /**
     * Constructor del controlador de dominio encargado de leer y guardar datos en archivos.
     */
    public PersistenceDomainController(AutomatonJsonPersistenceService automatonJsonPersistenceService) {
        this.automatonJsonPersistenceService = automatonJsonPersistenceService;
    }

    /**
     * Guarda el autómata con todas sus posiciones directamente a disco.
     */
    public void save(Path path, AutomatonDefinition definition, Map<String, StatePosition> statePositions) throws Exception {
        automatonJsonPersistenceService.save(path, definition, statePositions);
    }

    /**
     * Carga el archivo completo de un autómata parseándolo a objetos del programa.
     */
    public AutomatonFile loadFile(Path path) throws Exception {
        return automatonJsonPersistenceService.loadFile(path);
    }
}
