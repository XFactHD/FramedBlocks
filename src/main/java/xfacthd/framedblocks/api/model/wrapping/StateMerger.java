package xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.world.level.block.state.BlockState;

import java.util.function.UnaryOperator;

public interface StateMerger extends UnaryOperator<BlockState>
{
    StateMerger NO_OP = state -> state;

    @Override
    BlockState apply(BlockState state);
}
