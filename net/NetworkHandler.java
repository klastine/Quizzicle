package net;

import app.game.QuizClient;
import app.game.QuizServer;
import net.packet.IMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Network handler represents a connection
 */
public class NetworkHandler implements Runnable
{
    /**
     * Side this handler was made on
     */
    private final Side side;
    /**
     * Socket with connection to remote handler
     */
    private final Socket socket;
    /**
     * Stream of received messages
     */
    private final ObjectInputStream inputStream;
    /**
     * Stream of sent messages
     */
    private final ObjectOutputStream outputStream;

    /**
     * Create a new network handler/connection
     *
     * @param socket socket to bind
     * @param side   this side
     * @throws IOException data streams could not be created
     */
    public NetworkHandler(Socket socket, Side side) throws IOException
    {
        this.side = side;
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Close this connection
     *
     * @throws IOException Connection could not be closed
     */
    public void Close() throws IOException
    {
        this.socket.close();
        this.outputStream.close();
        this.inputStream.close();
    }

    /**
     * Get this side
     *
     * @return this side
     */
    public Side getSide()
    {
        return this.side;
    }

    /**
     * Sends a message to the remote handler
     *
     * @param message Message object to send
     * @throws IOException            Remote socket could not be reached
     * @throws InvalidSenderException Message attempted to be sent from the wrong side
     */
    public void Send(IMessage message) throws IOException, InvalidSenderException
    {
        System.out.println("SENDING MESSAGE: " + message.getClass().getSimpleName());
        Side validSenderSide = message.getValidSide();
        if (validSenderSide != Side.BOTH && validSenderSide != this.side)
            throw new InvalidSenderException("PACKET '" + message.getClass().getSimpleName() + "' CAN ONLY BE SENT ON '" + validSenderSide.name() + "'");
        this.outputStream.writeObject(message);
        this.outputStream.flush();
        this.outputStream.reset();
    }

    /**
     * Handler event loop
     */
    @Override
    public void run()
    {
        try
        {
            if (this.side == Side.SERVER)
            {
                QuizServer.OnPlayerConnect(this);
            } else if (this.side == Side.CLIENT)
            {
                QuizClient.OnConnectToServer(this);
            }
            while (true)
            {
                ((IMessage) this.inputStream.readObject()).Handle(this);
            }
        } catch (ClassNotFoundException | InvalidSenderException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            if (this.side == Side.SERVER)
            {
                QuizServer.OnPlayerDisconnect(this);
            } else if (this.side == Side.CLIENT)
            {
                QuizClient.OnDisconnectFromServer(this);
            }
            e.printStackTrace();
        }
    }
}
