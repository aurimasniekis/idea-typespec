package com.aurimasniekis.idea.typespec.editor;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.codeInsight.completion.WordCompletionContributor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.util.ProcessingContext;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import com.aurimasniekis.idea.typespec.TypeSpecLanguage;

public class TypeSpecCompletionContributor extends CompletionContributor implements DumbAware {

  public TypeSpecCompletionContributor() {
    extend(CompletionType.BASIC,
           psiElement().inFile(psiFile().withLanguage(TypeSpecLanguage.INSTANCE)),
           new CompletionProvider<>() {
             @Override
             protected void addCompletions(
               @NotNull CompletionParameters parameters,
               @NotNull ProcessingContext context,
               @NotNull CompletionResultSet result
             ) {
               String              prefix              = CompletionUtil.findJavaIdentifierPrefix(
                 parameters);
               CompletionResultSet resultSetWithPrefix = result.withPrefixMatcher(prefix);

               WordCompletionContributor.addWordCompletionVariants(
                 resultSetWithPrefix,
                 parameters,
                 Collections.emptySet(),
                 true
               );
             }
           }
    );
  }
}
