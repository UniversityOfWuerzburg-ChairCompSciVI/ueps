package de.uniwue.info6.comparator;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.parser.errors.Error;
import de.uniwue.info6.parser.errors.ResultError;

/**
 * 
 * @author Christian
 *
 */
public class SqlResult {

	private ResultSetMetaData resultMetaData;
	private String[][] data;
	private String[][] dataWithColumnNames;
	private static final Log LOGGER = LogFactory.getLog(SqlResult.class);

	
	/**
	 *
	 *
	 * @param rawResult
	 * @param resultMetaData
	 */
	public SqlResult(ResultSet rawResult, ResultSetMetaData resultMetaData){
		
		int numberOfColumns, numberOfRows;
		this.setResultMetaData(resultMetaData);
		
		try {
			
			numberOfColumns = resultMetaData.getColumnCount();
			rawResult.last();
			numberOfRows = rawResult.getRow();
	
			// Reset row before iterating to get data
			rawResult.beforeFirst();
			
			data = new String[numberOfRows][numberOfColumns];

			while (rawResult.next()) {
				
				int row = rawResult.getRow();
				for (int i = 1; i < numberOfColumns+1; i++) {
					try {
					data[row - 1][i - 1] = rawResult.getString(i);
					} catch (SQLException e){
						data[row - 1][i - 1] = null;
					}
				}

			}
			
		} catch (Exception e) {
			LOGGER.error("ERROR OCCURRED PREPARING SQL RESULT TABLE", e);
		}
		
	}

	public String[][] getData() {
		return data;
	}
	
	/**
	 * @return the dataWithColumnNames
	 */
	public String[][] getDataWithColumnNames() {
		return dataWithColumnNames;
	}

	public LinkedList<Error> equals(SqlResult anotherResult) throws SQLException{
		
		
		LinkedList<Error> tmp = new LinkedList<Error>();
		
		for(int i = 1; i < resultMetaData.getColumnCount()+1; i++){
			
			String tmpLabel = resultMetaData.getColumnCount() == 1 ? "egal" : resultMetaData.getColumnLabel(i);
			boolean found = false;
			
			if(data.length != anotherResult.getData().length){
				
				if(data.length > anotherResult.getData().length)
					tmp.add(new ResultError(System.getProperty("COMPARATOR.DYN_RESULT"), System.getProperty("COMPARATOR.DYN_ROW_SURPLUS"), false)); //zu viele zeilen
				else
					tmp.add(new ResultError(System.getProperty("COMPARATOR.DYN_RESULT"), System.getProperty("COMPARATOR.DYN_ROW_MISSING"), false)); //zu wenmig  zeile
				
				return tmp; // num of rows not equal
				
			}
	
			if(resultMetaData.getColumnCount() != anotherResult.getResultMetaData().getColumnCount()){
				
				if(resultMetaData.getColumnCount() > anotherResult.getResultMetaData().getColumnCount())
					tmp.add(new ResultError(System.getProperty("COMPARATOR.DYN_RESULT"), System.getProperty("COMPARATOR.DYN_COL_SURPLUS"), false));  //zu viele spalten
				else
					tmp.add(new ResultError(System.getProperty("COMPARATOR.DYN_RESULT"), System.getProperty("COMPARATOR.DYN_COL_MISSING"), false)); //zu wenig spatöen
				
				return tmp; // num of columns not equal
				
			}
			
			boolean wrongEntries = false;

			for(int j = 1; j < anotherResult.getResultMetaData().getColumnCount() + 1; j++){
				
				String tmpLabel2 = anotherResult.getResultMetaData().getColumnCount() == 1 ? "egal" : anotherResult.getResultMetaData().getColumnLabel(j);
	
				if(tmpLabel.toLowerCase().equals(tmpLabel2.toLowerCase())){ // find corresponding column
					
					//tatsächliche Spaltennamen unabhängig vom alias vergleichen
					found = true;
					
					for(int k=0; k < data.length; k++){ // compare all entries

						if((data[k][i-1] != null) && (anotherResult.getData()[k][j-1] != null) && data[k][i-1].equals(anotherResult.getData()[k][j-1])){
							
						} else if(data[k][i-1] == null && anotherResult.getData()[k][j-1] == null){

						} else { // entries not equal				
							wrongEntries = true;

						}
						
					}
					
				}
				
			
				
			}
			
			if(wrongEntries || !found  /* column not found*/){ //ein oder mehrere einträge weichen ab
				tmp.add(new ResultError(System.getProperty("COMPARATOR.DYN_RESULT"), System.getProperty("COMPARATOR.DYN_ENTRY_WRONG"), false)); 
				return tmp;
			}

		}

		return tmp;
		
	}

	public ResultSetMetaData getResultMetaData() {
		return resultMetaData;
	}

	public void setResultMetaData(ResultSetMetaData resultMetaData) {
		this.resultMetaData = resultMetaData;
	}

}
