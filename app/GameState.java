package app;

/**
 * Enumeration used to track the current state of the round.
 */
public enum GameState
{
    /**
     * Before the game starts. Roster is visible and host can choose topic
     */
    MAIN_MENU,
    /**
     * In-game, questions are being answered
     */
    IN_PROGRESS,
    /**
     * The game has ended, leaderboards are visible
     */
    END_GAME,
    /**
     * The server has closed
     */
    CONNECTION_CLOSED
}
