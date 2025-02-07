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
package org.sonar.java.checks.tests;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.sonar.java.checks.helpers.UnitTestUtils;
import org.sonar.java.model.ModifiersUtils;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifiersTree;
import org.sonar.plugins.java.api.tree.Tree;

public abstract class AbstractJUnit5NotCompliantModifierChecker extends IssuableSubscriptionVisitor {

  protected abstract boolean isNotCompliantModifier(Modifier modifier, boolean isMethod);

  protected abstract void raiseIssueOnNotCompliantReturnType(MethodTree methodTree);

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return Collections.singletonList(Tree.Kind.CLASS);
  }

  @Override
  public void visitNode(Tree tree) {
    ClassTree classTree = (ClassTree) tree;
    if (classTree.symbol().isAbstract()) {
      return;
    }
    List<MethodTree> methods = classTree.members().stream()
      .filter(member -> member.is(Tree.Kind.METHOD))
      .map(MethodTree.class::cast)
      .collect(Collectors.toList());

    List<MethodTree> testMethods = methods.stream()
      .filter(UnitTestUtils::hasJUnit5TestAnnotation)
      .filter(AbstractJUnit5NotCompliantModifierChecker::isNotOverriding)
      .collect(Collectors.toList());

    for (MethodTree testMethod : testMethods) {
      raiseIssueOnNotCompliantModifiers(testMethod.modifiers(), true);
      raiseIssueOnNotCompliantReturnType(testMethod);
    }

    methods.removeAll(testMethods);
    if (methods.stream()
      .map(MethodTree::modifiers)
      .anyMatch(AbstractJUnit5NotCompliantModifierChecker::isPublicStaticMethod)) {
      // if there is public static methods, we can not ask for a change of visibility of the class
      return;
    }

    if (!testMethods.isEmpty()) {
      raiseIssueOnNotCompliantModifiers(classTree.modifiers(), false);
    }
  }

  private static boolean isPublicStaticMethod(ModifiersTree modifiers) {
    return ModifiersUtils.hasModifier(modifiers, Modifier.PUBLIC) && ModifiersUtils.hasModifier(modifiers, Modifier.STATIC);
  }

  private void raiseIssueOnNotCompliantModifiers(ModifiersTree modifierTree, boolean isMethod) {
    modifierTree.modifiers().stream()
      .filter(modifier -> isNotCompliantModifier(modifier.modifier(), isMethod))
      .findFirst()
      .ifPresent(modifier -> reportIssue(modifier, "Remove this '" + modifier.keyword().text() + "' modifier."));
  }

  private static boolean isNotOverriding(MethodTree tree) {
    // When it cannot be decided, isOverriding will return null, we consider that it as an override to avoid FP.
    return Boolean.FALSE.equals(tree.isOverriding());
  }

}
