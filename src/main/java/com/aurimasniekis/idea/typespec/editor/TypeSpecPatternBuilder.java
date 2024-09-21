package com.aurimasniekis.idea.typespec.editor;

import com.intellij.lexer.EmptyLexer;
import com.intellij.lexer.Lexer;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.textmate.TextMatePatternBuilder;
import com.aurimasniekis.idea.typespec.psi.TypeSpecParserDefinition.TypeSpecFile;

public class TypeSpecPatternBuilder extends TextMatePatternBuilder {

  @Override
  public @Nullable Lexer getIndexingLexer(@NotNull PsiFile file) {
    return file instanceof TypeSpecFile ? new EmptyLexer() : null;
  }
}
