package de.uniwue.info6.database.jaxb;

import static de.uniwue.info6.misc.properties.PropString.SCENARIO_RESOURCES_PATH;
import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;
import static de.uniwue.info6.misc.properties.PropertiesFile.MAIN_CONFIG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.misc.properties.Cfg;

public class ScenarioExporter {
  private static String scriptSystemPath = Cfg.inst().getProp(MAIN_CONFIG, SCENARIO_RESOURCES_PATH);
  private static final String RESOURCE_PATH = "scn";
  private static final Log LOGGER = LogFactory.getLog(ScenarioExporter.class);

  private ExerciseGroupDao exerciseGroupDao;
  private ExerciseDao exerciseDao;
  private SolutionQueryDao solutionDao;

  /**
   *
   */
  public ScenarioExporter() {
    exerciseGroupDao = new ExerciseGroupDao();
    exerciseDao = new ExerciseDao();
    solutionDao = new SolutionQueryDao();
  }

  /**
   *
   *
   * @param scenario
   */
  public File generateScenarioXml(Scenario scenario) {
    scenario = populateScenario(scenario);
    File base = new File(scriptSystemPath + File.separator + RESOURCE_PATH);
    File saveDir = new File(base, String.valueOf(scenario.getId()));

    try {
      if (!saveDir.exists() && saveDir.getParentFile().exists()) {
        saveDir.mkdir();
      }
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
      String baseName = "scenario_" + scenario.getId() + "_export_" + fmt.format(new Date());

      String conflict = "VERSION_CONFLICT";
      File scenarioXml = new File(saveDir, baseName + ".xml");
      File scenarioXmlConflict = new File(saveDir, baseName + "_" + conflict + ".xml");
      File scenarioXsd = new File(saveDir, baseName + ".xsd");
      File scenarioXsdConflict = new File(saveDir, baseName + "_" + conflict + ".xsd");
      File scenarioMain = new File(base, "0" + File.separator + "schema_latest.xsd");
      File scenarioMainConflict = new File(saveDir, baseName + "_" + "REFERENCE_SCHEMA.xsd");

      JAXBContext jaxbContext = JAXBContext.newInstance(Scenario.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

      // output pretty printed
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      jaxbMarshaller.marshal(scenario, scenarioXml);

      SchemaOutputResolver sor = new CustomOutputResolver(saveDir, baseName + ".xsd");
      jaxbContext.generateSchema(sor);

      if (scenarioMain.exists() && scenarioXsd.exists() && scenarioXml.exists()) {
        String referenceMD5 = calculateMD5FromFile(scenarioMain);
        String newMD5 = calculateMD5FromFile(scenarioXsd);

        File export = null;
        boolean falseMD5 = !referenceMD5.equals(newMD5);
        File conflictReadme = new File(saveDir, conflict + "_README.txt");

        if (falseMD5) {
          FileUtils.moveFile(scenarioXml, scenarioXmlConflict);
          FileUtils.moveFile(scenarioXsd, scenarioXsdConflict);
          FileUtils.copyFile(scenarioMain, scenarioMainConflict);
          scenarioXml = scenarioXmlConflict;
          scenarioXsd = scenarioXsdConflict;
          export = new File(saveDir, "scenario_" + scenario.getId() + "_export_" + conflict
                            + ".zip");
          String readme = Cfg.inst().getProp(DEF_LANGUAGE, "MISC.XSD_XML_CONFLICT") + "\n"
                          + scenarioMainConflict.getName();

          if (conflictReadme.exists()) {
            conflictReadme.delete();
          }
          if (!conflictReadme.exists()) {
            conflictReadme.createNewFile();
          }

          PrintWriter out = new PrintWriter(conflictReadme);
          out.println(readme);
          out.flush();
          out.close();
        } else {
          export = new File(saveDir, "scenario_" + scenario.getId() + "_export.zip");
        }

        ArrayList<File> exportFiles = new ArrayList<File>();
        if (scenarioXml.exists()) {
          exportFiles.add(scenarioXml);
        }
        if (scenarioXsd.exists()) {
          exportFiles.add(scenarioXsd);
        }
        if (falseMD5 && conflictReadme.exists() && scenarioMainConflict.exists()) {
          exportFiles.add(conflictReadme);
          exportFiles.add(scenarioMainConflict);
        }
        if (export.exists()) {
          export.delete();
        }
        return zip(exportFiles, export);
      }

    } catch (Exception e) {
      LOGGER.error("EXPORT XSD/XML FAILED!", e);
    }
    return null;
  }

  /**
   *
   *
   * @param file
   * @return
   * @throws IOException
   */
  private static String calculateMD5FromFile(File file) throws IOException {
    FileInputStream fis = new FileInputStream(file);
    String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
    return md5;
  }

  /**
   *
   *
   * @param files
   * @param zipfile
   * @return
   */
  private File zip(List<File> files, File zipfile) {
    byte[] buf = new byte[1024];
    try {
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
      for (int i = 0; i < files.size(); i++) {
        FileInputStream in = new FileInputStream(files.get(i));
        out.putNextEntry(new ZipEntry(files.get(i).getName()));
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
        out.closeEntry();
        in.close();
      }
      out.close();
      for (File file : files) {
        if (file.isFile() && file.exists() && file.canWrite()) {
          file.delete();
        }
      }
      return zipfile;
    } catch (Exception e) {
      LOGGER.error("FAILED TO ZIP FILES", e);
    }
    return null;
  }

  /**
   *
   *
   * @param scenario
   * @return
   */
  private Scenario populateScenario(Scenario scenario) {
    scenario.setScenarios(null);
    scenario.setUserRights(null);

    Set<ExerciseGroup> groupSet = exerciseGroupDao.findByScenarioAsSet(scenario);
    for (ExerciseGroup group : groupSet) {
      group.setExerciseGroup(null);
      group.setExerciseGroups(null);
      group.setScenario(scenario);

      Set<Exercise> exSet = exerciseDao.findByExGroupAsSet(group);
      for (Exercise e : exSet) {
        e.setExerciseGroup(group);
        e.setExercise(null);
        e.setExercises(null);

        // solutions
        Set<SolutionQuery> solutions = solutionDao.findByExerciceAsSet(e);
        for (SolutionQuery solution : solutions) {
          solution.setExercise(e);
          solution.setUserEntry(null);
          solution.setUserResults(null);
        }
        e.setSolutionQueries(solutions);

        e.setUserEntries(null);
      }
      group.setExercises(exSet);
    }
    scenario.setExerciseGroups(groupSet);
    return scenario;
  }

}
