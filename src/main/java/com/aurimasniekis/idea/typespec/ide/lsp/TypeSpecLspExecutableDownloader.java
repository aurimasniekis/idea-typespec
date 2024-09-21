package com.aurimasniekis.idea.typespec.ide.lsp;

import com.intellij.javascript.nodejs.util.NodePackageRef;
import com.intellij.lang.typescript.lsp.LspServerDownloader;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TypeSpecLspExecutableDownloader extends LspServerDownloader {

  public static final TypeSpecLspExecutableDownloader INSTANCE =
    new TypeSpecLspExecutableDownloader();

  public TypeSpecLspExecutableDownloader(
  ) {
    super(TypeSpecLspServerPackageDescriptor.INSTANCE);
  }

  @Override
  public @NotNull NodePackageRef getSelectedPackageRef(@NotNull Project project) {
    return TypeSpecServiceSettings.getInstance(project).getLspServerPackageNameRef();
  }

  @Override
  public void restartService(@NotNull Project project) {
    TypeSpecLspServerSupportProvider.restartTypeSpecLspServer(project);
  }
}
