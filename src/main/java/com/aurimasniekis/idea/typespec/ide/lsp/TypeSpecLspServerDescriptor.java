package com.aurimasniekis.idea.typespec.ide.lsp;

import com.intellij.lang.typescript.lsp.JSLspServerDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TypeSpecLspServerDescriptor extends JSLspServerDescriptor {

  public TypeSpecLspServerDescriptor(
    @NotNull Project project
  ) {
    super(project, TypeSpecLspServerActivationRule.INSTANCE, "TypeSpec");
  }
}
