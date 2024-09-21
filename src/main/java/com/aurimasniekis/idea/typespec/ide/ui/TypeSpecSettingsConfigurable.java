package com.aurimasniekis.idea.typespec.ide.ui;

import static com.intellij.ui.dsl.builder.UtilsKt.DEFAULT_COMMENT_WIDTH;

import com.intellij.javascript.nodejs.util.NodePackageField;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.UiDslUnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts.ConfigurableName;
import com.intellij.ui.dsl.builder.AlignX;
import com.intellij.ui.dsl.builder.HyperlinkEventAction;
import com.intellij.ui.dsl.builder.MutableProperty;
import com.intellij.ui.dsl.builder.MutablePropertyKt;
import com.intellij.ui.dsl.builder.Panel;
import javax.swing.JLabel;
import org.jetbrains.annotations.NotNull;
import com.aurimasniekis.idea.typespec.TypeSpecBundle;
import com.aurimasniekis.idea.typespec.ide.lsp.TypeSpecLspExecutableDownloader;
import com.aurimasniekis.idea.typespec.ide.lsp.TypeSpecServiceSettings;
import com.aurimasniekis.idea.typespec.ide.lsp.TypeSpecServiceSettings.TypeSpecServiceMode;

public class TypeSpecSettingsConfigurable extends UiDslUnnamedConfigurable.Simple implements
  Configurable {

  protected final Project project;

  protected final TypeSpecServiceSettings settings;

  public TypeSpecSettingsConfigurable(Project project) {
    this.project = project;
    this.settings = TypeSpecServiceSettings.getInstance(project);
  }

  @Override
  public @ConfigurableName String getDisplayName() {
    return TypeSpecBundle.message("typespec.settings.configurable.title");
  }

  @Override
  public void createContent(@NotNull Panel panel) {
    panel.group(
      TypeSpecBundle.message("typespec.settings.service.configurable.service.group"),
      true,
      (group) -> {
        group.row(
          TypeSpecBundle.message(
            "typespec.settings.service.configurable.service.languageServerPackage"),
          row -> {
            row
              .cell(TypeSpecLspExecutableDownloader.INSTANCE.createNodePackageField(project))
              .align(AlignX.FILL)
              .bind(
                NodePackageField::getSelectedRef,
                (f, v) -> {
                  if (v != null) {
                    f.setSelectedRef(v);
                  }
                  return null;
                },
                MutablePropertyKt.MutableProperty(
                  settings::getLspServerPackageNameRef,
                  value -> {
                    settings.setLspServerPackageNameRef(value);

                    return null;
                  }
                )
              );

            return null;
          }
        );

        group.buttonsGroup(null, false, btnGroup -> {
          btnGroup.row((JLabel) null, row -> {
            row.radioButton(
                 TypeSpecBundle.message("typespec.settings.service.configurable.service.disabled"),
                 TypeSpecServiceMode.DISABLED
               )
               .comment(
                 TypeSpecBundle.message(
                   "typespec.settings.service.configurable.service.disabled.help"),
                 DEFAULT_COMMENT_WIDTH,
                 HyperlinkEventAction.HTML_HYPERLINK_INSTANCE
               );

            return null;
          });

          btnGroup.row((JLabel) null, row -> {
            row.radioButton(
                 TypeSpecBundle.message("typespec.settings.service.configurable.service.enabled"),
                 TypeSpecServiceMode.ENABLED
               )
               .comment(
                 TypeSpecBundle.message(
                   "typespec.settings.service.configurable.service.enabled.help"),
                 DEFAULT_COMMENT_WIDTH,
                 HyperlinkEventAction.HTML_HYPERLINK_INSTANCE
               );

            return null;
          });

          return null;
        }).bind(
          new MutableProperty<>() {
            @Override
            public TypeSpecServiceMode get() {
              return settings.getServiceMode();
            }

            @Override
            public void set(TypeSpecServiceMode typeSpecServiceMode) {
              settings.setServiceMode(typeSpecServiceMode);
            }
          }
          , TypeSpecServiceMode.class);

        return null;
      }
    );
  }
}
