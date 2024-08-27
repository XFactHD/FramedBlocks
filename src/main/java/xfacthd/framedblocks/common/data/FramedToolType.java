package xfacthd.framedblocks.common.data;

import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Objects;

public enum FramedToolType
{
    HAMMER("framed_hammer", Utils.ACTION_WRENCH_EMPTY),
    WRENCH("framed_wrench", Utils.ACTION_WRENCH_ROTATE),
    BLUEPRINT("framed_blueprint", null),
    KEY("framed_key", null),
    SCREWDRIVER("framed_screwdriver", Utils.ACTION_WRENCH_CONFIGURE),
    ;

    private final String name;
    @Nullable
    private final ItemAbility ability;

    FramedToolType(String name, @Nullable ItemAbility ability)
    {
        this.name = name;
        this.ability = ability;
    }

    public String getName()
    {
        return name;
    }

    public boolean hasAbility()
    {
        return ability != null;
    }

    public ItemAbility getAbility()
    {
        return Objects.requireNonNull(ability);
    }
}
