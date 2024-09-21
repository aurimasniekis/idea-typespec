package com.aurimasniekis.idea.typespec.ide.lsp;

import com.intellij.lang.typescript.lsp.LspServerActivationRule;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import com.aurimasniekis.idea.typespec.filetype.TypeSpecFileType;
import com.aurimasniekis.idea.typespec.ide.lsp.TypeSpecServiceSettings.TypeSpecServiceMode;

public class TypeSpecLspServerActivationRule extends LspServerActivationRule {

  public static final TypeSpecLspServerActivationRule INSTANCE = new TypeSpecLspServerActivationRule();

  public TypeSpecLspServerActivationRule() {
    super(TypeSpecLspExecutableDownloader.INSTANCE);
  }

  @Override
  protected boolean isEnabledInSettings(@NotNull Project project) {
    return TypeSpecServiceSettings.getInstance(project).getServiceMode() == TypeSpecServiceMode.ENABLED;
  }

  @Override
  public boolean isFileAcceptableForLspServer(@NotNull VirtualFile file) {
    return file.getFileType() == TypeSpecFileType.INSTANCE;
  }

  @Override
  protected boolean isProjectContext(@NotNull Project project, @NotNull VirtualFile file) {
    return file.getFileType() == TypeSpecFileType.INSTANCE;
  }
}
