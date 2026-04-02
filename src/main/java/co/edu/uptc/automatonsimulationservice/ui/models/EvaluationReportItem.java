package co.edu.uptc.automatonsimulationservice.ui.models;

import co.edu.uptc.automatonsimulationservice.evaluation.models.EvaluationResult;

public class EvaluationReportItem {
    private final EvaluationResult result;

    public EvaluationReportItem(EvaluationResult result) {
        this.result = result;
    }

    public EvaluationResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        String status = result.isAccepted() ? "ACCEPTED" : "REJECTED";
        return result.getInput() + " -> " + status;
    }
}

