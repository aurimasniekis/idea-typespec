package com.aurimasniekis.idea.typespec.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.EmptyLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import com.aurimasniekis.idea.typespec.filetype.TypeSpecFileType;
import com.aurimasniekis.idea.typespec.TypeSpecLanguage;

public class TypeSpecParserDefinition implements ParserDefinition {

  private final static IFileElementType FILE_ELEMENT_TYPE = new IFileElementType(
    "TypeSpec",
    TypeSpecLanguage.INSTANCE
  );

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new EmptyLexer();
  }

  @Override
  public @NotNull PsiParser createParser(Project project) {
    return new TypeSpecParser();
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return FILE_ELEMENT_TYPE;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens() {
    return TokenSet.EMPTY;
  }

  @NotNull
  @Override
  public TokenSet getStringLiteralElements() {
    return TokenSet.EMPTY;
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode node) {
    return new TypeSpecPsiElement(node.getElementType());
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new TypeSpecFile(viewProvider);
  }

  @Override
  public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }

  public static class TypeSpecParser implements PsiParser {

    @NotNull
    @Override
    public ASTNode parse(@NotNull IElementType root, PsiBuilder builder) {
      PsiBuilder.Marker mark = builder.mark();
      while (!builder.eof()) {
        builder.advanceLexer();
      }
      mark.done(root);
      return builder.getTreeBuilt();
    }
  }

  public static class TypeSpecPsiElement extends CompositePsiElement {

    protected TypeSpecPsiElement(IElementType type) {
      super(type);
    }
  }

  public static class TypeSpecFile extends PsiFileBase {

    public TypeSpecFile(FileViewProvider provider) {
      super(provider, TypeSpecLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
      return TypeSpecFileType.INSTANCE;
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
      return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }
  }
}
