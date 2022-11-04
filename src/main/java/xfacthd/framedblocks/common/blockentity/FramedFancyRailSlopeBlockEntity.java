package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.DoubleSoundMode;
import xfacthd.framedblocks.common.util.FramedUtils;

public class FramedFancyRailSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedFancyRailSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedFancyRailSlope.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        return side == Direction.UP || side == getFacing().getOpposite();
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.FIRST; }

    @Override
    public boolean isSolidSide(Direction side)
    {
        if (side == Direction.DOWN || side == getFacing())
        {
            //noinspection ConstantConditions
            return getCamoState().isSolidRender(level, worldPosition);
        }
        return false;
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        BlockType type = (BlockType) getBlockType();
        return getBlockPair(
                type,
                state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE),
                type != BlockType.FRAMED_FANCY_RAIL_SLOPE && state.getValue(BlockStateProperties.POWERED)
        );
    }

    private Direction getFacing()
    {
        RailShape shape = getBlockState().getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        return FramedUtils.getDirectionFromAscendingRailShape(shape);
    }



    public static Tuple<BlockState, BlockState> getBlockPair(BlockType type, RailShape shape, boolean powered)
    {
        BlockState slopeState = FBContent.blockFramedSlope.get().defaultBlockState();
        BlockState railState = (switch(type)
        {
            case FRAMED_FANCY_RAIL_SLOPE -> FBContent.blockFramedFancyRail;
            case FRAMED_FANCY_POWERED_RAIL_SLOPE -> FBContent.blockFramedFancyPoweredRail;
            case FRAMED_FANCY_DETECTOR_RAIL_SLOPE -> FBContent.blockFramedFancyDetectorRail;
            case FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE -> FBContent.blockFramedFancyActivatorRail;
            default -> throw new IllegalArgumentException("Invalid block type");
        }).get().defaultBlockState();

        if (type != BlockType.FRAMED_FANCY_RAIL_SLOPE)
        {
            railState = railState.setValue(BlockStateProperties.POWERED, powered);
        }

        EnumProperty<RailShape> shapeProp = getShapeProperty(type);
        Direction facing = FramedUtils.getDirectionFromAscendingRailShape(shape);

        return new Tuple<>(
                slopeState.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM)
                        .setValue(FramedProperties.FACING_HOR, facing),
                railState.setValue(shapeProp, shape)
        );
    }

    private static EnumProperty<RailShape> getShapeProperty(BlockType type)
    {
        if (type == BlockType.FRAMED_FANCY_RAIL_SLOPE)
        {
            return BlockStateProperties.RAIL_SHAPE;
        }
        else
        {
            return BlockStateProperties.RAIL_SHAPE_STRAIGHT;
        }
    }
}
