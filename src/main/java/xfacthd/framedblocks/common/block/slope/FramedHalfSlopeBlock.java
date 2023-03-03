package xfacthd.framedblocks.common.block.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.item.VerticalAndWallBlockItem;

public class FramedHalfSlopeBlock extends FramedBlock
{
    public FramedHalfSlopeBlock()
    {
        super(BlockType.FRAMED_HALF_SLOPE);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.RIGHT, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.RIGHT,
                BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction side = context.getClickedFace();
        Vec3 hitVec = context.getClickLocation();

        Direction dir = context.getHorizontalDirection();
        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, dir);

        boolean right = Utils.fractionInDir(hitVec, dir.getClockWise()) > .5D;
        state = state.setValue(PropertyHolder.RIGHT, right);

        state = withTop(state, side, hitVec);
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
        if (Utils.isY(face) || face == facing.getOpposite())
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(facing));
        }
        else if (face == facing)
        {
            return state.cycle(PropertyHolder.RIGHT);
        }
        else
        {
            return state.cycle(FramedProperties.TOP);
        }
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
    public Pair<IFramedBlock, BlockItem> createItemBlock()
    {
        return Pair.of(
                this,
                new VerticalAndWallBlockItem(
                        this,
                        FBContent.blockFramedVerticalHalfSlope.get(),
                        new Item.Properties().tab(FramedBlocks.FRAMED_TAB)
                )
        );
    }



    public static final VoxelShape SHAPE_BOTTOM_LEFT = Shapes.or(
            box(0,    0, 0, 8,   .5,   16),
            box(0,   .5, 0, 8,    4, 15.5),
            box(0,    4, 0, 8,    8,   12),
            box(0,    8, 0, 8,   12,    8),
            box(0,   12, 0, 8, 15.5,    4),
            box(0, 15.5, 0, 8,   16,   .5)
    ).optimize();

    public static final VoxelShape SHAPE_BOTTOM_RIGHT = Shapes.or(
            box(8,    0, 0, 16,   .5,   16),
            box(8,   .5, 0, 16,    4, 15.5),
            box(8,    4, 0, 16,    8,   12),
            box(8,    8, 0, 16,   12,    8),
            box(8,   12, 0, 16, 15.5,    4),
            box(8, 15.5, 0, 16,   16,   .5)
    ).optimize();

    public static final VoxelShape SHAPE_TOP_LEFT = Shapes.or(
            box(0,    0, 0, 8,   .5,   .5),
            box(0,   .5, 0, 8,    4,    4),
            box(0,    4, 0, 8,    8,    8),
            box(0,    8, 0, 8,   12,   12),
            box(0,   12, 0, 8, 15.5, 15.5),
            box(0, 15.5, 0, 8,   16,   16)
    ).optimize();

    public static final VoxelShape SHAPE_TOP_RIGHT = Shapes.or(
            box(8,    0, 0, 16,   .5,   .5),
            box(8,   .5, 0, 16,    4,    4),
            box(8,    4, 0, 16,    8,    8),
            box(8,    8, 0, 16,   12,   12),
            box(8,   12, 0, 16, 15.5, 15.5),
            box(8, 15.5, 0, 16,   16,   16)
    ).optimize();

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            boolean top = state.getValue(FramedProperties.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);

            VoxelShape shape;
            if (top)
            {
                shape = right ? SHAPE_TOP_RIGHT : SHAPE_TOP_LEFT;
            }
            else
            {
                shape = right ? SHAPE_BOTTOM_RIGHT : SHAPE_BOTTOM_LEFT;
            }

            builder.put(
                    state,
                    Utils.rotateShape(
                            Direction.NORTH,
                            state.getValue(FramedProperties.FACING_HOR),
                            shape
                    )
            );
        }

        return builder.build();
    }
}
