package net;

import app.GameManager;
import app.Player;
import app.Team;
import net.packet.IMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager
{
    /**
     * Constant host port
     */
    private static final int HOST_PORT = 5372;
    /**
     * List of remote connections
     */
    public static final ArrayList<NetworkHandler> connections = new ArrayList<>();
    /**
     * Server's Thread Manager
     */
    private static ExecutorService connectionExecutor;

    /**
     * Start the server network driver
     */
    public static void StartServer()
    {
        System.out.println("Starting Server...");
        connectionExecutor = Executors.newCachedThreadPool();
        InternalServer serverConnector = new InternalServer();
        connectionExecutor.execute(serverConnector);
    }

    /**
     * Managed Internal server thread
     */
    private static class InternalServer implements Runnable
    {
        public void run()
        {
            try
            {
                ServerSocket serverSocket = new ServerSocket(HOST_PORT);
                while (GameManager.isHost)
                {
                    NetworkHandler connection = new NetworkHandler(serverSocket.accept(), Side.SERVER);
                    NetworkManager.connections.add(connection);
                    connectionExecutor.execute(connection);
                }
                // Host backs out to host/join selection screen or joins a game
                for (NetworkHandler connection : NetworkManager.connections)
                {
                    connection.Close();
                    NetworkManager.connections.remove(connection);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * Start the client network driver
     */
    public static void StartClient(String ip) throws IOException
    {
        NetworkHandler connection = new NetworkHandler(new Socket(ip, HOST_PORT), Side.CLIENT);
        NetworkManager.connections.add(connection);
        //new Thread(connection).start();

        connectionExecutor = Executors.newCachedThreadPool();
        connectionExecutor.execute(connection);

    }

    /**
     * Client-Side only
     *
     * @param message Message to send
     * @throws IOException            Socket is inaccessible
     * @throws InvalidSenderException Message sent from wrong side
     */
    public static void SendToServer(IMessage message) throws IOException, InvalidSenderException
    {
        if (GameManager.isHost)
        {
            throw new InvalidSenderException("Cannot call SendToServer on server! (You are doing it wrong)");
        }
        connections.get(0).Send(message);
    }

    /**
     * Sends a message to an entire team
     *
     * @param message Message to send
     * @param team    Team to send message to
     * @throws IOException            Error connecting to team member
     * @throws InvalidSenderException Sent from wrong side
     */
    public static void SendToTeam(IMessage message, Team team) throws IOException, InvalidSenderException
    {
        for (Player player : team.GetMembers())
        {
            if (player.connection != null)  // null when host (or self)
                player.connection.Send(message);
        }
    }

    /**
     * Sends a message to all connected clients
     *
     * @param message Message to send
     * @throws IOException            Error connecting to player
     * @throws InvalidSenderException Sent from wrong side
     */
    public static void SendToAll(IMessage message) throws IOException, InvalidSenderException
    {
        for (NetworkHandler connection : connections)
        {
            connection.Send(message);
        }
    }
}
