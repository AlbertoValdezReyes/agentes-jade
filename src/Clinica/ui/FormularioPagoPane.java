package Clinica.ui;

import Clinica.Pago;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.Consumer;

/**
 * Panel de formulario para realizar el pago de una cita.
 */
public class FormularioPagoPane extends VBox {

    private Label lblMonto;
    private ToggleGroup grupoPago;
    private RadioButton rbEfectivo;
    private RadioButton rbTarjeta;
    private TextField txtTarjeta;
    private Label lblTarjetaLabel;
    private Button btnPagar;

    private double monto;
    private Consumer<Pago> onPagar;

    public FormularioPagoPane(Consumer<Pago> onPagar) {
        this.onPagar = onPagar;
        this.monto = 0;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setSpacing(15);
        setPadding(new Insets(20));
        getStyleClass().add("formulario-pago");

        // Titulo
        Label titulo = new Label("Realizar Pago");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.getStyleClass().add("formulario-titulo");

        // Monto a pagar
        VBox montoBox = new VBox(5);
        Label lblMontoTitulo = new Label("Monto a Pagar:");
        lblMontoTitulo.getStyleClass().add("campo-label");
        lblMonto = new Label("$0.00");
        lblMonto.setFont(Font.font("System", FontWeight.BOLD, 24));
        lblMonto.getStyleClass().add("monto-label");
        montoBox.getChildren().addAll(lblMontoTitulo, lblMonto);

        // Metodo de pago
        VBox pagoBox = new VBox(10);
        Label lblMetodo = new Label("Seleccione Metodo de Pago:");
        lblMetodo.getStyleClass().add("campo-label");

        grupoPago = new ToggleGroup();

        rbEfectivo = new RadioButton("Efectivo");
        rbEfectivo.setToggleGroup(grupoPago);
        rbEfectivo.getStyleClass().add("radio-pago");

        rbTarjeta = new RadioButton("Tarjeta de Credito/Debito");
        rbTarjeta.setToggleGroup(grupoPago);
        rbTarjeta.getStyleClass().add("radio-pago");

        // Campo de tarjeta
        lblTarjetaLabel = new Label("Numero de Tarjeta (ultimos 4 digitos):");
        lblTarjetaLabel.getStyleClass().add("campo-label");
        lblTarjetaLabel.setVisible(false);
        lblTarjetaLabel.setManaged(false);

        txtTarjeta = new TextField();
        txtTarjeta.setPromptText("XXXX");
        txtTarjeta.setMaxWidth(100);
        txtTarjeta.getStyleClass().add("campo-texto");
        txtTarjeta.setVisible(false);
        txtTarjeta.setManaged(false);

        // Listener
        grupoPago.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean esTarjeta = newVal == rbTarjeta;
            lblTarjetaLabel.setVisible(esTarjeta);
            lblTarjetaLabel.setManaged(esTarjeta);
            txtTarjeta.setVisible(esTarjeta);
            txtTarjeta.setManaged(esTarjeta);
        });

        pagoBox.getChildren().addAll(lblMetodo, rbEfectivo, rbTarjeta, lblTarjetaLabel, txtTarjeta);

        // Boton pagar
        btnPagar = new Button("Confirmar Pago");
        btnPagar.getStyleClass().addAll("btn", "btn-success");
        btnPagar.setMaxWidth(Double.MAX_VALUE);
        btnPagar.setOnAction(e -> procesarPago());

        getChildren().addAll(titulo, montoBox, pagoBox, btnPagar);
    }

    private void procesarPago() {
        if (grupoPago.getSelectedToggle() == null) {
            mostrarError("Por favor seleccione un metodo de pago");
            return;
        }
        if (rbTarjeta.isSelected() && txtTarjeta.getText().trim().length() != 4) {
            mostrarError("Por favor ingrese los ultimos 4 digitos de su tarjeta");
            return;
        }

        Pago pago = new Pago();
        pago.setMonto(monto);

        if (rbEfectivo.isSelected()) {
            pago.setTipoPago(Pago.EFECTIVO);
        } else {
            pago.setTipoPago(Pago.TARJETA);
            pago.setNumeroTarjeta(txtTarjeta.getText().trim());
        }

        pago.setPagado(true);

        if (onPagar != null) {
            onPagar.accept(pago);
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validacion");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setMonto(double monto) {
        this.monto = monto;
        lblMonto.setText(String.format("$%.2f", monto));
    }

    public void limpiar() {
        grupoPago.selectToggle(null);
        txtTarjeta.clear();
        lblTarjetaLabel.setVisible(false);
        lblTarjetaLabel.setManaged(false);
        txtTarjeta.setVisible(false);
        txtTarjeta.setManaged(false);
    }
}
