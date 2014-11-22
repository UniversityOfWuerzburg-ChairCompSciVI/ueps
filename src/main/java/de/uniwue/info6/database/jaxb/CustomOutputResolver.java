package de.uniwue.info6.database.jaxb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 *
 *
 * @author Michael
 */
public class CustomOutputResolver extends SchemaOutputResolver {

	private File baseDir;
	private String baseName;

	/**
	 * @throws FileNotFoundException
	 *
	 */
	public CustomOutputResolver(File baseDir, String baseName) throws FileNotFoundException {
		super();
		if (baseDir == null || baseName == null) {
			throw new NullPointerException();
		}

		if (baseDir.exists() && baseDir.isDirectory()) {
			this.baseDir = baseDir;
			this.baseName = baseName;
		} else {
			throw new FileNotFoundException("GIVEN DIRECTORY DOES NOT EXIST!");
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see SchemaOutputResolver#createOutput(String,String)
	 */
	public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
		return new StreamResult(new File(baseDir, baseName));
	}
}
