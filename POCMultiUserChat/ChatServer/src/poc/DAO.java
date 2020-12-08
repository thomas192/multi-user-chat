package poc;

import java.sql.*;

public class DAO {
    public DAO() {
    }

    public static void main(String[] args) {
        DAO dao = new DAO();
        boolean res = dao.connectUser("thomas", "thomas");
        System.out.println(res);
    }

    public boolean connectUser(String login, String password) {
        Connection connection = Database.getInstance().getConnection();
        PreparedStatement requestUser;
        try {
            requestUser = connection.prepareStatement("SELECT * FROM chatuser WHERE login = ? AND password = ?" );
            requestUser.setString(1, login);
            requestUser.setString(2, password);

            ResultSet resultSet = requestUser.executeQuery();

            if (!resultSet.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }
}
