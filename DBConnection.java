package librarySystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection
{
      public static Connection connection() throws ClassNotFoundException
       {
    		Connection connection=null;
    	   try 
    	   {
    		   String URL="jdbc:mysql://localhost:3306/librarydb";
    		   String USER="root";
    		   String PASSWORD="root";
    		   Class.forName("com.mysql.cj.jdbc.Driver");
			   connection= DriverManager.getConnection(URL,USER,PASSWORD);
	       } 
    	   catch (SQLException e) 
    	   {
			  e.printStackTrace();
		   }
    	   return connection;
       }
}
