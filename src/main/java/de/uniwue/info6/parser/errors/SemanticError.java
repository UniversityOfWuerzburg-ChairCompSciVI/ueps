package de.uniwue.info6.parser.errors;

/**
 * 
 * @author Christian
 *
 */
public class SemanticError extends Error{

	public SemanticError(String title, String text) {
		super(title, text);
	}
	
}