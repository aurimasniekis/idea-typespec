package com.aurimasniekis.idea.typespec.ide.lsp;

import com.intellij.lang.typescript.lsp.LspServerPackageDescriptor;
import org.jetbrains.annotations.NotNull;

public class TypeSpecLspServerPackageDescriptor extends LspServerPackageDescriptor {

  public static final TypeSpecLspServerPackageDescriptor INSTANCE = new TypeSpecLspServerPackageDescriptor();

  public TypeSpecLspServerPackageDescriptor() {
    super("@typespec/compiler", "0.60.1", "/cmd/tsp-server.js");
  }

  @Override
  public @NotNull String getDefaultVersion() {
    return "0.60.1";
  }
}
