package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDividedSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDividedSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DIVIDED_SLAB.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (face == dir.getOpposite())
        {
            return false;
        }
        if (face == dir)
        {
            return true;
        }

        double xz = Utils.fractionInDir(hit.getLocation(), dir);
        return xz > .5D;
    }

    @Override
    protected DoubleSoundMode calculateSoundMode()
    {
        return DoubleSoundMode.EITHER;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        if (edge == null)
        {
            return EMPTY_GETTER;
        }

        Direction dirOne = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = getBlockState().getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
        if (edge == dirTwo)
        {
            if (side == dirOne)
            {
                return this::getCamo;
            }
            if (side == dirOne.getOpposite())
            {
                return this::getCamoTwo;
            }
        }
        else if (side == dirTwo)
        {
            if (edge == dirOne)
            {
                return this::getCamo;
            }
            if (edge == dirOne.getOpposite())
            {
                return this::getCamoTwo;
            }
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }
}
