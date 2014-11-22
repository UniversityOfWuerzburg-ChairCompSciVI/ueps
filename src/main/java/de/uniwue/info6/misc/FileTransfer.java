package de.uniwue.info6.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import de.uniwue.info6.database.jdbc.ConnectionManager;
import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.ExerciseGroupDao;
import de.uniwue.info6.database.map.daos.ScenarioDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;

/**
 *
 *
 * @author Michael
 */
public class FileTransfer {

	private static final Log LOGGER = LogFactory.getLog(FileTransfer.class);
	private static String scriptPath = System.getProperty("SCENARIO_RESOURCES");
	private static final String RESOURCE_PATH = "scn";
	private static final String COPY_STRING = "[" + System.getProperty("COPY") + "]";

	// daos
	private ExerciseGroupDao exGroupDao;
	private ScenarioDao scenarioDao;
	private ExerciseDao exerciseDao;
	private SolutionQueryDao solutionDao;
	private ConnectionManager connectionPool;

	/**
	 *
	 */
	public FileTransfer() {
		// init hibernate daos
		exGroupDao = new ExerciseGroupDao();
		scenarioDao = new ScenarioDao();
		exerciseDao = new ExerciseDao();
		solutionDao = new SolutionQueryDao();
		connectionPool = ConnectionManager.instance();
	}

