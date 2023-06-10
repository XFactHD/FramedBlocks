package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.Mth;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

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
    public DoubleSoundMode getSoundMode()
    {
        return DoubleSoundMode.SECOND;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        if (side == Direction.UP)
        {
            return getCamoTwo();
        }
        if (side == Direction.DOWN)
        {
            return getCamo();
        }
        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        if (Utils.isY(side))
        {
            return getCamo(side).isSolid(level, worldPosition);
        }
        return getCamo().isSolid(level, worldPosition) && getCamoTwo().isSolid(level, worldPosition);
    }
}