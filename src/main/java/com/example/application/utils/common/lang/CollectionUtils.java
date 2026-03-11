package com.example.application.utils.common.lang;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Objects;

public class CollectionUtils {

    private CollectionUtils() {
        /* Hidden constructor */
    }

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static Collection<?> requireNotEmpty(@Nullable Collection<?> collection) {
        return requireNotEmpty(collection, "Collection is null or empty");
    }

    public static <T extends Collection<?>> T requireNotEmpty(@Nullable T collection, @NotNull String message) {
        if (collection == null || collection.isEmpty())
            throw new AssertionError(Objects.requireNonNull(message, "message"));

        return collection;
    }

}
