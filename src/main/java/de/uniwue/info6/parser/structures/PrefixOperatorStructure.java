package de.uniwue.info6.parser.structures;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  PrefixOperatorStructure.java
 * ************************************************************************
 * %%
 * Copyright (C) 2014 - 2015 Institute of Computer Science, University of Wuerzburg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
