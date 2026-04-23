package de.oliverpabst.pqt.ui;

import de.oliverpabst.pqt.controller.WelcomeScreenController;
import de.oliverpabst.pqt.viewmodel.WelcomeViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
@Tag("ui")
class WelcomeScreenUiTest {

    private WelcomeScreenController controller;

    @Start
    private void start(final Stage stage) throws Exception {
        final ResourceBundle resBundle = ResourceBundle.getBundle(
                "de.oliverpabst.pqt.lang_properties.guistrings");

        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader()
                .getResource("de/oliverpabst/pqt/views/WelcomeScreen.fxml"));
        final Parent root = loader.load();

        controller = loader.getController();
        final WelcomeViewModel viewModel = new WelcomeViewModel(resBundle);
        viewModel.setPrimaryStage(stage);
        controller.setViewModel(viewModel);
        controller.setStage(stage);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @AfterEach
    void tearDown() {
        if (controller != null) {
            controller.dispose();
        }
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void welcomeScreenShowsCoreControls(final FxRobot robot) {
        final TitledPane connectionsPane = robot.lookup("#connectionsTitledPane").queryAs(TitledPane.class);
        final Accordion connectionAccordion = robot.lookup("#connectionAccordion").queryAs(Accordion.class);
        final Button addConnectionButton = robot.lookup("#addConnection").queryAs(Button.class);

        assertNotNull(connectionsPane);
        assertNotNull(connectionAccordion);
        assertNotNull(addConnectionButton);
        assertFalse(addConnectionButton.getText().isBlank());
        assertEquals(0, connectionAccordion.getPanes().size());
    }
}
