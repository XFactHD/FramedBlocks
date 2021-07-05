package xfacthd.framedblocks.client.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.*;

public class FramedBlockData extends ModelDataMap
{
    public static final ModelProperty<World> WORLD = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS = new ModelProperty<>();
    public static final ModelProperty<BlockState> CAMO = new ModelProperty<>();

    private World world = null;
    private BlockPos pos = BlockPos.ZERO;
    private BlockState camoState = Blocks.AIR.defaultBlockState();

    @Override
    public boolean hasProperty(ModelProperty<?> prop) { return prop == CAMO || super.hasProperty(prop); }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getData(ModelProperty<T> prop)
    {
        if (prop == CAMO) { return (T)camoState; }
        if (prop == WORLD) { return (T)world; }
        if (prop == POS) { return (T)pos; }
        return super.getData(prop);
    }

    @Override
    public <T> T setData(ModelProperty<T> prop, T data)
    {
        if (prop == CAMO) { camoState = (BlockState)data; }
        else if (prop == WORLD) { world = (World)data; }
        else if (prop == POS) { pos = (BlockPos)data; }
        else { return super.setData(prop, data); }
        return data;
    }

    public void setWorld(World world) { this.world = world; }

    public void setPos(BlockPos pos) { this.pos = pos; }

    public void setCamoState(BlockState camoState) { this.camoState = camoState; }

    public World getWorld() { return world; }

    public BlockPos getPos() { return pos; }

    public BlockState getCamoState() { return camoState; }
}