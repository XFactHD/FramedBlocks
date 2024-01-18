package xfacthd.framedblocks.common.block.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
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
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.RIGHT,
                BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacing()
                .withTop()
                .withRight()
                .withWater()
                .build();
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
    public BlockItem createBlockItem()
    {
        return new VerticalAndWallBlockItem(
                this,
                FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.value(),
                new Item.Properties()
        );
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }



    public record ShapeKey(boolean top, boolean right) { }

    public static final ShapeCache<ShapeKey> SHAPES = new ShapeCache<>(map ->
    {
        map.put(new ShapeKey(false, false), ShapeUtils.orUnoptimized(
                box(0,    0, 0, 8,   .5,   16),
                box(0,   .5, 0, 8,    4, 15.5),
                box(0,    4, 0, 8,    8,   12),
                box(0,    8, 0, 8,   12,    8),
                box(0,   12, 0, 8, 15.5,    4),
                box(0, 15.5, 0, 8,   16,   .5)
        ));

        map.put(new ShapeKey(false, true), ShapeUtils.orUnoptimized(
                box(8,    0, 0, 16,   .5,   16),
                box(8,   .5, 0, 16,    4, 15.5),
                box(8,    4, 0, 16,    8,   12),
                box(8,    8, 0, 16,   12,    8),
                box(8,   12, 0, 16, 15.5,    4),
                box(8, 15.5, 0, 16,   16,   .5)
        ));

        map.put(new ShapeKey(true, false), ShapeUtils.orUnoptimized(
                box(0,    0, 0, 8,   .5,   .5),
                box(0,   .5, 0, 8,    4,    4),
                box(0,    4, 0, 8,    8,    8),
                box(0,    8, 0, 8,   12,   12),
                box(0,   12, 0, 8, 15.5, 15.5),
                box(0, 15.5, 0, 8,   16,   16)
        ));

        map.put(new ShapeKey(true, true), ShapeUtils.orUnoptimized(
                box(8,    0, 0, 16,   .5,   .5),
                box(8,   .5, 0, 16,    4,    4),
                box(8,    4, 0, 16,    8,    8),
                box(8,    8, 0, 16,   12,   12),
                box(8,   12, 0, 16, 15.5, 15.5),
                box(8, 15.5, 0, 16,   16,   16)
        ));
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int maskTop = 0b0100;
        int maskRight = 0b1000;
        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (int i = 0; i < 4; i++)
        {
            ShapeUtils.makeHorizontalRotations(
                    SHAPES.get(new ShapeKey((i & 0b01) != 0, (i & 0b10) != 0)),
                    Direction.NORTH,
                    shapes,
                    i << 2
            );
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int top = state.getValue(FramedProperties.TOP) ? maskTop : 0;
            int right = state.getValue(PropertyHolder.RIGHT) ? maskRight : 0;
            int idx = dir.get2DDataValue() | (top | right);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}
