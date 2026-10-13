package com.ryderbelserion.common.api.sender;

import com.ryderbelserion.fusion.api.objects.FusionKey;
import org.jspecify.annotations.NonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ISenderAdapter<C, S> {

    public abstract UUID getUniqueId(@NonNull final S sender);

    public abstract String getName(@NonNull final S sender);

    public abstract void sendActionBar(@NonNull final S sender, @NonNull final FusionKey id, @NonNull final Map<String, String> placeholders);

    public abstract void sendMessage(@NonNull final S sender, @NonNull final FusionKey id, @NonNull final Map<String, String> placeholders);

    public abstract C getComponent(@NonNull final S sender, @NonNull final FusionKey id, @NonNull final Map<String, String> placeholders);

    public abstract String getMessage(@NonNull final S sender, @NonNull final FusionKey id, @NonNull final Map<String, String> placeholders);

    public String getMessage(@NonNull final S sender, @NonNull final FusionKey id) {
        return getMessage(sender, id, Map.of());
    }

    public C getComponent(@NonNull final S sender, @NonNull final FusionKey id) {
        return getComponent(sender, id, new HashMap<>());
    }

    public void sendActionBar(@NonNull final S sender, @NonNull final FusionKey id) {
        sendActionBar(sender, id, new HashMap<>());
    }

    public void sendMessage(@NonNull final S sender, @NonNull final FusionKey id) {
        sendMessage(sender, id, new HashMap<>());
    }

    public abstract boolean isConsole(S sender);
}