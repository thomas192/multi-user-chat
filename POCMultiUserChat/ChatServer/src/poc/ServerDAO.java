package poc;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class ServerDAO {
    private final Connection connection;

    public ServerDAO() {
        this.connection = Database.getInstance().getConnection();
    }

    public static void main(String[] args) {
        ServerDAO serverDao = new ServerDAO();
        HashSet<String> res = serverDao.fetchTopicsFollowed("thomas");
        System.out.println(res);

        res.add("ete");
        serverDao.updateTopicsFollowed("thomas", res);

        res = serverDao.fetchTopicsFollowed("thomas");
        System.out.println(res);
    }

    public boolean connectUser(String login, String password) {
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM chatuser WHERE login = ? AND password = ?" );
            query.setString(1, login);
            query.setString(2, password);

            ResultSet rs = query.executeQuery();

            if (!rs.next())
                return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void addTopicMessage(String topic, String body, String sender) {
        try {
            PreparedStatement query = connection.prepareStatement("INSERT INTO topicmessage(topic, body, sender) VALUES(?, ?, ?)");
            query.setString(1, topic);
            query.setString(2, body);
            query.setString(3, sender);

            query.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashSet<String> fetchTopicsFollowed(String login) {
        HashSet<String> topicsFollowed = new HashSet<>();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT topics FROM topicsfollowed WHERE login = ?");
            query.setString(1, login);

            ResultSet rs = query.executeQuery();

            if (rs.next()) {
                Array a = rs.getArray("topics");
                if (a != null) {
                    topicsFollowed = Arrays.stream((String[]) a.getArray()).collect(Collectors.toCollection(HashSet::new));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topicsFollowed;
    }

    public void updateTopicsFollowed(String login, HashSet<String> topicsFollowed) {
        try {
            Object[] o = topicsFollowed.toArray();

            PreparedStatement query = connection.prepareStatement("UPDATE topicsfollowed SET topics = ? WHERE login = ?");
            query.setArray(1, connection.createArrayOf("text", o));
            query.setString(2, login);

            query.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
