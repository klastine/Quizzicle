package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Controller for a popup that appears at program launch, asking for Username and Host/Join side.
 *
 * @author gscott4
 * @version 1.2.1
 */
public class popupController
{

    @FXML
    private Text promptLabel;

    @FXML
    private TextField usernameTextBox;

    @FXML
    private Button hostButton;

    @FXML
    private Button joinButton;

    /**
     * Reference to an external List that popup results get passed to.
     */
    ArrayList<String> results;

    @FXML
    void hostButtonPressed(ActionEvent event)
    {
        results.add("host");
        results.add(usernameTextBox.getText().trim());
        close();
    }

    @FXML
    void joinButtonPressed(ActionEvent event)
    {
        results.add("join");
        results.add(usernameTextBox.getText().trim());
        close();
    }

    /**
     * Closes the window.
     */
    private void close()
    {
        ((Stage) usernameTextBox.getScene().getWindow()).close();
    }

    /**
     * Links the results of user interaction with the GUI to an external List.
     * REQUIRED IN ORDER TO SAVE RESULTS.
     *
     * @param link the list to link to.
     */
    public void linkResults(ArrayList link)
    {
        results = link;
    }

}
