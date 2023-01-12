package xfacthd.framedblocks.api.model.data;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.*;

public final class FramedBlockData
{
    public static final ModelProperty<FramedBlockData> PROPERTY = new ModelProperty<>();
    public static final ModelProperty<ModelData> CAMO_DATA = new ModelProperty<>();

    private final boolean[] hidden = new boolean[6];
    private BlockState camoState = Blocks.AIR.defaultBlockState();

    public FramedBlockData() { }

    public void setCamoState(BlockState camoState) { this.camoState = camoState; }

    public void setSideHidden(Direction side, boolean hide) { hidden[side.ordinal()] = hide; }

    public BlockState getCamoState() { return camoState; }

    public boolean isSideHidden(Direction side) { return hidden[side.ordinal()]; }
}