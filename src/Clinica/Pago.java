package Clinica;

import jade.content.Concept;

/**
 * Clase de datos que representa la informacion de pago de una cita.
 */
public class Pago implements Concept {

    public static final String EFECTIVO = "EFECTIVO";
    public static final String TARJETA = "TARJETA";

    private String tipoPago;
    private double monto;
    private String numeroTarjeta;
    private boolean pagado;

    public Pago() {
        this.pagado = false;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public boolean isPagado() {
        return pagado;
    }

    public boolean getPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }
}
