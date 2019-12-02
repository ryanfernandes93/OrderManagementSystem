import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainPayment {

	public static void main(String[] args) throws ClassNotFoundException, SQLException 
	{
		
		//Establish a connection with the database
		PaymentManagement pm=new PaymentManagement();
		Connection database =null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		database = DriverManager.getConnection("jdbc:mysql://127.0.0.1/","root","root");		
		pm.reconcilePayments(database);
		
		//sample code to check for order payment
		ArrayList <Integer> payOrders=new ArrayList<Integer>();
		payOrders.add(10400);
		payOrders.add(10407);
		pm.payOrder(database,9999.99f , "EF9999", payOrders);

			//get unpaid orders
		ArrayList<Integer> orders=pm.unpaidOrders(database);

			System.out.println(orders);

		//get cheques with no orders
		ArrayList<String> payments=pm.unknownPayments(database);

			System.out.println(payments);
	}

}
