package de.uniwue.info6.jaxb;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class Child {

	int childId;
	Parent parent;


	/**
	 * @return the childId
	 */
	public int getChildId() {
		return childId;
	}

	/**
	 * @param childId the childId to set
	 */
	@XmlElement
	public void setChildId(int childId) {
		this.childId = childId;
	}

	/**
	 * @return the parent
	 */
	public Parent getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	@XmlTransient
	public void setParent(Parent parent) {
		this.parent = parent;
	}

//	/**
//	 *
//	 *
//	 * @param u
//	 * @param parent
//	 */
//	public void afterUnmarshal(Unmarshaller u, Object parent) {
//		this.parent = (Parent) parent;
//	}

}
