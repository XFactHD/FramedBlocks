package xfacthd.framedblocks.api.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.model.data.*;

import java.lang.ref.WeakReference;

public class FramedBlockData extends ModelDataMap
{
    public static final ModelProperty<BlockAndTintGetter> LEVEL = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS = new ModelProperty<>();
    public static final ModelProperty<BlockState> CAMO = new ModelProperty<>();

    private final boolean ghostData;
    private final boolean[] hidden = new boolean[6];
    private WeakReference<BlockAndTintGetter> level = new WeakReference<>(null);
    private BlockPos pos = BlockPos.ZERO;
    private BlockState camoState = Blocks.AIR.defaultBlockState();

    public FramedBlockData(boolean ghostData) { this.ghostData = ghostData; }

    @Override
    public boolean hasProperty(ModelProperty<?> prop) { return prop == CAMO || super.hasProperty(prop); }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getData(ModelProperty<T> prop)
    {
        if (prop == CAMO) { return (T)camoState; }
        if (prop == LEVEL) { return (T) level.get(); }
        if (prop == POS) { return (T)pos; }
        return super.getData(prop);
    }

    @Override
    public <T> T setData(ModelProperty<T> prop, T data)
    {
        if (prop == CAMO) { camoState = (BlockState)data; }
        else if (prop == LEVEL) { level = new WeakReference<>((BlockAndTintGetter) data); }
        else if (prop == POS) { pos = (BlockPos)data; }
        else { return super.setData(prop, data); }
        return data;
    }

    public boolean isGhostData() { return ghostData; }

    public void setCamoState(BlockState camoState) { this.camoState = camoState; }

    public void setSideHidden(Direction side, boolean hide) { hidden[side.ordinal()] = hide; }

    public BlockState getCamoState() { return camoState; }

    public boolean isSideHidden(Direction side) { return hidden[side.ordinal()]; }
}