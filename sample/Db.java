package sample;

import java.sql.*;

public class Db
{
    private String url;
    private String user;
    private String password;
    public Statement statement;

    public Db()
    {
        url = "jdbc:mysql://localhost/school?useSSL=false";
        user = "root";
        password = "root";
        try {
            Connection conn = DriverManager.getConnection(url,user,password);
            statement = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
