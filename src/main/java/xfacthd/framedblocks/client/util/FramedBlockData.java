package xfacthd.framedblocks.client.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.IdentityHashMap;
import java.util.Map;

public class FramedBlockData implements IModelData
{
    public static final ModelProperty<World> WORLD = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS = new ModelProperty<>();
    public static final ModelProperty<BlockState> CAMO = new ModelProperty<>();

    private final Map<ModelProperty<?>, Object> backingMap = new IdentityHashMap<>();
    private World world = null;
    private BlockPos pos = BlockPos.ZERO;
    private BlockState camoState = Blocks.AIR.getDefaultState();

    @Override
    public boolean hasProperty(ModelProperty<?> prop) { return prop == CAMO || backingMap.containsKey(prop); }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getData(ModelProperty<T> prop)
    {
        if (prop == CAMO) { return (T)camoState; }
        if (prop == WORLD) { return (T)world; }
        if (prop == POS) { return (T)pos; }
        return (T) backingMap.get(prop);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T setData(ModelProperty<T> prop, T data)
    {
        if (prop == CAMO) { camoState = (BlockState)data; }
        else if (prop == WORLD) { world = (World)data; }
        else if (prop == POS) { pos = (BlockPos)data; }
        return (T) backingMap.put(prop, data);
    }


}