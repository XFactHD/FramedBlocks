package xfacthd.framedblocks.api.model.wrapping.statemerger;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

final class IgnoreAllStateMerger implements StateMerger
{
    @Override
    public BlockState apply(BlockState state)
    {
        return state.getBlock().defaultBlockState();
    }

    @Override
    public Set<Property<?>> getHandledProperties(Holder<Block> block)
    {
        return Set.copyOf(block.value().getStateDefinition().getProperties());
    }
}
