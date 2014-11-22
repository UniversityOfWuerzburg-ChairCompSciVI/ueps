package de.uniwue.info6.jaxb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class CustomOutputResolver extends SchemaOutputResolver {

	private File baseDir;

	/**
	 * @throws FileNotFoundException
	 *
	 */
	public CustomOutputResolver(File baseDir) throws FileNotFoundException {
		super();
		if (baseDir.exists() && baseDir.isDirectory()) {
			this.baseDir = baseDir;
		}
		else {
			throw new FileNotFoundException("GIVEN DIRECTORY DOES NOT EXIST!");
		}
	}

	/**
	 * {@inheritDoc}
	 * @see SchemaOutputResolver#createOutput(String,String)
	 */
	public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
		return new StreamResult(new File(baseDir, suggestedFileName));
	}
}
