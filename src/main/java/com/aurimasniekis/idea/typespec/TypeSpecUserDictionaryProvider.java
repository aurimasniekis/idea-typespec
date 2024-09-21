package com.aurimasniekis.idea.typespec;

import com.intellij.spellchecker.dictionary.Dictionary;
import com.intellij.spellchecker.dictionary.RuntimeDictionaryProvider;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypeSpecUserDictionaryProvider implements RuntimeDictionaryProvider {

  @Override
  public Dictionary[] getDictionaries() {
    return new Dictionary[] {
      new Dictionary() {
        @Override
        public @NotNull String getName() {
          return "TypeSpec Plugin";
        }

        @Override
        public @Nullable Boolean contains(@NotNull String word) {
          return word != null && word.equals("typespec");
        }

        @Override
        public @NotNull Set<String> getWords() {
          throw new UnsupportedOperationException();
        }
      }
    };
  }
}
