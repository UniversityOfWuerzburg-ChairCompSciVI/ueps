package de.uniwue.info6.database.jaxb;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  CustomOutputResolver.java
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
