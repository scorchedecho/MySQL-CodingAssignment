package projects.service;/*
 *  Copyright 2022 scorchedE.C.H.O
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import projects.dao.ProjectDao;
import projects.entity.Project;

/**
 * ProjectService class of the database test application.
 */
public class ProjectService {
  private ProjectDao projectDao = new ProjectDao();

  /**
   * Add a project to the database.
   * @param project The project to add.
   * @return The project added.
   */
  public Project addProject(Project project) {
    return projectDao.insertProject(project);
  }
}
