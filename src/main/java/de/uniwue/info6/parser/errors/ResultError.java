package de.uniwue.info6.parser.errors;

/**
 * 
 * @author Christian
 *
 */
public class ResultError extends Error{
	
	private boolean correct;

	public ResultError(String title, String text, boolean correct) {
		super(title, text);
		this.setCorrect(correct);
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
	
}