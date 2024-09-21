package com.aurimasniekis.idea.typespec;

import com.intellij.lang.Language;

public class TypeSpecLanguage extends Language {

  public static final TypeSpecLanguage INSTANCE = new TypeSpecLanguage();

  private TypeSpecLanguage() {
    super("TypeSpec");
  }

}
