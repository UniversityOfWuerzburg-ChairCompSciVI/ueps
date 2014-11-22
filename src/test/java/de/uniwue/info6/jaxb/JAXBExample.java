package de.uniwue.info6.jaxb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;

public class JAXBExample {


	public static void main(String[] args) {
		List<Child> children = new ArrayList<Child>();

		Parent parent = new Parent();
		parent.setId(100);
		parent.setName("mkyong");
		parent.setAge(29);

		Child child1 = new Child();
		child1.setChildId(0);
		children.add(child1);

		Child child2 = new Child();
		child2.setChildId(2);
		children.add(child2);

		parent.setChildren(children);
		child1.setParent(parent);

		try {
			File file = new File("/home/mischka/test.xml");

			JAXBContext jaxbContext = JAXBContext.newInstance(Parent.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(parent, file);
			jaxbMarshaller.marshal(parent, System.out);

			SchemaOutputResolver sor = new CustomOutputResolver(new File("/home/mischka/"));
			jaxbContext.generateSchema(sor);

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
