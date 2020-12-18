package poc.data;

import poc.model.Message;

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
        boolean res = dao.hadAConversation("ted", "bob");
        System.out.println(res);
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

    public List<Message> fetchTopicMessagesHistory(String topic) {
        List<Message> messages = new ArrayList<Message>();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM topicmessage WHERE topic = ?");
            query.setString(1, topic);

            ResultSet rs = query.executeQuery();

            while (rs.next()) {
                Message msg = new Message();
                msg.setBody(rs.getString("body"));
                msg.setSender(rs.getString("sender"));
                messages.add(msg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> fetchPrivateMessagesHistory(String user1, String user2) {
        List<Message> messages = new ArrayList<Message>();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM privatemessage WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?)");
            query.setString(1, user1);
            query.setString(2, user2);
            query.setString(3, user2);
            query.setString(4, user1);

            ResultSet rs = query.executeQuery();

            while (rs.next()) {
                Message msg = new Message();
                msg.setBody(rs.getString("body"));
                msg.setSender(rs.getString("sender"));
                messages.add(msg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<String> fetchConversationsHistory(String login) {
        List<String> loginsCombined = new ArrayList<>();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT DISTINCT recipient FROM privatemessage WHERE sender = ?");
            query.setString(1, login);
            ResultSet rs = query.executeQuery();
            List<String> logins = new ArrayList<>();
            while (rs.next()) {
                logins.add(rs.getString("recipient"));
            }

            PreparedStatement queryBis = connection.prepareStatement("SELECT DISTINCT sender FROM privatemessage WHERE recipient = ?");
            queryBis.setString(1, login);
            ResultSet rsBis = queryBis.executeQuery();
            List<String> loginsBis = new ArrayList<>();
            while (rsBis.next()) {
                loginsBis.add(rsBis.getString("sender"));
            }

            // Merge lists without duplicates
            Set<String> set = new LinkedHashSet<>(logins);
            set.addAll(loginsBis);
            loginsCombined = new ArrayList<>(set);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loginsCombined;
    }

    public boolean hadAConversation(String login1, String login2) {
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM privatemessage WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?) LIMIT 1");
            query.setString(1, login1);
            query.setString(2, login2);
            query.setString(3, login2);
            query.setString(4, login1);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
