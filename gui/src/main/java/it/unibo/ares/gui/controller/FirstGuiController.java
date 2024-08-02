package it.unibo.ares.gui.controller;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import it.unibo.ares.core.controller.AresSupplier;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.StringCaster;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.gui.utils.GuiDinamicWriter;
import it.unibo.ares.gui.utils.GuiDinamicWriterImpl;
import it.unibo.ares.gui.utils.HandlerAdapter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * GuiController is a class that controls the first GUI of the application.
 * It implements the Initializable interface and manages the interaction between
 * the user and the GUI.
 */
public final class FirstGuiController implements Initializable {

    /**
     * GuiWriter is an instance of WriteOnGUIImpl used to manage the
     * GUI.
     */
    private final GuiDinamicWriter guiWriter = new GuiDinamicWriterImpl();
    /*
     * configurationSessionId is a string that holds the ID of the configuration
     * 
     */
    private String configurationSessionId;
    private static final int MAXSIZE = 35;
    private static Stage stage;
    private static boolean alertShown;

    /**
     * calculatorSupplier is an instance of CalculatorSupplier used to supply
     * calculator instances, models, and agents.
     */
    private final AresSupplier calculatorSupplier = AresSupplier.getInstance();

    /*
     * FXML variables
     */
    @FXML
    private Button btnStart, btnInitialize, btnSetAgent;

    /*
     * The VBox that holds the parameters of the agent and the model
     */
    @FXML
    private VBox vboxAgentPar, vboxModelPar;

    /*
     * The choice boxes that hold the agents and the models
     */
    @FXML
    private ChoiceBox<String> choiceAgent, choiceModel;

    /**
     * The initialize method is called after all @FXML annotated members have been
     * injected.
     * This method initializes the choiceModel with the model names from the
     * calculator initializer.
     * It also sets an action event handler adapter for the FXML elements and
     * disables
     * all the rest of the GUI
     * for preventing the user to interact with it before the model is selected.
     *
     * @param arg0 The location used to resolve relative paths for the root object,
     *             or null if the location is not known.
     * @param arg1 The resources used to localize the root object, or null if the
     *             root object was not localized.
     */
    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {
        guiWriter.writeChoiceBox(choiceModel, calculatorSupplier.getModels());
        guiWriter.disableElement(btnStart);
        guiWriter.disableElement(btnSetAgent);
        guiWriter.disableElement(btnInitialize);
        guiWriter.disableElement(choiceAgent);
        choiceModel.setOnAction(new HandlerAdapter(this::writeModelParametersList));
        btnInitialize.setOnAction(new HandlerAdapter(this::initializeModel));
        btnSetAgent.setOnAction(new HandlerAdapter(this::setAgentParameter));
        btnStart.setOnAction(new HandlerAdapter(this::startSecondGui));
    }

    /**
     * This method reads the parameters for the model from the VBox, sets them in
     * the calculator supplier,
     * and then disables the VBox and the Initialize button.
     * It also enables the Set Agent button and the ChoiceBox for the agents adding
     * an event handler adapter to it.
     * If any exception occurs during this process, it shows the error message.
     */
    void initializeModel() {
        try {
            final BiConsumer<String, Serializable> parameterSetter = (key, value) -> {
                calculatorSupplier.setModelParameter(configurationSessionId, key, value);
            };
            final Parameters modelParameters = calculatorSupplier.getModelParametersParameters(configurationSessionId);
            readParamatersValueAndSet(vboxModelPar, modelParameters, parameterSetter);
            guiWriter.writeChoiceBox(choiceAgent,
                    calculatorSupplier.getAgentsSimplified(configurationSessionId));
            guiWriter.disableElement(vboxModelPar);
            guiWriter.disableElement(btnInitialize);
            guiWriter.enableElement(btnSetAgent);
            guiWriter.enableElement(choiceAgent);
            guiWriter.enableElement(vboxAgentPar);
            choiceAgent.setOnAction(new HandlerAdapter(this::writeAgentParametersList));
            guiWriter.showAlert("Model correctly initialized, you can now set the parameters for the agents");
        } catch (IllegalStateException e) {
            guiWriter.showError(e.getMessage());
        }
    }

