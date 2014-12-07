package de.uniwue.info6.webapp.admin;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.database.map.UserResult;
import de.uniwue.info6.database.map.UserRight;
import de.uniwue.info6.database.map.daos.UserRightDao;
import de.uniwue.info6.misc.StringTools;

/**
 *
 * @author Christian
 *
 */
public class SubmissionRow {

  private boolean isEntry = false;
  private boolean isExercise = false;
  private boolean isExGroup = false;
  private boolean isScenario;

  private String text;
  private String styleClass = "";

  private UserEntry userEntry = null;
  private UserResult userResult = null;
  private Exercise exercise = null;
  private ExerciseGroup exGroup = null;
  private Scenario scenario;
  private String avgCredits;
  private boolean canBeRemoved = false;

  public SubmissionRow(UserEntry ue, UserResult ur, Exercise ex) {
    this.userResult = ur;
    this.userEntry = ue;
    this.exercise = ex;
    this.isEntry = true;
    this.text = ue.getUser().getId();
    this.styleClass = "user_class";

    UserRightDao userRightDao = new UserRightDao();
    UserRights rights = new UserRights().initialize();

    List<UserRight> tmp = null;
    tmp = userRightDao.getByUser(ue.getUser());
    this.setCanBeRemoved(tmp.size() > 0 || rights.isAdmin(ue.getUser()) ? true : false);
  }

  public SubmissionRow(String text) {
    this.text = text;
  }

  public SubmissionRow(Exercise e, Scenario scenario) {
    this.exercise = e;
    this.isExercise = true;
    this.text = System.getProperty("EXERCISE_SHORT") + ": [" + StringTools.zeroPad(e.getId(), 2)
        + "]";
    this.styleClass = "exercise_class";
    this.scenario = scenario;
  }

  public SubmissionRow(ExerciseGroup eg) {
    this.exGroup = eg;
    this.isExGroup = true;
    this.text = "[" + StringTools.zeroPad(eg.getId(), 2) + "]: "
        + StringTools.trimToLengthIndicator(eg.getName(), 30);
    this.styleClass = "exercise_group_class";
  }

  public SubmissionRow(Scenario sc) {
    this.scenario = sc;
    this.isScenario = true;
    this.text = "[" + StringTools.zeroPad(sc.getId(), 2) + "]: "
        + StringTools.trimToLengthIndicator(sc.getName(), 30);
    this.styleClass = "scenario_class";
  }

  public String getEntryDate() {
    if (userEntry != null) {
      DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, new Locale(
          "de", "DE"));
      return df.format(userEntry.getEntryTime());
    }
    return "---";
  }

  public boolean getIsExercise() {
    return isExercise;
  }

  public void setIsExercise(boolean isExercise) {
    this.isExercise = isExercise;
  }

  public boolean getIsEntry() {
    return isEntry;
  }

  public void setIsEntry(boolean isEntry) {
    this.isEntry = isEntry;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public boolean getIsExGroup() {
    return isExGroup;
  }

  public void setIsExGroup(boolean isExGroup) {
    this.isExGroup = isExGroup;
  }

  public boolean getIsScenario() {
    return isScenario;
  }

  public void setIsScenario(boolean isScenario) {
    this.isScenario = isScenario;
  }

  public UserEntry getUserEntry() {
    return userEntry;
  }

  public void setUserEntry(UserEntry userEntry) {
    this.userEntry = userEntry;
  }

  public Exercise getExercise() {
    return exercise;
  }

  public void setExercise(Exercise exercise) {
    this.exercise = exercise;
  }

  public String getExerciseText() {
    return StringTools.stripHtmlTags(exercise.getQuestion());
  }

  public ExerciseGroup getExGroup() {
    return exGroup;
  }

  public void setExGroup(ExerciseGroup exGroup) {
    this.exGroup = exGroup;
  }

  public Scenario getScenario() {
    return scenario;
  }

  public void setScenario(Scenario scenario) {
    this.scenario = scenario;
  }

  public UserResult getUserResult() {
    return userResult;
  }

  public void setUserResult(UserResult userResult) {
    this.userResult = userResult;
  }

  /**
   *
   *
   * @param d
   * @param group
   */
  public void setAvgCredits(double d, Exercise ex) {
    if (ex != null) {
      if (ex.getCredits() != null && !Double.isNaN(d)) {
        this.avgCredits = formatDouble(d, 1).replace(".0", "").replace(",0", "") + " / "
            + String.valueOf(ex.getCredits());
      } else {
        this.avgCredits = "- / " + String.valueOf(ex.getCredits());
      }
    }
  }

  /**
   *
   *
   * @param d
   * @return
   */
  private static String formatDouble(double d, int places) {
    DecimalFormat f = new DecimalFormat("#0." + StringUtils.repeat("0", places));
    return f.format(d);
  }

  public String getAvgCredits() {
    return avgCredits;
  }

  public String getStyleClass() {
    return styleClass;
  }

  public void setStyleClass(String styleClass) {
    this.styleClass = styleClass;
  }

  public boolean getCanBeRemoved() {
    return canBeRemoved;
  }

  public void setCanBeRemoved(boolean isDeletable) {
    this.canBeRemoved = isDeletable;
  }

}
