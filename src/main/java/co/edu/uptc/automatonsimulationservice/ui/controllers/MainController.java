package co.edu.uptc.automatonsimulationservice.ui.controllers;

import co.edu.uptc.automatonsimulationservice.automaton.controllers.AutomatonDomainController;
import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonDefinition;
import co.edu.uptc.automatonsimulationservice.automaton.models.AutomatonType;
import co.edu.uptc.automatonsimulationservice.automaton.models.TransitionRule;
import co.edu.uptc.automatonsimulationservice.automaton.models.ValidationResult;
import co.edu.uptc.automatonsimulationservice.automaton.services.AutomatonValidationService;
import co.edu.uptc.automatonsimulationservice.persistence.controllers.PersistenceDomainController;
import co.edu.uptc.automatonsimulationservice.persistence.models.AutomatonFile;
import co.edu.uptc.automatonsimulationservice.persistence.models.StatePosition;
import co.edu.uptc.automatonsimulationservice.persistence.services.AutomatonJsonPersistenceService;
import co.edu.uptc.automatonsimulationservice.ui.models.StateNodeView;
import co.edu.uptc.automatonsimulationservice.ui.models.TransitionEdgeView;
import co.edu.uptc.automatonsimulationservice.ui.services.AutomatonDiagramLayoutService;
import co.edu.uptc.automatonsimulationservice.ui.services.AutomatonDiagramRenderService;
import co.edu.uptc.automatonsimulationservice.ui.services.DialogService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Controlador principal de la aplicación que coordina edición, validación, persistencia y visualización de autómatas.
 */
public class MainController {
    private static final double NODE_RADIUS = 28;

    private enum ToolMode {
        SELECT,
        ADD_STATE,
        ADD_TRANSITION
    }

    private enum LoopSide {
        RIGHT,
        LEFT,
        TOP,
        BOTTOM
    }

    @FXML
    private ToggleButton selectToolButton;
    @FXML
    private ToggleButton addStateToolButton;
    @FXML
    private ToggleButton addTransitionToolButton;
    @FXML
    private ComboBox<AutomatonType> automatonTypeComboBox;
    @FXML
    private Label validationMessageLabel;
    @FXML
    private Label initialStateValueLabel;
    @FXML
    private ListView<String> statesListView;
    @FXML
    private ListView<String> alphabetListView;
    @FXML
    private ListView<String> acceptingStatesListView;
    @FXML
    private Pane automatonDiagramPane;

    private final AutomatonDomainController automatonDomainController;
    private final PersistenceDomainController persistenceDomainController;
    private final DialogService dialogService;
    private final AutomatonDiagramLayoutService automatonDiagramLayoutService;
    private final AutomatonDiagramRenderService automatonDiagramRenderService;

    private final Set<String> states;
    private final Set<String> alphabet;
    private final Set<String> acceptingStates;
    private final List<TransitionRule> transitions;
    private final Map<String, Point2D> statePositions;

    private String initialState;
    private ToolMode activeTool;
    private String transitionSourceState;
    private String draggedState;
    private List<StateNodeView> renderedStateViews;
    private List<TransitionEdgeView> renderedEdges;
    private final ContextMenu diagramContextMenu;

    /**
     * Construye el controlador inicializando dependencias de dominio, persistencia y renderizado.
     */
    public MainController() {
        this.automatonDomainController = new AutomatonDomainController(new AutomatonValidationService());
        this.persistenceDomainController = new PersistenceDomainController(new AutomatonJsonPersistenceService());
        this.dialogService = new DialogService();
        this.automatonDiagramLayoutService = new AutomatonDiagramLayoutService();
        this.automatonDiagramRenderService = new AutomatonDiagramRenderService();
        this.states = new LinkedHashSet<>();
        this.alphabet = new LinkedHashSet<>();
        this.acceptingStates = new LinkedHashSet<>();
        this.transitions = new ArrayList<>();
        this.statePositions = new LinkedHashMap<>();
        this.renderedStateViews = List.of();
        this.renderedEdges = List.of();
        this.activeTool = ToolMode.ADD_STATE;
        this.diagramContextMenu = new ContextMenu();
    }

