package app.game;

import app.GameManager;
import app.GameState;
import app.Player;
import app.Team;
import gui.AnswerHandler;
import net.InvalidSenderException;
import net.NetworkHandler;
import net.NetworkManager;
import net.packet.CheckAnswer;
import net.packet.SetUsername;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static gui.Launcher.GUI;

/**
 * Quiz Client Manager
 */
public class QuizClient
{
    /**
     * Player ID
     */
    public static UUID localPlayerUUID;
    /**
     * Team ID
     */
    public static UUID localTeamUUID;

    /**
     * Setup and initialize the QuizClient
     */
    public static void setup()
    {
        GUI.addAnswerHandler(new AnswerHandler()
        {//handler for when questions are answered
            public void handle(ArrayList<String> answers)
            {
                if (GameManager.isHost)//if server
                {
                    Player localPlayer = GameManager.GetPlayerByUUID(QuizClient.localPlayerUUID);
                    boolean correct = QuizServer.AreAnswersCorrect(answers, localPlayer.GetTeam().currentQuestion);//check if answers are correct
                    QuizServer.UpdateTeamProgFromCorrect(correct, localPlayer.GetTeam(), localPlayer.GetTeam().currentQuestion);//update based on correctness
                    GUI.displayAnswerResult(answers, correct);
                } else//if not server
                {
                    try
                    {
                        CheckAnswer checkAnswer = new CheckAnswer(answers);
                        NetworkManager.SendToServer(checkAnswer);//send checkanswer packet to server
                    } catch (IOException | InvalidSenderException exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Called when a team is updated
     *
     * @param team team updated
     */
    public static void OnTeamUpdate(Team team)
    {
        boolean teamExists = false;
        for (Team iTeam : GameManager.teams) // Update the team in the update
        {
            if (iTeam.uuid.equals(team.uuid))
            {
                teamExists = true;
                team.currentQuestion = iTeam.currentQuestion;  // Correct Answers are lost over network
                GameManager.teams.remove(iTeam);
                GameManager.teams.add(team);
                break;
            }
        }
        if (!teamExists)
        {
            GameManager.teams.add(team);
        }

        for (Player player : team.GetMembers())  // Test if the player is on the team from the update
        {
            if (player.uuid.equals(localPlayerUUID))
            {
                localTeamUUID = team.uuid;
                GUI.setTeamName(team.name);
                GUI.setTeamScore(team.points);
                break;
            }
        }
        GameManager.updateTeamGUI();//update team info on gui
    }

    /**
     * Called when client connects to server
     *
     * @param connection socket connection
     */
    public static void OnConnectToServer(NetworkHandler connection)
    {
        GameManager.SetGameState(GameState.MAIN_MENU);//go to main menu
    }

    /**
     * called when client disconnects from server
     *
     * @param connection socket connection
     */
    public static void OnDisconnectFromServer(NetworkHandler connection)
    {
        GameManager.SetGameState(GameState.CONNECTION_CLOSED);
    }

    /**
     * Called when client runs out of questions to answer
     */
    public static void OnOutOfQuestions()
    {
        GUI.lockAll(true);//lock answer buttons
        GUI.outOfQuestionsMessage();
    }

    /**
     * Called when timers synchronized
     *
     * @param timeRemaining amount of time remaining in seconds
     */
    public static void OnTimerSync(int timeRemaining)
    {
        GameManager.timeRemaining = timeRemaining;
    }

    /**
     * Tell server desired username
     *
     * @param username username to be set
     */
    public static void TrySetLocalUsername(String username)
    {
        try
        {
            NetworkManager.SendToServer(new SetUsername(username));//send setusername packet to server
        } catch (IOException | InvalidSenderException e)
        {
            e.printStackTrace();
        }
    }
}
