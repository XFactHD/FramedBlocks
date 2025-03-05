package xfacthd.framedblocks.common.compat.create;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.block.sign.AbstractFramedSignBlock;
import xfacthd.framedblocks.common.data.BlockType;

public final class FramedBlockMovementChecks
{
    private static BlockMovementChecks.CheckResult result(boolean value)
    {
        return BlockMovementChecks.CheckResult.of(value);
    }

    public static final class FramedBlockAttachedCheck implements BlockMovementChecks.AttachedCheck {
        @Override
        public BlockMovementChecks.CheckResult isBlockAttachedTowards(BlockState state, Level level, BlockPos pos, Direction side)
        {
            if (state.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
            {
                return switch (type)
                {
                    case FRAMED_SIGN, FRAMED_FLOWER_POT -> result(side == Direction.DOWN);
                    case FRAMED_FLOOR_BOARD -> result(side == (state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN));
                    case FRAMED_WALL_SIGN -> result(state.getValue(FramedProperties.FACING_HOR) == side.getOpposite());
                    case FRAMED_WALL_BOARD -> result(state.getValue(FramedProperties.FACING_HOR) == side);
                    default -> BlockMovementChecks.CheckResult.PASS;
                };
            }
            return BlockMovementChecks.CheckResult.PASS;
        }
    }

    public static final class FramedBlockBrittleCheck implements BlockMovementChecks.BrittleCheck {
        @Override
        public BlockMovementChecks.CheckResult isBrittle(BlockState state)
        {
            Block block = state.getBlock();
            if (block instanceof AbstractFramedSignBlock)
            {
                return BlockMovementChecks.CheckResult.SUCCESS;
            }
            return BlockMovementChecks.CheckResult.PASS;
        }
    }

}
