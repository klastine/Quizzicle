package net.packet;

import app.Team;
import app.game.QuizClient;
import net.NetworkHandler;
import net.Side;

/**
 * Notify client of a team's progress
 */
public class NotifyTeamInfo extends IMessage
{
    /**
     * Team
     */
    private final Team team;

    /**
     * Server-Side only
     * Create NotifyTeamInfo message
     *
     * @param team the team
     */
    public NotifyTeamInfo(Team team)
    {
        this.team = team;
    }

    /**
     * Client-Side only
     * Updates info about this team on the client
     *
     * @param context NetworkHandler connection which received this message
     */
    @Override
    public void Handle(NetworkHandler context)
    {
        QuizClient.OnTeamUpdate(this.team);
    }

    @Override
    public Side getValidSide()
    {
        return Side.SERVER;
    }
}
