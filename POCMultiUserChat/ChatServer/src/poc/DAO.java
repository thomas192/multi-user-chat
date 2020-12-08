package poc;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DAO {
    private final Connection connection;

    public DAO() {
        this.connection = Database.getInstance().getConnection();
    }

    public static void main(String[] args) {
        DAO dao = new DAO();
        HashSet<String> res = dao.getTopicsFollowed("thomas");
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

    public HashSet<String> getTopicsFollowed(String login) {
        HashSet<String> topicsFollowed = null;
        try {
            PreparedStatement query = connection.prepareStatement("SELECT topics FROM topicsfollowed WHERE login = ?");
            query.setString(1, login);

            ResultSet rs = query.executeQuery();
            rs.next();
            Array a = rs.getArray("topics");

            topicsFollowed = Arrays.stream((String[]) a.getArray()).collect(Collectors.toCollection(HashSet::new));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topicsFollowed;
    }
}
