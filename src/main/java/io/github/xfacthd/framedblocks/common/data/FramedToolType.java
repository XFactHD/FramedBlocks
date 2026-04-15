package io.github.xfacthd.framedblocks.common.data;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.neoforged.neoforge.common.ItemAbility;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public enum FramedToolType {
    HAMMER("framed_hammer", FramedConstants.ItemAbilities.ACTION_WRENCH_EMPTY),
    WRENCH("framed_wrench", FramedConstants.ItemAbilities.ACTION_WRENCH_ROTATE),
    BLUEPRINT("framed_blueprint", null),
    KEY("framed_key", null),
    SCREWDRIVER("framed_screwdriver", FramedConstants.ItemAbilities.ACTION_WRENCH_CONFIGURE),
    AXE("framed_axe", null),
    PAINT_ROLLER("paint_roller", null),
    ;

    private final String name;
    @Nullable
    private final ItemAbility ability;

    FramedToolType(String name, @Nullable ItemAbility ability) {
        this.name = name;
        this.ability = ability;
    }

    public String getName() {
        return name;
    }

    public boolean hasAbility() {
        return ability != null;
    }

    public ItemAbility getAbility() {
        return Objects.requireNonNull(ability);
    }
}
