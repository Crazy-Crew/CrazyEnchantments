package com.badbones69.crazyenchantments.paper.api.exceptions;

import org.jetbrains.annotations.NotNull;

public class CrazyException extends IllegalStateException {

    public CrazyException(@NotNull final String message, @NotNull final Exception exception) {
        super(message, exception);
    }

    public CrazyException(@NotNull final String message) {
        super(message);
    }
}