package xfacthd.framedblocks.common.block.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.FramedDoubleHalfSlopeBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.item.VerticalAndWallBlockItem;

public class FramedDoubleHalfSlopeBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleHalfSlopeBlock()
    {
        super(BlockType.FRAMED_DOUBLE_HALF_SLOPE);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.RIGHT, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.RIGHT, BlockStateProperties.WATERLOGGED,
                FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Vec3 hitVec = context.getClickLocation();

        Direction dir = context.getHorizontalDirection();
        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, dir);

        boolean right = Utils.fractionInDir(hitVec, dir.getClockWise()) > .5D;
        state = state.setValue(PropertyHolder.RIGHT, right);

        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (rot == Rotation.NONE) { return state; }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(facing));
        }
        else if (face.getAxis() == facing.getAxis())
        {
            return state.cycle(PropertyHolder.RIGHT);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, Direction.UP, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE) { return state; }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if ((mirror == Mirror.FRONT_BACK && Utils.isX(dir)) || (mirror == Mirror.LEFT_RIGHT && Utils.isZ(dir)))
        {
            state = state.setValue(FramedProperties.FACING_HOR, dir.getOpposite());
        }
        return state.cycle(PropertyHolder.RIGHT);
    }

    @Override
    public BlockItem createBlockItem()
    {
        return new VerticalAndWallBlockItem(
                this,
                FBContent.blockFramedVerticalDoubleHalfSlope.get(),
                new Item.Properties()
        );
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        BlockState defState = FBContent.blockFramedHalfSlope.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, false)
                        .setValue(PropertyHolder.RIGHT, right)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, true)
                        .setValue(PropertyHolder.RIGHT, !right)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleHalfSlopeBlockEntity(pos, state);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shape = box(0, 0, 0, 8, 16, 16);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            if (state.getValue(PropertyHolder.RIGHT))
            {
                dir = dir.getOpposite();
            }
            builder.put(
                    state,
                    Utils.rotateShape(
                            Direction.NORTH,
                            dir,
                            shape
                    )
            );
        }

        return ShapeProvider.of(builder.build());
    }

    public static BlockState itemSource()
    {
        return FBContent.blockFramedDoubleHalfSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
