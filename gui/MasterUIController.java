package gui;

import app.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Handles all direct interaction with the GUI.
 *
 * @author gscott4
 * @version 1.22.8
 * @see AnswerHandler Used when the user submits answers
 * @see BeginHandler Used when the host begins the game from their GUI
 */
public class MasterUIController
{
    @FXML
    private TabPane MasterPane;
    private ChoiceBox<String> serverTopicChoiceBox;
    @FXML
    private AnchorPane serverChoiceBoxPane;
    @FXML
    private Button serverBeginGameButton;
    //@FXML
    //private TextArea serverRosterDisplay;
    @FXML
    private HBox serverRosterHBox;
    @FXML
    private Text clientTeamDisplay;
    @FXML
    private Text clientTeamScoreDisplay;
    @FXML
    private Text clientTopicDisplay;
    @FXML
    private TextArea clientQuestionTextDisplay;
    @FXML
    private ImageView clientQuestionImageDisplay;
    @FXML
    private Button clientAnswerButton1;
    @FXML
    private Button clientAnswerButton2;
    @FXML
    private Button clientAnswerButton3;
    @FXML
    private Button clientAnswerButton4;
    @FXML
    private Button clientSubmitAnswerButton;
    @FXML
    private ProgressBar clientProgressBar;

    /**
     * Arraylist of all generated AnswerHandlers to call during submitAnswers
     */
    private ArrayList<AnswerHandler> answerHandlers;
    /**
     * Arraylist of all generated BeginHandlers to call during beginGamePressed
     */
    private ArrayList<BeginHandler> beginHandlers;
    /**
     * Convenience string storing the style tags for toggled toggle buttons
     */
    private final String ONSTYLE = "-fx-font: 13 sans-serif; -fx-font-weight: bold; -fx-base: #111111;";
    /**
     * Convenience string storing the style tags for untoggled toggle buttons
     */
    private final String OFFSTYLE = "-fx-font: 13 sans-serif; -fx-font-weight: normal; -fx-base: #dedeea;";
    /**
     * Convenience string storing the style tags for normal buttons
     */
    private final String PRESSEDSTYLE = "-fx-font: 13 sans-serif; -fx-font-weight: bolder; -fx-base: #eeeefe;";
    /**
     * Convenience string storing the style tags for normal buttons that are known incorrect answers
     */
    private final String WRONGSTYLE = "-fx-font: 13 sans-serif; -fx-font-weight: bolder; -fx-base: #ff5555;";
    /**
     * Convenience string storing the style tags for normal buttons that are known correct answers
     */
    private final String RIGHTSTYLE = "-fx-font: 13 sans-serif; -fx-font-weight: bolder; -fx-base: #55ff55;";

    /**
     * The question currently loaded into the GUI. Needs to be saved for the runLater() functions.
     */
    private Question currentQuestion;
    /**
     * Tracks which buttons are toggled
     */
    private boolean[] buttonStates;
    /**
     * Array of references to the answer buttons for convenient iteration
     */
    private Button[] answerButtons;
    /**
     * Whether the tabs at the top should be locked off
     */
    private boolean navigationIsLocked;
    /**
     * Whether the whole interface should be locked off
     */
    private boolean isFullLocked;
    /**
     * The leaderboard chart at the end
     */
    private BarChart<String, Number> leaderBoardChart;
    /**
     * Backround music
     */
    public AudioClip sound;

    /**
     * Runs automatically at the start. Initializes instance variables, sets up the choiceBox and leaderboard, starts soundtrack.
     */
    public void initialize()
    {
        clientSubmitAnswerButton.setVisible(false);

        answerHandlers = new ArrayList<>();
        beginHandlers = new ArrayList<>();

        buttonStates = new boolean[]{false, false, false, false};
        answerButtons = new Button[]{clientAnswerButton1, clientAnswerButton2, clientAnswerButton3, clientAnswerButton4};

        // https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html#introexamples
        setPressedButtonStyles();

        serverTopicChoiceBox = new ChoiceBox<String>();
        serverChoiceBoxPane.getChildren().add(serverTopicChoiceBox);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Teams");
        yAxis.setLabel("Score");
        leaderBoardChart = new BarChart<String, Number>(xAxis, yAxis);
        leaderBoardChart.setTitle("LEADERBOARD");
        MasterPane.getTabs().get(2).setContent(leaderBoardChart);
        leaderBoardChart.setLegendVisible(false);

        sound = new AudioClip(getClass().getResource("../res/track1.mp3").toExternalForm());
        sound.setVolume(.1);

        //TODO: Delete
        clientQuestionImageDisplay.setImage(new Image("https://i.imgur.com/r6BTDh0.png"));
    }

