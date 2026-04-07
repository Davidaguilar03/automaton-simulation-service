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

/**
 * Gestiona la serializacion y deserializacion de autómatas en formato JSON.
 */
public class AutomatonJsonPersistenceService {
    private final ObjectMapper objectMapper;

    /**
     * Construye un mapeador para serializar con un formato legible (identado).
     */
    public AutomatonJsonPersistenceService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    /**
     * Guarda la definición y un esquema de coordenadas del canvas en un archivo JSON.
     */
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

    /**
     * Carga exclusivamente la definición matemática de un autómata parseado.
     */
    public AutomatonDefinition load(Path path) throws IOException {
        return loadFile(path).getAutomaton();
    }

    /**
     * Descarga y valida un archivo JSON construyendo los datos en memoria.
     */
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
