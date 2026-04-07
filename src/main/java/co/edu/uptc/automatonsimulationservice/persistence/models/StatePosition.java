package co.edu.uptc.automatonsimulationservice.persistence.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la posicion cartesiana de un estado en el lienzo de la interfaz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatePosition {
    private double x;
    private double y;
}

