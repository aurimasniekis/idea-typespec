package com.aurimasniekis.idea.typespec;

import com.intellij.DynamicBundle;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class TypeSpecBundle extends DynamicBundle {

  public static final TypeSpecBundle INSTANCE = new TypeSpecBundle();

  public TypeSpecBundle() {
    super("messages.TypeSpecBundle");
  }

  public static @NotNull String message(
    @PropertyKey(resourceBundle = "messages.TypeSpecBundle") @NotNull String key, Object... params
  ) {
    return INSTANCE.getMessage(key, params);
  }

  public static @NotNull Supplier<String> messagePointer(
    @PropertyKey(resourceBundle = "messages.TypeSpecBundle") @NotNull String key, Object... params
  ) {
    return INSTANCE.getLazyMessage(key, params);
  }
}
