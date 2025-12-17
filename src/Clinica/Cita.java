package Clinica;

import jade.content.Concept;

public class Cita implements Concept {
    private String nombrePaciente;
    private String hora;
    private String tipoTerapia;

    public String getNombrePaciente() {
        return nombrePaciente;
    }
    public void setNombrePaciente(String n) {
        nombrePaciente = n;
    }
    public String getHora() {
        return hora;
    }
    public void setHora(String h) {
        hora = h;
    }
    public String getTipoTerapia() {
        return tipoTerapia;
    }
    public void setTipoTerapia(String t) {
        tipoTerapia = t;
    }
}
