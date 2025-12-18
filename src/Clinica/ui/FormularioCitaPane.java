package Clinica.ui;

import Clinica.Cita;
import Clinica.Pago;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.Consumer;

/**
 * Panel de formulario para crear una nueva cita.
 * Incluye seleccion de tipo de terapia y metodo de pago.
 */
public class FormularioCitaPane extends VBox {

    private TextField txtNombre;
    private ComboBox<String> cmbHora;
    private ComboBox<String> cmbTerapia;
    private ToggleGroup grupoPago;
    private RadioButton rbEfectivo;
    private RadioButton rbTarjeta;
    private TextField txtTarjeta;
    private Label lblTarjeta;
    private Button btnEnviar;

    private Consumer<Cita> onEnviar;

    // Tipos de terapia predefinidos
    private static final String[] TIPOS_TERAPIA = {
        "Masaje Terapeutico",
        "Rehabilitacion Fisica",
        "Electroterapia",
        "Hidroterapia",
        "Terapia Manual",
        "Ejercicios Terapeuticos"
    };

    // Precios por tipo de terapia
    private static final double[] PRECIOS_TERAPIA = {
        500.0,  // Masaje Terapeutico
        800.0,  // Rehabilitacion Fisica
        600.0,  // Electroterapia
        700.0,  // Hidroterapia
        550.0,  // Terapia Manual
        450.0   // Ejercicios Terapeuticos
    };

    // Horarios disponibles
    private static final String[] HORARIOS = {
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "14:00", "14:30", "15:00",
        "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"
    };

    public FormularioCitaPane(Consumer<Cita> onEnviar) {
        this.onEnviar = onEnviar;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setSpacing(15);
        setPadding(new Insets(20));
        getStyleClass().add("formulario-cita");

        // Titulo
        Label titulo = new Label("Nueva Cita");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.getStyleClass().add("formulario-titulo");

        // Campo nombre
        VBox nombreBox = crearCampo("Nombre del Paciente:");
        txtNombre = new TextField();
        txtNombre.setPromptText("Ingrese su nombre completo");
        txtNombre.getStyleClass().add("campo-texto");
        nombreBox.getChildren().add(txtNombre);

        // Campo hora
        VBox horaBox = crearCampo("Hora de la Cita:");
        cmbHora = new ComboBox<>();
        cmbHora.getItems().addAll(HORARIOS);
        cmbHora.setPromptText("Seleccione un horario");
        cmbHora.getStyleClass().add("campo-combo");
        cmbHora.setMaxWidth(Double.MAX_VALUE);
        horaBox.getChildren().add(cmbHora);

        // Campo tipo de terapia
        VBox terapiaBox = crearCampo("Tipo de Terapia:");
        cmbTerapia = new ComboBox<>();
        cmbTerapia.getItems().addAll(TIPOS_TERAPIA);
        cmbTerapia.setPromptText("Seleccione el tipo de terapia");
        cmbTerapia.getStyleClass().add("campo-combo");
        cmbTerapia.setMaxWidth(Double.MAX_VALUE);

        // Mostrar precio al seleccionar terapia
        Label lblPrecio = new Label("");
        lblPrecio.getStyleClass().add("precio-label");
        cmbTerapia.setOnAction(e -> {
            int idx = cmbTerapia.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                lblPrecio.setText("Precio: $" + PRECIOS_TERAPIA[idx]);
            }
        });
        terapiaBox.getChildren().addAll(cmbTerapia, lblPrecio);

        // Seccion de pago
        VBox pagoBox = crearCampo("Metodo de Pago:");
        grupoPago = new ToggleGroup();

        rbEfectivo = new RadioButton("Efectivo");
        rbEfectivo.setToggleGroup(grupoPago);
        rbEfectivo.getStyleClass().add("radio-pago");

        rbTarjeta = new RadioButton("Tarjeta");
        rbTarjeta.setToggleGroup(grupoPago);
        rbTarjeta.getStyleClass().add("radio-pago");

