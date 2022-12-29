package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
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
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.RIGHT, BlockStateProperties.WATERLOGGED);
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
    public BlockItem createItemBlock()
    {
        BlockItem item = new VerticalAndWallBlockItem(
                this,
                FBContent.blockFramedVerticalDoubleHalfSlope.get(),
                new Item.Properties().tab(FramedBlocks.FRAMED_TAB)
        );
        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleHalfSlopeBlockEntity(pos, state);
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
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

        return builder.build();
    }
}