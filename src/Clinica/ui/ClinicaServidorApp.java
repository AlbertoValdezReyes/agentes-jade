package Clinica.ui;

import Clinica.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Aplicacion JavaFX para el servidor de la clinica.
 * Muestra las actividades del Fisioterapeuta y Ayudante.
 */
public class ClinicaServidorApp extends Application {

    // Componentes principales
    private VBox logContainer;
    private ScrollPane logScrollPane;
    private Label estadoLabel;

    // Panel de informacion
    private VBox infoCitaPanel;
    private VBox infoPacientePanel;
    private VBox infoDiagnosticoPanel;

    // Labels de informacion
    private Label lblCitaId;
    private Label lblPacienteNombre;
    private Label lblCitaHora;
    private Label lblCitaTerapia;
    private Label lblCitaEstado;

    private Label lblPacienteAltura;
    private Label lblPacientePeso;
    private Label lblPacienteIMC;
    private Label lblPacienteDiabetes;
    private Label lblPacienteAlergias;

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void start(Stage primaryStage) {
        // Registrar esta app en el bridge
        GUIBridgeServidor.getInstance().setApp(this);

        // Crear la interfaz
        BorderPane root = crearInterfazPrincipal();

        // Configurar escena
        Scene scene = new Scene(root, 1100, 750);

        // Intentar cargar estilos, si no existe usar estilos inline
        try {
            scene.getStylesheets().add(getClass().getResource("/Clinica/ui/styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("[SERVIDOR-GUI] No se encontro styles.css, usando estilos por defecto");
        }

        primaryStage.setTitle("Clinica de Fisioterapia - Panel del Servidor");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        // Manejar cierre de ventana
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        // Mensaje inicial
        agregarLog("Servidor iniciado. Esperando conexiones...", "SISTEMA");
    }

    private BorderPane crearInterfazPrincipal() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Header
        HBox header = crearHeader();
        root.setTop(header);

        // Contenido principal
        HBox contenido = new HBox(15);
        contenido.setPadding(new Insets(15));

        // Panel de log (izquierda)
        VBox logPanel = crearPanelLog();
        HBox.setHgrow(logPanel, Priority.ALWAYS);

        // Panel de informacion (derecha)
        VBox infoPanel = crearPanelInfo();
        infoPanel.setPrefWidth(400);
        infoPanel.setMinWidth(350);

        contenido.getChildren().addAll(logPanel, infoPanel);
        root.setCenter(contenido);

        return root;
    }

    private HBox crearHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setSpacing(20);
        header.setStyle("-fx-background-color: #16213e;");

        // Titulo
        Label titulo = new Label("Panel del Servidor - Fisioterapeuta & Ayudante");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 22));
        titulo.setTextFill(Color.WHITE);

        // Separador flexible
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Estado
        HBox estadoBox = new HBox(10);
        estadoBox.setAlignment(Pos.CENTER_RIGHT);
        Label estadoTitulo = new Label("Estado:");
        estadoTitulo.setTextFill(Color.WHITE);
        estadoLabel = new Label("Esperando");
        estadoLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5;");

        estadoBox.getChildren().addAll(estadoTitulo, estadoLabel);

        header.getChildren().addAll(titulo, spacer, estadoBox);
        return header;
    }

    private VBox crearPanelLog() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #0f0f23; -fx-background-radius: 10;");

        // Titulo del log
        Label titulo = new Label("Registro de Actividades");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        titulo.setTextFill(Color.web("#00d4ff"));

        // Contenedor de logs
        logContainer = new VBox(5);
        logContainer.setPadding(new Insets(10));
        logContainer.setStyle("-fx-background-color: #0f0f23;");

        logScrollPane = new ScrollPane(logContainer);
        logScrollPane.setFitToWidth(true);
        logScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        logScrollPane.setStyle("-fx-background: #0f0f23; -fx-background-color: #0f0f23;");
        VBox.setVgrow(logScrollPane, Priority.ALWAYS);

        // Boton para limpiar log
        Button btnLimpiar = new Button("Limpiar Log");
        btnLimpiar.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> logContainer.getChildren().clear());

        panel.getChildren().addAll(titulo, logScrollPane, btnLimpiar);
        return panel;
    }

    private VBox crearPanelInfo() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #0f0f23; -fx-background-radius: 10;");

        // Titulo
        Label titulo = new Label("Informacion Actual");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        titulo.setTextFill(Color.web("#00d4ff"));

        // Panel de cita actual
        infoCitaPanel = crearPanelInfoCita();

        // Panel de paciente
        infoPacientePanel = crearPanelInfoPaciente();

        // Panel de diagnostico
        infoDiagnosticoPanel = crearPanelInfoDiagnostico();

        panel.getChildren().addAll(titulo, infoCitaPanel, infoPacientePanel, infoDiagnosticoPanel);
        return panel;
    }

    private VBox crearPanelInfoCita() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");

        Label titulo = new Label("Cita Actual");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        titulo.setTextFill(Color.web("#e94560"));

        lblCitaId = crearLabelInfo("ID: -");
        lblPacienteNombre = crearLabelInfo("Paciente: -");
        lblCitaHora = crearLabelInfo("Hora: -");
        lblCitaTerapia = crearLabelInfo("Terapia: -");
        lblCitaEstado = crearLabelInfo("Estado: -");

        panel.getChildren().addAll(titulo, lblCitaId, lblPacienteNombre, lblCitaHora, lblCitaTerapia, lblCitaEstado);
        return panel;
    }

    private VBox crearPanelInfoPaciente() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");

        Label titulo = new Label("Datos del Paciente");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        titulo.setTextFill(Color.web("#e94560"));

        lblPacienteAltura = crearLabelInfo("Altura: -");
        lblPacientePeso = crearLabelInfo("Peso: -");
        lblPacienteIMC = crearLabelInfo("IMC: -");
        lblPacienteDiabetes = crearLabelInfo("Diabetes: -");
        lblPacienteAlergias = crearLabelInfo("Alergias: -");

        panel.getChildren().addAll(titulo, lblPacienteAltura, lblPacientePeso, lblPacienteIMC,
                                   lblPacienteDiabetes, lblPacienteAlergias);
        return panel;
    }

    private VBox crearPanelInfoDiagnostico() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");

        Label titulo = new Label("Ultimo Diagnostico");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        titulo.setTextFill(Color.web("#e94560"));

        Label lblVacio = crearLabelInfo("Sin diagnostico activo");

        panel.getChildren().addAll(titulo, lblVacio);
        return panel;
    }

    private Label crearLabelInfo(String texto) {
        Label label = new Label(texto);
        label.setTextFill(Color.WHITE);
        label.setWrapText(true);
        return label;
    }

    // Metodo para agregar logs
    public void agregarLog(String mensaje, String agente) {
        String tiempo = LocalDateTime.now().format(timeFormatter);

        HBox logEntry = new HBox(10);
        logEntry.setAlignment(Pos.CENTER_LEFT);
        logEntry.setPadding(new Insets(5, 10, 5, 10));

        // Determinar color segun agente
        String color;
        String icono;
        switch (agente.toUpperCase()) {
            case "FISIOTERAPEUTA":
                color = "#4CAF50";
                icono = "[F]";
                break;
            case "AYUDANTE":
                color = "#2196F3";
                icono = "[A]";
                break;
            case "SISTEMA":
                color = "#FF9800";
                icono = "[S]";
                break;
            default:
                color = "#9E9E9E";
                icono = "[?]";
        }

        Label lblTiempo = new Label(tiempo);
        lblTiempo.setTextFill(Color.GRAY);
        lblTiempo.setFont(Font.font("Monospace", 11));
        lblTiempo.setMinWidth(70);

        Label lblAgente = new Label(icono);
        lblAgente.setTextFill(Color.web(color));
        lblAgente.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        lblAgente.setMinWidth(30);

        Label lblMensaje = new Label(mensaje);
        lblMensaje.setTextFill(Color.WHITE);
        lblMensaje.setWrapText(true);
        lblMensaje.setFont(Font.font("Monospace", 11));

        logEntry.getChildren().addAll(lblTiempo, lblAgente, lblMensaje);
        logEntry.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 3;");

        logContainer.getChildren().add(logEntry);

        // Auto-scroll al final
        Platform.runLater(() -> logScrollPane.setVvalue(1.0));
    }

    // Metodos para actualizar informacion
    public void actualizarInfoCita(Cita cita) {
        lblCitaId.setText("ID: " + cita.getId());
        lblPacienteNombre.setText("Paciente: " + cita.getNombrePaciente());
        lblCitaHora.setText("Hora: " + cita.getHora());
        lblCitaTerapia.setText("Terapia: " + cita.getTipoTerapia());
        lblCitaEstado.setText("Estado: " + cita.getEstado());
    }

    public void actualizarInfoPaciente(Paciente paciente, String idCita) {
        lblPacienteAltura.setText("Altura: " + paciente.getAltura() + " m");
        lblPacientePeso.setText("Peso: " + paciente.getPeso() + " kg");
        lblPacienteIMC.setText("IMC: " + String.format("%.1f", paciente.calcularIMC()));
        lblPacienteDiabetes.setText("Diabetes: " + (paciente.isDiabetes() ? "Si" : "No"));
        lblPacienteAlergias.setText("Alergias: " + (paciente.getAlergias() != null ? paciente.getAlergias() : "Ninguna"));
    }

    public void mostrarDiagnostico(Consulta consulta, String idCita) {
        infoDiagnosticoPanel.getChildren().clear();

        Label titulo = new Label("Diagnostico Generado");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        titulo.setTextFill(Color.web("#e94560"));

        Label lblZona = crearLabelInfo("Zona: " + consulta.getZonaDolor());
        Label lblDolor = crearLabelInfo("Nivel dolor: " + consulta.getNivelDolor() + "/10");
        Label lblProc = crearLabelInfo("Procedimiento: " + consulta.getProcedimiento());
        Label lblSesiones = crearLabelInfo("Sesiones: " + consulta.getSesionesRecomendadas());

        infoDiagnosticoPanel.getChildren().addAll(titulo, lblZona, lblDolor, lblProc, lblSesiones);
    }

    public void actualizarEstado(String estado) {
        estadoLabel.setText(estado);
    }

    public void limpiarInfo() {
        lblCitaId.setText("ID: -");
        lblPacienteNombre.setText("Paciente: -");
        lblCitaHora.setText("Hora: -");
        lblCitaTerapia.setText("Terapia: -");
        lblCitaEstado.setText("Estado: -");

        lblPacienteAltura.setText("Altura: -");
        lblPacientePeso.setText("Peso: -");
        lblPacienteIMC.setText("IMC: -");
        lblPacienteDiabetes.setText("Diabetes: -");
        lblPacienteAlergias.setText("Alergias: -");

        infoDiagnosticoPanel.getChildren().clear();
        Label titulo = new Label("Ultimo Diagnostico");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        titulo.setTextFill(Color.web("#e94560"));
        Label lblVacio = crearLabelInfo("Sin diagnostico activo");
        infoDiagnosticoPanel.getChildren().addAll(titulo, lblVacio);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
