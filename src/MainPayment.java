import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainPayment {

	public static void main(String[] args) throws ClassNotFoundException, SQLException 
	{
		// TODO Auto-generated method stub
		//Establish a connection with the database
		PaymentManagement pm=new PaymentManagement();
		Connection database =null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		database = DriverManager.getConnection("jdbc:mysql://127.0.0.1/classicmodels","root","root");		
	//	pm.reconcilePayments(database);
		
		ArrayList <Integer> payOrders=new ArrayList<Integer>();
		payOrders.add(10400);
		payOrders.add(10407);
	//	pm.payOrder(database,59551.38f , "EF485824", payOrders);
		
		//get unpaid orders
		ArrayList<Integer> orders=pm.unpaidOrders(database);
		
			System.out.println(orders);
		
		//get cheques with no orders
		ArrayList<String> payments=pm.unknownPayments(database);
		
			System.out.println(payments);
	}

}
