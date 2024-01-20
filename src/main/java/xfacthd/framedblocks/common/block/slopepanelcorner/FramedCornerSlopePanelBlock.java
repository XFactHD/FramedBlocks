package xfacthd.framedblocks.common.block.slopepanelcorner;

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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.block.slopepanel.FramedSlopePanelBlock;
import xfacthd.framedblocks.common.block.slopepanel.SlopePanelShape;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.CornerSlopePanelShape;
import xfacthd.framedblocks.common.item.VerticalAndWallBlockItem;

@SuppressWarnings("deprecation")
public class FramedCornerSlopePanelBlock extends FramedBlock
{
    private final boolean inner;
    private final boolean frontEdge;

    public FramedCornerSlopePanelBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
        this.inner = type == BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL ||
                     type == BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL;
        this.frontEdge = type == BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL ||
                         type == BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP,
                FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return getStateForPlacement(this, ctx, inner, frontEdge);
    }

    public static BlockState getStateForPlacement(
            Block block, BlockPlaceContext ctx, boolean invert, boolean invertFracDir
    )
    {
        return PlacementStateBuilder.of(block, ctx)
                .withCustom((state, modCtx) ->
                {
                    Direction dir = modCtx.getHorizontalDirection();
                    if (invert)
                    {
                        dir = dir.getOpposite();
                    }
                    Direction fracDir = modCtx.getHorizontalDirection();
                    if (invertFracDir)
                    {
                        fracDir = fracDir.getOpposite();
                    }
                    if (Utils.fractionInDir(modCtx.getClickLocation(), fracDir.getClockWise()) > .5)
                    {
                        dir = dir.getClockWise();
                    }
                    return state.setValue(FramedProperties.FACING_HOR, dir);
                })
                .withTop()
                .tryWithWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction side = hit.getDirection();
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        switch (getBlockType())
        {
            case FRAMED_SMALL_CORNER_SLOPE_PANEL, FRAMED_LARGE_CORNER_SLOPE_PANEL ->
            {
                if (side == dir.getOpposite() || side == dir.getClockWise())
                {
                    side = Direction.UP;
                }
            }
            case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL ->
            {
                if (side == dir || side == dir.getCounterClockWise())
                {
                    boolean top = state.getValue(FramedProperties.TOP);
                    Vec3 hitVec = hit.getLocation();
                    double y = Utils.fractionInDir(hitVec, top ? Direction.UP : Direction.DOWN);
                    double xz = Utils.fractionInDir(hitVec, side == dir ? dir.getCounterClockWise() : dir) - .5;
                    if (xz * 2D > y)
                    {
                        side = Direction.UP;
                    }
                }
            }
        }
        return rotate(state, side, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        return state.cycle(FramedProperties.TOP);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, Direction.UP, rotation);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockItem createBlockItem()
    {
        Block other = switch (getBlockType())
        {
            case FRAMED_SMALL_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_LARGE_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL.value();
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
        return new VerticalAndWallBlockItem(this, other, new Item.Properties());
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, inner ? Direction.EAST : Direction.WEST);
    }



    public static final ShapeCache<CornerSlopePanelShape> SHAPES = ShapeCache.createEnum(CornerSlopePanelShape.class, map ->
    {
        {
            VoxelShape panelShapeBottom = FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.UP_BACK);
            map.put(CornerSlopePanelShape.SMALL_BOTTOM, ShapeUtils.andUnoptimized(
                    panelShapeBottom,
                    ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeBottom)
            ));
        }

        {
            VoxelShape panelShapeTop = FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.DOWN_BACK);
            map.put(CornerSlopePanelShape.SMALL_TOP, ShapeUtils.andUnoptimized(
                    panelShapeTop,
                    ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeTop)
            ));
        }

        {
            VoxelShape panelShapeBot = FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.UP_FRONT);
            VoxelShape panelShapeBotRot = ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeBot);
            map.put(CornerSlopePanelShape.LARGE_BOTTOM, ShapeUtils.orUnoptimized(
                    ShapeUtils.andUnoptimized(panelShapeBot, panelShapeBotRot),
                    ShapeUtils.orUnoptimized(
                            ShapeUtils.andUnoptimized(panelShapeBot, box(0, 0, 8, 8, 16, 16)),
                            ShapeUtils.andUnoptimized(panelShapeBotRot, box(8, 0, 0, 16, 16, 8))
                    )
            ));
        }

        {
            VoxelShape panelShapeTop = FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.DOWN_FRONT);
            VoxelShape panelShapeTopRot = ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeTop);
            map.put(CornerSlopePanelShape.LARGE_TOP, ShapeUtils.orUnoptimized(
                    ShapeUtils.andUnoptimized(panelShapeTop, panelShapeTopRot),
                    ShapeUtils.orUnoptimized(
                            ShapeUtils.andUnoptimized(panelShapeTop, box(0, 0, 8, 8, 16, 16)),
                            ShapeUtils.andUnoptimized(panelShapeTopRot, box(8, 0, 0, 16, 16, 8))
                    )
            ));
        }

        {
            VoxelShape panelShapeBottom = FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.UP_FRONT);
            map.put(CornerSlopePanelShape.SMALL_INNER_BOTTOM, ShapeUtils.andUnoptimized(
                    box(8, 0, 8, 16, 16, 16),
                    ShapeUtils.orUnoptimized(
                            panelShapeBottom,
                            ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeBottom)
                    )
            ));
        }

        {
            VoxelShape panelShapeTop = FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.DOWN_FRONT);
            map.put(CornerSlopePanelShape.SMALL_INNER_TOP, ShapeUtils.andUnoptimized(
                    box(8, 0, 8, 16, 16, 16),
                    ShapeUtils.orUnoptimized(
                            panelShapeTop,
                            ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeTop)
                    )
            ));
        }

        {
            VoxelShape panelShapeBottom = FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.UP_BACK);
            map.put(CornerSlopePanelShape.LARGE_INNER_BOTTOM, ShapeUtils.orUnoptimized(
                    panelShapeBottom,
                    ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeBottom)
            ));
        }

        {
            VoxelShape panelShapeTop = FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.DOWN_BACK);
            map.put(CornerSlopePanelShape.LARGE_INNER_TOP, ShapeUtils.orUnoptimized(
                    panelShapeTop,
                    ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, panelShapeTop)
            ));
        }
    });

    public static ShapeProvider generateSmallShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                SHAPES.get(CornerSlopePanelShape.SMALL_BOTTOM),
                SHAPES.get(CornerSlopePanelShape.SMALL_TOP),
                Direction.NORTH
        );

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static ShapeProvider generateLargeShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                SHAPES.get(CornerSlopePanelShape.LARGE_BOTTOM),
                SHAPES.get(CornerSlopePanelShape.LARGE_TOP),
                Direction.NORTH
        );

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static ShapeProvider generateSmallInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                SHAPES.get(CornerSlopePanelShape.SMALL_INNER_BOTTOM),
                SHAPES.get(CornerSlopePanelShape.SMALL_INNER_TOP),
                Direction.SOUTH
        );

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static ShapeProvider generateLargeInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                SHAPES.get(CornerSlopePanelShape.LARGE_INNER_BOTTOM),
                SHAPES.get(CornerSlopePanelShape.LARGE_INNER_TOP),
                Direction.SOUTH
        );

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }
}
