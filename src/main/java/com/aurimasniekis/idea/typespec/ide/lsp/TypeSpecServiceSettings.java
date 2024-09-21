package com.aurimasniekis.idea.typespec.ide.lsp;

import static com.intellij.lang.typescript.lsp.ExternalDefinitionsNodePackageKt.createPackageRef;
import static com.intellij.lang.typescript.lsp.ExternalDefinitionsNodePackageKt.defaultPackageKey;
import static com.intellij.lang.typescript.lsp.ExternalDefinitionsNodePackageKt.extractRefText;

import com.intellij.javascript.nodejs.util.NodePackageRef;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.SimplePersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import com.aurimasniekis.idea.typespec.ide.lsp.TypeSpecServiceSettings.TypeSpecServiceState;

@Service(Service.Level.PROJECT)
@State(
  name = "TypeSpecServiceSettings",
  storages = {
    @Storage(StoragePathMacros.WORKSPACE_FILE)
  }
)
public final class TypeSpecServiceSettings extends SimplePersistentStateComponent<TypeSpecServiceState> {

  private final Project project;

  public TypeSpecServiceSettings(
    @NotNull Project project
  ) {
    super(new TypeSpecServiceState());

    this.project = project;
  }

  public TypeSpecServiceMode getServiceMode() {
    return getState().serviceMode;
  }

  public void setServiceMode(@NotNull TypeSpecServiceMode serviceMode) {
    var changed = getState().serviceMode != serviceMode;

    getState().serviceMode = serviceMode;

    if (changed) {
      TypeSpecLspServerSupportProvider.restartTypeSpecLspServer(project);
    }
  }

  public NodePackageRef getLspServerPackageNameRef() {
    return createPackageRef(
      getState().lspServerPackageName,
      TypeSpecLspExecutableDownloader.INSTANCE.getPackageDescriptor().getServerPackage()
    );
  }

  public void setLspServerPackageNameRef(@NotNull NodePackageRef value) {
    var refText = extractRefText(value);
    var changed = !getState().lspServerPackageName.equals(refText);

    getState().lspServerPackageName = refText;

    if (changed) {
      TypeSpecLspServerSupportProvider.restartTypeSpecLspServer(project);
    }
  }

  public static TypeSpecServiceSettings getInstance(@NotNull Project project) {
    return project.getService(TypeSpecServiceSettings.class);
  }

  public static class TypeSpecServiceState extends BaseState {
    public TypeSpecServiceMode serviceMode = TypeSpecServiceMode.ENABLED;
    public String lspServerPackageName =  defaultPackageKey;
  }

  public enum TypeSpecServiceMode {
    ENABLED,
    DISABLED
  }
}
