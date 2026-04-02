package co.edu.uptc.automatonsimulationservice.ui.services;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonType;
import co.edu.uptc.automatonsimulationservice.automaton.models.TransitionRule;
import co.edu.uptc.automatonsimulationservice.ui.models.StateNodeView;
import co.edu.uptc.automatonsimulationservice.ui.models.TransitionEdgeView;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutomatonDiagramLayoutServiceTest {

    private final AutomatonDiagramLayoutService service = new AutomatonDiagramLayoutService();

    @Test
    void shouldLayoutAllStatesInRequestedBounds() {
        AutomatonDefinition definition = createDefinition(
            Set.of("q0", "q1", "q2"),
            List.of(new TransitionRule("q0", "a", "q1"))
        );

        List<StateNodeView> nodes = service.layoutStates(definition, 600, 400);

        assertEquals(3, nodes.size());
        assertTrue(nodes.stream().allMatch(node -> node.x() >= 0 && node.x() <= 600));
        assertTrue(nodes.stream().allMatch(node -> node.y() >= 0 && node.y() <= 400));
    }

    @Test
    void shouldGroupTransitionLabelsForSameStatePair() {
        AutomatonDefinition definition = createDefinition(
            Set.of("q0", "q1"),
            List.of(
                new TransitionRule("q0", "a", "q1"),
                new TransitionRule("q0", "b", "q1")
            )
        );

        List<TransitionEdgeView> edges = service.buildEdges(definition);

        assertEquals(1, edges.size());
        assertEquals("a,b", edges.getFirst().label());
        assertFalse(edges.getFirst().selfLoop());
    }

    @Test
    void shouldRespectFixedPositionsWhenProvided() {
        AutomatonDefinition definition = createDefinition(
            Set.of("q0", "q1"),
            List.of(new TransitionRule("q0", "a", "q1"))
        );

        List<StateNodeView> nodes = service.layoutStates(
            definition,
            600,
            400,
            Map.of("q0", new Point2D(140, 170), "q1", new Point2D(430, 240))
        );

        StateNodeView q0 = nodes.stream().filter(node -> node.name().equals("q0")).findFirst().orElseThrow();
        StateNodeView q1 = nodes.stream().filter(node -> node.name().equals("q1")).findFirst().orElseThrow();
        assertEquals(140, q0.x());
        assertEquals(170, q0.y());
        assertEquals(430, q1.x());
        assertEquals(240, q1.y());
    }

    @Test
    void shouldMarkSelfLoopAndBidirectionalEdges() {
        AutomatonDefinition definition = createDefinition(
            Set.of("q0", "q1"),
            List.of(
                new TransitionRule("q0", "a", "q0"),
                new TransitionRule("q0", "b", "q1"),
                new TransitionRule("q1", "c", "q0")
            )
        );

        List<TransitionEdgeView> edges = service.buildEdges(definition);

        assertTrue(edges.stream().anyMatch(TransitionEdgeView::selfLoop));
        long curvedCount = edges.stream().filter(TransitionEdgeView::curved).count();
        assertEquals(2, curvedCount);
    }

    private AutomatonDefinition createDefinition(Set<String> states, List<TransitionRule> transitions) {
        AutomatonDefinition definition = new AutomatonDefinition();
        definition.setType(AutomatonType.DFA);
        definition.setStates(new LinkedHashSet<>(states));
        definition.setAlphabet(new LinkedHashSet<>(Set.of("a", "b", "c")));
        definition.setInitialState(states.iterator().next());
        definition.setAcceptingStates(new LinkedHashSet<>());
        definition.setTransitions(transitions);
        return definition;
    }
}

