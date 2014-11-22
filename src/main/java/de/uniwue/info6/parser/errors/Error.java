package de.uniwue.info6.parser.errors;

/**
 * 
 * @author Christian
 *
 */
public class Error {
	
	private String title;
	private String text;
	
	public Error(String title, String text){
		this.title = title;
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String toString(){
		return title + ": " + text;
	}
	
}