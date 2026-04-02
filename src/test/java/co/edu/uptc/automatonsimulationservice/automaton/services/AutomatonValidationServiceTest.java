package co.edu.uptc.automatonsimulationservice.automaton.services;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonType;
import co.edu.uptc.automatonsimulationservice.automaton.models.TransitionRule;
import co.edu.uptc.automatonsimulationservice.automaton.models.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutomatonValidationServiceTest {

    private final AutomatonValidationService service = new AutomatonValidationService();

    @Test
    void shouldValidateDeterministicDefinition() {
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

        ValidationResult validationResult = service.validate(definition);

        assertTrue(validationResult.valid());
    }

    @Test
    void shouldRejectDfaWithMissingTransition() {
        AutomatonDefinition definition = new AutomatonDefinition();
        definition.setType(AutomatonType.DFA);
        definition.setStates(Set.of("q0", "q1"));
        definition.setAlphabet(Set.of("a", "b"));
        definition.setInitialState("q0");
        definition.setAcceptingStates(Set.of("q1"));
        definition.setTransitions(List.of(
            new TransitionRule("q0", "a", "q1"),
            new TransitionRule("q1", "a", "q1")
        ));

        ValidationResult validationResult = service.validate(definition);

        assertFalse(validationResult.valid());
        assertTrue(validationResult.errors().stream().anyMatch(error -> error.contains("DFA requires exactly one transition")));
    }
}

