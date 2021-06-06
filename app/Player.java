package app;

import net.NetworkHandler;

import java.io.Serializable;
import java.util.UUID;

/**
 * Handles all information and interaction involving a single player in the game.
 *
 * @author obergman
 * @author klastine
 * @see Team
 */
public class Player implements Serializable
{
    /**
     * Player identifier
     */
    public final UUID uuid;
    /**
     * Player Username
     */
    public final String username;
    /**
     * Connection to player
     */
    public final transient NetworkHandler connection;
    /**
     * Player team
     */
    private final Team team;

    /**
     * Returns the player's team
     *
     * @return the player's team
     */
    public Team GetTeam()
    {
        return this.team;
    }

    /**
     * Creates a new player from SQL database.
     *
     * @param username   The name associated with the player
     * @param team       The team the player is on.
     * @param connection Networkhandler the player is connected through.
     */
    public Player(String username, Team team, NetworkHandler connection)
    {
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.team = team;
        this.connection = connection;
    }
}
