package co.edu.uptc.automatonsimulationservice.evaluation.services;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonType;
import co.edu.uptc.automatonsimulationservice.automaton.models.TransitionRule;
import co.edu.uptc.automatonsimulationservice.evaluation.models.EvaluationResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutomatonEvaluationServiceTest {

    private final AutomatonEvaluationService service = new AutomatonEvaluationService();

    @Test
    void shouldAcceptAndRejectBatchWithDfa() {
        AutomatonDefinition definition = buildDfa();

        List<EvaluationResult> results = service.evaluateBatch(definition, List.of("ab", "aa"));

        assertEquals(2, results.size());
        assertFalse(results.get(0).isAccepted());
        assertTrue(results.get(1).isAccepted());
    }

    @Test
    void shouldFailWhenBatchExceedsLimit() {
        AutomatonDefinition definition = buildDfa();

        assertThrows(IllegalArgumentException.class,
            () -> service.evaluateBatch(definition, List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")));
    }

    private AutomatonDefinition buildDfa() {
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


