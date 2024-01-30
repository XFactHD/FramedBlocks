package xfacthd.framedblocks.common.block.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import xfacthd.framedblocks.common.blockentity.doubled.slope.FramedDoubleCornerBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedDoubleCornerBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleCornerBlock()
    {
        super(BlockType.FRAMED_DOUBLE_CORNER);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.CORNER_TYPE, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx).withHorizontalFacingAndCornerType().build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction side, Rotation rot)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type.isHorizontal())
        {
            return state.setValue(PropertyHolder.CORNER_TYPE, type.rotate(rot));
        }

        return rotate(state, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type.isHorizontal())
        {
            BlockState newState = Utils.mirrorFaceBlock(state, mirror);
            if (newState != state)
            {
                return newState.setValue(PropertyHolder.CORNER_TYPE, type.horizontalOpposite());
            }
            return state;
        }
        else
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.value().defaultBlockState()
                        .setValue(PropertyHolder.CORNER_TYPE, type)
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.BLOCK_FRAMED_CORNER_SLOPE.value().defaultBlockState()
                        .setValue(PropertyHolder.CORNER_TYPE, type.verticalOpposite())
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type == CornerType.BOTTOM)
        {
            return DoubleBlockTopInteractionMode.SECOND;
        }
        else if (type.isTop())
        {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = type.isTop() ? Direction.UP : Direction.DOWN;

        if (type.isHorizontal())
        {
            Direction dirThree = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
            if (side == dir || side == dirTwo || side == dirThree)
            {
                return CamoGetter.FIRST;
            }
            if (side == dir.getOpposite())
            {
                return CamoGetter.SECOND;
            }
            if (side == dirTwo.getOpposite())
            {
                if (edge == dir || edge == dirThree)
                {
                    return CamoGetter.FIRST;
                }
                if (edge == dir.getOpposite() || edge == dirThree.getOpposite())
                {
                    return CamoGetter.SECOND;
                }
                return CamoGetter.NONE;
            }
            if (side == dirThree.getOpposite())
            {
                if (edge == dir || edge == dirTwo)
                {
                    return CamoGetter.FIRST;
                }
                if (edge == dir.getOpposite() || edge == dirTwo.getOpposite())
                {
                    return CamoGetter.SECOND;
                }
                return CamoGetter.NONE;
            }
        }
        else
        {
            if (side == dir || side == dir.getCounterClockWise() || side == dirTwo)
            {
                return CamoGetter.FIRST;
            }
            if (side == dirTwo.getOpposite())
            {
                return CamoGetter.SECOND;
            }
            if (side == dir.getClockWise())
            {
                if (edge == dirTwo || edge == dir)
                {
                    return CamoGetter.FIRST;
                }
                if (edge == dirTwo.getOpposite() || edge == dir.getOpposite())
                {
                    return CamoGetter.SECOND;
                }
                return CamoGetter.NONE;
            }
            if (side == dir.getOpposite())
            {
                if (edge == dirTwo || edge == dir.getCounterClockWise())
                {
                    return CamoGetter.FIRST;
                }
                if (edge == dirTwo.getOpposite() || edge == dir.getClockWise())
                {
                    return CamoGetter.SECOND;
                }
                return CamoGetter.NONE;
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        if (type.isHorizontal())
        {
            if (side == facing)
            {
                return SolidityCheck.FIRST;
            }
            else if (side == facing.getOpposite())
            {
                return SolidityCheck.SECOND;
            }
            else if ((!type.isRight() && side == facing.getCounterClockWise()) || (type.isRight() && side == facing.getClockWise()))
            {
                return SolidityCheck.FIRST;
            }
            else if ((!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP))
            {
                return SolidityCheck.FIRST;
            }
        }
        else
        {
            if (side == facing || side == facing.getCounterClockWise())
            {
                return SolidityCheck.FIRST;
            }
            else if ((!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP))
            {
                return SolidityCheck.FIRST;
            }
            else if ((!type.isTop() && side == Direction.UP) || (type.isTop() && side == Direction.DOWN))
            {
                return SolidityCheck.SECOND;
            }
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleCornerBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}