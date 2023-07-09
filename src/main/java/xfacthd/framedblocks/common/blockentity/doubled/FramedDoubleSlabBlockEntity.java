package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedDoubleSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_DOUBLE_FRAMED_SLAB.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        if (hit.getDirection() == Direction.DOWN)
        {
            return false;
        }
        return hit.getDirection() == Direction.UP || Mth.frac(hit.getLocation().y()) >= .5F;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        if (side == Direction.UP)
        {
            return this::getCamoTwo;
        }
        else if (side == Direction.DOWN)
        {
            return this::getCamo;
        }
        else if (edge == Direction.UP)
        {
            return this::getCamoTwo;
        }
        else if (edge == Direction.DOWN)
        {
            return this::getCamo;
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        return switch (side)
        {
            case DOWN -> SolidityCheck.FIRST;
            case UP -> SolidityCheck.SECOND;
            default -> SolidityCheck.BOTH;
        };
    }
}