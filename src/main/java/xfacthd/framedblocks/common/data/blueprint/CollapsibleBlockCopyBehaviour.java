package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.component.CollapsibleBlockData;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;

public final class CollapsibleBlockCopyBehaviour extends DummyDataHandlingCopyBehaviour<CollapsibleBlockData>
{
    public CollapsibleBlockCopyBehaviour()
    {
        super(FBContent.DC_TYPE_COLLAPSIBLE_BLOCK_DATA.value(), CollapsibleBlockData.EMPTY);
    }

    @Override
    public List<Property<?>> getPropertiesToCopy(BlockState state)
    {
        return List.of(PropertyHolder.NULLABLE_FACE, PropertyHolder.ROTATE_SPLIT_LINE);
    }
}
