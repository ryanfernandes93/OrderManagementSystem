import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.*;

public class PaymentManagement 
{
	Connection database;
	Statement statement;
	void reconcilePayments( Connection database ) throws SQLException
	{
		try {
			this.database=database;			
			statement = database.createStatement();
			System.out.println("conn established");
			//Upgrade the db schema
			String updateQuery="update orders inner join orderdetails"+
					" on orders.ordernumber=orderdetails.ordernumber"+
					" set orders.totalAmount=(select sum(orderdetails.quantityOrdered*orderdetails.priceEach) from orderdetails"+
					" where orders.ordernumber=orderdetails.ordernumber group by ordernumber);";
			statement.executeUpdate(updateQuery);

			updateQuery="update orders inner join payments"+
					" on orders.customerNumber=payments.customerNumber"+
					" set orders.checkNumber=(select checkNumber from payments where orders.totalAmount=payments.amount);";
			statement.executeUpdate(updateQuery);
		} catch (SQLException e) 
		{
			// SQL Exception
			System.out.println("SQL Exception");
			e.printStackTrace();
		}
		statement.close();
		//database.close();
	}

	boolean payOrder( Connection database, float amount, String cheque_number,ArrayList<Integer> orders )
	{
		//establish the connection
		try {
			this.database=database;			
			statement = database.createStatement();
			System.out.println("conn established");
			
			//check if check number exists in db
			String sqlQuery="select customerNumber from payments where checkNumber='"+cheque_number+"';";
			String customerNumber;
			ResultSet resultSet=statement.executeQuery(sqlQuery);
			if(!resultSet.next())
			{
				//cheque number does not exists in db
				sqlQuery="select customerNumber from orders where orderNumber='"+orders.get(0)+"';";
				System.out.println(sqlQuery);
				
			}
			else
			{
				//cheque number exists in db
				
			}

			//update the tables
			for (Integer i:orders)
			{
				sqlQuery="update orders set checkNumber='"+cheque_number+"' where orderNumber="+i+";";
				statement.executeUpdate(sqlQuery);
			}

		} catch (SQLException e) 
		{
			// SQL Exception
			System.out.println("SQL Exception");
			e.printStackTrace();
		}


		//close the connection
		try {
			statement.close();
			//database.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	ArrayList<Integer> unpaidOrders( Connection database ) throws SQLException
	{

		try {
			this.database=database;			
			statement = database.createStatement();
			ArrayList <Integer> orders=new ArrayList<Integer>();
			String getOrders="select orderNumber from orders where checkNumber is null and (status != 'cancelled' OR status != 'disputed');";
			ResultSet resultSet = statement.executeQuery(getOrders);

			while (resultSet.next()) {
				//store in hashmap key value pair
				orders.add(resultSet.getInt("orderNumber"));
			}

			return orders;
		} catch (SQLException e) 
		{
			// SQL Exception
			System.out.println("SQL Exception");
			e.printStackTrace();
		}
		statement.close();
		return null;

	}

	ArrayList<String> unknownPayments( Connection database )
	{
		try {
			this.database=database;			
			statement = database.createStatement();
			ArrayList <String> payments=new ArrayList<String>();
			String getOrders="select payments.checkNumber from orders right join payments on orders.checkNumber=payments.checkNumber where orderNumber is null;";
			ResultSet resultSet = statement.executeQuery(getOrders);

			while (resultSet.next()) {
				payments.add(resultSet.getString("checkNumber"));
			}

			return payments;
		} catch (SQLException e) 
		{
			// SQL Exception
			System.out.println("SQL Exception");
			e.printStackTrace();
		}
		try {
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
