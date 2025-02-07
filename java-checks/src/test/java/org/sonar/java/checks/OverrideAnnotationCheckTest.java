/*
 * SonarQube Java
 * Copyright (C) 2012-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class OverrideAnnotationCheckTest {

  @Test
  void test_java() {
    CheckVerifier.newVerifier()
      .onFile("src/test/files/checks/OverrideAnnotationCheck.java")
      .withCheck(new OverrideAnnotationCheck())
      .verifyIssues();
  }

  @Test
  void test_java_8() {
    CheckVerifier.newVerifier()
      .onFile("src/test/files/checks/OverrideAnnotationCheck_java8.java")
      .withCheck(new OverrideAnnotationCheck())
      .withJavaVersion(8)
      .verifyIssues();
    CheckVerifier.newVerifier()
      .onFile("src/test/files/checks/OverrideAnnotationCheck.java")
      .withCheck(new OverrideAnnotationCheck())
      .withJavaVersion(8)
      .verifyIssues();
  }

  @Test
  void test_java_6() {
    CheckVerifier.newVerifier()
      .onFile("src/test/files/checks/OverrideAnnotationCheck.java")
      .withCheck(new OverrideAnnotationCheck())
      .withJavaVersion(6)
      .verifyIssues();
  }

  @Test
  void test_java_5() {
    CheckVerifier.newVerifier()
      .onFile("src/test/files/checks/OverrideAnnotationCheck_java5.java")
      .withCheck(new OverrideAnnotationCheck())
      .withJavaVersion(5)
      .verifyIssues();
  }

  @Test
  void test_java_4() {
    CheckVerifier.newVerifier()
      .onFile("src/test/files/checks/OverrideAnnotationCheck_java4.java")
      .withCheck(new OverrideAnnotationCheck())
      .withJavaVersion(4)
      .verifyNoIssues();
  }

}
