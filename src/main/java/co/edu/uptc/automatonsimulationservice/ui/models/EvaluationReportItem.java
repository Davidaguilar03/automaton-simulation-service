package co.edu.uptc.automatonsimulationservice.ui.models;

import co.edu.uptc.automatonsimulationservice.evaluation.models.EvaluationResult;

/**
 * Adaptador de presentación para mostrar un resultado de evaluación en listas de la interfaz.
 */
public class EvaluationReportItem {
    private final EvaluationResult result;

    /**
     * Construye un elemento de reporte a partir de un resultado de evaluación.
     */
    public EvaluationReportItem(EvaluationResult result) {
        this.result = result;
    }

    /**
     * Retorna el resultado de evaluación encapsulado por este ítem de UI.
     */
    public EvaluationResult getResult() {
        return result;
    }

    /**
     * Define la representación textual visible para el usuario en la lista de resultados.
     */
    @Override
    public String toString() {
        String status = result.isAccepted() ? "ACEPTADA" : "RECHAZADA";
        return result.getInput() + " -> " + status;
    }
}

