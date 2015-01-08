package de.uniwue.info6.webapp.admin;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ExerciseNode.java
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

import java.io.Serializable;

import de.uniwue.info6.database.map.Exercise;
import de.uniwue.info6.database.map.ExerciseGroup;
import de.uniwue.info6.database.map.Scenario;
import de.uniwue.info6.misc.StringTools;

/**
 *
 *
 * @author Michael
 */
public class ExerciseNode implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 *
	 */
	private Scenario scenario;
	private ExerciseGroup group;
	private Exercise exercise;

	/**
	 *
	 */
	public ExerciseNode(Scenario scenario) {
		this.scenario = scenario;
	}

	/**
	 *
	 */
	public ExerciseNode(ExerciseGroup group) {
		this.group = group;
	}

	/**
	 *
	 */
	public ExerciseNode(Exercise exercise) {
		this.exercise = exercise;
	}

	/**
	 *
	 */
	public ExerciseNode() {
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isScenario() {
		return scenario != null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isExercise() {
		return exercise != null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isExerciseGroup() {
		return group != null;
	}

	/**
	 * @return the scenario
	 */
	public Scenario getScenario() {
		return scenario;
	}

	/**
	 * @return the group
	 */
	public ExerciseGroup getGroup() {
		return group;
	}

	/**
	 * @return the exercise
	 */
	public Exercise getExercise() {
		return exercise;
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isRootNode() {
		return scenario == null && exercise == null && group == null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public String getStyleClass() {
		if (isScenario()) {
			return "scenario_class";
		} else if (isExerciseGroup()) {
			if (group.getIsRated() == true) {
				return "rated_group_class";
			}
			return "group_class";
		} else if (isExercise()) {
			return "exercise_class";
		}
		return "";
	}

	/**
	 *
	 *
	 * @return
	 */
	public String getURL() {
		if (isExercise()) {
			return "edit-exercise-" + exercise.getId();
		}
		return null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public String getMessage() {
		if (isScenario()) {
			return "[" + StringTools.zeroPad(scenario.getId(), 2) + "]: " + StringTools.stripHtmlTags(scenario.getName());
		} else if (isExerciseGroup()) {
			return "[" + StringTools.zeroPad(group.getId(), 2) + "]: " + StringTools.stripHtmlTags(group.getName());
		} else if (isExercise()) {
			return "[" + StringTools.zeroPad(exercise.getId(), 2) + "]: " + StringTools.stripHtmlTags(exercise.getQuestion());
		}
		return "Szenarien";
	}


}
