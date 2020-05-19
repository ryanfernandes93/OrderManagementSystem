Overview
--------

This program is used to solve the shortcomings in the database design.It is used to link the orders table with payments table.
A summary of the formatting requirements are in the csci 3901 course assignment #6 information in the course's brightspace space.

Files and external data:
-----------------------

There are three main files:
	-Problem3.sql -- this file contains alter table commands to add checkNumber and totalAmount columns to orders table.
					It is necessary to runthis sql file before executing the program.
  - MainPayment.java  -- main program that contains the function calls to PaymentManagement
  - PaymentManagement.java -- class that is responsible for upgrading the database design,linking orders with payments,display the list of unpaid orders and unlinked checks and 
								paying orders
								This program is called from the main class
  
The directory contains following versions of the code:
v_1:Establish a connection with the local database
v_2:Link the orders and payments on basis of their amount
	Display the list of unpaid orders
	Display the list of checks that we have not been able to link with an order
	
directory version -- Updated code to work on bluenose.cs.dal.ca
					Link multiple orders that have been paid using a single check based on order date

Data structures & methods:
---------------------------
-Arraylist orders-data structure used to store the list of orders 
-Arraylist customers-data structure used to store the list of customer numbers

Methods:
------
Class PaymentManagement
	-reconcilePayments(Connection database)
		Upgrade the database design and link the records in orders table with those of payments table
	-payOrder(Connection database,float amount ,String cheque_number,ArrayList<Integer> orders)
		Accept as an input from the customer database connection,cheque amount,cheque number and the list of orders that are to be paid using the given cheque
		Link the orders with the provided cheque numbers
	-unpaidOrders(Connection database)
		List the orders that have not been able to be linked with a cheque after database upgrade
	-unknownPayments(Connection database)
		List the cheques that have not been able to be linked with an order

Assumptions:
-----------

	-No past order was partially paid and no past order was paid in installments.
	-Orders are paid in full or not at all
	-Overpayments are not allowed
	-Customers can only pay for their own orders
	-Orders cannot be pre paid 
  
Input:
-------

Class PaymentManagement
	-reconcilePayments(Connection database)
		Accept database connection object as an input
	-payOrder(Connection database,float amount ,String cheque_number,ArrayList<Integer> orders)
		Accept database connection object,cheque amount,cheque number and the list of orders that need to be payed with the cheque as inputs
	-unpaidOrders(Connection database)
		Accept database connection object as an input
	-unknownPayments(Connection database)
		Accept database connection object as an input  

Choices
-------

	-User can pay for orders
	-user can view the list of orders that have not been paid
	-User can view the cheque numbers that have not been linked to a payment

Key algorithms and design elements
----------------------------------

Establish a connection with the database using the default JDBC ODBC MySQL driver
1)the method reconcilePayments is called 
	this method upgrades the database design and links the orders with the payments
	Firstly we calculate the total value of each order and update the totalAmount column of orders
	Next we compare the totalAmount of each cheque with the cheque value in the payments table
	If an amount matches we update that cheque number against that order in the checkNumber column
	To check for multiple orders that have been paid with a single cheque number we extract a list of all the orders of a customer that have not been linked to a cheque yet
	then based on the orderDate we sum the orders and check the count against the amounts in the payment table
	for amounts that match we simply update the cheque number against the corresponding order
	Return type is void

2)payOrder is called
	this method is used to pay orders with a cheque number
	Firstly extract the customer number from the order list provided
	Check if the cheque number provided is present in the payments table against the customer
	If the cheque is available update the checkNumber against each order provided in the orders table
	If the cheque number is not available simply add the cheque number,amount and customer number in the payments table
	next update the cheque number against each order provided in the orders table
	return true if executed as expected else return false
	
3)unpaidOrders
	Execute an sql query that returns an arraylist of all the orders that have not been able to be linked by a payment
	the result set(orderNumber) is stored in an array list and returned 
	
4)unknownPayments
	Execute an sql query that returns an arraylist of all the cheques that have not been able to be linked with an order
	the result set(checkNumber) is stored in an array list and returned

Limitations
-----------
-Customers can only pay for their orders
-Customers cannot pre pay for their orders
-Cstomers can either pay in full or not at all
