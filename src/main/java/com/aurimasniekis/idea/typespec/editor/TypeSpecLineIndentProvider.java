package com.aurimasniekis.idea.typespec.editor;

import com.intellij.application.options.CodeStyle;
import com.intellij.formatting.IndentInfo;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions;
import com.intellij.psi.codeStyle.lineIndent.LineIndentProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.TextMateService;
import org.jetbrains.plugins.textmate.editor.TextMateEditorUtils;
import org.jetbrains.plugins.textmate.editor.Utils;
import org.jetbrains.plugins.textmate.language.preferences.IndentAction;
import org.jetbrains.plugins.textmate.language.preferences.IndentationRules;
import org.jetbrains.plugins.textmate.language.preferences.OnEnterRule;
import com.aurimasniekis.idea.typespec.TypeSpecLanguage;

public class TypeSpecLineIndentProvider implements LineIndentProvider {

  private static final Logger LOG = Logger.getInstance(TypeSpecLineIndentProvider.class);

  @Override
  public String getLineIndent(
    @NotNull Project project, @NotNull Editor editor, Language language, int offset
  ) {
    if (!(language instanceof TypeSpecLanguage)) {
      return null;
    }

    var actualScope = TextMateEditorUtils.getCurrentScopeSelector((EditorEx) editor);
    if (actualScope == null) {
      return null;
    }

    var registry        = TextMateService.getInstance().getPreferenceRegistry();
    var preferencesList = registry.getPreferences(actualScope);

    var indentationRules = IndentationRules.empty();
    for (var r : preferencesList) {
      indentationRules = indentationRules.updateWith(r.getIndentationRules());
    }

    List<OnEnterRule> onEnterRules = new ArrayList<>();
    for (var pref : preferencesList) {
      var rules = pref.getOnEnterRules();
      if (rules != null) {
        onEnterRules.addAll(rules);
      }
    }

    Document document   = editor.getDocument();
    int      lineNumber = document.getLineNumber(offset);
    if (lineNumber <= 0) {
      return null;
    }
    int lineOffset = document.getLineStartOffset(lineNumber);
    if (lineOffset <= 0) {
      return null;
    }

    int    prevLineNumber = lineNumber - 1;
    String prevLineText   = document.getText().lines().toArray(String[]::new)[prevLineNumber];

    /*
     * This works well with .editorConfig,
     * without .editorconfig indent style is not auto-detected
     * Proper solution would require:
     * - improving parser using `indentationRules` from the TextMate bundle,
     * - adding TextMateFormattingModelBuilder, which will fuel `DetectableIndentOptionsProvider`
     */
    IndentOptions options = CodeStyle.getSettings(project, editor.getVirtualFile())
                                     .getIndentOptionsByFile(
                                       project,
                                       editor.getVirtualFile(),
                                       null
                                     );
    Integer indentChange = getIndentChange(prevLineText, indentationRules, onEnterRules);

    if (indentChange != null) {
      int        baseLineIndent = Utils.Companion.indentOfLine(prevLineText, options);
      int        indentSpaces   = baseLineIndent + indentChange * options.TAB_SIZE;
      IndentInfo indentInfo     = new IndentInfo(0, Math.max(0, indentSpaces), 0);
      return indentInfo.generateNewWhiteSpace(options);
    }

    return null;
  }

  @Override
  public boolean isSuitableFor(Language language) {
    return language instanceof TypeSpecLanguage;
  }

  private Integer getIndentChange(
    String prevLineText,
    IndentationRules indentationRules,
    List<OnEnterRule> onEnterRules
  ) {

    for (OnEnterRule onEnterRule : onEnterRules) {
      String beforeTextPattern = onEnterRule.getBeforeText().getText();
      try {
        if (Pattern.compile(beforeTextPattern).matcher(prevLineText).find()) {
          IndentAction indentAction = onEnterRule.getAction().getIndent();
          if (indentAction == IndentAction.INDENT) {
            return 1;
          } else if (indentAction == IndentAction.NONE) {
            return null;
          }
        }
      } catch (Exception e) {
        LOG.error("Using regex onEnterRule.beforeText failed", e);
      }
    }

    String increasePattern = indentationRules.getIncreaseIndentPattern();
    try {
      if (increasePattern != null && Pattern
        .compile(increasePattern)
        .matcher(prevLineText)
        .matches()) {
        return 1;
      }
    } catch (Exception e) {
      LOG.error("Using regex indentationRules.increaseIndentPattern failed", e);
    }

    String decreasePattern = indentationRules.getDecreaseIndentPattern();
    try {
      if (decreasePattern != null && Pattern
        .compile(decreasePattern)
        .matcher(prevLineText)
        .matches()) {
        return -1;
      }
    } catch (Exception e) {
      LOG.error("Using regex indentationRules.decreaseIndentPattern failed", e);
    }

    return null;
  }
}

