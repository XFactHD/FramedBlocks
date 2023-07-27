package xfacthd.framedblocks.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.interactive.FramedWallHangingSignBlock;

public class FramedHangingSignItem extends FramedSignItem
{
    public FramedHangingSignItem()
    {
        super(FBContent.BLOCK_FRAMED_HANGING_SIGN, FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN, Direction.UP);
    }

    @Override
    protected boolean canPlace(LevelReader level, BlockState state, BlockPos pos)
    {
        if (state.getBlock() instanceof FramedWallHangingSignBlock && !FramedWallHangingSignBlock.canPlace(state, level, pos))
        {
            return false;
        }
        return super.canPlace(level, state, pos);
    }
}
