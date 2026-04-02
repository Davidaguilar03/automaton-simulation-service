package co.edu.uptc.automatonsimulationservice.ui.controllers;

import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.evaluation.controllers.EvaluationDomainController;
import co.edu.uptc.automatonsimulationservice.evaluation.models.EvaluationResult;
import co.edu.uptc.automatonsimulationservice.evaluation.models.TraceStep;
import co.edu.uptc.automatonsimulationservice.evaluation.services.AutomatonEvaluationService;
import co.edu.uptc.automatonsimulationservice.ui.models.EvaluationReportItem;
import co.edu.uptc.automatonsimulationservice.ui.models.StateNodeView;
import co.edu.uptc.automatonsimulationservice.ui.models.TransitionEdgeView;
import co.edu.uptc.automatonsimulationservice.ui.services.AutomatonDiagramLayoutService;
import co.edu.uptc.automatonsimulationservice.ui.services.AutomatonDiagramRenderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class TestWindowController {
    private static final double NODE_RADIUS = 28;
    private static final double FIT_PADDING = 24;
    private static final double MAX_FIT_SCALE = 0.88;

    @FXML
    private Pane automatonDiagramPane;
    @FXML
    private ListView<String> statesListView;
    @FXML
    private ListView<String> alphabetListView;
    @FXML
    private ListView<String> acceptingStatesListView;
    @FXML
    private Label initialStateValueLabel;
    @FXML
    private TextArea batchInputArea;
    @FXML
    private ListView<EvaluationReportItem> evaluationReportListView;
    @FXML
    private TextArea traceArea;
    @FXML
    private Label statusLabel;

    private final EvaluationDomainController evaluationDomainController;
    private final AutomatonDiagramLayoutService automatonDiagramLayoutService;
    private final AutomatonDiagramRenderService automatonDiagramRenderService;

    private AutomatonDefinition automatonDefinition;
    private final Map<String, Point2D> statePositions;

    public TestWindowController() {
        this.evaluationDomainController = new EvaluationDomainController(new AutomatonEvaluationService());
        this.automatonDiagramLayoutService = new AutomatonDiagramLayoutService();
        this.automatonDiagramRenderService = new AutomatonDiagramRenderService();
        this.statePositions = new LinkedHashMap<>();
    }

    @FXML
    private void initialize() {
        evaluationReportListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                renderTrace(newValue.getResult());
            }
        });
        automatonDiagramPane.widthProperty().addListener((observable, oldValue, newValue) -> renderDiagram());
        automatonDiagramPane.heightProperty().addListener((observable, oldValue, newValue) -> renderDiagram());
    }

    public void loadData(AutomatonDefinition definition, Map<String, Point2D> positions) {
        this.automatonDefinition = copyDefinition(definition);
        this.statePositions.clear();
        if (positions != null) {
            this.statePositions.putAll(positions);
        }
        refreshSummary();
        renderDiagram();
        statusLabel.setText("Listo para evaluar cadenas.");
    }

    @FXML
    private void onRunBatch() {
        try {
            if (automatonDefinition == null) {
                throw new IllegalArgumentException("No hay datos del automata cargados.");
            }

            List<String> inputs = parseBatchInputs(batchInputArea.getText());
            if (inputs.isEmpty()) {
                throw new IllegalArgumentException("Debes ingresar al menos una cadena.");
            }

            List<EvaluationResult> results = evaluationDomainController.evaluateBatch(automatonDefinition, inputs);
            List<EvaluationReportItem> report = results.stream().map(EvaluationReportItem::new).toList();
            evaluationReportListView.setItems(FXCollections.observableArrayList(report));
            if (!report.isEmpty()) {
                evaluationReportListView.getSelectionModel().select(0);
                renderTrace(report.getFirst().getResult());
            }
            statusLabel.setText("Evaluacion por lotes completada.");
        } catch (Exception exception) {
            statusLabel.setText(exception.getMessage());
        }
    }

    @FXML
    private void onCloseWindow() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    private void refreshSummary() {
        statesListView.setItems(FXCollections.observableArrayList(automatonDefinition.getStates()));
        alphabetListView.setItems(FXCollections.observableArrayList(automatonDefinition.getAlphabet()));
        acceptingStatesListView.setItems(FXCollections.observableArrayList(automatonDefinition.getAcceptingStates()));
        initialStateValueLabel.setText(automatonDefinition.getInitialState() == null ? "-" : automatonDefinition.getInitialState());
    }

    private void renderDiagram() {
        if (automatonDefinition == null) {
            return;
        }
        Map<String, Point2D> centeredPositions = fitAndCenterPositionsForPane(
            automatonDefinition,
            statePositions,
            automatonDiagramPane.getWidth(),
            automatonDiagramPane.getHeight()
        );
        List<StateNodeView> stateViews = automatonDiagramLayoutService.layoutStates(
            automatonDefinition,
            automatonDiagramPane.getWidth(),
            automatonDiagramPane.getHeight(),
            centeredPositions
        );
        List<TransitionEdgeView> edges = automatonDiagramLayoutService.buildEdges(automatonDefinition);
        automatonDiagramRenderService.render(automatonDiagramPane, stateViews, edges);
    }

    private Map<String, Point2D> fitAndCenterPositionsForPane(
        AutomatonDefinition definition,
        Map<String, Point2D> originalPositions,
        double paneWidth,
        double paneHeight
    ) {
        Map<String, Point2D> availablePositions = new LinkedHashMap<>();
        for (String state : definition.getStates()) {
            Point2D position = originalPositions.get(state);
            if (position != null) {
                availablePositions.put(state, position);
            }
        }
        if (availablePositions.isEmpty() || paneWidth <= 0 || paneHeight <= 0) {
            return availablePositions;
        }

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        for (Point2D position : availablePositions.values()) {
            minX = Math.min(minX, position.getX());
            maxX = Math.max(maxX, position.getX());
            minY = Math.min(minY, position.getY());
            maxY = Math.max(maxY, position.getY());
        }

        double contentWidth = Math.max(1, maxX - minX);
        double contentHeight = Math.max(1, maxY - minY);

        double minMargin = NODE_RADIUS + FIT_PADDING;
        double availableWidth = Math.max(1, paneWidth - (2 * minMargin));
        double availableHeight = Math.max(1, paneHeight - (2 * minMargin));

        double scaleX = availableWidth / contentWidth;
        double scaleY = availableHeight / contentHeight;
        double scale = Math.min(Math.min(scaleX, scaleY), MAX_FIT_SCALE);

        double tx = minMargin + ((availableWidth - (contentWidth * scale)) / 2.0) - (minX * scale);
        double ty = minMargin + ((availableHeight - (contentHeight * scale)) / 2.0) - (minY * scale);

        Map<String, Point2D> centeredPositions = new LinkedHashMap<>();
        for (Map.Entry<String, Point2D> entry : availablePositions.entrySet()) {
            double centeredX = clamp((entry.getValue().getX() * scale) + tx, minMargin, Math.max(minMargin, paneWidth - minMargin));
            double centeredY = clamp((entry.getValue().getY() * scale) + ty, minMargin, Math.max(minMargin, paneHeight - minMargin));
            centeredPositions.put(entry.getKey(), new Point2D(centeredX, centeredY));
        }
        return centeredPositions;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private List<String> parseBatchInputs(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        String[] lines = value.split("\\R");
        List<String> inputs = new ArrayList<>();
        for (String line : lines) {
            if (!line.isBlank()) {
                inputs.add(line.trim());
            }
        }
        if (inputs.size() > 10) {
            throw new IllegalArgumentException("Solo puedes evaluar hasta 10 cadenas por lote.");
        }
        return inputs;
    }

    private void renderTrace(EvaluationResult result) {
        StringBuilder builder = new StringBuilder();
        for (TraceStep step : result.getTrace()) {
            if (!builder.isEmpty()) {
                builder.append("\n");
            }
            builder.append(step.expression());
        }
        builder.append("\n");
        builder.append("Estados finales: ").append(result.getFinalStates()).append("\n");
        builder.append("Resultado: ").append(result.isAccepted() ? "ACEPTADA" : "RECHAZADA");
        traceArea.setText(builder.toString());
    }

    private AutomatonDefinition copyDefinition(AutomatonDefinition source) {
        AutomatonDefinition copy = new AutomatonDefinition();
        copy.setType(source.getType());
        copy.setStates(new LinkedHashSet<>(source.getStates()));
        copy.setAlphabet(new LinkedHashSet<>(source.getAlphabet()));
        copy.setInitialState(source.getInitialState());
        copy.setAcceptingStates(new LinkedHashSet<>(source.getAcceptingStates()));
        copy.setTransitions(new ArrayList<>(source.getTransitions()));
        return copy;
    }
}

