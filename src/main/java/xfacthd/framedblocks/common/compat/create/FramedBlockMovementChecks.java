package xfacthd.framedblocks.common.compat.create;

import com.simibubi.create.content.contraptions.components.structureMovement.BlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.block.AbstractFramedSignBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedBlockMovementChecks implements BlockMovementChecks.AllChecks
{
    @Override
    public BlockMovementChecks.CheckResult isBlockAttachedTowards(BlockState state, Level level, BlockPos pos, Direction side)
    {
        if (state.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
                    {
                        case FRAMED_SIGN, FRAMED_FLOWER_POT, FRAMED_FLOOR_BOARD -> result(side == Direction.DOWN);
                        case FRAMED_WALL_SIGN -> result(state.getValue(FramedProperties.FACING_HOR) == side.getOpposite());
                        case FRAMED_WALL_BOARD -> result(state.getValue(FramedProperties.FACING_HOR) == side);
                        default -> BlockMovementChecks.CheckResult.PASS;
                    };
        }
        return BlockMovementChecks.CheckResult.PASS;
    }

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

    @Override
    public BlockMovementChecks.CheckResult isMovementAllowed(BlockState state, Level level, BlockPos pos)
    {
        return BlockMovementChecks.CheckResult.PASS;
    }

    @Override
    public BlockMovementChecks.CheckResult isMovementNecessary(BlockState state, Level level, BlockPos pos)
    {
        return BlockMovementChecks.CheckResult.PASS;
    }

    @Override
    public BlockMovementChecks.CheckResult isNotSupportive(BlockState state, Direction side)
    {
        return BlockMovementChecks.CheckResult.PASS;
    }



    private static BlockMovementChecks.CheckResult result(boolean value)
    {
        return BlockMovementChecks.CheckResult.of(value);
    }
}
