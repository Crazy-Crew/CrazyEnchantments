package com.badbones69.crazyenchantments.paper.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.paper.FusionPaper;
import me.arcaniax.hdb.enums.CategoryEnum;
import me.arcaniax.hdb.object.head.Head;
import java.util.ArrayList;
import java.util.List;

public class HeadManager {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final FusionPaper fusion = this.plugin.getFusion();

    public List<String> getHeads() {
        final List<String> names = new ArrayList<>();

        this.fusion.getHeadDatabaseAPI().ifPresentOrElse(api -> {
            for (final CategoryEnum category : values()) {
                final List<Head> heads = api.getHeads(category);

                for (final Head head : heads) {
                    final String itemStack = head.b64;
                    final String name = head.name;

                    names.add("%s,%s".formatted(name, itemStack));
                }
            }
        }, () -> this.fusion.log("warn", "HeadDatabaseAPI is not present on the server!"));

        return names;
    }

    public CategoryEnum[] values() {
        return CategoryEnum.values();
    }
}