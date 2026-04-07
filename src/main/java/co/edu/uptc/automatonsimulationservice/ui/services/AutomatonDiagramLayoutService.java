package co.edu.uptc.automatonsimulationservice.ui.services;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.TransitionRule;
import co.edu.uptc.automatonsimulationservice.ui.models.StateNodeView;
import co.edu.uptc.automatonsimulationservice.ui.models.TransitionEdgeView;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calcula la distribución geométrica de estados y la consolidación visual de transiciones para el diagrama.
 */
public class AutomatonDiagramLayoutService {
    private static final double DEFAULT_WIDTH = 900;
    private static final double DEFAULT_HEIGHT = 300;
    private static final double PADDING = 30;

    /**
     * Distribuye estados usando configuración por defecto cuando no se proveen posiciones fijas.
     */
    public List<StateNodeView> layoutStates(AutomatonDefinition definition, double width, double height) {
        return layoutStates(definition, width, height, Map.of());
    }

    /**
     * Distribuye estados con soporte de posiciones fijas y ajuste al tamaño disponible del panel.
     */
    public List<StateNodeView> layoutStates(
        AutomatonDefinition definition,
        double width,
        double height,
        Map<String, Point2D> fixedPositions
    ) {
        double safeWidth = width > 0 ? width : DEFAULT_WIDTH;
        double safeHeight = height > 0 ? height : DEFAULT_HEIGHT;

        List<String> states = new ArrayList<>(definition.getStates());
        List<StateNodeView> nodeViews = new ArrayList<>();
        if (states.isEmpty()) {
            return nodeViews;
        }

        double centerX = safeWidth / 2.0;
        double centerY = safeHeight / 2.0;
        if (states.size() == 1) {
            String state = states.getFirst();
            Point2D fixedPoint = fixedPositions == null ? null : fixedPositions.get(state);
            double x = fixedPoint == null ? centerX : clampWithPadding(fixedPoint.getX(), safeWidth);
            double y = fixedPoint == null ? centerY : clampWithPadding(fixedPoint.getY(), safeHeight);
            nodeViews.add(new StateNodeView(
                state,
                x,
                y,
                state.equals(definition.getInitialState()),
                definition.getAcceptingStates().contains(state)
            ));
            return nodeViews;
        }

        double radius = Math.max(80, Math.min(safeWidth, safeHeight) * 0.36);
        for (int index = 0; index < states.size(); index++) {
            String state = states.get(index);
            Point2D fixedPoint = fixedPositions == null ? null : fixedPositions.get(state);
            double x;
            double y;
            if (fixedPoint != null) {
                x = clampWithPadding(fixedPoint.getX(), safeWidth);
                y = clampWithPadding(fixedPoint.getY(), safeHeight);
            } else {
                double angle = (2 * Math.PI * index / states.size()) - (Math.PI / 2);
                x = centerX + (radius * Math.cos(angle));
                y = centerY + (radius * Math.sin(angle));
            }
            nodeViews.add(new StateNodeView(
                state,
                x,
                y,
                state.equals(definition.getInitialState()),
                definition.getAcceptingStates().contains(state)
            ));
        }
        return nodeViews;
    }

    /**
     * Agrupa transiciones por par origen-destino para generar aristas etiquetadas en una sola entidad visual.
     */
    public List<TransitionEdgeView> buildEdges(AutomatonDefinition definition) {
        Map<TransitionPair, Set<String>> groupedSymbols = new LinkedHashMap<>();
        for (TransitionRule transition : definition.getTransitions()) {
            TransitionPair pair = new TransitionPair(transition.getFromState(), transition.getToState());
            groupedSymbols.computeIfAbsent(pair, key -> new LinkedHashSet<>()).add(transition.getSymbol());
        }

        List<TransitionEdgeView> edges = new ArrayList<>();
        for (Map.Entry<TransitionPair, Set<String>> entry : groupedSymbols.entrySet()) {
            TransitionPair pair = entry.getKey();
            boolean selfLoop = pair.fromState().equals(pair.toState());
            boolean hasReverse = groupedSymbols.containsKey(new TransitionPair(pair.toState(), pair.fromState()));
            boolean curved = !selfLoop && hasReverse;
            int curveDirection = 1;
            String label = String.join(",", entry.getValue());
            edges.add(new TransitionEdgeView(
                pair.fromState(),
                pair.toState(),
                label,
                selfLoop,
                curved,
                curveDirection
            ));
        }

        return edges;
    }

    /**
     * Identificador compuesto para indexar transiciones por estado origen y destino.
     */
    private record TransitionPair(String fromState, String toState) {
    }

    /**
     * Restringe coordenadas para preservar un margen visual mínimo dentro del lienzo.
     */
    private double clampWithPadding(double value, double maxValue) {
        return Math.max(PADDING, Math.min(maxValue - PADDING, value));
    }
}