    /**
     * Configura el estado inicial de la vista principal y registra listeners de interacción.
     */
    @FXML
    private void initialize() {
        automatonTypeComboBox.setItems(FXCollections.observableArrayList(AutomatonType.values()));
        automatonTypeComboBox.setValue(AutomatonType.DFA);
        setupToolButtons();
        setupDiagramInteractions();
        automatonDiagramPane.widthProperty().addListener((observable, oldValue, newValue) -> renderAutomatonDiagram());
        automatonDiagramPane.heightProperty().addListener((observable, oldValue, newValue) -> renderAutomatonDiagram());
        refreshSummary();
    }

    /**
     * Ejecuta la validación formal del autómata dibujado y publica mensajes de resultado.
     */
    @FXML
    private void onValidateAutomaton() {
        try {
            ValidationResult result = automatonDomainController.validate(buildDefinitionFromCanvas());
            if (result.valid()) {
                validationMessageLabel.setText("La definicion del automata es valida.");
                renderAutomatonDiagram();
            } else {
                validationMessageLabel.setText(String.join("\n", result.errors()));
            }
        } catch (Exception exception) {
            validationMessageLabel.setText(exception.getMessage());
        }
    }

    /**
     * Importa un autómata desde JSON, lo valida y lo carga en el lienzo de trabajo.
     */
    @FXML
    private void onImportJson() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importar automata JSON");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos JSON", "*.json"));
            File sourceFile = fileChooser.showOpenDialog(resolveStage());
            if (sourceFile == null) {
                return;
            }

            AutomatonFile automatonFile = persistenceDomainController.loadFile(Path.of(sourceFile.getAbsolutePath()));
            AutomatonDefinition definition = automatonFile.getAutomaton();
            ValidationResult validationResult = automatonDomainController.validate(definition);
            if (!validationResult.valid()) {
                throw new IllegalArgumentException(String.join("\n", validationResult.errors()));
            }

            loadDefinitionIntoCanvas(definition, automatonFile.getStatePositions());
            validationMessageLabel.setText("Automata importado correctamente.");
            dialogService.showInformation(resolveStage(), "Importacion completada", "Automata importado correctamente.");
        } catch (Exception exception) {
            dialogService.showError(resolveStage(), "Error de importacion", exception.getMessage());
        }
    }

    /**
     * Exporta el autómata actual a JSON si la definición cumple las restricciones formales.
     */
    @FXML
    private void onExportJson() {
        try {
            AutomatonDefinition definition = buildDefinitionFromCanvas();
            ValidationResult result = automatonDomainController.validate(definition);
            if (!result.valid()) {
                validationMessageLabel.setText(String.join("\n", result.errors()));
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar automata JSON");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos JSON", "*.json"));
            File destinationFile = fileChooser.showSaveDialog(resolveStage());
            if (destinationFile == null) {
                return;
            }

            persistenceDomainController.save(destinationFile.toPath(), definition, toPersistedPositions());
            validationMessageLabel.setText("Automata exportado correctamente.");
            dialogService.showInformation(resolveStage(), "Exportacion completada", "Automata exportado correctamente.");
        } catch (Exception exception) {
            dialogService.showError(resolveStage(), "Error de exportacion", exception.getMessage());
        }
    }

    /**
     * Abre la ventana de pruebas y transfiere una copia de la definición actual.
     */
    @FXML
    private void onOpenTestWindow() {
        try {
            AutomatonDefinition definition = buildDefinitionFromCanvas();
            ValidationResult result = automatonDomainController.validate(definition);
            if (!result.valid()) {
                validationMessageLabel.setText(String.join("\n", result.errors()));
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                MainController.class.getResource("/co/edu/uptc/automatonsimulationservice/ui/views/test-view.fxml")
            );
            Parent root = loader.load();
            TestWindowController testWindowController = loader.getController();
            testWindowController.loadData(copyDefinition(definition), new LinkedHashMap<>(statePositions));

            Stage stage = new Stage();
            stage.setTitle("Panel de Pruebas del Automata");
            stage.getIcons().add(new Image(MainController.class.getResourceAsStream("/co/edu/uptc/automatonsimulationservice/logo.png")));
            stage.setScene(new Scene(root, 1150, 760));
            stage.initOwner(resolveStage());
            stage.show();
        } catch (Exception ex) {
            dialogService.showError(resolveStage(), "No se pudo abrir la ventana de pruebas", ex.getMessage());
        }
    }

    /**
     * Limpia por completo el espacio de trabajo y restablece el estado interno del controlador.
     */
    @FXML
    private void onClearAutomaton() {
        states.clear();
        alphabet.clear();
        acceptingStates.clear();
        transitions.clear();
        statePositions.clear();
        initialState = null;
        transitionSourceState = null;
        draggedState = null;
        refreshSummary();
        renderAutomatonDiagram();
        validationMessageLabel.setText("Area de trabajo limpiada.");
    }

    /**
     * Agrupa y enlaza los botones de herramientas para alternar entre modos de edición.
     */
    private void setupToolButtons() {
        ToggleGroup toggleGroup = new ToggleGroup();
        configureToolButton(selectToolButton, toggleGroup, ToolMode.SELECT);
        configureToolButton(addStateToolButton, toggleGroup, ToolMode.ADD_STATE);
        configureToolButton(addTransitionToolButton, toggleGroup, ToolMode.ADD_TRANSITION);
        addStateToolButton.setSelected(true);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                activeTool = ToolMode.SELECT;
                return;
            }
            activeTool = (ToolMode) newValue.getUserData();
            transitionSourceState = null;
        });
    }

    /**
     * Asocia un botón de herramienta con su grupo de selección y metadato de modo.
     */
    private void configureToolButton(ToggleButton button, ToggleGroup group, ToolMode toolMode) {
        button.setToggleGroup(group);
        button.setUserData(toolMode);
    }

    /**
     * Registra los manejadores de ratón que gobiernan la interacción con el diagrama.
     */
    private void setupDiagramInteractions() {
        automatonDiagramPane.setOnMousePressed(this::onDiagramMousePressed);
        automatonDiagramPane.setOnMouseDragged(this::onDiagramMouseDragged);
        automatonDiagramPane.setOnMouseReleased(this::onDiagramMouseReleased);
    }

    /**
     * Gestiona el inicio de arrastre de estados cuando el modo activo es selección.
     */
    private void onDiagramMousePressed(MouseEvent event) {
        diagramContextMenu.hide();
        if (event.getButton() != MouseButton.PRIMARY || activeTool != ToolMode.SELECT) {
            return;
        }
        draggedState = findStateAt(event.getX(), event.getY());
    }

    /**
     * Actualiza la posición del estado arrastrado respetando límites del lienzo.
     */
    private void onDiagramMouseDragged(MouseEvent event) {
        if (!event.isPrimaryButtonDown() || activeTool != ToolMode.SELECT || draggedState == null) {
            return;
        }
        double x = clampToCanvas(event.getX(), automatonDiagramPane.getWidth());
        double y = clampToCanvas(event.getY(), automatonDiagramPane.getHeight());
        statePositions.put(draggedState, new Point2D(x, y));
        renderAutomatonDiagram();
    }

    /**
     * Procesa la liberación del ratón para crear estados, transiciones o acciones contextuales.
     */
    private void onDiagramMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            showContextMenu(event);
            draggedState = null;
            return;
        }

        if (event.getButton() != MouseButton.PRIMARY) {
            draggedState = null;
            return;
        }

        String clickedState = findStateAt(event.getX(), event.getY());

        if (activeTool == ToolMode.SELECT) {
            if (clickedState != null && event.getClickCount() >= 2) {
                renameState(clickedState);
            }
            draggedState = null;
            return;
        }

        if (activeTool == ToolMode.ADD_TRANSITION) {
            if (clickedState != null) {
                handleTransitionStateClick(clickedState);
            }
            return;
        }

        if (clickedState == null) {
            createStateAt(event.getX(), event.getY());
        }
    }

    /**
     * Crea un nuevo estado en coordenadas dadas y refresca vista y resumen.
     */
    private void createStateAt(double x, double y) {
        String stateName = generateNextStateName();
        states.add(stateName);
        statePositions.put(stateName, new Point2D(x, y));

        refreshSummary();
        renderAutomatonDiagram();
        validationMessageLabel.setText("Estado creado: " + stateName);
    }

    /**
     * Orquesta la creación de una transición seleccionando origen, símbolo y destino.
     */
    private void handleTransitionStateClick(String clickedState) {
        if (transitionSourceState == null) {
            transitionSourceState = clickedState;
            validationMessageLabel.setText("Origen seleccionado: " + clickedState + ". Selecciona el estado destino.");
            return;
        }

        String sourceState = transitionSourceState;
        transitionSourceState = null;
        Optional<String> symbolValue = promptTransitionSymbol();
        if (symbolValue.isEmpty()) {
            return;
        }

        String symbol = symbolValue.get();
        if (isDeterministicConflict(sourceState, symbol)) {
            validationMessageLabel.setText("El DFA ya contiene una transicion para " + sourceState + " con el simbolo " + symbol + ".");
            return;
        }

        transitions.add(new TransitionRule(sourceState, symbol, clickedState));
        alphabet.add(symbol);
        refreshSummary();
        renderAutomatonDiagram();
        validationMessageLabel.setText("Transicion creada: " + sourceState + " --" + symbol + "--> " + clickedState);
    }

    /**
     * Solicita al usuario el símbolo de transición y valida su formato unitario.
     */
    private Optional<String> promptTransitionSymbol() {
        TextInputDialog dialog = new TextInputDialog();
        dialogService.configureTextInputDialog(dialog, resolveStage());
        dialog.setTitle("Crear transicion");
        dialog.setHeaderText("Ingresa el simbolo de la transicion");
        dialog.setContentText("Simbolo:");
        Optional<String> value = dialog.showAndWait();
        if (value.isEmpty()) {
            return Optional.empty();
        }

        String symbol = normalizeValue(value.get());
        if (symbol == null || symbol.length() != 1) {
            validationMessageLabel.setText("Los simbolos de transicion deben tener exactamente un caracter.");
            return Optional.empty();
        }

        return Optional.of(symbol);
    }

    /**
     * Determina si una transición propuesta viola determinismo en un DFA.
     */
    private boolean isDeterministicConflict(String fromState, String symbol) {
        if (automatonTypeComboBox.getValue() != AutomatonType.DFA) {
            return false;
        }
        return transitions.stream().anyMatch(
            transition -> transition.getFromState().equals(fromState) && transition.getSymbol().equals(symbol)
        );
    }

    /**
     * Sincroniza el panel resumen con los conjuntos y estados vigentes del autómata.
     */
    private void refreshSummary() {
        statesListView.setItems(FXCollections.observableArrayList(states));
        alphabetListView.setItems(FXCollections.observableArrayList(alphabet));
        acceptingStatesListView.setItems(FXCollections.observableArrayList(acceptingStates));
        initialStateValueLabel.setText(initialState == null ? "-" : initialState);
    }

    /**
     * Recalcula y renderiza estados y aristas del autómata según el tamaño actual del lienzo.
     */
    private void renderAutomatonDiagram() {
        AutomatonDefinition definition = buildDefinitionFromCanvas();
        List<StateNodeView> stateViews = automatonDiagramLayoutService.layoutStates(
            definition,
            automatonDiagramPane.getWidth(),
            automatonDiagramPane.getHeight(),
            statePositions
        );
        List<TransitionEdgeView> edges = automatonDiagramLayoutService.buildEdges(definition);
        automatonDiagramRenderService.render(automatonDiagramPane, stateViews, edges);
        renderedStateViews = stateViews;
        renderedEdges = edges;
        syncStatePositions(stateViews);
    }

    /**
     * Construye una definición inmutable del autómata a partir del estado visual actual.
     */
    private AutomatonDefinition buildDefinitionFromCanvas() {
        AutomatonDefinition definition = new AutomatonDefinition();
        definition.setType(automatonTypeComboBox.getValue() == null ? AutomatonType.DFA : automatonTypeComboBox.getValue());
        definition.setStates(new LinkedHashSet<>(states));
        definition.setAlphabet(new LinkedHashSet<>(alphabet));
        definition.setInitialState(initialState);
        definition.setAcceptingStates(new LinkedHashSet<>(acceptingStates));
        definition.setTransitions(new ArrayList<>(transitions));
        return definition;
    }

    /**
     * Carga una definición externa en el canvas, restaurando transiciones y posiciones persistidas.
     */
    private void loadDefinitionIntoCanvas(AutomatonDefinition definition, Map<String, StatePosition> persistedPositions) {
        states.clear();
        states.addAll(definition.getStates());

        alphabet.clear();
        alphabet.addAll(definition.getAlphabet());

        acceptingStates.clear();
        acceptingStates.addAll(definition.getAcceptingStates());

        transitions.clear();
        transitions.addAll(definition.getTransitions());

        initialState = definition.getInitialState();

        statePositions.clear();
        if (persistedPositions != null) {
            persistedPositions.forEach((state, position) -> {
                if (position != null) {
                    statePositions.put(state, new Point2D(position.getX(), position.getY()));
                }
            });
        }

        transitionSourceState = null;
        draggedState = null;
        refreshSummary();
        renderAutomatonDiagram();
    }

    /**
     * Localiza el estado cuyo nodo contiene un punto dado en coordenadas del pane.
     */
    private String findStateAt(double x, double y) {
        for (StateNodeView state : renderedStateViews) {
            double distance = Math.hypot(state.x() - x, state.y() - y);
            if (distance <= NODE_RADIUS) {
                return state.name();
            }
        }
        return null;
    }

    /**
     * Genera el siguiente identificador de estado en formato secuencial qN.
     */
    private String generateNextStateName() {
        int index = 0;
        while (states.contains("q" + index)) {
            index++;
        }
        return "q" + index;
    }

    /**
     * Decide y muestra el menú contextual adecuado según el elemento bajo el cursor.
     */
    private void showContextMenu(MouseEvent event) {
        String state = findStateAt(event.getX(), event.getY());
        if (state != null) {
            showStateContextMenu(state, event.getScreenX(), event.getScreenY());
            return;
        }
        TransitionEdgeView edge = findTransitionAt(event.getX(), event.getY());
        if (edge != null) {
            showTransitionContextMenu(edge, event.getScreenX(), event.getScreenY());
        }
    }

    /**
     * Construye y despliega las acciones contextuales disponibles para un estado.
     */
    private void showStateContextMenu(String state, double screenX, double screenY) {
        MenuItem toggleInitial = new MenuItem(initialState != null && initialState.equals(state) ? "Quitar inicial" : "Marcar como inicial");
        toggleInitial.setOnAction(actionEvent -> {
            if (state.equals(initialState)) {
                initialState = null;
            } else {
                initialState = state;
            }
            refreshSummary();
            renderAutomatonDiagram();
        });

        MenuItem toggleFinal = new MenuItem(acceptingStates.contains(state) ? "Quitar final" : "Marcar como final");
        toggleFinal.setOnAction(actionEvent -> {
            if (acceptingStates.contains(state)) {
                acceptingStates.remove(state);
            } else {
                acceptingStates.add(state);
            }
            refreshSummary();
            renderAutomatonDiagram();
        });

        MenuItem renameStateItem = new MenuItem("Renombrar");
        renameStateItem.setOnAction(actionEvent -> renameState(state));

        MenuItem deleteStateItem = new MenuItem("Eliminar estado");
        deleteStateItem.setOnAction(actionEvent -> deleteState(state));

        diagramContextMenu.getItems().setAll(toggleInitial, toggleFinal, renameStateItem, deleteStateItem);
        diagramContextMenu.show(automatonDiagramPane, screenX, screenY);
    }

    /**
     * Construye y despliega la acción contextual para eliminar una transición.
     */
    private void showTransitionContextMenu(TransitionEdgeView edge, double screenX, double screenY) {
        MenuItem deleteTransitionItem = new MenuItem("Eliminar transicion");
        deleteTransitionItem.setOnAction(actionEvent -> {
            transitions.removeIf(transition ->
                transition.getFromState().equals(edge.fromState()) && transition.getToState().equals(edge.toState())
            );
            recomputeAlphabetFromTransitions();
            transitionSourceState = null;
            refreshSummary();
            renderAutomatonDiagram();
            validationMessageLabel.setText("Transicion eliminada.");
        });

        diagramContextMenu.getItems().setAll(deleteTransitionItem);
        diagramContextMenu.show(automatonDiagramPane, screenX, screenY);
    }

    /**
     * Renombra un estado y propaga el cambio a todos los componentes dependientes.
     */
    private void renameState(String oldName) {
        TextInputDialog dialog = new TextInputDialog(oldName);
        dialogService.configureTextInputDialog(dialog, resolveStage());
        dialog.setTitle("Renombrar estado");
        dialog.setHeaderText("Ingresa el nuevo nombre del estado");
        dialog.setContentText("Nombre:");
        Optional<String> value = dialog.showAndWait();
        if (value.isEmpty()) {
            return;
        }

        String newName = normalizeValue(value.get());
        if (newName == null) {
            validationMessageLabel.setText("El nombre del estado no puede estar vacio.");
            return;
        }
        if (newName.contains(",")) {
            validationMessageLabel.setText("El nombre del estado no puede contener comas.");
            return;
        }
        if (!oldName.equals(newName) && states.contains(newName)) {
            validationMessageLabel.setText("El nombre del estado ya existe.");
            return;
        }

        if (oldName.equals(newName)) {
            return;
        }

        states.remove(oldName);
        states.add(newName);

        if (oldName.equals(initialState)) {
            initialState = newName;
        }
        if (acceptingStates.remove(oldName)) {
            acceptingStates.add(newName);
        }

        Point2D point = statePositions.remove(oldName);
        if (point != null) {
            statePositions.put(newName, point);
        }

        for (TransitionRule transition : transitions) {
            if (transition.getFromState().equals(oldName)) {
                transition.setFromState(newName);
            }
            if (transition.getToState().equals(oldName)) {
                transition.setToState(newName);
            }
        }

        if (oldName.equals(transitionSourceState)) {
            transitionSourceState = newName;
        }

        refreshSummary();
        renderAutomatonDiagram();
        validationMessageLabel.setText("Estado renombrado a " + newName + ".");
    }

    /**
     * Elimina un estado y depura todas las estructuras asociadas a su existencia.
     */
    private void deleteState(String state) {
        states.remove(state);
        acceptingStates.remove(state);
        statePositions.remove(state);
        transitions.removeIf(transition -> transition.getFromState().equals(state) || transition.getToState().equals(state));
        recomputeAlphabetFromTransitions();
        if (state.equals(initialState)) {
            initialState = null;
        }
        if (state.equals(transitionSourceState)) {
            transitionSourceState = null;
        }
        refreshSummary();
        renderAutomatonDiagram();
        validationMessageLabel.setText("Estado eliminado: " + state);
    }

    /**
     * Reconstruye el alfabeto a partir de los símbolos usados por las transiciones actuales.
     */
    private void recomputeAlphabetFromTransitions() {
        alphabet.clear();
        for (TransitionRule transition : transitions) {
            alphabet.add(transition.getSymbol());
        }
    }

    /**
     * Identifica la transición más cercana a un punto para permitir interacción contextual.
     */
    private TransitionEdgeView findTransitionAt(double x, double y) {
        for (TransitionEdgeView edge : renderedEdges) {
            Point2D from = statePoint(edge.fromState());
            Point2D to = statePoint(edge.toState());
            if (from == null || to == null) {
                continue;
            }
            if (edge.selfLoop()) {
                if (isNearSelfLoop(x, y, from)) {
                    return edge;
                }
                continue;
            }
            if (edge.curved()) {
                if (isNearCurvedEdge(x, y, from, to, edge.curveDirection())) {
                    return edge;
                }
                continue;
            }
            if (isNearStraightEdge(x, y, from, to)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Obtiene la coordenada de un estado renderizado por su nombre lógico.
     */
    private Point2D statePoint(String stateName) {
        for (StateNodeView state : renderedStateViews) {
            if (state.name().equals(stateName)) {
                return new Point2D(state.x(), state.y());
            }
        }
        return null;
    }

    /**
     * Evalúa proximidad de un punto a una arista recta entre dos estados.
     */
    private boolean isNearStraightEdge(double x, double y, Point2D from, Point2D to) {
        Point2D[] points = edgeEndpoints(from, to);
        return distanceToSegment(x, y, points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY()) <= 10;
    }

    /**
     * Evalúa proximidad de un punto a una arista curva muestreando su trayectoria cuadrática.
     */
    private boolean isNearCurvedEdge(double x, double y, Point2D from, Point2D to, int direction) {
        Point2D[] points = edgeEndpoints(from, to);
        Point2D start = points[0];
        Point2D end = points[1];

        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double length = Math.hypot(dx, dy);
        if (length == 0) {
            return false;
        }

        double ux = dx / length;
        double uy = dy / length;
        double px = -uy;

        double controlX = (start.getX() + end.getX()) / 2.0 + px * 44 * direction;
        double controlY = (start.getY() + end.getY()) / 2.0 + ux * 44 * direction;

        double minDistance = Double.MAX_VALUE;
        int samples = 24;
        for (int i = 0; i <= samples; i++) {
            double t = (double) i / samples;
            double oneMinusT = 1 - t;
            double curveX = oneMinusT * oneMinusT * start.getX() + 2 * oneMinusT * t * controlX + t * t * end.getX();
            double curveY = oneMinusT * oneMinusT * start.getY() + 2 * oneMinusT * t * controlY + t * t * end.getY();
            minDistance = Math.min(minDistance, Math.hypot(x - curveX, y - curveY));
        }
        return minDistance <= 10;
    }

    /**
     * Evalúa proximidad de un punto a un auto-lazo asociado a un estado.
     */
    private boolean isNearSelfLoop(double x, double y, Point2D state) {
        LoopSide side = resolveLoopSide(state);
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
        double startX = state.getX() + endpointRadius * Math.cos(startAngle);
        double startY = state.getY() + endpointRadius * Math.sin(startAngle);
        double endX = state.getX() + endpointRadius * Math.cos(endAngle);
        double endY = state.getY() + endpointRadius * Math.sin(endAngle);
        double controlX = switch (side) {
            case RIGHT -> state.getX() + (NODE_RADIUS * 2.25);
            case LEFT -> state.getX() - (NODE_RADIUS * 2.25);
            default -> state.getX();
        };
        double controlY = switch (side) {
            case TOP -> state.getY() - (NODE_RADIUS * 2.25);
            case BOTTOM -> state.getY() + (NODE_RADIUS * 2.25);
            default -> state.getY();
        };

        double minDistance = Double.MAX_VALUE;
        int samples = 26;
        for (int i = 0; i <= samples; i++) {
            double t = (double) i / samples;
            double oneMinusT = 1 - t;
            double curveX = oneMinusT * oneMinusT * startX + 2 * oneMinusT * t * controlX + t * t * endX;
            double curveY = oneMinusT * oneMinusT * startY + 2 * oneMinusT * t * controlY + t * t * endY;
            minDistance = Math.min(minDistance, Math.hypot(x - curveX, y - curveY));
        }
        return minDistance <= 11;
    }

    /**
     * Selecciona el lado óptimo para dibujar auto-lazos con mínima colisión visual.
     */
    private LoopSide resolveLoopSide(Point2D state) {
        double availableRight = automatonDiagramPane.getWidth() - state.getX() - NODE_RADIUS;
        double availableLeft = state.getX() - NODE_RADIUS;
        double availableTop = state.getY() - NODE_RADIUS;
        double availableBottom = automatonDiagramPane.getHeight() - state.getY() - NODE_RADIUS;

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
     * Calcula puntos extremos de una arista recortando por el radio de los nodos.
     */
    private Point2D[] edgeEndpoints(Point2D from, Point2D to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double length = Math.hypot(dx, dy);
        if (length == 0) {
            return new Point2D[]{from, to};
        }
        double ux = dx / length;
        double uy = dy / length;
        Point2D start = new Point2D(from.getX() + ux * NODE_RADIUS, from.getY() + uy * NODE_RADIUS);
        Point2D end = new Point2D(to.getX() - ux * NODE_RADIUS, to.getY() - uy * NODE_RADIUS);
        return new Point2D[]{start, end};
    }

    /**
     * Calcula la distancia mínima entre un punto y un segmento de recta.
     */
    private double distanceToSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1);
        }
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        double projectionX = x1 + t * dx;
        double projectionY = y1 + t * dy;
        return Math.hypot(px - projectionX, py - projectionY);
    }

    /**
     * Sincroniza el mapa de posiciones con el resultado final del layout renderizado.
     */
    private void syncStatePositions(List<StateNodeView> stateViews) {
        Map<String, Point2D> normalized = new LinkedHashMap<>();
        for (StateNodeView stateView : stateViews) {
            normalized.put(stateView.name(), new Point2D(stateView.x(), stateView.y()));
        }
        statePositions.clear();
        statePositions.putAll(normalized);
    }

    /**
     * Convierte posiciones de interfaz a representación persistible para archivos JSON.
     */
    private Map<String, StatePosition> toPersistedPositions() {
        Map<String, StatePosition> persisted = new LinkedHashMap<>();
        statePositions.forEach((state, point) -> persisted.put(state, new StatePosition(point.getX(), point.getY())));
        return persisted;
    }

    /**
     * Genera una copia defensiva de la definición para intercambio entre ventanas.
     */
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

    /**
     * Normaliza entradas textuales eliminando espacios y descartando vacíos.
     */
    private String normalizeValue(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized;
    }

    /**
     * Resuelve la ventana principal propietaria de los diálogos de la vista.
     */
    private Stage resolveStage() {
        return (Stage) automatonDiagramPane.getScene().getWindow();
    }

    /**
     * Acota un valor de coordenada para mantener nodos dentro del área visible.
     */
    private double clampToCanvas(double value, double size) {
        double min = NODE_RADIUS + 8;
        double max = Math.max(min, size - NODE_RADIUS - 8);
        return Math.max(min, Math.min(max, value));
    }
}

