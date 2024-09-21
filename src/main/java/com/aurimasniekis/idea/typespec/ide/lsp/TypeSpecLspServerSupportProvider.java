package com.aurimasniekis.idea.typespec.ide.lsp;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.lsp.api.LspServer;
import com.intellij.platform.lsp.api.LspServerManager;
import com.intellij.platform.lsp.api.LspServerSupportProvider;
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.aurimasniekis.idea.typespec.TypeSpecIcons;
import com.aurimasniekis.idea.typespec.ide.ui.TypeSpecSettingsConfigurable;

public class TypeSpecLspServerSupportProvider implements LspServerSupportProvider {

  @Override
  public void fileOpened(
    @NotNull Project project, @NotNull VirtualFile virtualFile,
    @NotNull LspServerSupportProvider.LspServerStarter lspServerStarter
  ) {
    if (TypeSpecLspServerActivationRule.INSTANCE.isLspServerEnabledAndAvailable(project, virtualFile)) {
      lspServerStarter.ensureServerStarted(new TypeSpecLspServerDescriptor(project));
    }
  }

  @Override
  public @Nullable LspServerWidgetItem createLspServerWidgetItem(
    @NotNull LspServer lspServer, @Nullable VirtualFile currentFile
  ) {
    return new LspServerWidgetItem(
      lspServer,
      currentFile,
      TypeSpecIcons.FILE,
      TypeSpecSettingsConfigurable.class
    );
  }

  public static void restartTypeSpecLspServer(@NotNull Project project) {
    ApplicationManager.getApplication().invokeLater(() -> {
      LspServerManager
        .getInstance(project)
        .stopAndRestartIfNeeded(TypeSpecLspServerSupportProvider.class);
    }, project.getDisposed());
  }
}
