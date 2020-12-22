package poc.data;

import java.sql.*;

public class Database {
    private Connection connection = null;

    private Database() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://tilodry.fr:5432/POCMultiUserChat", "postgres", "postgres");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Database instance = null;

    public static Database getInstance() {
        if(null == instance) instance = new Database();
        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
