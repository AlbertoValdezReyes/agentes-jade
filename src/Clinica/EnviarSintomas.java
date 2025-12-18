package Clinica;

import jade.content.AgentAction;

/**
 * Accion para enviar los sintomas del paciente.
 * El Recepcionista envia esta accion al Fisioterapeuta.
 */
public class EnviarSintomas implements AgentAction {

    private Consulta consulta;
    private String idCita;

    public EnviarSintomas() {
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }
}
