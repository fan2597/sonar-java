/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.surefire;

import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.MutableTestPlan;
import org.sonar.api.test.TestCase;
import org.sonar.plugins.java.api.JavaResourceLocator;
import org.sonar.plugins.surefire.api.AbstractSurefireParser;
import org.sonar.plugins.surefire.data.UnitTestClassReport;
import org.sonar.plugins.surefire.data.UnitTestResult;

/**
 * @since 1.2
 */
public class SurefireJavaParser extends AbstractSurefireParser implements BatchExtension {

  private final ResourcePerspectives perspectives;
  private final JavaResourceLocator javaResourceLocator;

  public SurefireJavaParser(ResourcePerspectives perspectives, JavaResourceLocator javaResourceLocator) {
    this.perspectives = perspectives;
    this.javaResourceLocator = javaResourceLocator;
  }

  @Override
  protected void saveResults(SensorContext context, Resource testFile, UnitTestClassReport report) {
    for (UnitTestResult unitTestResult : report.getResults()) {
      MutableTestPlan testPlan = perspectives.as(MutableTestPlan.class, testFile);
      if (testPlan != null) {
        testPlan.addTestCase(unitTestResult.getName())
          .setDurationInMs(unitTestResult.getDurationMilliseconds())
          .setStatus(TestCase.Status.of(unitTestResult.getStatus()))
          .setMessage(unitTestResult.getMessage())
          .setType(TestCase.TYPE_UNIT)
          .setStackTrace(unitTestResult.getStackTrace());
      }
    }
  }

  @Override
  protected Resource getUnitTestResource(String classKey) {
    return javaResourceLocator.findResourceByClassName(classKey);
  }

}
