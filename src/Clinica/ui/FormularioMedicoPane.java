package Clinica.ui;

import Clinica.Paciente;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.Consumer;

/**
 * Panel de formulario para capturar datos medicos del paciente.
 * Solicitado por el Ayudante antes de la consulta.
 */
public class FormularioMedicoPane extends VBox {

    private TextField txtAltura;
    private TextField txtPeso;
    private CheckBox chkDiabetes;
    private TextArea txtAlergias;
    private Button btnEnviar;

    private Consumer<Paciente> onEnviar;

    public FormularioMedicoPane(Consumer<Paciente> onEnviar) {
        this.onEnviar = onEnviar;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setSpacing(15);
        setPadding(new Insets(20));
        getStyleClass().add("formulario-medico");

        // Titulo
        Label titulo = new Label("Datos Medicos");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.getStyleClass().add("formulario-titulo");

        // Subtitulo explicativo
        Label subtitulo = new Label("Por favor complete la siguiente informacion para su expediente:");
        subtitulo.setWrapText(true);
        subtitulo.getStyleClass().add("formulario-subtitulo");

        // Campo altura
        VBox alturaBox = crearCampo("Altura (metros):");
        txtAltura = new TextField();
        txtAltura.setPromptText("Ej: 1.75");
        txtAltura.getStyleClass().add("campo-texto");
        alturaBox.getChildren().add(txtAltura);

        // Campo peso
        VBox pesoBox = crearCampo("Peso (kilogramos):");
        txtPeso = new TextField();
        txtPeso.setPromptText("Ej: 70");
        txtPeso.getStyleClass().add("campo-texto");
        pesoBox.getChildren().add(txtPeso);

        // Checkbox diabetes
        VBox diabetesBox = new VBox(5);
        chkDiabetes = new CheckBox("Padece Diabetes");
        chkDiabetes.getStyleClass().add("checkbox-medico");
        diabetesBox.getChildren().add(chkDiabetes);

        // Campo alergias
        VBox alergiasBox = crearCampo("Alergias a medicamentos:");
        txtAlergias = new TextArea();
        txtAlergias.setPromptText("Liste los medicamentos a los que es alergico, o escriba 'Ninguna'");
        txtAlergias.setPrefRowCount(3);
        txtAlergias.setWrapText(true);
        txtAlergias.getStyleClass().add("campo-textarea");
        alergiasBox.getChildren().add(txtAlergias);

        // Nota informativa
        Label nota = new Label("Esta informacion es confidencial y sera utilizada unicamente para su atencion medica.");
        nota.setWrapText(true);
        nota.getStyleClass().add("nota-informativa");

        // Boton enviar
        btnEnviar = new Button("Enviar Datos");
        btnEnviar.getStyleClass().addAll("btn", "btn-primary");
        btnEnviar.setMaxWidth(Double.MAX_VALUE);
        btnEnviar.setOnAction(e -> enviarFormulario());

        getChildren().addAll(titulo, subtitulo, alturaBox, pesoBox, diabetesBox, alergiasBox, nota, btnEnviar);
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
        double altura;
        double peso;

        try {
            altura = Double.parseDouble(txtAltura.getText().trim().replace(",", "."));
            if (altura <= 0 || altura > 3) {
                mostrarError("Por favor ingrese una altura valida (entre 0 y 3 metros)");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Por favor ingrese una altura valida en metros");
            return;
        }

        try {
            peso = Double.parseDouble(txtPeso.getText().trim().replace(",", "."));
            if (peso <= 0 || peso > 500) {
                mostrarError("Por favor ingrese un peso valido");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Por favor ingrese un peso valido en kilogramos");
            return;
        }

        String alergias = txtAlergias.getText().trim();
        if (alergias.isEmpty()) {
            alergias = "Ninguna";
        }

        // Crear objeto Paciente
        Paciente paciente = new Paciente();
        paciente.setAltura(altura);
        paciente.setPeso(peso);
        paciente.setDiabetes(chkDiabetes.isSelected());
        paciente.setAlergias(alergias);

        // Notificar
        if (onEnviar != null) {
            onEnviar.accept(paciente);
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
        txtAltura.clear();
        txtPeso.clear();
        chkDiabetes.setSelected(false);
        txtAlergias.clear();
    }
}
