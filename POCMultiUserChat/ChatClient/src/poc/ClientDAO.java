package poc;

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
        HashSet res = dao.getTopicsFollowed("bob");
        System.out.println(res);
    }

    public HashSet<String> getTopicsFollowed(String login) {
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
}