    /**
     * This method reads the parameters from the VBox and sets them in the
     * calculator supplier.
     * It iterates over the children of the VBox, and if the child is a TextField,
     * it gets its ID and uses it to retrieve the parameter from the Parameters
     * object.
     * The TextField's ID is then used as the key, and the TextField's text is
     * used as the value in the calculator.
     *
     * @param vbox            the VBox from which to read the parameters
     * @param params          the Parameters object from which to retrieve the
     *                        parameter
     *                        types
     * @param parameterSetter the BiConsumer that sets the parameter in the
     *                        calculator
     */
    private void readParamatersValueAndSet(final VBox vbox, final Parameters params,
            final BiConsumer<String, Serializable> parameterSetter) {
        /*
         * iterate over the children of the vbox and if the child is a TextField, get
         * its
         * ID and use it to retrieve the parameter from the Parameters object. The
         * TextField's ID is then used as the key, and the TextField's text is used as
         * the value.
         */
        final String errorString = "Per il parametro ";
        vbox.getChildren().stream().filter(node -> node instanceof TextField).map(node -> (TextField) node)
                .forEach(txt -> {
                    final String typeToString = params.getParameter(txt.getId()).map(Parameter::getType)
                            .map(Class::getSimpleName)
                            .orElse("");
                    final Class<Serializable> type = params.getParameter(txt.getId()).map(Parameter::getType)
                            .orElse(null);
                    final Parameter<?> parameter = params.getParameter(txt.getId()).orElse(null);
                    /*
                     * switch on the type of the parameter and cast the text of the TextField to the
                     * correct type for setting it in the calculator
                     * switch on simpleName instead of the class because we can also have not
                     * built-in types
                     */
                    switch (typeToString) {
                        /*
                         * try to set the parameter in the calculator, if an exception occurs, show the
                         * error message
                         */
                        case "String":
                            parameterSetter.accept(txt.getId(), txt.getText());
                            break;
                        case "Integer":
                            try {
                                final int value = Integer.parseInt(txt.getText());
                                if (Model.SIZEKEY.equals(parameter.getKey()) && value > MAXSIZE) {
                                    guiWriter.showAlert("The size of the space must be less than 35!");
                                }
                                parameterSetter.accept(txt.getId(), value);
                            } catch (NumberFormatException e) {
                                guiWriter.showError(
                                        errorString + parameter.getKey()
                                                + " il valore deve essere un intero");
                            }
                            break;
                        case "Double":
                            try {
                                final double value = Double.parseDouble(txt.getText().replace(",", "."));
                                parameterSetter.accept(txt.getId(), value);
                            } catch (NumberFormatException e) {
                                guiWriter.showError(
                                        errorString + parameter.getKey()
                                                + " il valore deve essere un decimale");
                            }
                            break;
                        case "Boolean":
                            try {
                                parameterSetter.accept(txt.getId(), StringCaster.cast(txt.getText(), type));
                            } catch (NumberFormatException e) {
                                guiWriter.showError(
                                        errorString + parameter.getKey()
                                                + " il valore deve essere un valore booleano (true/false)");
                            }
                            break;
                        case "Float":
                            try {
                                parameterSetter.accept(txt.getId(), StringCaster.cast(txt.getText(), type));
                            } catch (NumberFormatException e) {
                                guiWriter.showError(
                                        errorString + parameter.getKey()
                                                + " il valore deve essere un decimale");
                            }
                            break;
                        default:
                            break;
                    }
                });
    }

