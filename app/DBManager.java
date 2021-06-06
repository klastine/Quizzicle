package app;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DBManager
{

    /**
     * Database location
     */
    private static final String DATABASE_URL = "jdbc:mysql://s-l112.engr.uiowa.edu:3306/swd_db009";
    /**
     * socket connection
     */
    private final Connection connection;

    /**
     * Main constructor
     *
     * @throws SQLException if connection unsuccessful
     */
    public DBManager() throws SQLException
    {
        //create DB connection
        this.connection = DriverManager.getConnection(DATABASE_URL, "swd_group009", "swd_group009-xyz-21");
    }

    /**
     * Return a random question
     *
     * @param topic      the category of the question
     * @param difficulty the difficulty of the question
     * @param answered   the questions already answered by the team
     * @return the question
     */
    public Question GetRandomQuestion(int topic, int difficulty, ArrayList<Integer> answered)
    {
        try
        {
            //request question from DB
            String preparedArray = Arrays.toString(answered.toArray()).replace("[", "(").replace("]", ")");
            System.out.println(preparedArray);
            preparedArray = preparedArray.equals("()") ? "(-1)" : preparedArray;
            PreparedStatement statement = connection.prepareStatement("SELECT QuestionID, QuestionType, QuestionText, Difficulty FROM Questions WHERE TopicID = ?" + (difficulty >= 0 ? " AND Difficulty = ? " : " ") + "AND QuestionID NOT IN " + preparedArray + " ORDER BY RAND() LIMIT 1");
            statement.setInt(1, topic);
            if (difficulty >= 0) statement.setInt(2, difficulty);
            ResultSet result = statement.executeQuery();
            if (!result.first())
            {
                System.out.println("Failed getting question of topic " + topic + " and difficulty " + difficulty);
                if (difficulty > -1)
                {
                    System.out.println("Trying again...");
                    return this.GetRandomQuestion(topic, difficulty - 1, answered);
                }
                return null;
            }
            int id = result.getInt(1);//format data into a question object
            int type = result.getInt(2);
            int diff = result.getInt(4);
            String questionText = result.getString(3);
            result.close();

            //get image from DB, if applicable
            statement = connection.prepareStatement("SELECT ImageLink FROM Images WHERE QuestionID = ? LIMIT 1");
            statement.setInt(1, id);
            result = statement.executeQuery();
            String remoteMediaURL = "";
            if (result.first())
            {
                remoteMediaURL = result.getString("ImageLink");
            }
            result.close();

            //request correct answers
            statement = connection.prepareStatement("SELECT CorrectText FROM Correct WHERE QuestionID = ?");
            statement.setInt(1, id);
            result = statement.executeQuery();
            result.next();
            ArrayList<String> correctAnswers = new ArrayList<>(Arrays.asList(result.getObject("CorrectText").toString().split(",")));
            result.close();

            //request possible answers
            statement = connection.prepareStatement("SELECT AnswersText FROM Answers WHERE QuestionID = ?");
            statement.setInt(1, id);
            result = statement.executeQuery();
            result.next();
            ArrayList<String> answers = new ArrayList<>(Arrays.asList(result.getObject("AnswersText").toString().split(",")));
            result.close();

            //instantiate question depending on question type
            switch (type)
            {
                case 0:
                    return new SAQuestion(id, questionText, answers, correctAnswers, diff, remoteMediaURL);
                case 1:
                    return new MAQuestion(id, questionText, answers, correctAnswers, diff, remoteMediaURL);
                case 2:
                    System.out.println(remoteMediaURL);
                    return new ImageQuestion(id, questionText, answers, correctAnswers, diff, remoteMediaURL);
                default:
                    System.out.println("ERROR LOADING QUESTION");
                    return null;
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * get topics in database
     *
     * @return all topics in DB
     */
    public ArrayList<String> GetTopics()
    {
        ArrayList<String> topics = new ArrayList<>();
        try
        {//connect to DB
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Topic FROM Topics ORDER BY TopicID");
            while (resultSet.next())
            {
                topics.add(resultSet.getObject("Topic").toString()); // add results to result list
            }
            for (String string : topics)
            {
                System.out.println(string + " in arraylist");
            }
            resultSet.close();
        } catch (SQLException sqlException)//SQL exception if connection failed
        {
            sqlException.printStackTrace();
            topics = null;
        }
        System.out.println("Topic is of size " + topics.size());
        return topics;
    }

}
