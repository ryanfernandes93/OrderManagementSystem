import java.io.IOException;
import java.util.ArrayList;
import java.sql.*;
import java.text.DecimalFormat;

public class PaymentManagement 
{
	Connection database;
	Statement statement,statement1,statement2;
	ResultSet resultSet,resultSet1;
	//Connect orders to payments as part of database modification
	void reconcilePayments( Connection database )
	{
		try {
			this.database=database;			
			statement = database.createStatement();
			statement1 = database.createStatement();
			statement2 = database.createStatement();
			String updateQuery="USE classicmodels";
			statement.executeQuery(updateQuery);
			//Upgrade the db schema
			statement.executeUpdate("UPDATE orders INNER JOIN orderdetails"+
					" ON orders.ordernumber=orderdetails.ordernumber"+
					" SET orders.totalAmount=(SELECT SUM(orderdetails.quantityOrdered*orderdetails.priceEach) FROM orderdetails"+
					" WHERE orders.ordernumber=orderdetails.ordernumber GROUP BY ordernumber);");


			statement.executeUpdate("UPDATE orders INNER JOIN payments"+
					" ON orders.customerNumber=payments.customerNumber"+
					" SET orders.checkNumber=(SELECT checkNumber FROM payments WHERE orders.totalAmount=payments.amount);");
			ArrayList<String> orders = new ArrayList<String>();
			ArrayList<String> customers = new ArrayList<String>();
			double sum = 0;
			double amount1 = 0;
			resultSet = statement.executeQuery("SELECT orderNumber, SUM(quantityOrdered*priceEach) AS sum, a.customerNumber AS customerNumber, a.shippedDate FROM orderdetails"+
					" JOIN (SELECT * from orders)  AS a USING (orderNumber) WHERE status IN ('Shipped','Resolved') GROUP BY(orderNumber)"+
					" ORDER BY customerNumber, orderDate");
			while(resultSet.next())
			{
				if(!customers.contains(resultSet.getString("customerNumber"))) {
					sum = 0;
					orders.clear();
				}
				String customerNo = resultSet.getString("customerNumber");
				String orderNumber = resultSet.getString("orderNumber");
				String amount = resultSet.getString("sum");
				String amountS = resultSet.getString("sum");
				amount1 = Double.parseDouble(amount);
				if(sum!=0) 
				{
					amount1= amount1+sum;
					String a1=new DecimalFormat("#.##").format(amount1);
					amount1=Double.parseDouble(a1);
				}
				resultSet1 = statement1.executeQuery("SELECT checkNumber FROM payments WHERE payments.customerNumber = '"+customerNo+"' AND payments.amount = '"+amount1+"'");
				if(!customers.contains(customerNo))
				{
					customers.add(customerNo);
				}
				if(resultSet1.next())
				{
					String checkNo = resultSet1.getString("checkNumber");
					if(orders.size()>0)
					{
						for(int i = 0;i<orders.size();i++)
						{
							statement2.executeUpdate("UPDATE orders SET checkNumber = '"+checkNo+"' WHERE orderNumber = '"+orders.get(i)+"'");
						}
						orders.clear();
						sum=0;
					}
					statement2.executeUpdate("UPDATE orders SET checkNumber = '"+checkNo+"' WHERE orderNumber = '"+orderNumber+"'");
				}
				else
				{
					orders.add(resultSet.getString("orderNumber"));
					sum += Double.parseDouble(amountS);	
				}				
			}
			statement.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	//Records the receipt of a payment, with the given cheque number, that is supposed to cover all of the listed orders. Return true if the payments were
	//recorded as proper payments and false if the payments aren’t recorded.
	boolean payOrder( Connection database, float amount, String cheque_number,ArrayList<Integer> orders )
	{
		//get customer number from orders
		try{
			this.database=database;			
			statement = database.createStatement();
			statement.executeQuery("USE classicmodels");
			ResultSet resultSet=statement.executeQuery("SELECT customerNumber FROM orders WHERE orderNumber="+orders.get(0)+";");
			resultSet.next();
			String customerNumber = resultSet.getString("customerNumber");
			//check if cheque number exists
			resultSet=statement.executeQuery("SELECT count(*) AS chequeCount FROM payments WHERE checkNumber='"+cheque_number+"' AND customerNumber="+customerNumber+";");
			resultSet.next();
			Integer chequeCount=0;
			chequeCount = resultSet.getInt("chequeCount");
			if(chequeCount>0)
			{
				for(Integer order:orders)
				{
					statement.executeUpdate("UPDATE orders SET checkNumber='"+cheque_number+"' WHERE orderNumber="+order+";");						
				}
			}
			else
			{
				statement.execute("INSERT INTO payments(customerNumber,checkNumber,paymentDate,amount) VALUES("+customerNumber+",'"+cheque_number+"',CURDATE(),"+amount+")");
				for(Integer order:orders)
				{
					statement.executeUpdate("UPDATE orders SET checkNumber='"+cheque_number+"' WHERE orderNumber="+order+";");						
				}				
			}
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	//Return a list of all the orders for which we have no record of a payment in the
	//database. Exclude cancelled and disputed orders.
	ArrayList<Integer> unpaidOrders( Connection database ) throws SQLException
	{
		try {
			this.database=database;			
			statement = database.createStatement();
			statement.executeQuery("USE classicmodels");
			ArrayList <Integer> orders=new ArrayList<Integer>();
			ResultSet resultSet = statement.executeQuery("SELECT orderNumber FROM orders WHERE checkNumber IS null AND (status != 'cancelled' OR status != 'disputed');");
			while (resultSet.next()) 
			{
				//store in arraylist
				orders.add(resultSet.getInt("orderNumber"));
			}
			statement.close();
			return orders;			
		} catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	//Return a list of all the cheque numbers that we haven’t managed to pair up with
	//orders in the database.
	ArrayList<String> unknownPayments( Connection database )
	{
		try {
			this.database=database;			
			statement = database.createStatement();
			statement.executeQuery("USE classicmodels");
			ArrayList <String> payments=new ArrayList<String>();
			ResultSet resultSet = statement.executeQuery("SELECT payments.checkNumber FROM orders RIGHT JOIN payments ON orders.checkNumber=payments.checkNumber WHERE orderNumber IS null;");
			while (resultSet.next()) 
			{
				//store in arraylist
				payments.add(resultSet.getString("checkNumber"));
			}
			statement.close();
			return payments;
		} catch (Exception e) 
		{
			// SQL Exception
			e.printStackTrace();
			return null;
		}
	}
}
