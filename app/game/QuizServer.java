package app.game;

import app.*;
import gui.BeginHandler;
import net.InvalidSenderException;
import net.NetworkHandler;
import net.NetworkManager;
import net.packet.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static gui.Launcher.GUI;

public class QuizServer
{
    /**
     * Database manager instance
     */
    public static DBManager dbManager;
    /**
     * Constant team name list
     */
    private static final String[] teamNames = {"The Drunk Sharks", "The Fightin' Peanuts", "The Flyin' Onions", "The Obscure References", "The Adjective Plural Noun"};

    /**
     * Sets up some GUI handlers
     */
    public static void setup()
    {
        GUI.addBeginHandler(new BeginHandler()
        {
            public void handle()
            {
                QuizServer.StartGame();
            }
        });
    }

    /**
     * Starts the game by setting the game state and sending a question to every team
     */
    public static void StartGame()
    {
        GameManager.SetGameState(GameState.IN_PROGRESS);
        for (Team team : GameManager.teams)
        {
            QuizServer.TryGiveTeamQuestion(team);
        }
    }

    /**
     * Inits the server side of things
     * Defines the host, creates DB connection and retrieves topics.
     * Sets up teams and NetworkManager
     * Then sets up local client
     *
     * @param hostUsername username for local client
     */
    public static void InitServer(String hostUsername)
    {
        try
        {
            GameManager.isHost = true;
            GameManager.teams.clear();
            dbManager = new DBManager();

            ArrayList<String> topicArray = QuizServer.dbManager.GetTopics();
            for (int i = 0; i < topicArray.size(); i++)
            {
                GameManager.topics.put(i, topicArray.get(i));
            }
            GUI.setTopicChoices(topicArray);
            for (int i = 0; i < GameManager.NUM_TEAMS; i++)
            {
                Team team = new Team();
                team.name = teamNames[i];
                GameManager.teams.add(team);
            }
            NetworkManager.StartServer();

            Player localPlayer = RegisterPlayer(hostUsername, null);  // init client side player stuff
            QuizClient.localPlayerUUID = localPlayer.uuid;
            QuizClient.OnTeamUpdate(localPlayer.GetTeam());
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets the smallest team
     *
     * @return The smallest team
     */
    public static Team SmallestTeam()
    {
        Team smallest = null;
        for (Team team : GameManager.teams)
        {
            if (smallest == null || team.GetMembers().size() < smallest.GetMembers().size())
            {
                smallest = team;
            }
        }
        return smallest;
    }

    /**
     * Registers a client with a username and a connection
     * Creates a player and sets the player's team
     *
     * @param username   Username to register with
     * @param connection Connection to register with
     * @return The new player object
     */
    public static Player RegisterPlayer(String username, NetworkHandler connection)
    {
        Team playerTeam = SmallestTeam();
        Player newPlayer = new Player(username, playerTeam, connection);
        playerTeam.Add(newPlayer);
        GameManager.players.add(newPlayer);
        GUI.setRoster(GameManager.teams);
        return newPlayer;
    }

    /**
     * Get the player of a connection
     *
     * @param connection Net Handler connection
     * @return Player of connection
     */
    public static Player GetPlayer(NetworkHandler connection)
    {
        for (Player player : GameManager.players)
        {
            if (player.connection != null && player.connection.equals(connection))
            {
                return player;
            }
        }
        return null;
    }

    /**
     * Checks whether a list of answers corresponds to the correct answers of a question
     *
     * @param answers  The answers to check
     * @param question The question to check against
     * @return Whether the answers exclusively match the question correct answers
     */
    public static boolean AreAnswersCorrect(ArrayList<String> answers, Question question)
    {
        int numCorrect = 0;
        ArrayList<String> correctAnswers = question.getCorrectAnswers();
        for (String correctAnswer : correctAnswers)
        {
            if (answers.contains(correctAnswer))
            {
                numCorrect++;
            }
        }
        return correctAnswers.size() == numCorrect && correctAnswers.size() == answers.size();
    }

    /**
     * Determines if a given username is valid
     *
     * @param username Username to check
     * @return Whether or not the username is taken
     */
    public static boolean IsUsernameValid(String username)
    {
        for (Team team : GameManager.teams)
        {
            for (Player player : team.GetMembers())
            {
                if (player.username.equals(username))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * On player connect handler
     * Called when a player connection is first made
     * Updates the client of all the current teams, game state and topic
     *
     * @param connection Player's connection
     */
    public static void OnPlayerConnect(NetworkHandler connection)
    {
        System.out.println("Player Connected.");
        try
        {  // Send player current game info, Player object not fully created
            for (Team team : GameManager.teams)
            {
                connection.Send(new NotifyTeamInfo(team));
            }
            connection.Send(new NotifyQuizTopic(GameManager.GetCategoryString(GameManager.GetCategory())));
            connection.Send(new NotifyGameState(GameManager.GetGameState()));
        } catch (IOException | InvalidSenderException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Player has joined and successfully gotten a username
     * Notifies player of their new team and UUID and sends the current question for their team if possible
     *
     * @param player     Player to finalize connection with
     * @param connection Connection to the player
     */
    public static void OnPlayerAuthorized(Player player, NetworkHandler connection)
    {
        System.out.println("Player Authorized.");
        try
        {
            connection.Send(new NotifyPlayer(player.uuid));
            Team team = player.GetTeam();
            NetworkManager.SendToAll(new NotifyTeamInfo(team));
            if (team.currentQuestion != null)
            {
                connection.Send(new NotifyQuestion(team.currentQuestion));
            }
        } catch (IOException | InvalidSenderException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * On player disconnect handler
     *
     * @param connection Connection that was closed
     */
    public static void OnPlayerDisconnect(NetworkHandler connection)
    {
        System.out.println("Player disconnected...");
    }

    /**
     * Tries to give a team a new question
     * If a new question can't be found then the team is notified that they are out of questions
     *
     * @param team The team to send the question to
     */
    public static void TryGiveTeamQuestion(Team team)
    {
        try
        {
            Question nextQuestion = QuizServer.dbManager.GetRandomQuestion(GameManager.GetCategory(), team.difficultyLevel, team.answered);
            team.currentQuestion = nextQuestion;
            if (nextQuestion != null)
            {
                NotifyQuestion notifyQuestion = new NotifyQuestion(nextQuestion);
                NetworkManager.SendToTeam(notifyQuestion, team);
                if (GameManager.isHost && QuizClient.localTeamUUID.equals(team.uuid))
                {
                    GUI.loadQuestion(nextQuestion);
                }
            } else
            {
                NetworkManager.SendToTeam(new NotifyOutOfQuestions(), team);
                if (GameManager.isHost && QuizClient.localTeamUUID.equals(team.uuid))
                {
                    QuizClient.OnOutOfQuestions();
                }
            }

        } catch (IOException | InvalidSenderException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Updates a team's score based on whether they got a question correct
     * This also notifies everyone about the current team and syncs the time remaining
     *
     * @param correct  Whether the question is correct
     * @param team     The team to update
     * @param question The question in question
     */
    public static void UpdateTeamProgFromCorrect(boolean correct, Team team, Question question)
    {
        boolean answered = team.answered.contains(question.getId());
        try
        {
            if (correct)
            {
                if (!answered)
                {
                    team.answered.add(question.getId());
                    if (team.difficultyLevel < 2)
                    {
                        team.difficultyLevel++;
                    }
                    team.points += 100;
                }

                QuizServer.TryGiveTeamQuestion(team);
            } else if (!answered)
            {
                if (team.points >= 50)
                {
                    team.points -= 50;
                }
                if (team.difficultyLevel > 0)
                {
                    team.difficultyLevel--;
                }
                team.answered.add(question.getId());
            }
            if (team.uuid.equals(QuizClient.localTeamUUID))
            {
                QuizClient.OnTeamUpdate(team);
            }
            NetworkManager.SendToAll(new NotifyTeamInfo(team));
            NetworkManager.SendToAll(new NotifyTimer(GameManager.timeRemaining));
        } catch (IOException | InvalidSenderException e)
        {
            e.printStackTrace();
        }
    }
}
