package Clinica;

import jade.content.AgentAction;

/**
 * Accion para realizar el pago de una cita.
 */
public class RealizarPago implements AgentAction {

    private Pago pago;
    private String idCita;

    public RealizarPago() {
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }
}