    // -------------------------- GUI EVENT HANDLERS ---------------------------

    /**
     * Calls all the handlers addded through addBeginHandler.
     *
     * @param event unused
     */
    @FXML
    void beginGamePressed(ActionEvent event)
    {
        for (BeginHandler handler : beginHandlers)
        {
            try
            {
                handler.handle();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calls all the handlers addded through addAnswerHandler.
     *
     * @param answers The text of all the answers being submitted. Will only have one item for single-answer questions.
     */
    void submitAnswers(ArrayList<String> answers)
    {
        for (AnswerHandler handler : answerHandlers)
            try
            {
                handler.handle(answers);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        lockAllButtons(true);
    }

    @FXML
    void button1Pressed(ActionEvent event)
    {
        answerButtonPressed(1);
    }

    @FXML
    void button2Pressed(ActionEvent event)
    {
        answerButtonPressed(2);
    }

    @FXML
    void button3Pressed(ActionEvent event)
    {
        answerButtonPressed(3);
    }

    @FXML
    void button4Pressed(ActionEvent event)
    {
        answerButtonPressed(4);
    }

    /**
     * Called when any answerButton is pressed. If currentQuestion is multiple-answer, it toggles the button.
     * If it's single-answer, it calls submitAnswers with the button pressed.
     *
     * @param buttonNum the number of the button pressed
     */
    private void answerButtonPressed(int buttonNum)
    {
        if (currentQuestion.hasMultipleAnswers())
        {
            buttonStates[buttonNum - 1] = !buttonStates[buttonNum - 1];
            setToggledButtonStyles(buttonStates);
        } else
        {
            ArrayList<String> answers = new ArrayList<>();
            answers.add(answerButtons[buttonNum - 1].getText());
            submitAnswers(answers);
        }
    }

    /**
     * Triggered when the 'submit answers' button is pressed during a multi-answer question.
     * Calls submitAnswers() with the buttons selected.
     *
     * @param event unused.
     */
    @FXML
    void submitButtonPressed(ActionEvent event)
    {
        if (!currentQuestion.hasMultipleAnswers())
            return;
        ArrayList<String> answers = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            if (buttonStates[i])
                answers.add(answerButtons[i].getText());

        submitAnswers(answers);
    }

    /**
     * Registers a new BeginHandler to be called when beginGamePressed is called.
     *
     * @param handler the handler to be called
     */
    public void addBeginHandler(BeginHandler handler)
    {
        beginHandlers.add(handler);
    }

    /**
     * Registers a new AnswerHandler to be called when submitAnswers is called.
     *
     * @param handler the handler to be called
     */
    public void addAnswerHandler(AnswerHandler handler)
    {
        answerHandlers.add(handler);
    }

    // ------------------  GUI CHANGERS -----------------

    /**
     * Sets which side of the connection the GUI should represent.
     * On the client side, locks all serverside-only controls.
     *
     * @param host True if host, false if client.
     */
    public void setSide(boolean host)
    {
        if (host)
        {
            setActiveTab(0);
        } else
        {
            setActiveTab(1);
            serverTopicChoiceBox.setDisable(true);
            serverBeginGameButton.setDisable(true);
        }
        lockNavigation(true);
    }

    /**
     * Makes all the necessary updates to load a question into the GUI.
     * Changes button text and style, changes question text, loads images if any are needed.
     *
     * @param q The question to be loaded.
     * @throws NullPointerException If q is null.
     */
    public void loadQuestion(Question q) throws NullPointerException
    {
        if (q == null)
            throw new NullPointerException();
        currentQuestion = q;
        Platform.runLater(() ->
        {
            lockAll(false);
            lockAllButtons(false);
            buttonStates = new boolean[]{false, false, false, false};
            if (currentQuestion.hasMultipleAnswers())
            {
                setToggledButtonStyles(buttonStates);
                clientSubmitAnswerButton.setVisible(true);
            } else
            {
                setPressedButtonStyles();
                clientSubmitAnswerButton.setVisible(false);
            }

            clientQuestionTextDisplay.setText(currentQuestion.getQuestionText());
            ArrayList<String> answers = currentQuestion.getAnswers();
            for (int i = 0; i < Math.min(answers.size(), answerButtons.length); i++)
                answerButtons[i].setText(answers.get(i));

            if (currentQuestion.getRemoteMediaURL().length() > 5)
                clientQuestionImageDisplay.setImage(new Image(currentQuestion.getRemoteMediaURL()));
            else
                clientQuestionImageDisplay.setImage(new Image("https://i.imgur.com/kTtBh3q.png"));
        });
    }

    /**
     * Forces the gui to a certain tab.
     *
     * @param tabNum The index of the tab to change to (0, 1, or 2)
     */
    public void setActiveTab(int tabNum)
    {
        MasterPane.getSelectionModel().select(tabNum);
    }

    /**
     * Forces the gui to the tab associated with a certain gamestate.
     *
     * @param state The state to change gui tab to.
     */
    public void setActiveTab(GameState state)
    {
        if (state == GameState.MAIN_MENU)
        {
            setActiveTab(0);
        } else if (state == GameState.IN_PROGRESS)
        {
            setActiveTab(1);
        } else if (state == GameState.END_GAME)
        {
            setActiveTab(2);
        }
        lockNavigation();
    }

    /**
     * Sets the options available in the Server view's "select topic" choiceBox.
     *
     * @param choices The list of choices to put in the box.
     */
    public void setTopicChoices(ArrayList<String> choices)
    {

        serverTopicChoiceBox.getItems().clear();
        serverTopicChoiceBox.getItems().addAll(choices);
        serverTopicChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldIndex, Number newIndex)
            {
                GameManager.SetCategory(serverTopicChoiceBox.getItems().get((Integer) newIndex));
            }
        });
        serverTopicChoiceBox.setValue(choices.get(0));  // Set default value
    }

    /**
     * Populates the roster view with all the teams and players in them currently in the game.
     *
     * @param roster List of teams to display.
     */
    public void setRoster(ArrayList<Team> roster)
    {
        Platform.runLater(() ->
        {
            serverRosterHBox.getChildren().clear();
            for (Team team : roster)
            {
                if (team.GetMembers().size() > 0)
                {
                    VBox teamVBox = new VBox();
                    teamVBox.setAlignment(Pos.TOP_CENTER);
                    teamVBox.setSpacing(3);
                    Label teamNameLabel = new Label(team.name);
                    teamVBox.getChildren().add(teamNameLabel);
                    for (Player player : team.GetMembers())
                    {
                        Label playerNameLabel = new Label(player.username);
                        teamVBox.getChildren().add(playerNameLabel);
                    }
                    serverRosterHBox.getChildren().add(teamVBox);
                }
            }
        });
    }

    /**
     * Sets the team name in the team display bar.
     *
     * @param name Name of current team.
     */
    public void setTeamName(String name)
    {
        clientTeamDisplay.setText("Current Team: " + name);
    }

    /**
     * Sets the team score in the score display bar.
     *
     * @param score Score of current team.
     */
    public void setTeamScore(int score)
    {
        clientTeamScoreDisplay.setText("Score: " + score);
    }

    /**
     * Sets the topic in the topic display bar.
     *
     * @param topicName Topic of the current quiz.
     */
    public void setTopicDisplay(String topicName)
    {
        clientTopicDisplay.setText("Topic: " + topicName);
    }

    /**
     * Deprecated.
     *
     * @param name Unused.
     */
    public void setPlayerName(String name)
    {
        //Todo: implement
    }

    /**
     * Creates a popup dialog that prompts the user to enter a username.
     *
     * @param error If true, sends the user an additional message that the last-entered username was invalid.
     * @return The name the user entered, or just "username"
     */
    public String userNamePopup(boolean error)
    {
        String init = "username";
        TextInputDialog popup = new TextInputDialog(init);
        popup.setWidth(100);
        popup.setTitle("Enter your username" + (error ? " (name already taken)" : ""));
        popup.setHeaderText("Enter Username");
        Optional<String> response = popup.showAndWait();
        if (response.isPresent()) init = response.get();
        return init;
    }

    /**
     * Changes the UI to reflect which of the user's submitted answers was correct.
     * If the currentQuestion is single-answer, the last-pressed button will turn green or red depending
     * on whether the submitted answer was right or wrong.
     *
     * @param answers The answers the user submitted.
     * @param correct Whether the answers were right or wrong.
     */
    public void displayAnswerResult(ArrayList<String> answers, boolean correct)
    {
        Platform.runLater(() ->
        {
            lockAll(false);
            lockAllButtons(false);
            if (!currentQuestion.hasMultipleAnswers())
                for (Button button : answerButtons)
                    if (button.getText().compareTo(answers.get(0)) == 0)
                        if (correct)
                            button.setStyle(RIGHTSTYLE);
                        else
                            button.setStyle(WRONGSTYLE);
        });
    }

    /**
     * Runs code in the GUI's thread.
     *
     * @param runnable Lambda to be run.
     */
    public void runLater(Runnable runnable)
    {
        Platform.runLater(runnable);
    }

    /**
     * Updates the progressbar at the bottom of the screen.
     *
     * @param progress double, 0.0-1.0
     */
    public void updateTimerBar(double progress)
    {
        clientProgressBar.setProgress(progress);
    }

    /**
     * Updates the leaderboard chart with Teams' scores
     *
     * @param teams List of teams to put on the chart.
     */
    public void updateLeaderboard(ArrayList<Team> teams)
    {
        Platform.runLater(() ->
        {
            int maxScore = 0;
            for (Team team : teams)
                if (team.points > maxScore) maxScore = team.points;

            XYChart.Series teamSeries = new XYChart.Series();
            teamSeries.setName("Teams");

            for (Team team : teams)
                if (team.GetMembers().size() > 0)
                    teamSeries.getData().add(new XYChart.Data(team.name, team.points));

            leaderBoardChart.getData().clear();
            leaderBoardChart.getData().add(teamSeries);
        });
    }

    /**
     * Shows a popup stating there are no questions left.
     */
    public void outOfQuestionsMessage()
    {
        Platform.runLater(() ->
        {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setContentText("No questions remain. Please wait");
            popup.showAndWait();
            lockAllButtons(true);
        });
    }


    //    ------------------- INTERNAL FUNCTIONS --------------------

    @Deprecated
    private String getSelectedTopic()
    {
        return serverTopicChoiceBox.getValue();
    }

    /**
     * Returns a list of the text on the selected buttons
     *
     * @return a list of the text on the selected buttons
     */
    private ArrayList<String> getSelectedAnswers()
    {
        return new ArrayList<String>();
    }

    /**
     * Sets the CSS style of the answerbuttons
     *
     * @param states Which CSS style to set each
     */
    private void setToggledButtonStyles(boolean[] states)
    {
        for (int i = 0; i < 4; i++)
            if (states[i])
                answerButtons[i].setStyle(ONSTYLE);
            else
                answerButtons[i].setStyle(OFFSTYLE);
    }

    /**
     * Sets the CSS style of the answerButtons
     */
    private void setPressedButtonStyles()
    {
        for (Button button : answerButtons)
            button.setStyle(PRESSEDSTYLE);
    }

    /**
     * Sets the lock of all the UI elements
     *
     * @param lock Lock or unlock
     */
    public void lockAll(boolean lock)
    {
        isFullLocked = lock;
        lockAll();
    }

    /**
     * Double-checks the lock status
     */
    public void lockAll()
    {
        MasterPane.setDisable(isFullLocked);
    }

    /**
     * Sets the lock of all the buttons
     *
     * @param lock Lock or unlock
     */
    public void lockAllButtons(boolean lock)
    {
        for (int i = 0; i < 4; i++)
            lockButton(i, lock);
    }

    /**
     * Sets the lock status of a single button
     *
     * @param buttonNum Number of the button to lock
     * @param lock      Lock or unlock
     */
    private void lockButton(int buttonNum, boolean lock)
    {
        answerButtons[buttonNum].setDisable(lock);
    }

    /**
     * Lock tab navigation
     *
     * @param lock Lock or unlock
     */
    public void lockNavigation(boolean lock)
    {
        navigationIsLocked = lock;
        lockNavigation();
    }

    /**
     * Checks lock state
     */
    private void lockNavigation()
    {
        int selectedIndex = MasterPane.getSelectionModel().getSelectedIndex();
        List<Tab> tabsList = MasterPane.getTabs();
        for (int i = 0; i < tabsList.size(); i++)
            tabsList.get(i).setDisable((i != selectedIndex) && navigationIsLocked);
    }
}
