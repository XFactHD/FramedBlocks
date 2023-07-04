package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedFlatInverseDoubleSlopeSlabCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedFlatInverseDoubleSlopeSlabCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if (hit.getDirection() == Direction.DOWN)
        {
            return top;
        }
        else if (hit.getDirection() == Direction.UP)
        {
            return !top;
        }
        return (Mth.frac(hit.getLocation().y()) >= .5F) != top;
    }

    @Override
    protected DoubleBlockTopInteractionMode calculateTopInteractionMode()
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        return top ? DoubleBlockTopInteractionMode.FIRST : DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            boolean top = getBlockState().getValue(FramedProperties.TOP);

            if ((!top && edge == Direction.DOWN) || (top && edge == Direction.UP))
            {
                return this::getCamo;
            }
        }

        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        return SolidityCheck.NONE;
    }
}
