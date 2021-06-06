package app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Handles all information and interaction regarding teams of players
 *
 * @author obergman
 * @author klastine
 * @version 1.10.0
 * @see Player the object that represents the team's players
 */
public class Team implements Serializable
{
    /**
     * Team identifier
     */
    public final UUID uuid;
    /**
     * Team members
     */
    private final ArrayList<Player> members;
    /**
     * Current question for team
     */
    public Question currentQuestion;
    /**
     * Difficulty level of the team's questions
     */
    public int difficultyLevel;
    /**
     * Team points
     */
    public int points;
    /**
     * Team Name
     */
    public String name;
    /**
     * Questions answered by team
     */
    public ArrayList<Integer> answered;

    /**
     * Adds a player to the team
     *
     * @param player the player to add.
     */
    public void Add(Player player)
    {
        this.members.add(player);
    }

    /**
     * Returns reference to members, DO NOT EDIT RETURNED MEMBERS
     *
     * @return reference to members
     */
    public ArrayList<Player> GetMembers()
    {
        return this.members;
    }

    /**
     * Create new team (from server)
     */
    public Team()
    {
        this.uuid = UUID.randomUUID();
        this.members = new ArrayList<>();
        this.currentQuestion = null;
        this.difficultyLevel = 0;
        this.points = 0;
        this.answered = new ArrayList<>();
    }

    /**
     * Used to print Team for debugging.
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Team " + name + "\n");
        for (Player member : members)
            builder.append("\t" + member.username + "\n");
        return builder.toString();
    }
}
