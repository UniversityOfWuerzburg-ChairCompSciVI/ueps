package de.uniwue.info6.webapp.misc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ImageServlet.java
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

import static de.uniwue.info6.misc.properties.PropString.SCENARIO_RESOURCES_PATH;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.misc.properties.Cfg;

/**
 *
 *
 * @author Michael
 */
@WebServlet("/images/*")
public class ImageServlet extends HttpServlet {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final String RESOURCE_PATH = "scn";
  private static final Log LOGGER = LogFactory.getLog(ImageServlet.class);

  /**
   * {@inheritDoc}
   *
   * @see HttpServlet#doGet(HttpServletRequest,HttpServletResponse)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String filename = request.getPathInfo();
    File file = new File(Cfg.inst().getProp(MAIN_CONFIG, SCENARIO_RESOURCES_PATH), RESOURCE_PATH + "/"
        + filename);
    response.setHeader("Content-Type", getServletContext().getMimeType(filename));
    response.setHeader("Content-Length", String.valueOf(file.length()));
    response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
    if (file != null && file.exists()) {
      Files.copy(file.toPath(), response.getOutputStream());
    } else {
      LOGGER.error("IMAGE TRANSFER FAILED: " + file.getAbsolutePath());
    }
  }

}