	/**
	 *
	 *
	 * @param originExercise
	 * @param group
	 * @param move
	 * @return
	 */
	public Exercise copy(Exercise originExercise, ExerciseGroup group, boolean move) {
		Boolean sameGroup = false;

		if (originExercise != null) {
			try {
				if (group != null) {
					if (originExercise.getExerciseGroup() != null
							&& originExercise.getExerciseGroup().getId() == group.getId()) {
						sameGroup = true;
					}
				}

				if (move) {
					originExercise.setExerciseGroup(group);
					exerciseDao.updateInstance(originExercise);

					return originExercise;
				} else {
					Exercise newExercise = new Exercise();
					// primitive datatypes
					newExercise.setCredits(originExercise.getCredits());
					newExercise.setLastModified(new Date());
					newExercise.setQuestion(originExercise.getQuestion());
					newExercise.setExerciseGroup(group);
					newExercise.setGeneratedTags(originExercise.getGeneratedTags());

					SolutionQuery solExample = new SolutionQuery();
					solExample.setExercise(originExercise);
					List<SolutionQuery> orSol = solutionDao.findByExample(solExample);

					if (sameGroup) {
						newExercise.setQuestion(COPY_STRING + " " + originExercise.getQuestion());
					} else {
						newExercise.setName(originExercise.getName());
					}

					// set original exercise
					newExercise.setExercise(originExercise);
					exerciseDao.insertNewInstance(newExercise);

					for (SolutionQuery s : orSol) {
						SolutionQuery qu = new SolutionQuery(newExercise, s.getQuery());
						qu.setUserEntry(s.getUserEntry());
						qu.setExplanation(s.getExplanation());
						solutionDao.insertNewInstance(qu);
					}

					return newExercise;
				}
			} catch (Exception e) {
				LOGGER.error("Problem copying Exercise", e);
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param originGroup
	 * @param scenario
	 * @param move
	 * @return
	 */
	public ExerciseGroup copy(ExerciseGroup originGroup, Scenario scenario, boolean move) {
		Boolean sameScenario = false;

		if (originGroup != null) {
			try {
				if (scenario != null) {
					if (originGroup.getScenario() != null && (originGroup.getScenario().getId() == scenario.getId())) {
						sameScenario = true;
					}
				}

				if (move) {
					originGroup.setScenario(scenario);
					exGroupDao.updateInstance(originGroup);
					return originGroup;
				} else {
					ExerciseGroup newGroup = new ExerciseGroup();
					newGroup.setDescription(originGroup.getDescription());
					newGroup.setEndTime(originGroup.getEndTime());
					newGroup.setStartTime(originGroup.getStartTime());
					newGroup.setIsRated(originGroup.getIsRated());
					newGroup.setLastModified(new Date());
					newGroup.setScenario(scenario);

					if (sameScenario) {
						newGroup.setName(COPY_STRING + " " + originGroup.getName());
					} else {
						newGroup.setName(originGroup.getName());
					}
					// original exercise-group
					newGroup.setExerciseGroup(originGroup);
					exGroupDao.insertNewInstance(newGroup);

					List<Exercise> exercises = exerciseDao.findByExGroup(originGroup);
					for (Exercise ex : exercises) {
						if (move) {
							copy(ex, newGroup, true);
						} else {
							copy(ex, newGroup, false);
						}
					}
					return newGroup;
				}
			} catch (Exception e) {
				LOGGER.error("Problem copying ExerciseGroup", e);
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param originScenario
	 * @return
	 */
	public Scenario copy(Scenario originScenario) {

		if (originScenario != null) {
			try {
				Scenario newScenario = new Scenario();
				newScenario.setCreateScriptPath(originScenario.getCreateScriptPath());
				newScenario.setDbHost(originScenario.getDbHost());
				newScenario.setDbName(originScenario.getDbName());
				newScenario.setDbPass(originScenario.getDbPass());
				newScenario.setDbPort(originScenario.getDbPort());
				newScenario.setDbUser(originScenario.getDbUser());
				newScenario.setDescription(originScenario.getDescription());
				newScenario.setEndTime(originScenario.getEndTime());
				newScenario.setImagePath(originScenario.getImagePath());
				newScenario.setName(COPY_STRING + " " + originScenario.getName());
				newScenario.setStartTime(originScenario.getStartTime());

				newScenario.setLastModified(new Date());
				newScenario.setScenario(originScenario);

				scenarioDao.insertNewInstance(newScenario);

				List<ExerciseGroup> groups = exGroupDao.findByScenario(originScenario);
				for (ExerciseGroup gr : groups) {
					copy(gr, newScenario, false);
				}

				String scenarioFolder = scriptPath + File.separator + RESOURCE_PATH + File.separator
						+ newScenario.getId();

				File scriptFile = new File(scenarioFolder + File.separator + newScenario.getCreateScriptPath());

				if (!scriptFile.exists()) {
					String oldScriptPath = originScenario.getCreateScriptPath();
					if (oldScriptPath != null && !oldScriptPath.trim().isEmpty()) {
						File oldScriptFile = new File(scriptPath + File.separator + RESOURCE_PATH + File.separator
								+ originScenario.getId() + File.separator + oldScriptPath);

						if (oldScriptFile.exists()) {
							try {
								FileUtils.copyFile(oldScriptFile, scriptFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							LOGGER.error(scriptFile + " DOES NOT EXIST!");
						}
					}
				}

				File imageFile = new File(scenarioFolder + File.separator + newScenario.getImagePath());
				if (!imageFile.exists()) {
					String oldImagePath = originScenario.getImagePath();
					if (oldImagePath != null && !oldImagePath.trim().isEmpty()) {
						File oldImageFile = new File(scriptPath + File.separator + RESOURCE_PATH + File.separator
								+ originScenario.getId() + File.separator + oldImagePath);

						if (oldImageFile.exists()) {
							try {
								FileUtils.copyFile(oldImageFile, imageFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				connectionPool.updateScenarios();
				return newScenario;
			} catch (Exception e) {
				LOGGER.error("Problem copying Scenario", e);
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param scriptFile
	 * @param imageFile
	 * @param id
	 * @return
	 */
	public boolean copy(File scriptFile, File imageFile, String id) {
		File folder = new File(scriptPath + File.separator + RESOURCE_PATH + File.separator + id);
		try {
			if (!folder.exists()) {
				folder.mkdir();
			}

			if (imageFile != null && imageFile.exists()) {
				FileUtils.copyFile(imageFile, new File(folder, imageFile.getName()));
			}

			if (scriptFile != null && scriptFile.exists()) {
				FileUtils.copyFile(scriptFile, new File(folder, scriptFile.getName()));
				return true;
			}

			if (!scriptFile.exists()) {
				throw new FileNotFoundException(scriptFile.getAbsolutePath());
			}
		} catch (Exception e) {
			LOGGER.error("Problem copying image/script-file", e);
			return false;
		}
		return false;
	}

	/**
	 *
	 *
	 * @param event
	 */
	public File handleUpload(FileUploadEvent event, String folder) {
		// String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		String fileName = normalize(event.getFile().getFileName());
		String name = StringTools.normalize(StringTools.deleteDate(fileName.substring(0, fileName.lastIndexOf('.'))))
				+ "_" + fmt.format(new Date()) + fileName.substring(fileName.lastIndexOf('.'));
		File file = new File(scriptPath + File.separator + RESOURCE_PATH + File.separator + folder + File.separator
				+ name);

		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}

		InputStream is;
		try {
			is = event.getFile().getInputstream();
			OutputStream out = new FileOutputStream(file);
			byte buf[] = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0)
				out.write(buf, 0, len);
			is.close();
			out.close();
		} catch (IOException e) {
			LOGGER.error("FILE UPLOADING FAILED", e);
			return null;
		}
		return file;
	}

	/**
	 *
	 *
	 * @param file
	 * @return
	 */
	public StreamedContent getFileToDownload(File file) {
		InputStream stream;
		StreamedContent streamedFile;
		try {
			stream = new FileInputStream(file);
			streamedFile = new DefaultStreamedContent(stream, null, file.getName());
			return streamedFile;
		} catch (FileNotFoundException e) {
			LOGGER.error("SCENARIO FILE NOT FOUND!", e);
		}
		return null;
	}

	/**
	 *
	 *
	 * @param name
	 * @return
	 */
	public static String normalize(String name) {
		return name.toLowerCase();
	}

}
