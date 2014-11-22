package de.uniwue.info6.parser.structures;

import java.util.LinkedList;

/**
 * MySQL OP
 * 
 * @author Christian
 *
 */
public class PrefixOperatorStructure extends Structure {

	private LinkedList<Structure> operands = new LinkedList<Structure>();

	public PrefixOperatorStructure(String value, LinkedList<Structure> operands) {

		super(value);

		this.setOperands(operands);

	}
	
	public boolean isOrderRelevant() {

		if (value.equalsIgnoreCase("concat"))
			return true;

		return false;

	}
	
	@Override
	public boolean equals(Structure anotherStructure) {
		
		boolean isEqual = false;

		if (anotherStructure instanceof PrefixOperatorStructure) {
			
			isEqual = super.equals(anotherStructure);
			
			if (isOrderRelevant()){
				
				for(Structure operand : operands){
					
					boolean tmp = false;
					
					int i = 0;
					
					Structure accOperand = (((PrefixOperatorStructure) anotherStructure).getOperands()).get(i);
					
					if(comparator != null){
						if(operand.equals(accOperand, comparator)){
								tmp = true;
						}
					} else {
						if(operand.equals(accOperand)){
							tmp = true;
						}
					}
							
					isEqual = isEqual && tmp;
					i++;
					
				}

			} else {
				
				for(Structure operand : operands){
					
					boolean tmp = false;
					
					for(Structure accOperand : (((PrefixOperatorStructure) anotherStructure).getOperands())){
						if(comparator != null){
							if(operand.equals(accOperand, comparator)){
									tmp = true;
							}
						} else {
							if(operand.equals(accOperand)){
								tmp = true;
							}
						}
					}
					
					isEqual = isEqual && tmp;
					
				}
				
			}
			
		}

		return isEqual;

	}

	@Override
	public String toString() {
		return super.toString() + "(" + operands + ")";
	}

	public LinkedList<Structure> getOperands() {
		return operands;
	}

	public void setOperands(LinkedList<Structure> operands) {
		this.operands = operands;
	}

}