        HBox opcionesPago = new HBox(20);
        opcionesPago.getChildren().addAll(rbEfectivo, rbTarjeta);

        // Campo de tarjeta (solo visible si se selecciona tarjeta)
        lblTarjeta = new Label("Numero de Tarjeta (ultimos 4 digitos):");
        lblTarjeta.getStyleClass().add("campo-label");
        lblTarjeta.setVisible(false);
        lblTarjeta.setManaged(false);

        txtTarjeta = new TextField();
        txtTarjeta.setPromptText("XXXX");
        txtTarjeta.setMaxWidth(100);
        txtTarjeta.getStyleClass().add("campo-texto");
        txtTarjeta.setVisible(false);
        txtTarjeta.setManaged(false);

        // Listener para mostrar/ocultar campo de tarjeta
        grupoPago.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean esTarjeta = newVal == rbTarjeta;
            lblTarjeta.setVisible(esTarjeta);
            lblTarjeta.setManaged(esTarjeta);
            txtTarjeta.setVisible(esTarjeta);
            txtTarjeta.setManaged(esTarjeta);
        });

        pagoBox.getChildren().addAll(opcionesPago, lblTarjeta, txtTarjeta);

        // Boton enviar
        btnEnviar = new Button("Agendar Cita");
        btnEnviar.getStyleClass().addAll("btn", "btn-primary");
        btnEnviar.setMaxWidth(Double.MAX_VALUE);
        btnEnviar.setOnAction(e -> enviarFormulario());

        // Agregar todos los componentes
        getChildren().addAll(titulo, nombreBox, horaBox, terapiaBox, pagoBox, btnEnviar);
    }

    private VBox crearCampo(String etiqueta) {
        VBox box = new VBox(5);
        Label lbl = new Label(etiqueta);
        lbl.getStyleClass().add("campo-label");
        box.getChildren().add(lbl);
        return box;
    }

    private void enviarFormulario() {
        // Validar campos
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese su nombre");
            return;
        }
        if (cmbHora.getValue() == null) {
            mostrarError("Por favor seleccione un horario");
            return;
        }
        if (cmbTerapia.getValue() == null) {
            mostrarError("Por favor seleccione el tipo de terapia");
            return;
        }
        if (grupoPago.getSelectedToggle() == null) {
            mostrarError("Por favor seleccione un metodo de pago");
            return;
        }
        if (rbTarjeta.isSelected() && txtTarjeta.getText().trim().length() != 4) {
            mostrarError("Por favor ingrese los ultimos 4 digitos de su tarjeta");
            return;
        }

        // Crear objeto Cita
        Cita cita = new Cita();
        cita.setNombrePaciente(txtNombre.getText().trim());
        cita.setHora(cmbHora.getValue());
        cita.setTipoTerapia(cmbTerapia.getValue());

        // Crear objeto Pago
        Pago pago = new Pago();
        int idx = cmbTerapia.getSelectionModel().getSelectedIndex();
        pago.setMonto(PRECIOS_TERAPIA[idx]);

        if (rbEfectivo.isSelected()) {
            pago.setTipoPago(Pago.EFECTIVO);
        } else {
            pago.setTipoPago(Pago.TARJETA);
            pago.setNumeroTarjeta(txtTarjeta.getText().trim());
        }

        cita.setPago(pago);

        // Notificar
        if (onEnviar != null) {
            onEnviar.accept(cita);
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validacion");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void limpiar() {
        txtNombre.clear();
        cmbHora.getSelectionModel().clearSelection();
        cmbTerapia.getSelectionModel().clearSelection();
        grupoPago.selectToggle(null);
        txtTarjeta.clear();
        lblTarjeta.setVisible(false);
        lblTarjeta.setManaged(false);
        txtTarjeta.setVisible(false);
        txtTarjeta.setManaged(false);
    }
}
