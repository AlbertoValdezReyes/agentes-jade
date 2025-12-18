package Clinica.ui;

import Clinica.Consulta;
import Clinica.Paciente;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.Consumer;

/**
 * Panel de formulario para capturar sintomas del paciente.
 * Solicitado por el Fisioterapeuta durante la consulta.
 */
public class FormularioSintomasPane extends VBox {

    private ComboBox<String> cmbZonaDolor;
    private Slider sliderDolor;
    private Label lblNivelDolor;
    private TextArea txtSintomas;
    private Button btnEnviar;
    private Label lblInfoPaciente;

    private Paciente paciente;
    private Consumer<Consulta> onEnviar;

    // Zonas del cuerpo para el dolor
    private static final String[] ZONAS_DOLOR = {
        "Cuello / Cervical",
        "Hombros",
        "Espalda Alta / Dorsal",
        "Espalda Baja / Lumbar",
        "Brazos",
        "Manos / Munecas",
        "Cadera",
        "Piernas / Muslos",
        "Rodillas",
        "Tobillos / Pies",
        "Otra zona"
    };

    public FormularioSintomasPane(Consumer<Consulta> onEnviar) {
        this.onEnviar = onEnviar;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setSpacing(15);
        setPadding(new Insets(20));
        getStyleClass().add("formulario-sintomas");

        // Titulo
        Label titulo = new Label("Consulta de Sintomas");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.getStyleClass().add("formulario-titulo");

        // Info del paciente
        lblInfoPaciente = new Label("");
        lblInfoPaciente.setWrapText(true);
        lblInfoPaciente.getStyleClass().add("info-paciente");

        // Zona de dolor
        VBox zonaBox = crearCampo("Donde siente el dolor o molestia?");
        cmbZonaDolor = new ComboBox<>();
        cmbZonaDolor.getItems().addAll(ZONAS_DOLOR);
        cmbZonaDolor.setPromptText("Seleccione la zona afectada");
        cmbZonaDolor.getStyleClass().add("campo-combo");
        cmbZonaDolor.setMaxWidth(Double.MAX_VALUE);
        zonaBox.getChildren().add(cmbZonaDolor);

        // Nivel de dolor
        VBox dolorBox = crearCampo("Nivel de dolor (1-10):");
        sliderDolor = new Slider(1, 10, 5);
        sliderDolor.setShowTickLabels(true);
        sliderDolor.setShowTickMarks(true);
        sliderDolor.setMajorTickUnit(1);
        sliderDolor.setMinorTickCount(0);
        sliderDolor.setSnapToTicks(true);
        sliderDolor.getStyleClass().add("slider-dolor");

        lblNivelDolor = new Label("5 - Moderado");
        lblNivelDolor.getStyleClass().add("nivel-dolor-label");

        sliderDolor.valueProperty().addListener((obs, oldVal, newVal) -> {
            int nivel = newVal.intValue();
            String descripcion;
            switch (nivel) {
                case 1: case 2:
                    descripcion = "Leve";
                    break;
                case 3: case 4:
                    descripcion = "Menor";
                    break;
                case 5: case 6:
                    descripcion = "Moderado";
                    break;
                case 7: case 8:
                    descripcion = "Severo";
                    break;
                case 9: case 10:
                    descripcion = "Muy Severo";
                    break;
                default:
                    descripcion = "";
            }
            lblNivelDolor.setText(nivel + " - " + descripcion);
        });

        dolorBox.getChildren().addAll(sliderDolor, lblNivelDolor);

        // Descripcion de sintomas
        VBox sintomasBox = crearCampo("Describa sus sintomas:");
        txtSintomas = new TextArea();
        txtSintomas.setPromptText("Describa como se siente el dolor, cuando empezo, que lo provoca, etc.");
        txtSintomas.setPrefRowCount(4);
        txtSintomas.setWrapText(true);
        txtSintomas.getStyleClass().add("campo-textarea");
        sintomasBox.getChildren().add(txtSintomas);

        // Boton enviar
        btnEnviar = new Button("Enviar Sintomas");
        btnEnviar.getStyleClass().addAll("btn", "btn-primary");
        btnEnviar.setMaxWidth(Double.MAX_VALUE);
        btnEnviar.setOnAction(e -> enviarFormulario());

        getChildren().addAll(titulo, lblInfoPaciente, zonaBox, dolorBox, sintomasBox, btnEnviar);
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
        if (cmbZonaDolor.getValue() == null) {
            mostrarError("Por favor seleccione la zona donde siente dolor");
            return;
        }
        if (txtSintomas.getText().trim().isEmpty()) {
            mostrarError("Por favor describa sus sintomas");
            return;
        }

        // Crear objeto Consulta
        Consulta consulta = new Consulta();
        consulta.setZonaDolor(cmbZonaDolor.getValue());
        consulta.setNivelDolor((int) sliderDolor.getValue());
        consulta.setDescripcionSintomas(txtSintomas.getText().trim());

        // Notificar
        if (onEnviar != null) {
            onEnviar.accept(consulta);
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validacion");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null) {
            String info = String.format("Datos del paciente - Altura: %.2fm, Peso: %.1fkg, IMC: %.1f",
                paciente.getAltura(), paciente.getPeso(), paciente.calcularIMC());
            if (paciente.isDiabetes()) {
                info += " | Diabetico";
            }
            if (paciente.getAlergias() != null && !paciente.getAlergias().equalsIgnoreCase("ninguna")) {
                info += " | Alergias: " + paciente.getAlergias();
            }
            lblInfoPaciente.setText(info);
        }
    }

    public void limpiar() {
        cmbZonaDolor.getSelectionModel().clearSelection();
        sliderDolor.setValue(5);
        txtSintomas.clear();
    }
}
