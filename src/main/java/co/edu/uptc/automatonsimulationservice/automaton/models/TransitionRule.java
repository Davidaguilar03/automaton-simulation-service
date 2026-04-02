package co.edu.uptc.automatonsimulationservice.automaton.models;

import java.util.Objects;

public class TransitionRule {
    private String fromState;
    private String symbol;
    private String toState;

    public TransitionRule() {
    }

    public TransitionRule(String fromState, String symbol, String toState) {
        this.fromState = fromState;
        this.symbol = symbol;
        this.toState = toState;
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof TransitionRule transitionRule)) {
            return false;
        }
        return Objects.equals(fromState, transitionRule.fromState)
            && Objects.equals(symbol, transitionRule.symbol)
            && Objects.equals(toState, transitionRule.toState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromState, symbol, toState);
    }
}

