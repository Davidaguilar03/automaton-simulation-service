package co.edu.uptc.automatonsimulationservice.persistence.services;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.persistence.models.AutomatonFile;
import co.edu.uptc.automatonsimulationservice.persistence.models.StatePosition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class AutomatonJsonPersistenceService {
    private final ObjectMapper objectMapper;

    public AutomatonJsonPersistenceService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void save(Path path, AutomatonDefinition definition) throws IOException {
        save(path, definition, Map.of());
    }

    public void save(Path path, AutomatonDefinition definition, Map<String, StatePosition> statePositions) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Target path is required.");
        }
        if (definition == null) {
            throw new IllegalArgumentException("Automaton definition is required.");
        }
        Map<String, StatePosition> safePositions = statePositions == null ? new LinkedHashMap<>() : new LinkedHashMap<>(statePositions);
        AutomatonFile automatonFile = new AutomatonFile(definition, safePositions);
        objectMapper.writeValue(path.toFile(), automatonFile);
    }

    public AutomatonDefinition load(Path path) throws IOException {
        return loadFile(path).getAutomaton();
    }

    public AutomatonFile loadFile(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Source path is required.");
        }
        if (!Files.exists(path)) {
            throw new IOException("Source file does not exist: " + path);
        }
        AutomatonFile automatonFile = objectMapper.readValue(path.toFile(), AutomatonFile.class);
        if (automatonFile == null || automatonFile.getAutomaton() == null) {
            throw new IOException("Invalid JSON structure. Expected 'automaton' payload.");
        }
        if (automatonFile.getStatePositions() == null) {
            automatonFile.setStatePositions(new LinkedHashMap<>());
        }
        return automatonFile;
    }
}

