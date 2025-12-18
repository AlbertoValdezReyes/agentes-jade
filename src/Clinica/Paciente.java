package Clinica;

import jade.content.Concept;

/**
 * Clase de datos que almacena la informacion medica de un paciente.
 * No es un agente, solo un contenedor de datos para la ontologia.
 */
public class Paciente implements Concept {

    private String nombre;
    private double altura;      // en metros
    private double peso;        // en kilogramos
    private boolean diabetes;
    private String alergias;    // lista de alergias a medicamentos

    public Paciente() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public boolean isDiabetes() {
        return diabetes;
    }

    public void setDiabetes(boolean diabetes) {
        this.diabetes = diabetes;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public double calcularIMC() {
        if (altura > 0) {
            return peso / (altura * altura);
        }
        return 0;
    }
}
