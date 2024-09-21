package com.aurimasniekis.idea.typespec.syntax.highlighting;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.Interner;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;
import kotlin.LazyKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.Constants;
import org.jetbrains.plugins.textmate.TextMateService;
import org.jetbrains.plugins.textmate.bundles.TextMateFileNameMatcher;
import org.jetbrains.plugins.textmate.bundles.TextMateGrammar;
import org.jetbrains.plugins.textmate.language.TextMateLanguageDescriptor;
import org.jetbrains.plugins.textmate.language.TextMateStandardTokenType;
import org.jetbrains.plugins.textmate.language.preferences.Action;
import org.jetbrains.plugins.textmate.language.preferences.IndentAction;
import org.jetbrains.plugins.textmate.language.preferences.IndentationRules;
import org.jetbrains.plugins.textmate.language.preferences.OnEnterRule;
import org.jetbrains.plugins.textmate.language.preferences.Preferences;
import org.jetbrains.plugins.textmate.language.preferences.PreferencesRegistryImpl;
import org.jetbrains.plugins.textmate.language.preferences.ShellVariablesRegistryImpl;
import org.jetbrains.plugins.textmate.language.preferences.TextMateAutoClosingPair;
import org.jetbrains.plugins.textmate.language.preferences.TextMateBracePair;
import org.jetbrains.plugins.textmate.language.preferences.TextMateShellVariable;
import org.jetbrains.plugins.textmate.language.preferences.TextRule;
import org.jetbrains.plugins.textmate.language.syntax.TextMateSyntaxTable;
import org.jetbrains.plugins.textmate.language.syntax.highlighting.TextMateHighlighter;
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateHighlightingLexer;
import org.jetbrains.plugins.textmate.plist.CompositePlistReader;

public class TypeSpecSyntaxHighlighterFactory extends SyntaxHighlighterFactory {

  @Override
  public @NotNull SyntaxHighlighter getSyntaxHighlighter(
    @Nullable Project project, @Nullable VirtualFile virtualFile
  ) {
    if (virtualFile == null) {
      return new TextMateHighlighter(null);
    }

    return new TextMateHighlighter(
      new TextMateHighlightingLexer(
        buildSyntaxNodeDescriptor(),
        Registry.get("textmate.line.highlighting.limit").asInteger()
      )
    );
  }

  protected TextMateLanguageDescriptor buildSyntaxNodeDescriptor() {
    try {
      var grammar = readSyntaxHighlighter();
      var syntaxTable = new TextMateSyntaxTable();
      var interner = Interner.<CharSequence>createWeakInterner();
      var scopeName = syntaxTable.loadSyntax(grammar.getPlist().getValue(), interner);

      if (scopeName == null) {
        throw new IOException("Could not find scope name");
      }

      var syntaxDescriptor = syntaxTable.getSyntax(scopeName);

      var pr = TextMateService.getInstance().getPreferenceRegistry();
      if (pr instanceof PreferencesRegistryImpl prefReg) {
        prefReg.addPreferences(
          new Preferences(
            String.valueOf(scopeName),
            Set.of(
              new TextMateBracePair("{", "}"),
              new TextMateBracePair("[", "]"),
              new TextMateBracePair("(", ")")
            ),
            Set.of(
              new TextMateAutoClosingPair("{", "}", null),
              new TextMateAutoClosingPair("[", "]", null),
              new TextMateAutoClosingPair("(", ")", null),
              new TextMateAutoClosingPair("\"", "\"", null),
              new TextMateAutoClosingPair(
                "/**",
                " */",
                EnumSet.of(TextMateStandardTokenType.STRING)
              )
            ),
            Set.of(
              new TextMateBracePair("{", "}"),
              new TextMateBracePair("[", "]"),
              new TextMateBracePair("(", ")"),
              new TextMateBracePair("\"", "\"")
            ),
            null,
            new IndentationRules(
              "^((?!//).)*(\\{([^}\"'`/]*|(\\t|[ ])*//.*)|\\([^)\"'`/]*|\\[[^\\]\"'`/]*)$",
              "^((?!.*?/\\*).*\\*/)?\\s*[\\}\\]].*$",
              null,
              "^(\\t|[ ])*[ ]\\*[^/]*\\*/\\s*$|^(\\t|[ ])*[ ]\\*/\\s*$|^(\\t|[ ])*[ ]\\*([ ]([^\\*]|\\*(?!/))*)?$"
            ),
            Set.of(
              new OnEnterRule(
                new TextRule("^\\s*/\\*\\*(?!/)([^\\*]|\\*(?!/))*$"),
                new TextRule("^\\s*\\*/$"),
                null,
                new Action(
                  IndentAction.INDENT_OUTDENT,
                  " * ",
                  null
                )
              ),
              new OnEnterRule(
                new TextRule("^\\s*/\\*\\*(?!/)([^\\*]|\\*(?!/))*$"),
                null,
                null,
                new Action(
                  IndentAction.NONE,
                  " * ",
                  null
                )
              ),
              new OnEnterRule(
                new TextRule("^(\\t|[ ])*[ ]\\*([ ]([^\\*]|\\*(?!/))*)?$"),
                null,
                new TextRule("(?=^(\\s*(/\\*\\*|\\*)).*)(?=(?!(\\s*\\*/)))"),
                new Action(
                  IndentAction.NONE,
                  "* ",
                  null
                )
              ),
              new OnEnterRule(
                new TextRule("^(\\t|[ ])*[ ]\\*/\\s*$"),
                null,
                null,
                new Action(
                  IndentAction.NONE,
                  null,
                  1
                )
              ),
              new OnEnterRule(
                new TextRule("^(\\t|[ ])*[ ]\\*[^/]*\\*/\\s*$"),
                null,
                null,
                new Action(
                  IndentAction.NONE,
                  null,
                  1
                )
              )
            )
          )
        );
      }

      var vr = TextMateService.getInstance().getShellVariableRegistry();
      if (vr instanceof ShellVariablesRegistryImpl varReg) {
        varReg.addVariable(new TextMateShellVariable(
          scopeName,
          Constants.COMMENT_START_VARIABLE,
          "// "
        ));

        varReg.addVariable(new TextMateShellVariable(
          scopeName,
          Constants.COMMENT_START_VARIABLE + "_2",
          "/* "
        ));

        varReg.addVariable(new TextMateShellVariable(
          scopeName,
          Constants.COMMENT_END_VARIABLE + "_2",
          " */"
        ));
      }

      return new TextMateLanguageDescriptor(
        scopeName,
        syntaxDescriptor
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected TextMateGrammar readSyntaxHighlighter() throws IOException {
    try (
      var tmLanguageFile = getClass().getClassLoader().getResourceAsStream("typespec.tmLanguage")
    ) {
      if (tmLanguageFile == null) {
        throw new IOException("Could not find typespec.tmLanguage");
      }

      var plistReader = new CompositePlistReader();
      var plist       = plistReader.read(new BufferedInputStream(tmLanguageFile));

      var fileNameMatchers = plist
        .getPlistValue(Constants.FILE_TYPES_KEY, new ArrayList<String>())
        .getStringArray()
        .stream()
        .flatMap(s -> Stream.of(
          new TextMateFileNameMatcher.Name(s),
          new TextMateFileNameMatcher.Extension(s)
        ));

      var firstLinePattern = plist
        .getPlistValue(Constants.FIRST_LINE_MATCH, null);

      return new TextMateGrammar(
        fileNameMatchers.toList(),
        firstLinePattern != null ? firstLinePattern.getString() : null,
        LazyKt.lazyOf(plist),
        null,
        null
      );
    }
  }
}
