package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDividedPanelBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean vertical;

    public FramedDividedPanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DIVIDED_PANEL.get(), pos, state);
        this.vertical = state.getBlock() == FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.get();
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        if (vertical)
        {
            Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
            if (face == dir.getClockWise())
            {
                return true;
            }
            if (face == dir.getCounterClockWise())
            {
                return false;
            }

            double xz = Utils.fractionInDir(hit.getLocation(), dir.getClockWise());
            return xz > .5D;
        }
        else
        {
            return switch (face)
            {
                case UP -> true;
                case DOWN -> false;
                default ->
                {
                    Vec3 frac = Utils.fraction(hit.getLocation());
                    yield frac.y > .5D;
                }
            };
        }
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return vertical ? DoubleSoundMode.EITHER : DoubleSoundMode.SECOND;
    }

    @Override
    public CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        if (edge == null)
        {
            return EMPTY_GETTER;
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (edge == facing)
        {
            if ((!vertical && side == Direction.DOWN) || (vertical && side == facing.getCounterClockWise()))
            {
                return this::getCamo;
            }
            if ((!vertical && side == Direction.UP) || (vertical && side == facing.getClockWise()))
            {
                return this::getCamoTwo;
            }
        }
        else if (side == facing)
        {
            if ((!vertical && edge == Direction.DOWN) || (vertical && edge == facing.getCounterClockWise()))
            {
                return this::getCamo;
            }
            if ((!vertical && edge == Direction.UP) || (vertical && edge == facing.getClockWise()))
            {
                return this::getCamoTwo;
            }
        }
        return EMPTY_GETTER;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        if (side == getBlockState().getValue(FramedProperties.FACING_HOR))
        {
            return getCamo().isSolid(level, worldPosition) && getCamoTwo().isSolid(level, worldPosition);
        }
        return false;
    }
}
