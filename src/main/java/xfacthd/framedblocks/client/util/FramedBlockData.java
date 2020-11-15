package xfacthd.framedblocks.client.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class FramedBlockData implements IModelData
{
    public static final ModelProperty<World> WORLD = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS = new ModelProperty<>();
    public static final ModelProperty<BlockState> CAMO = new ModelProperty<>();

    private World world = null;
    private BlockPos pos = BlockPos.ZERO;
    private BlockState camoState = Blocks.AIR.getDefaultState();

    @Override
    public boolean hasProperty(ModelProperty<?> prop) { return prop == CAMO; }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getData(ModelProperty<T> prop)
    {
        if (prop == CAMO) { return (T)camoState; }
        if (prop == WORLD) { return (T)world; }
        if (prop == POS) { return (T)pos; }
        return null;
    }

    @Override
    public <T> T setData(ModelProperty<T> prop, T data)
    {
        if (prop == CAMO) { camoState = (BlockState)data; }
        else if (prop == WORLD) { world = (World)data; }
        else if (prop == POS) { pos = (BlockPos)data; }
        return data;
    }


}