package xfacthd.framedblocks.api.camo.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.util.Utils;

public abstract class AbstractBlockCamoContainer<T extends AbstractBlockCamoContainer<T>> extends CamoContainer<BlockCamoContent, T>
{
    protected AbstractBlockCamoContainer(BlockState state)
    {
        super(new BlockCamoContent(state));
    }

    public final BlockState getState()
    {
        return content.getState();
    }

    @Override
    public boolean canRotateCamo()
    {
        return Utils.getRotatableProperty(content.getState()) != null;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public T rotateCamo()
    {
        BlockState state = content.getState();
        Property<?> prop = Utils.getRotatableProperty(state);
        if (prop != null)
        {
            return getFactory().copyContainerWithState((T) this, state.cycle(prop));
        }
        return null;
    }

    @Override
    public abstract AbstractBlockCamoContainerFactory<T> getFactory();
}
