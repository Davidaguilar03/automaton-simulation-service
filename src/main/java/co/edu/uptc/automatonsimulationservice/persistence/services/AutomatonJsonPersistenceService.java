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
            throw new IllegalArgumentException("La ruta de destino es obligatoria.");
        }
        if (definition == null) {
            throw new IllegalArgumentException("La definicion del automata es obligatoria.");
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
            throw new IllegalArgumentException("La ruta de origen es obligatoria.");
        }
        if (!Files.exists(path)) {
            throw new IOException("El archivo de origen no existe: " + path);
        }
        AutomatonFile automatonFile = objectMapper.readValue(path.toFile(), AutomatonFile.class);
        if (automatonFile == null || automatonFile.getAutomaton() == null) {
            throw new IOException("Estructura JSON invalida. Se esperaba la propiedad 'automaton'.");
        }
        if (automatonFile.getStatePositions() == null) {
            automatonFile.setStatePositions(new LinkedHashMap<>());
        }
        return automatonFile;
    }
}

