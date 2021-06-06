package app;

import net.InvalidSenderException;
import net.NetworkManager;
import net.packet.NotifyGameState;
import net.packet.NotifyQuizTopic;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static gui.Launcher.GUI;

/**
 * Manage game logic
 */
public class GameManager
{
    /**
     * Number of teams in the game
     */
    public static final int NUM_TEAMS = 3;
    /**
     * if the current client is the host or not
     */
    public static boolean isHost = false;
    /**
     * game time remaining
     */
    public static int timeRemaining = 180;
    /**
     * how often to decrement timeRemaining, in ms
     */
    public final static int delay = 1000;

    /**
     * category being played
     */
    private static int category = 0;
    /**
     * all the teams in the game
     */
    public static final ArrayList<Team> teams = new ArrayList<>();
    /**
     * all the players in the game
     */
    public static final ArrayList<Player> players = new ArrayList<>();
    /**
     * current gamestate of the game
     */
    private static GameState gameState = GameState.MAIN_MENU;

    /**
     * all possible topics as a hashmap
     */
    public static HashMap<Integer, String> topics = new HashMap<>();

    /**
     * Setup GameManager
     */
    public static void setup()
    {
        ActionListener timeInc = e ->
        { //timer listener to end game when time runs out
            if (GetGameState() == GameState.IN_PROGRESS)
            {
                if (timeRemaining > 0)
                {
                    timeRemaining--;
                } else
                {
                    SetGameState(GameState.END_GAME);//end game
                }
                if (!GUI.sound.isPlaying())
                {
                    GUI.sound.play();
                }
            } else
            {
                GUI.sound.stop();
            }
            GUI.updateTimerBar((float) timeRemaining / 180);//update timer
        };
        new Timer(delay, timeInc).start();
    }

    /**
     * get the current gamestate
     *
     * @return current gamestate
     */
    public static GameState GetGameState()
    {
        return gameState;
    }

    /**
     * Set the gamestate
     *
     * @param gameState gamestate to be set
     */
    public static void SetGameState(GameState gameState)
    {
        GameManager.gameState = gameState;
        GUI.setActiveTab(gameState);
        if (isHost)//if is host, send new gamestate to clients
        {
            try
            {
                NetworkManager.SendToAll(new NotifyGameState(gameState));
            } catch (IOException | InvalidSenderException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * get current category
     *
     * @return category ID
     */
    public static int GetCategory()
    {
        return category;
    }

    /**
     * get string corresponding with category id
     *
     * @param id category id
     * @return corresponding string
     */
    public static String GetCategoryString(int id)
    {
        return GameManager.topics.get(id);
    }

    /**
     * set new category
     *
     * @param category category to be set to
     */
    public static void SetCategory(String category)
    {
        GameManager.SetCategory(GameManager.getTopicID(category));
    }

    /**
     * set new category
     *
     * @param category category string
     */
    public static void SetCategory(int category)
    {
        System.out.println("Setting category to " + category);
        GameManager.category = category;//current category
        GUI.setTopicDisplay(topics.get(category));
        System.out.println(topics.get(category));
        if (isHost)
        {
            try
            {
                NetworkManager.SendToAll(new NotifyQuizTopic(topics.get(category)));//send new category to all
            } catch (IOException | InvalidSenderException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * get the corresponding ID of a topic string
     *
     * @param topic string of topic
     * @return id of topic
     */
    public static int getTopicID(String topic)
    {
        for (int i = 0; i <= topics.size(); i++)
        {
            if (topic.equals(topics.get(i)))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the player of a UUID
     *
     * @param uuid Player UUID
     * @return Player of uuid
     */
    public static Player GetPlayerByUUID(UUID uuid)
    {
        for (Player player : GameManager.players)
        {
            if (player.uuid.equals(uuid))//iterate over players to find uuid
            {
                return player;
            }
        }
        return null;
    }

    /**
     * Get the team of a UUID
     *
     * @param uuid Team UUID
     * @return Team of uuid
     */
    public static Team GetTeamByUUID(UUID uuid)
    {
        for (Team team : GameManager.teams)//iterate over teams to find uuid of team
        {
            if (team.uuid.equals(uuid))
            {
                return team;
            }
        }
        return null;
    }

    /**
     * update team GUI
     */
    public static void updateTeamGUI()
    {
        GUI.updateLeaderboard((ArrayList<Team>) teams.clone());  // Clone before sending to other thread
        GUI.setRoster((ArrayList<Team>) teams.clone());
    }
}
