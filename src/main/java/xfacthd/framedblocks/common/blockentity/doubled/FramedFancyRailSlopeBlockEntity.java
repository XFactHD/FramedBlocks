package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;
import xfacthd.framedblocks.common.util.FramedUtils;

public class FramedFancyRailSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedFancyRailSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_FANCY_RAIL_SLOPE.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        return side == Direction.UP || side == getFacing().getOpposite();
    }

    @Override
    protected DoubleSoundMode calculateSoundMode()
    {
        return DoubleSoundMode.FIRST;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        return this::getCamo;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        if (side == Direction.DOWN || side == getFacing())
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoContainer getCamo(BlockState state)
    {
        //The primary camo is the only camo needed in skip predicates
        return getCamo();
    }

    @Override
    protected boolean isCamoSolid()
    {
        CamoContainer camo = getCamo();
        return !camo.isEmpty() && camo.isSolid(level, worldPosition);
    }

    private Direction getFacing()
    {
        RailShape shape = getBlockState().getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        return FramedUtils.getDirectionFromAscendingRailShape(shape);
    }
}
