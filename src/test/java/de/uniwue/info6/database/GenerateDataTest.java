package de.uniwue.info6.database;

import de.uniwue.info6.database.gen.GenerateData;

public class GenerateDataTest {
	public static void main (String [] args)
	{
		GenerateData gen = new GenerateData();
		gen.resetDB(false, true);
		gen.insertExampleScenario(false);
	}

}
