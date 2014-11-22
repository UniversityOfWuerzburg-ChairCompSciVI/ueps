package de.uniwue.info6.parser.structures;

import java.util.LinkedList;

import de.uniwue.info6.comparator.SqlQueryComparator;
import de.uniwue.info6.parser.visitors.RootVisitor;
import de.uniwue.info6.parser.errors.Error;

/**
 * MySQL Subquery
 * 
 * @author Christian
 *
 */
public class SubqueryStructure extends Structure{
	
	private RootVisitor rvisitor;

	public SubqueryStructure(String value, RootVisitor rvisitor) {
		super(value);
		this.setRvisitor(rvisitor);
	}
	
	@Override
	public String toString(){
		return "(" + rvisitor.toString() + ")";
	}
	
	@Override
	public boolean equals(Structure anotherStructure) {
		return equals(anotherStructure, null);
	}

	@Override
	public boolean equals(Structure anotherStructure, SqlQueryComparator comparator){

		if(anotherStructure instanceof SubqueryStructure){
			
			SqlQueryComparator comp = new SqlQueryComparator(rvisitor, ((SubqueryStructure) anotherStructure).getRvisitor());
			comp.setMsgPrefix(System.getProperty("COMPARATOR.SUBQUERY") + ": ");
			
			try {
				
				LinkedList<Error> messages = comp.compare();
				if(messages.size() == 0){ // work around
					return true;
				} else {
					if(comparator != null){
						comparator.setMessages(messages);
					}
					return false;
					// die compare mesgs sollten an den main comparator übergeben werden, aber wie ?
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return false;
		
	}

	public RootVisitor getRvisitor() {
		return rvisitor;
	}

	public void setRvisitor(RootVisitor rvisitor) {
		this.rvisitor = rvisitor;
	}

}
