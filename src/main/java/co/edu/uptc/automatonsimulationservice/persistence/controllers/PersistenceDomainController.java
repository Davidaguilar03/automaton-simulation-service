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

    public PersistenceDomainController(AutomatonJsonPersistenceService automatonJsonPersistenceService) {
        this.automatonJsonPersistenceService = automatonJsonPersistenceService;
    }

    public void save(Path path, AutomatonDefinition definition) throws IOException {
        automatonJsonPersistenceService.save(path, definition);
    }

    public void save(Path path, AutomatonDefinition definition, Map<String, StatePosition> statePositions) throws IOException {
        automatonJsonPersistenceService.save(path, definition, statePositions);
    }

    public AutomatonDefinition load(Path path) throws IOException {
        return automatonJsonPersistenceService.load(path);
    }

    public AutomatonFile loadFile(Path path) throws IOException {
        return automatonJsonPersistenceService.loadFile(path);
    }
}

