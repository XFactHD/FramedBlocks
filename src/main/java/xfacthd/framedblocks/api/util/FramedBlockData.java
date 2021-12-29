package xfacthd.framedblocks.api.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.*;

public class FramedBlockData extends ModelDataMap
{
    public static final ModelProperty<Level> LEVEL = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS = new ModelProperty<>();
    public static final ModelProperty<BlockState> CAMO = new ModelProperty<>();

    private final boolean ghostData;
    private Level level = null;
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
        if (prop == LEVEL) { return (T) level; }
        if (prop == POS) { return (T)pos; }
        return super.getData(prop);
    }

    @Override
    public <T> T setData(ModelProperty<T> prop, T data)
    {
        if (prop == CAMO) { camoState = (BlockState)data; }
        else if (prop == LEVEL) { level = (Level)data; }
        else if (prop == POS) { pos = (BlockPos)data; }
        else { return super.setData(prop, data); }
        return data;
    }

    public boolean isGhostData() { return ghostData; }

    public void setLevel(Level level) { this.level = level; }

    public void setPos(BlockPos pos) { this.pos = pos; }

    public void setCamoState(BlockState camoState) { this.camoState = camoState; }

    public Level getLevel() { return level; }

    public BlockPos getPos() { return pos; }

    public BlockState getCamoState() { return camoState; }
}