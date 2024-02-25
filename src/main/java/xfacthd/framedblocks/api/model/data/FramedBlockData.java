package xfacthd.framedblocks.api.model.data;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.*;

public final class FramedBlockData
{
    public static final ModelProperty<FramedBlockData> PROPERTY = new ModelProperty<>();
    public static final ModelProperty<ModelData> CAMO_DATA = new ModelProperty<>();
    private static final boolean[] NO_CULLED_FACES = new boolean[6];

    private final BlockState camoState;
    private final byte hidden;
    private final boolean altModel;
    private final boolean reinforced;

    public FramedBlockData(BlockState camoState, boolean altModel)
    {
        this(camoState, NO_CULLED_FACES, altModel, false);
    }

    public FramedBlockData(BlockState camoState, boolean[] hidden, boolean altModel, boolean reinforced)
    {
        this.camoState = camoState;
        byte mask = 0;
        for (int i = 0; i < hidden.length; i++)
        {
            if (hidden[i])
            {
                mask |= (byte) (1 << i);
            }
        }
        this.hidden = mask;
        this.altModel = altModel;
        this.reinforced = reinforced;
    }

    public BlockState getCamoState()
    {
        return camoState;
    }

    public boolean isSideHidden(Direction side)
    {
        return (hidden & (1 << side.ordinal())) != 0;
    }

    public boolean useAltModel()
    {
        return altModel;
    }

    public boolean isReinforced()
    {
        return reinforced;
    }
}
