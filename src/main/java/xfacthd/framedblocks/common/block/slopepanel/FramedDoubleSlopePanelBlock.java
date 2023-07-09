package xfacthd.framedblocks.common.block.slopepanel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleSlopePanelBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedDoubleSlopePanelBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleSlopePanelBlock()
    {
        super(BlockType.FRAMED_DOUBLE_SLOPE_PANEL);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.FRONT, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.ROTATION, PropertyHolder.FRONT,
                BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedSlopePanelBlock.getStateForPlacement(this, context);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (face.getAxis() == dir.getAxis())
        {
            HorizontalRotation blockRot = state.getValue(PropertyHolder.ROTATION);
            return state.setValue(PropertyHolder.ROTATION, blockRot.rotate(rot));
        }
        else if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(PropertyHolder.FRONT);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return FramedSlopePanelBlock.mirrorPanel(state, mirror);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        boolean front = state.getValue(PropertyHolder.FRONT);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        BlockState defState = FBContent.BLOCK_FRAMED_SLOPE_PANEL.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation)
                        .setValue(PropertyHolder.FRONT, front)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.ROTATION, rotation.isVertical() ? rotation.getOpposite() : rotation)
                        .setValue(PropertyHolder.FRONT, !front)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return switch (state.getValue(PropertyHolder.ROTATION))
        {
            case LEFT, RIGHT -> DoubleBlockTopInteractionMode.EITHER;
            case UP -> DoubleBlockTopInteractionMode.SECOND;
            case DOWN -> DoubleBlockTopInteractionMode.FIRST;
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlopePanelBlockEntity(pos, state);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shape = box(0, 0, 0, 16, 16, 8);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            if (state.getValue(PropertyHolder.FRONT))
            {
                dir = dir.getOpposite();
            }
            builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
        }

        return ShapeProvider.of(builder.build());
    }

    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL.get().defaultBlockState();
    }
}
