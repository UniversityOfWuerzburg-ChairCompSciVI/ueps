package de.uniwue.info6.comparator;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.SQLParser;
import com.akiban.sql.parser.StatementNode;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.UserDao;
import de.uniwue.info6.parser.errors.SqlError;
import de.uniwue.info6.parser.exceptions.ParserException;
import de.uniwue.info6.parser.structures.JoinTableStructure;
import de.uniwue.info6.parser.structures.TableStructure;
import de.uniwue.info6.parser.visitors.RootVisitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Kapselt SQL-Query und bietet Schnittstelle zum SQL-Parser.
 * 
 * @author Christian
 *
 */
public class SqlQuery {
	
	private String plainContent;
	private RootVisitor parsedContent = null;
	private SqlResult result;
	private LinkedList<TableStructure> tables;
	private SqlError error;
	private static final Log LOGGER = LogFactory.getLog(SqlError.class);
	
	public static void main(String[] args) throws Exception{
		
		System.out.println(System.getProperty("EDIT"));
		
		int exerciseID = 1;
		Connection connection = null;
		try {
			ConnectionManager pool = ConnectionManager.instance();

			Scenario scenario = new ScenarioDao().getByName("swt2014");
			User user = new UserDao().getById("s213549");
			Exercise exercise = new ExerciseDao().getById(exerciseID);
			List<SolutionQuery> solutions = new ExerciseDao().getSolutions(exercise);
			String solution = solutions.get(0).getQuery();
			connection = pool.getConnection(scenario);

			System.out.println("[Musterloesung]: " + solution);
			// liefert eine Nullpointer-Exception
			SqlQuery user_query = new SqlQuery("select title from books");
			
			SqlQuery solution_query = new SqlQuery(solution);
			SqlExecuter executer = new SqlExecuter(connection, user, scenario);
		
			SqlQueryComparator comparator = new SqlQueryComparator(user_query, solution_query, executer);
			System.out.println(comparator.compare());
			
			// tabellen zuruecksetzen
			pool.resetTables(scenario, user);
			
			System.out.println(solution_query.getRefLink());
			System.out.println(comparator.getRefLinks());

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		
		
		
		//SqlQuery query1 = new SqlQuery("SELECT Orders.OrderID as orders, Customers.CustomerName, Orders.OrderDate FROM Orders INNER JOIN Customers1 ON Orders.CustomerID=Customers1.CustomerID INNER JOIN Customers2 ON Orders2.CustomerID=Customers2.CustomerID WHERE orders>400 and title='hh'");
		//SqlQuery query1 = new SqlQuery("Select title, sum(title+1) from books, autor where (title=1) and title1>title2 order by table1.titel1 desc, titel2 asc, title3");
		//SqlQuery query1 = new SqlQuery("Select * from books group by autor having count(title)>5 and title=1");
		//SqlQuery query1 = new SqlQuery("Select max(sum(title+1)) as bla, title as title2 from books");
		//SqlQuery query1 = new SqlQuery("Update books set autor=autor+1, title=asd");
		//SqlQuery query1 = new SqlQuery("Select sum(sum(title+1)) as titlte from books where books='te s'");
		
		//SqlQuery query1 = new SqlQuery("SELECT title, price FROM books WHERE price < 10 ORDER BY price ASC");
		//SqlQuery query1 = new SqlQuery("SELECT signature FROM books WHERE title='Shades of Grey - Geheimes Verlangen'");
		//SqlQuery query1 = new SqlQuery("SELECT title, publishers.name, stock FROM books JOIN publishers ON publisher_id=publishers.id WHERE stock < 40000");
		//SqlQuery query1 = new SqlQuery("SELECT title, author FROM books ORDER BY price DESC LIMIT 3");
		//SqlQuery query1 = new SqlQuery("SELECT title, author FROM books ORDER BY price DESC LIMIT 2,3");
		//SqlQuery query1 = new SqlQuery("SELECT title, price FROM books WHERE author='J. R. R. Tolkien' AND price<10 ORDER BY year DESC");
		//SqlQuery query1 = new SqlQuery("SELECT stock*price AS revenue FROM books WHERE title='Animal Farm'");
		//SqlQuery query1 = new SqlQuery("SELECT first_name, family_name, birthday FROM customers WHERE birthday LIKE '%-02-%'");
		//SqlQuery query1 = new SqlQuery("SELECT * FROM customers WHERE birthday < '2000-01-01'");
		//SqlQuery query1 = new SqlQuery("SELECT birthday, FLOOR(DATEDIFF(NOW(), birthday)/365) AS age FROM customers");
		//SqlQuery query1 = new SqlQuery("SELECT email, COUNT(email) AS occurrences FROM customers GROUP BY email HAVING ( COUNT(email) > 1 )");
		//SqlQuery query1 = new SqlQuery("SELECT first_name, family_name FROM customers WHERE LENGTH(family_name) = 4 ORDER BY family_name ASC");
		//SqlQuery query1 = new SqlQuery("SELECT date, orders.id AS order_id, family_name, customers.id AS customer_id FROM customers JOIN orders ON customers.id = customer_id ORDER BY date DESC");
		//SqlQuery query1 = new SqlQuery("SELECT orders.id AS order_id, family_name, email FROM customers JOIN orders ON customers.id = customer_id WHERE paid=0");
		//SqlQuery query1 = new SqlQuery("SELECT customers.id, family_name, COUNT(customer_id) AS quantity FROM customers JOIN orders ON customers.id = customer_id GROUP BY customer_id HAVING ( COUNT(customer_id) > 1 )");
		//SqlQuery query1 = new SqlQuery("SELECT email FROM customers WHERE email LIKE '%gmx.de' OR  email LIKE '%gmail.com'");
		//SqlQuery query1 = new SqlQuery("SELECT id, SUM(price*amount) AS sum FROM order_positions GROUP BY order_id");
		//SqlQuery query1 = new SqlQuery("SELECT order_positions.id, title, books.price AS current_price, order_positions.price AS order_price FROM order_positions JOIN books ON books.id = book_id WHERE (books.price - order_positions.price) > 0");
		//SqlQuery query1 = new SqlQuery("SELECT first_name, family_name, COUNT(orders.id) AS order_count FROM customers JOIN orders ON customers.id = customer_id GROUP BY customers.id ORDER BY order_count DESC");
		//SqlQuery query1 = new SqlQuery("DELETE FROM books where title='bla'");
		//SqlQuery query1 = new SqlQuery("select author from books");
		//SqlQuery query2 = new SqlQuery("select author from books where title='bla'");
		//der tSqlQuery query1 = new SqlQuery("UPDATE customers SET email='paulina.r@yahoo.com' WHERE first_name='Paulina' AND family_name='Rohr'");
		//SqlQuery query1 = new SqlQuery("INSERT INTO custund ders (first_name, family_name, birthday, email) VALUES ('Ferdinandus', 'Merkle', '1990-11-24', 'ferdinandus_1856@yahoo.com')");
		//SqlQuery query1 = new SqlQuery("SELECT avg(rating) AS avg_rating, count(*) AS rating_count FROM ratings JOIN books ON books.id=book_id WHERE books.title='Harry Potter und der Orden des Ph�nix' GROUP BY book_id");
		//SqlQuery query1 = new SqlQuery("select * from book");
		//SqlQuery query2 = new SqlQuery("select * from book");
		//SqlQueryComparator comparator = new SqlQueryComparator(query1, query2, null);
		//System.out.println(comparator.compare());

	}

	public SqlQuery(String sqlString){
		plainContent = adjustPlainString(sqlString); 	
	}
	
	/*
	 * Hinweis:
	 * Der genutzte SQL Parser weist bei bestimmten Schlüsselwörtern Probleme auf:
	 * - year
	 * - ; am Ende des Query
	 * 
	 */
	public static String adjustPlainString(String plainContent){
		
		plainContent = plainContent.replaceAll("year", "yearx");
		plainContent = plainContent.replaceAll("group", "groupx");
		plainContent = plainContent.replaceAll(";", "");
		
		return plainContent;
		
	}
	
	public static String dejustPlainString(String plainContent){
		
		plainContent = plainContent.replaceAll("yearx", "year");
		plainContent = plainContent.replaceAll("groupx", "group");
		
		return plainContent;
		
	}
	
	public String toString(){
		return plainContent;
	}
	
	public SqlError getError(){
		return error;
	}
	
	public RootVisitor getParsedContent() throws ParserException, StandardException{
		
		if(parsedContent == null){

				SQLParser parser = new SQLParser();
				StatementNode data;
				
				data = parser.parseStatement(plainContent);
				parsedContent = new RootVisitor();
				
				data.accept(parsedContent);
				
				tables = parsedContent.getTables();

		} 
		
		return parsedContent;
		
	}
	
	public RefLink getRefLink(){
		
		try {
			return SqlDocLinker.getUrlByKeyword(getParsedContent().getMainKeyWord());
		} catch (ParserException e) {
			return null;
		} catch (StandardException e) {
			return null;
		}
		
	}

	public String getPlainContent(){
		return plainContent;
	}
	
	public void setPlainContent(String editedQuery){
		plainContent = editedQuery;
	}
	
	public void rebuild() throws ParserException, StandardException{
		
		parsedContent = null;
		//plainContent = plainContent;
		
		getParsedContent();
		
	}
	
	public LinkedList<TableStructure> getRelevantTables(){
		
		LinkedList<TableStructure> tmp = new LinkedList<TableStructure>();
		
		if(tables == null){
			return null;
		}

		for(TableStructure item : tables){
			if(item instanceof JoinTableStructure){
				tmp.addAll(getTablesFromJoin((JoinTableStructure) item));
			} else {
				tmp.add(item);
			}
		}
	
		return tmp;
		
	}
	
	public static LinkedList<TableStructure> getTablesFromJoin(JoinTableStructure join){
		
		LinkedList<TableStructure> tmp = new LinkedList<TableStructure>();
		
		if(join.getLeftTable() instanceof JoinTableStructure) {
			tmp.addAll(getTablesFromJoin((JoinTableStructure) join.getLeftTable()));
		} else {
			tmp.add(join.getLeftTable());
		}
		
		if(join.getRightTable() instanceof JoinTableStructure) {
			tmp.addAll(getTablesFromJoin((JoinTableStructure) join.getRightTable()));
		} else {
			tmp.add(join.getRightTable());
		}
		
		return tmp;
		
	}
	
	public LinkedList<String> getContainingTags(){
		
		//if(parsedContent == null)
			//return null;
		
		LinkedList<String> tmpList = new LinkedList<String>();
		
		//tmpList.add(parsedContent.getMainKeyWord());
		
		String[] relevantTags = {"SELECT", "UPDATE", "INSERT", "DELETE", "VALUES", "ORDER BY", "LIMIT", "HAVING", "WHERE", "JOIN", "GROUP BY", "SET"};
		
		for(String item : relevantTags){
			if(plainContent.toUpperCase().contains(item))
				tmpList.add(item);
		}
		
		return tmpList;
		
	}
	
	public void setResult(SqlResult result){
		this.result = result;
	}
	
	public SqlResult getResult(){
		return result;	
	}

	public void setError(SqlError error) {
		this.error = error;
	}
	
}
