package com.badbones69.crazyenchantments.paper.api.exceptions;

import org.jetbrains.annotations.NotNull;

public class CrazyException extends IllegalStateException {

    public CrazyException(@NotNull String message, @NotNull Exception exception) {
        super(message, exception);
    }

    public CrazyException(@NotNull String message) {
        super(message);
    }
}