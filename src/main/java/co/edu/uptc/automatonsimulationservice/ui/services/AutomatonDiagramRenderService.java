package co.edu.uptc.automatonsimulationservice.ui.services;

import co.edu.uptc.automatonsimulationservice.ui.models.StateNodeView;
import co.edu.uptc.automatonsimulationservice.ui.models.TransitionEdgeView;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutomatonDiagramRenderService {
    private static final double NODE_RADIUS = 28;
    private static final double ARROW_SIZE = 9;
    private static final double CURVE_OFFSET = 54;

    private enum LoopSide {
        RIGHT,
        LEFT,
        TOP,
        BOTTOM
    }

    /**
     * Dibuja los gráficos (círculos y flechas) en la ventana.
     */
    public void render(Pane pane, List<StateNodeView> states, List<TransitionEdgeView> edges) {
        pane.getChildren().clear();
        if (states.isEmpty()) {
            return;
        }

        Map<String, StateNodeView> stateIndex = states.stream()
            .collect(Collectors.toMap(StateNodeView::name, Function.identity()));

        for (TransitionEdgeView edge : edges) {
            StateNodeView from = stateIndex.get(edge.fromState());
            StateNodeView to = stateIndex.get(edge.toState());
            if (from == null || to == null) {
                continue;
            }
            if (edge.selfLoop()) {
                drawSelfLoop(pane, from, edge.label());
                continue;
            }
            if (edge.curved()) {
                drawCurvedEdge(pane, from, to, edge.label(), edge.curveDirection());
                continue;
            }
            drawStraightEdge(pane, from, to, edge.label());
        }

        for (StateNodeView state : states) {
            drawState(pane, state);
            if (state.initial()) {
                drawInitialMarker(pane, state);
            }
        }
    }

    /**
     * Pinta una trayectoria lineal entre dos diferentes estados.
     */
    private void drawStraightEdge(Pane pane, StateNodeView from, StateNodeView to, String label) {
        double dx = to.x() - from.x();
        double dy = to.y() - from.y();
        double length = Math.hypot(dx, dy);
        if (length == 0) {
            return;
        }

        double ux = dx / length;
        double uy = dy / length;

        double startX = from.x() + ux * NODE_RADIUS;
        double startY = from.y() + uy * NODE_RADIUS;
        double endX = to.x() - ux * NODE_RADIUS;
        double endY = to.y() - uy * NODE_RADIUS;

        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.web("#334155"));
        line.setStrokeWidth(1.8);
        pane.getChildren().add(line);

        drawArrowHead(pane, endX, endY, ux, uy);

        double labelX = (startX + endX) / 2.0;
        double labelY = (startY + endY) / 2.0;
        drawLabel(pane, label, labelX, labelY - 8);
    }

    /**
     * Pinta una trayectoria curva para evitar el cruce de transiciones bidireccionales.
     */
    private void drawCurvedEdge(Pane pane, StateNodeView from, StateNodeView to, String label, int direction) {
        double dx = to.x() - from.x();
        double dy = to.y() - from.y();
        double length = Math.hypot(dx, dy);
        if (length == 0) {
            return;
        }

        double ux = dx / length;
        double uy = dy / length;
        double px = -uy;

        double startX = from.x() + ux * NODE_RADIUS;
        double startY = from.y() + uy * NODE_RADIUS;
        double endX = to.x() - ux * NODE_RADIUS;
        double endY = to.y() - uy * NODE_RADIUS;

        double offset = Math.max(CURVE_OFFSET, Math.min(85, length * 0.28));
        double midX = (startX + endX) / 2.0;
        double midY = (startY + endY) / 2.0;
        double controlX = midX + px * offset * direction;
        double controlY = midY + ux * offset * direction;

        QuadCurve curve = new QuadCurve(startX, startY, controlX, controlY, endX, endY);
        curve.setFill(Color.TRANSPARENT);
        curve.setStroke(Color.web("#334155"));
        curve.setStrokeWidth(1.8);
        pane.getChildren().add(curve);

        double tx = endX - controlX;
        double ty = endY - controlY;
        double tangentLength = Math.hypot(tx, ty);
        if (tangentLength != 0) {
            drawArrowHead(pane, endX, endY, tx / tangentLength, ty / tangentLength);
        }

        double labelX = 0.25 * startX + 0.5 * controlX + 0.25 * endX;
        double labelY = 0.25 * startY + 0.5 * controlY + 0.25 * endY;
        drawLabel(pane, label, labelX + px * 8 * direction, labelY + ux * 8 * direction);
    }

    /**
     * Dibuja un lazo que sale y llega al mismo estado.
     */
    private void drawSelfLoop(Pane pane, StateNodeView state, String label) {
        LoopSide side = resolveLoopSide(pane, state);
        double endpointRadius = NODE_RADIUS + 2;
        double startAngle = Math.toRadians(switch (side) {
            case RIGHT -> -35;
            case LEFT -> 145;
            case TOP -> -125;
            case BOTTOM -> 55;
        });
        double endAngle = Math.toRadians(switch (side) {
            case RIGHT -> 35;
            case LEFT -> -145;
            case TOP -> -55;
            case BOTTOM -> 125;
        });
        double startX = state.x() + endpointRadius * Math.cos(startAngle);
        double startY = state.y() + endpointRadius * Math.sin(startAngle);
        double endX = state.x() + endpointRadius * Math.cos(endAngle);
        double endY = state.y() + endpointRadius * Math.sin(endAngle);

        double controlX = switch (side) {
            case RIGHT -> state.x() + (NODE_RADIUS * 2.25);
            case LEFT -> state.x() - (NODE_RADIUS * 2.25);
            default -> state.x();
        };
        double controlY = switch (side) {
            case TOP -> state.y() - (NODE_RADIUS * 2.25);
            case BOTTOM -> state.y() + (NODE_RADIUS * 2.25);
            default -> state.y();
        };

        QuadCurve loop = new QuadCurve(startX, startY, controlX, controlY, endX, endY);
        loop.setFill(Color.TRANSPARENT);
        loop.setStroke(Color.web("#334155"));
        loop.setStrokeWidth(2.3);
        pane.getChildren().add(loop);

        double tx = endX - controlX;
        double ty = endY - controlY;
        double tangentLength = Math.hypot(tx, ty);
        if (tangentLength != 0) {
            drawArrowHead(pane, endX, endY, tx / tangentLength, ty / tangentLength);
        }

        double labelX = switch (side) {
            case RIGHT -> controlX + 8;
            case LEFT -> controlX - 8;
            default -> controlX;
        };
        double labelY = switch (side) {
            case TOP -> controlY - 8;
            case BOTTOM -> controlY + NODE_RADIUS + 8;
            default -> controlY - (NODE_RADIUS * 0.9);
        };
        drawLabel(pane, label, labelX, labelY);
    }

    /**
     * Evalúa qué lado del estado es óptimo para dibujar un lazo sin salirse.
     */
    private LoopSide resolveLoopSide(Pane pane, StateNodeView state) {
        double availableRight = pane.getWidth() - state.x() - NODE_RADIUS;
        double availableLeft = state.x() - NODE_RADIUS;
        double availableTop = state.y() - NODE_RADIUS;
        double availableBottom = pane.getHeight() - state.y() - NODE_RADIUS;

        double preferredTopSpace = NODE_RADIUS * 2.3;
        if (availableTop >= preferredTopSpace) {
            return LoopSide.TOP;
        }

        double max = availableRight;
        LoopSide side = LoopSide.RIGHT;
        if (availableLeft > max) {
            max = availableLeft;
            side = LoopSide.LEFT;
        }
        if (availableBottom > max) {
            side = LoopSide.BOTTOM;
        }
        return side;
    }

    /**
     * Crea gráficamente la burbuja circular que identifica al estado matemático con su etiqueta.
     */
    private void drawState(Pane pane, StateNodeView state) {
        Circle outer = new Circle(state.x(), state.y(), NODE_RADIUS);
        outer.setFill(Color.WHITE);
        outer.setStroke(Color.web("#1f2937"));
        outer.setStrokeWidth(2);
        pane.getChildren().add(outer);

        if (state.accepting()) {
            Circle inner = new Circle(state.x(), state.y(), NODE_RADIUS - 6);
            inner.setFill(Color.TRANSPARENT);
            inner.setStroke(Color.web("#1f2937"));
            inner.setStrokeWidth(1.8);
            pane.getChildren().add(inner);
        }

        Text text = new Text(state.name());
        text.setFont(Font.font(13));
        Bounds bounds = text.getLayoutBounds();
        text.setX(state.x() - bounds.getWidth() / 2.0);
        text.setY(state.y() + bounds.getHeight() / 4.0);
        pane.getChildren().add(text);
    }

    /**
     * Inserta la flecha externa inicial que apunta al estado de arranque (q0).
     */
    private void drawInitialMarker(Pane pane, StateNodeView state) {
        double startX = state.x() - NODE_RADIUS - 24;
        double startY = state.y();
        double endX = state.x() - NODE_RADIUS;
        double endY = state.y();

        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.web("#334155"));
        line.setStrokeWidth(1.8);
        pane.getChildren().add(line);

        drawArrowHead(pane, endX, endY, 1, 0);
    }

    /**
     * Crea los punteros cónicos (triángulos) de las flechas.
     */
    private void drawArrowHead(Pane pane, double tipX, double tipY, double ux, double uy) {
        double leftX = tipX - (ux * ARROW_SIZE) + (uy * ARROW_SIZE * 0.6);
        double leftY = tipY - (uy * ARROW_SIZE) - (ux * ARROW_SIZE * 0.6);
        double rightX = tipX - (ux * ARROW_SIZE) - (uy * ARROW_SIZE * 0.6);
        double rightY = tipY - (uy * ARROW_SIZE) + (ux * ARROW_SIZE * 0.6);

        Line left = new Line(tipX, tipY, leftX, leftY);
        Line right = new Line(tipX, tipY, rightX, rightY);
        left.setStroke(Color.web("#334155"));
        right.setStroke(Color.web("#334155"));
        left.setStrokeWidth(1.8);
        right.setStrokeWidth(1.8);
        pane.getChildren().add(left);
        pane.getChildren().add(right);
    }

    /**
     * Formatea y ubica el texto flotante de los símbolos de las transiciones.
     */
    private void drawLabel(Pane pane, String label, double x, double y) {
        Text text = new Text(label);
        text.setFont(Font.font(12));
        Bounds bounds = text.getLayoutBounds();
        double textX = x - bounds.getWidth() / 2.0;
        double textY = y - bounds.getHeight() / 3.0;
        Rectangle background = new Rectangle(
            textX - 4,
            textY - bounds.getHeight() + 2,
            bounds.getWidth() + 8,
            bounds.getHeight() + 4
        );
        background.setArcWidth(6);
        background.setArcHeight(6);
        background.setFill(Color.web("#f8fafc"));
        background.setStroke(Color.web("#cbd5e1"));
        background.setStrokeWidth(0.8);
        pane.getChildren().add(background);

        text.setX(textX);
        text.setY(textY);
        text.setFill(Color.web("#0f172a"));
        pane.getChildren().add(text);
    }
}
