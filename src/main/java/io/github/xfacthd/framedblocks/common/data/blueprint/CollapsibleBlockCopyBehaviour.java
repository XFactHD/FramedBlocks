package io.github.xfacthd.framedblocks.common.data.blueprint;

import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.component.CollapsibleBlockData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

public final class CollapsibleBlockCopyBehaviour extends DummyDataHandlingCopyBehaviour<CollapsibleBlockData>
{
    public CollapsibleBlockCopyBehaviour()
    {
        super(FBContent.DC_TYPE_COLLAPSIBLE_BLOCK_DATA.value(), CollapsibleBlockData.EMPTY);
    }

    @Override
    public Set<Property<?>> getPropertiesToCopy(BlockState state)
    {
        return Set.of(PropertyHolder.NULLABLE_FACE, PropertyHolder.ROTATE_SPLIT_LINE);
    }
}
