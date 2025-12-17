package Clinica;

import jade.content.AgentAction;

public class Agendar implements AgentAction {
    private Cita cita;

    public Cita getCita() {
        return cita;
    }
    public void setCita(Cita c) {
        cita = c;
    }
}