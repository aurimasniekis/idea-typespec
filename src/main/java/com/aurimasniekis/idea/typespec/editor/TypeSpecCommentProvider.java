package com.aurimasniekis.idea.typespec.editor;

import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.editor.TextMateCommentProvider;
import com.aurimasniekis.idea.typespec.filetype.TypeSpecFileType;

public class TypeSpecCommentProvider extends TextMateCommentProvider {

  @Override
  public boolean canProcess(@NotNull PsiFile file, @NotNull FileViewProvider viewProvider) {
    return file.getFileType() == TypeSpecFileType.INSTANCE;
  }
}
