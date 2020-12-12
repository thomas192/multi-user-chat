package poc;

import poc.modele.TopicMessage;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class ClientDAO {
    private final Connection connection;

    public ClientDAO() {
        this.connection = Database.getInstance().getConnection();
    }

    public static void main(String[] args) {
        ClientDAO dao = new ClientDAO();
        List<TopicMessage> res = dao.fetchTopicMessagesHistory("#fete");
        for (TopicMessage t : res) {
            System.out.println(t.getBody());
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

    public List<TopicMessage> fetchTopicMessagesHistory(String topic) {
        List<TopicMessage> messages = new ArrayList<TopicMessage>();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM topicmessage WHERE topic = ?");
            query.setString(1, topic);

            ResultSet rs = query.executeQuery();

            while (rs.next()) {
                TopicMessage msg = new TopicMessage();
                msg.setBody(rs.getString("body"));
                msg.setSender(rs.getString("sender"));
                messages.add(msg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
