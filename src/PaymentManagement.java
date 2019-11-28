import java.io.IOException;
import java.util.ArrayList;
import java.sql.*;

public class PaymentManagement 
{
	Connection database;
	Statement statement;
	void reconcilePayments( Connection database )
	{
		try {
			this.database=database;
			Class.forName("com.mysql.cj.jdbc.Driver");
			database = DriverManager.getConnection("jdbc:mysql://127.0.0.1/classicmodels","root","root");
			statement = database.createStatement();
			System.out.println("conn established");
		} catch (ClassNotFoundException e) 
		{
			// Class not found exception
			System.out.println("Class not found exception");
			e.printStackTrace();
		} catch (SQLException e) 
		{
			// SQL Exception
			System.out.println("SQL Exception");
			e.printStackTrace();
		}
	}

	boolean payOrder( Connection database, float amount, String cheque_number,ArrayList orders )
	{
		return true;
	}
	/*
	ArrayList<int> unpaidOrders( Connection database )
	{
		return 
	}

	ArrayList<String> unknownPayments( Connection database )
	{
		return
	}
	 */
}
