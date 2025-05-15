package project2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtils {
	public static Connection getConnection() {
		try {
			Connection connection = DriverManager.getConnection(ConnectionConst.URL, ConnectionConst.USERNAME,
					ConnectionConst.PASSWORD);
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}
}