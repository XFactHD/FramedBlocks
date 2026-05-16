package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.world.level.block.state.BlockState;

public class FramedBookshelfBlock extends FramedBlock {
    public FramedBookshelfBlock(Properties props) {
        super(BlockType.FRAMED_BOOKSHELF, props);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.UNSUPPORTED;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return state;
    }
}
