package de.uniwue.info6.webapp.lists;

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.data.SortEvent;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.SolutionQuery;
import de.uniwue.info6.database.map.User;
import de.uniwue.info6.database.map.UserEntry;
import de.uniwue.info6.database.map.UserResult;
import de.uniwue.info6.database.map.daos.ExerciseDao;
import de.uniwue.info6.database.map.daos.SolutionQueryDao;
import de.uniwue.info6.database.map.daos.UserEntryDao;
import de.uniwue.info6.database.map.daos.UserResultDao;
import de.uniwue.info6.misc.DateFormatTools;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "overview")
@ViewScoped
public class ExGroupTableBean implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /*
   * **************************************************************
   * daos **************************************************************
   */
  private ExerciseDao exerciseDao;
  private UserEntryDao userEntryDao;
  private UserResultDao userResultDao;
  private SolutionQueryDao solutionQueryDao;

  /*
   * **************************************************************
   * exercise-info
   * **************************************************************
   */
  private List<Exercise> filteredExercises, selectedExercises;
  private HashMap<ExerciseGroup, List<Exercise>> exerciseMap;
  private ExerciseGroup lastExerciseGroup;

  /**
   * {@inheritDoc}
   *
   * @see Object#TableBean()
   */
  public ExGroupTableBean() {
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    // get exercise list
    exerciseDao = new ExerciseDao();
    userEntryDao = new UserEntryDao();
    userResultDao = new UserResultDao();
    solutionQueryDao = new SolutionQueryDao();

    exerciseMap = new HashMap<ExerciseGroup, List<Exercise>>();
    selectedExercises = new ArrayList<Exercise>();
  }

  /**
   *
   *
   * @return
   */
  public String getScoreInPercent(ExerciseGroup group) {
    double maxScore = getMaxPoints(group);
    double minScore = getAchievedPoints(group);
    String score = "100";
    if (maxScore != 0) {
      score = formatDouble(minScore / maxScore * 100, 1);
    }
    score += "%";
    return score;
  }

  /**
   *
   *
   * @param d
   * @return
   */
  public static String formatDouble(double d, int places) {
    DecimalFormat f = new DecimalFormat("#0." + StringUtils.repeat("0", places));
    return f.format(d);
  }

  /**
   *
   *
   * @param group
   * @return
   */
  public int getMaxPoints(ExerciseGroup group) {
    int score = 0;
    List<Exercise> exercises = getExercisesFromGroup(group);
    if (exercises != null) {
      for (Exercise ex : exercises) {
        Byte credits = ex.getCredits();
        if (credits != null) {
          score += credits;
        }
      }
    }
    return score;
  }

  /**
   *
   *
   * @param group
   * @return
   */
  public int getAchievedPoints(ExerciseGroup group) {
    int score = 0;
    List<Exercise> exercises = getExercisesFromGroup(group);
    if (exercises != null) {
      for (Exercise ex : exercises) {
        Byte credits = getLastUserEntryPoints(ex);
        if (credits != null) {
          score += credits;
        }
      }
    }
    return score;
  }

  /**
   *
   *
   * @return
   */
  public String normalizeQuestion(Exercise ex) {
    String question = null;
    if (ex != null) {
      question = ex.getQuestion();
      // strip html tags
      question = question.replaceAll("<[^>]*>", "");
    }

    return question;
  }

  /**
   *
   *
   * @param group
   * @return
   */
  public List<Exercise> getExercisesFromGroup(ExerciseGroup group) {
    List<Exercise> exerciseList = null;
    if (group != null) {
      if (!exerciseMap.containsKey(group)) {
        exerciseList = exerciseDao.findByExGroup(group);
        if (exerciseList != null) {
          exerciseMap.put(group, exerciseList);
        }
      } else {
        exerciseList = exerciseMap.get(group);
      }
    }
    return exerciseList;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String getTogglerStyle(Exercise ex) {
    if (ex != null && showResults(ex.getExerciseGroup())) {
      Byte credits = getLastUserEntryPoints(ex);
      if (credits != null) {
        if (credits > 0) {
          return "toggler_correct";
        } else {
          return "toggler_false";
        }
      } else {
        return "toggler_empty";
      }
    }
    return "toggler_info";
  }

  /**
   *
   *
   * @param group
   * @return
   */
  private String resultFeedback(ExerciseGroup group) {
    if (group != null && group.getIsRated() != null && group.getIsRated()) {
      if (showResults(group)) {
        return " - [" + Cfg.inst().getProp(DEF_LANGUAGE, "EX.ITS_RATED") + "]";
      } else {
        Date end = group.getEndTime();
        if (end != null) {
          return " " + getTimeDifference(new Date(), end);
        }
      }
    }
    return "";
  }

  /**
   *
   *
   * @param start
   * @param end
   * @return
   */
  private String getTimeDifference(Date start, Date end) {
    long diffInSeconds = (end.getTime() - start.getTime()) / 1000;

    long diff[] = new long[] { 0, 0, 0, 0 };
    /* sec */diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
    /* min */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60
        : diffInSeconds;
    /* hours */diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24
        : diffInSeconds;
    /* days */diff[0] = (diffInSeconds = (diffInSeconds / 24));

    return (" - [Noch "
        + String.format("%d Tag%s, %d Stunde%s, %d Minute%s", diff[0], diff[0] > 1 ? "e" : "",
            diff[1], diff[1] > 1 ? "n" : "", diff[2], diff[2] > 1 ? "n" : "") + "]").replace(
        "0 Tag, ", "").replace("0 Stunde, ", "").replace(", 0 Minute", "");
  }

  /**
   *
   *
   * @return
   */
  public String getGroupName(ExerciseGroup group) {
    if (group != null && group.getIsRated() != null) {
      return (group.getIsRated() ? Cfg.inst().getProp(DEF_LANGUAGE, "AC.EX_RA") : Cfg.inst().getProp(DEF_LANGUAGE, "AC.EX"))
          + "-&#8195;" + StringTools.trimToLengthIndicator(group.getName(), 45)
          + resultFeedback(group);
    }
    return "ERROR";
  }

  /**
   *
   *
   * @return
   */
  public boolean renderToggler(Exercise ex) {
    if (showResults(ex.getExerciseGroup()) && ex.getExerciseGroup().getIsRated()) {
      return true;
    }
    if (userEntryExists(ex)) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public String getEndDate(ExerciseGroup group) {
    if (group.getEndTime() != null) {
      String date = DateFormatTools.getGermanFormat(group.getEndTime());
      if (group.getEndTime().before(new Date())) {
        date += " [abgelaufen]";
      }
      return date;
    }
    return "[noch kein Termin festgelegt]";
  }

  /**
   *
   *
   * @return
   */
  public String getStartDate(ExerciseGroup group) {
    if (group.getStartTime() != null) {
      return DateFormatTools.getGermanFormat(group.getStartTime());
    }
    return null;
  }

  /**
   *
   *
   * @param group
   * @return
   */
  public boolean showResults(ExerciseGroup group) {
    if (group != null) {
      if (!group.getIsRated()) {
        return true;
      }
      Date end = group.getEndTime();
      if (end != null) {
        if (end.before(new Date())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   *
   *
   * @param group
   * @return
   */
  public boolean hideResults(ExerciseGroup group) {
    return !showResults(group);
  }

  /**
   *
   *
   * @param group
   * @return
   */
  public boolean showGroup(ExerciseGroup group) {
    Date start = group.getStartTime();
    if (start != null) {
      if (start.after(new Date())) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   *
   * @param group
   * @return
   */
  public boolean disableGroup(ExerciseGroup group) {
    return !showGroup(group);
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public Byte getLastUserEntryPoints(Exercise ex) {
    UserEntry entry = userEntryDao.getLastUserEntry(ex);
    if (entry != null) {
      UserResult result = userResultDao.getLastUserResultFromEntry(entry);
      if (result != null) {
        return result.getCredits();
      }
    }
    return null;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String getResultPoints(Exercise ex) {
    Byte points = getLastUserEntryPoints(ex);
    if (points == null) {
      return "0";
    }
    return String.valueOf(points);
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String getLastUserEntryErrorText(Exercise ex) {
    UserEntry entry = userEntryDao.getLastUserEntry(ex);
    Byte credits = ex.getCredits();

    if (entry != null && showResults(ex.getExerciseGroup())) {
      UserResult result = userResultDao.getLastUserResultFromEntry(entry);

      User corrector = result.getUser();
      Byte userCredits = result.getCredits();

      if (result != null && result.getComment() != null && !result.getComment().trim().isEmpty()) {
        if (credits != null && userCredits != null) {
          if (credits.equals(userCredits)) {
            if (corrector == null) {
              return null;
            }
          }
        }
        return result.getComment();
      }
    }
    return null;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String getSolutionText(Exercise ex) {
    UserEntry entry = userEntryDao.getLastUserEntry(ex);
    if (showResults(ex.getExerciseGroup())) {
      if (entry != null) {
        UserResult result = userResultDao.getLastUserResultFromEntry(entry);
        SolutionQuery query = result.getSolutionQuery();
        query = solutionQueryDao.getById(query.getId());
        if (result != null && query != null) {
          return query.getQuery();
        }
      } else {
        SolutionQuery example = new SolutionQuery();
        example.setExercise(ex);
        List<SolutionQuery> solutions = solutionQueryDao.findByExample(example);
        if (solutions != null && !solutions.isEmpty()) {
          return solutions.get(0).getQuery();
        }
      }
    }
    return null;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String getLastUserEntryString(Exercise ex) {
    UserEntry entry = userEntryDao.getLastUserEntry(ex);
    if (entry != null) {
      return entry.getUserQuery();
    }
    return null;
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public boolean userEntryExists(Exercise ex) {
    if (userEntryDao.getLastUserEntry(ex) != null) {
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @return
   */
  public List<Exercise> getViewedExercises(ExerciseGroup group) {
    if (filteredExercises != null) {
      return filteredExercises;
    }
    return exerciseMap.get(group);
  }

  /**
   *
   *
   * @param ex
   * @return
   */
  public String getLastUserEntryTime(Exercise ex) {
    UserEntry entry = userEntryDao.getLastUserEntry(ex);
    if (entry != null) {
      DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, new Locale(
          "de", "DE"));
      return df.format(entry.getEntryTime());
    }
    return null;
  }

  /**
   *
   *
   * @return
   */
  public int selectionSize(ExerciseGroup group) {
    if (selectedExercises != null && lastExerciseGroup != null && group != null
        && lastExerciseGroup.equals(group)) {
      return selectedExercises.size();
    } else {
      this.lastExerciseGroup = group;
      filteredExercises = null;
      selectedExercises = new ArrayList<Exercise>();
      return 0;
    }
  }

  /**
   * @return the filteredExercises
   */
  public List<Exercise> getFilteredExercises() {
    return filteredExercises;
  }

  /**
   * @param filteredExercises
   *          the filteredExercises to set
   */
  public void setFilteredExercises(List<Exercise> filteredExercises) {
    this.filteredExercises = filteredExercises;
  }

  /**
   * @return the selectedExercises
   */
  public List<Exercise> getSelectedExercises() {
    return selectedExercises;
  }

  /**
   * @param selectedExercises
   *          the selectedExercises to set
   */
  public void setSelectedExercises(List<Exercise> selectedExercises) {
    this.selectedExercises = selectedExercises;
  }

  /**
   *
   *
   * @param event
   */
  public void onRowSelect(SelectEvent event) {
    // ...
  }

  /**
   *
   *
   * @param event
   */
  public void onEdit(RowEditEvent event) {
    // ...
  }

  /**
   *
   *
   * @param event
   */
  public void onCancel(RowEditEvent event) {
    // ...
  }

  /**
   *
   *
   * @param event
   */
  public void onResize(ColumnResizeEvent event) {
  }

  /**
   *
   *
   * @param event
   */
  public void onFilter(FilterEvent event) {
    // ...
  }

  /**
   *
   *
   * @param event
   */
  public void onRowToggle(ToggleEvent event) {
    // ...
  }

  /**
   *
   *
   * @param event
   */
  public void onRowUnselect(UnselectEvent event) {
    // ...
  }

  /**
   *
   *
   * @param event
   */
  public void onSort(SortEvent event) {
    // ...
  }

  /**
   *
   *
   * @param event
   */
  public void groupChangeEvent(TabChangeEvent event) {
    // ..
  }

}
