package de.uniwue.info6.comparator;

/**
 * Klasse wandelt MySQL-Keywords in Urls um.
 * 
 * @author Christian
 *
 */
public class SqlDocLinker {
	
	public static RefLink getUrlByKeyword(String keyword){
		
		String url = "http://dev.mysql.com/doc/refman/5.1/de/";
		
		if(keyword.equals("SELECT")){
			url += "select.html";
		} else if(keyword.equals("UPDATE")){
			url += "select.html";
		} else if(keyword.equals("INSERT")){
			url += "insert.html";
		} else if(keyword.equals("DELETE")){
			url += "delete.html";
		} else if(keyword.equals("JOIN")){
			url += "join.html";
		} else if(keyword.equals("WHERE")){
			url += "functions.html";
		} else if(keyword.equals("GROUPBY")){
			url += "group-by-functions-and-modifiers.html";
		} else {
			return null;
		}
		
		return new RefLink(keyword, url);
	
	}
	
}
