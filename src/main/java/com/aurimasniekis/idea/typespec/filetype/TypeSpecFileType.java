package com.aurimasniekis.idea.typespec.filetype;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts.Label;
import com.intellij.openapi.util.NlsSafe;
import javax.swing.Icon;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.aurimasniekis.idea.typespec.TypeSpecIcons;
import com.aurimasniekis.idea.typespec.TypeSpecLanguage;

public class TypeSpecFileType extends LanguageFileType {

  public static final TypeSpecFileType INSTANCE = new TypeSpecFileType();

  protected TypeSpecFileType() {
    super(TypeSpecLanguage.INSTANCE);
  }

  @Override
  public @NonNls @NotNull String getName() {
    return "typespec";
  }

  @Override
  public @Label @NotNull String getDescription() {
    return "TypeSpec file";
  }

  @Override
  public @NlsSafe @NotNull String getDefaultExtension() {
    return "tsp";
  }

  @Override
  public Icon getIcon() {
    return TypeSpecIcons.FILE;
  }
}