    /**
     * This method reads the parameters for an from the VBox, sets them in the
     * calculator supplier, and then enables the Start button if all the parameters
     * are set.
     * If any exception occurs during this process, it shows the error message.
     */
        void setAgentParameter() {
        if (choiceAgent.getValue() == null) {
            guiWriter.showError("Please select an agent to parametrize");
            return;
        }
        final BiConsumer<String, Serializable> parameterSetter = (key, value) -> {
            calculatorSupplier.setAgentParameterSimplified(configurationSessionId, choiceAgent.getValue(), key,
                    value);
        };
        final Parameters agentParameters = calculatorSupplier.getAgentParametersSimplified(configurationSessionId,
                choiceAgent.getValue());
        try {
            readParamatersValueAndSet(vboxAgentPar, agentParameters, parameterSetter);
            if (everythingIsSet()) {
                guiWriter.showAlert("All the parameters are setted, you can start the simulation!");
                guiWriter.enableElement(btnStart);
            } else {
                guiWriter.showAlert("Parameters correctly setted for agent " + choiceAgent.getValue());
            }

        } catch (Exception e) {
            guiWriter.showError(e.getMessage());
        }
    }

    /**
     * This method is used to check if all the parameters are set.
     * It iterates over the agents and checks if all the parameters are set.
     * If all the parameters are set, it returns true, otherwise it returns false.
     * Helpfull to enable the start button.
     * 
     * @return true if all the parameters are set, false otherwise
     */
    private boolean everythingIsSet() {
        for (final String agentID : calculatorSupplier.getAgentsSimplified(configurationSessionId)) {
            if (!calculatorSupplier.getAgentParametersSimplified(configurationSessionId, agentID)
                    .areAllParametersSetted()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method is used to write the agent parameters to the VBox.
     * It first gets the agent parameters from the calculator supplier, then clears
     * the VBox,
     * and finally writes the parameters to the VBox.
     */
    private void writeAgentParametersList() {
        final Parameters agentParameters = calculatorSupplier.getAgentParametersSimplified(configurationSessionId,
                choiceAgent.getValue());
        guiWriter.clearVBox(vboxAgentPar);
        guiWriter.writeVBox(vboxAgentPar, agentParameters);
    }

    /**
     * This method is used to write the model parameters to the VBox.
     * It first disables the agent choice box and the start button, then clears the
     * VBoxes,
     * sets the model in the calculator supplier, gets the model parameters from the
     * calculator supplier,
     * writes the parameters to the VBox, enables the initialize button, and finally
     * disables the set agent button.
     */
    private void writeModelParametersList() {
        if (!alertShown) {
            guiWriter.showAlert("La versione gui supporta in maniera stabile solo fino a una size 35*35");
            alertShown = true;
        }
        choiceAgent.setOnAction(null);
        guiWriter.disableElement(choiceAgent);
        guiWriter.disableElement(btnSetAgent);
        guiWriter.disableElement(btnStart);
        guiWriter.enableElement(vboxModelPar);
        /*
         * detach the event handler from the choiceAgent and disable the choiceAgent
         * and the setAgent button to prevent the user to interact with them before the
         * model is selected, helpfull when the user changes the model after he has
         * already selected one
         */
        guiWriter.clearVBox(vboxModelPar);
        guiWriter.clearVBox(vboxAgentPar);
        configurationSessionId = calculatorSupplier.addNewModel(choiceModel.getValue());
        final Parameters modelParameters = calculatorSupplier
                .getModelParametersParameters(configurationSessionId);
        guiWriter.writeVBox(vboxModelPar, modelParameters);
        guiWriter.enableElement(btnInitialize);
    }

    static void setStage(final Stage stage) {
        FirstGuiController.stage = stage;
    }

    /**
     * This method is used to start the second GUI. It loads the scene from
     * "scene2.fxml",
     * sets the scene to the stage, and shows the stage.
     * If there is an error loading the scene, it prints the stack trace of the
     * exception.
     */
    private void startSecondGui() {
        final Parent root;
        try {
            SecondGuiController.setConfigurationId(configurationSessionId);
            root = FXMLLoader.load(ClassLoader.getSystemResource("scene2.fxml"));
            final Scene scene = new Scene(root);
            stage.setTitle("ARES");
            stage.setScene(scene);
            stage.setResizable(false);
            SecondGuiController.setStage(stage);
            stage.show();
        } catch (IOException e) {
            guiWriter.showError("Errore durante il caricamento della scena 2");
        }
    }
}
