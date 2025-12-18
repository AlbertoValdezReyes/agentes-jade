package Clinica.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para un mensaje en el chat.
 */
public class MensajeChat {

    public enum TipoAgente {
        RECEPCIONISTA,
        FISIOTERAPEUTA,
        AYUDANTE,
        SISTEMA
    }

    private String contenido;
    private TipoAgente agente;
    private LocalDateTime timestamp;

    public MensajeChat(String contenido, TipoAgente agente) {
        this.contenido = contenido;
        this.agente = agente;
        this.timestamp = LocalDateTime.now();
    }

    public String getContenido() {
        return contenido;
    }

    public TipoAgente getAgente() {
        return agente;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTimestampFormateado() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getNombreAgente() {
        switch (agente) {
            case RECEPCIONISTA:
                return "Recepcionista";
            case FISIOTERAPEUTA:
                return "Fisioterapeuta";
            case AYUDANTE:
                return "Ayudante";
            case SISTEMA:
                return "Sistema";
            default:
                return "Desconocido";
        }
    }

    public String getColorAgente() {
        switch (agente) {
            case RECEPCIONISTA:
                return "#4CAF50";  // Verde
            case FISIOTERAPEUTA:
                return "#2196F3";  // Azul
            case AYUDANTE:
                return "#FF9800";  // Naranja
            case SISTEMA:
                return "#9E9E9E";  // Gris
            default:
                return "#757575";
        }
    }
}
