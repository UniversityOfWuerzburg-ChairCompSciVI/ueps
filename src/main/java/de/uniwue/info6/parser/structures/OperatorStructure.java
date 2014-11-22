package de.uniwue.info6.parser.structures;

/**
 * MySQL OP
 * 
 * @author Christian
 *
 */
public class OperatorStructure extends Structure {

	private Structure leftOperand;
	private Structure rightOperand;

	public OperatorStructure(String value, Structure leftOperand,
			Structure rightOperand) {

		super(value);

		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;

	}

	public Structure getLeftOperand() {
		return leftOperand;
	}

	public void setLeftOperand(Structure leftOperand) {
		this.leftOperand = leftOperand;
	}

	public Structure getRightOperand() {
		return rightOperand;
	}

	public void setRightOperand(Structure rightOperand) {
		this.rightOperand = rightOperand;
	}

	public boolean isOrderRelevant() {

		if (value.equals(">"))
			return true;
		else if (value.equals("<"))
			return true;

		return false;

	}
	
	@Override
	public boolean equals(Structure anotherStructure) {

		if (anotherStructure instanceof OperatorStructure) {

			if (isOrderRelevant()){
				if(comparator != null)
					return super.equals(anotherStructure)
							&& leftOperand
									.equals(((OperatorStructure) anotherStructure)
											.getLeftOperand(), comparator)
							&& rightOperand
									.equals(((OperatorStructure) anotherStructure)
											.getRightOperand(), comparator);
				else
					return super.equals(anotherStructure)
							&& leftOperand
									.equals(((OperatorStructure) anotherStructure)
											.getLeftOperand())
							&& rightOperand
									.equals(((OperatorStructure) anotherStructure)
											.getRightOperand());
			} else {
				if(comparator != null)
					return super.equals(anotherStructure)
							&& (leftOperand
									.equals(((OperatorStructure) anotherStructure)
											.getLeftOperand(), comparator) || leftOperand
									.equals(((OperatorStructure) anotherStructure)
											.getRightOperand(), comparator))
							&& (rightOperand
									.equals(((OperatorStructure) anotherStructure)
											.getRightOperand(), comparator) || rightOperand
									.equals(((OperatorStructure) anotherStructure)
											.getLeftOperand(), comparator));
				else
					return super.equals(anotherStructure)
							&& (leftOperand
									.equals(((OperatorStructure) anotherStructure)
											.getLeftOperand()) || leftOperand
									.equals(((OperatorStructure) anotherStructure)
											.getRightOperand()))
							&& (rightOperand
									.equals(((OperatorStructure) anotherStructure)
											.getRightOperand()) || rightOperand
									.equals(((OperatorStructure) anotherStructure)
											.getLeftOperand()));
			}
		}

		return false;

	}

	@Override
	public String toString() {
		return leftOperand + " " + super.toString() + " " + rightOperand;
	}

}
