package co.edu.uptc.automatonsimulationservice.persistence.services;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonType;
import co.edu.uptc.automatonsimulationservice.automaton.models.TransitionRule;
import co.edu.uptc.automatonsimulationservice.persistence.models.AutomatonFile;
import co.edu.uptc.automatonsimulationservice.persistence.models.StatePosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutomatonJsonPersistenceServiceTest {

    private final AutomatonJsonPersistenceService service = new AutomatonJsonPersistenceService();

    @TempDir
    Path tempDir;

    @Test
    void shouldPersistAndLoadAutomatonDefinition() throws IOException {
        Path file = tempDir.resolve("dfa.json");
        AutomatonDefinition original = buildDefinition();
        Map<String, StatePosition> positions = Map.of(
            "q0", new StatePosition(120, 90),
            "q1", new StatePosition(340, 130)
        );

        service.save(file, original, positions);
        AutomatonFile loadedFile = service.loadFile(file);
        AutomatonDefinition loaded = loadedFile.getAutomaton();

        assertEquals(original.getType(), loaded.getType());
        assertEquals(original.getStates(), loaded.getStates());
        assertEquals(original.getAlphabet(), loaded.getAlphabet());
        assertEquals(original.getInitialState(), loaded.getInitialState());
        assertEquals(original.getAcceptingStates(), loaded.getAcceptingStates());
        assertEquals(original.getTransitions(), loaded.getTransitions());
        assertEquals(120, loadedFile.getStatePositions().get("q0").getX());
        assertEquals(130, loadedFile.getStatePositions().get("q1").getY());
    }

    @Test
    void shouldFailLoadingWhenPayloadIsMissing() throws IOException {
        Path file = tempDir.resolve("invalid.json");
        Files.writeString(file, "{\"wrong\":{}}\n");

        assertThrows(IOException.class, () -> service.load(file));
    }

    @Test
    void shouldFailLoadingWhenFileDoesNotExist() {
        Path missingFile = tempDir.resolve("missing.json");

        assertThrows(IOException.class, () -> service.load(missingFile));
    }

    private AutomatonDefinition buildDefinition() {
        AutomatonDefinition definition = new AutomatonDefinition();
        definition.setType(AutomatonType.DFA);
        definition.setStates(Set.of("q0", "q1"));
        definition.setAlphabet(Set.of("a", "b"));
        definition.setInitialState("q0");
        definition.setAcceptingStates(Set.of("q1"));
        definition.setTransitions(List.of(
            new TransitionRule("q0", "a", "q1"),
            new TransitionRule("q0", "b", "q0"),
            new TransitionRule("q1", "a", "q1"),
            new TransitionRule("q1", "b", "q0")
        ));
        return definition;
    }
}

