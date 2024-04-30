package xfacthd.framedblocks.common.block.slopepanelcorner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.block.slopepanel.FramedExtendedSlopePanelBlock;
import xfacthd.framedblocks.common.block.slopeslab.FramedElevatedSlopeSlabBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedExtendedCornerSlopePanelWallBlock extends FramedBlock
{
    private final Holder<Block> nonWallBlock;

    public FramedExtendedCornerSlopePanelWallBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, true));
        this.nonWallBlock = switch (type)
        {
            case FRAMED_EXT_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL;
            case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL;
            default -> throw new IllegalArgumentException("Unknown corner slope panel type: " + type);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.ROTATION, FramedProperties.Y_SLOPE,
                FramedProperties.SOLID, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return FramedCornerSlopePanelWallBlock.getStateForPlacement(
                this, ctx, getBlockType() == BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL
        );
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
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(dir);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        switch (getBlockType())
        {
            case FRAMED_EXT_CORNER_SLOPE_PANEL_W ->
            {
                if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
                {
                    side = dir;
                }
            }
            case FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W ->
            {
                if (side == rotDir || side == perpRotDir)
                {
                    Vec3 hitVec = hit.getLocation();
                    double paralell = Utils.fractionInDir(hitVec, dir);
                    double perp = Utils.fractionInDir(hitVec, side == rotDir ? perpRotDir : rotDir) - .5;
                    if (perp * 2D > paralell)
                    {
                        side = dir;
                    }
                }
            }
        }
        return rotate(state, side, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (face.getAxis() == dir.getAxis())
        {
            HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
            return state.setValue(PropertyHolder.ROTATION, rotation.rotate(rot));
        }
        else if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, state.getValue(FramedProperties.FACING_HOR), rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return FramedCornerSlopePanelWallBlock.mirrorCornerPanel(state, mirror);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return ((IFramedBlock) nonWallBlock.value()).getJadeRenderState(state);
    }



    public static ShapeCache<HorizontalRotation> SHAPES = ShapeCache.createEnum(HorizontalRotation.class, map ->
    {
        VoxelShape shapeOneUpLeft = ShapeUtils.rotateShapeUnoptimized(
                Direction.NORTH,
                Direction.WEST,
                FramedExtendedSlopePanelBlock.SHAPES.get(HorizontalRotation.LEFT)
        );
        VoxelShape shapeOneDownRight = ShapeUtils.rotateShapeUnoptimized(
                Direction.NORTH,
                Direction.EAST,
                FramedExtendedSlopePanelBlock.SHAPES.get(HorizontalRotation.RIGHT)
        );

        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape shapeOne = switch (rot)
            {
                case UP, LEFT -> shapeOneUpLeft;
                case DOWN, RIGHT -> shapeOneDownRight;
            };
            VoxelShape shapeTwo = switch (rot)
            {
                case UP, RIGHT ->  FramedElevatedSlopeSlabBlock.SHAPES.get(Boolean.TRUE);
                case DOWN, LEFT -> FramedElevatedSlopeSlabBlock.SHAPES.get(Boolean.FALSE);
            };
            map.put(rot, ShapeUtils.andUnoptimized(shapeOne, shapeTwo));
        }
    });

    private record ShapeKey(Direction dir, HorizontalRotation rot) { }

    private static final ShapeCache<ShapeKey> FINAL_SHAPES = ShapeCache.create(map ->
    {
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            ShapeUtils.makeHorizontalRotations(SHAPES.get(rot), Direction.NORTH, map, rot, ShapeKey::new);
        }
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            builder.put(state, FINAL_SHAPES.get(new ShapeKey(dir, rot)));
        }

        return ShapeProvider.of(builder.build());
    }

    public static final ShapeCache<HorizontalRotation> INNER_SHAPES = ShapeCache.createEnum(HorizontalRotation.class, map ->
    {
        VoxelShape shapeOneUpLeft = ShapeUtils.rotateShapeUnoptimized(
                Direction.NORTH,
                Direction.EAST,
                FramedExtendedSlopePanelBlock.SHAPES.get(HorizontalRotation.RIGHT)
        );
        VoxelShape shapeOneDownRight = ShapeUtils.rotateShapeUnoptimized(
                Direction.NORTH,
                Direction.WEST,
                FramedExtendedSlopePanelBlock.SHAPES.get(HorizontalRotation.LEFT)
        );

        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape shapeOne = switch (rot)
            {
                case UP, LEFT -> shapeOneUpLeft;
                case DOWN, RIGHT -> shapeOneDownRight;
            };
            VoxelShape shapeTwo = switch (rot)
            {
                case UP, RIGHT -> FramedElevatedSlopeSlabBlock.SHAPES.get(Boolean.FALSE);
                case DOWN, LEFT -> FramedElevatedSlopeSlabBlock.SHAPES.get(Boolean.TRUE);
            };
            map.put(rot, ShapeUtils.orUnoptimized(shapeOne, shapeTwo));
        }
    });

    private static final ShapeCache<ShapeKey> FINAL_INNER_SHAPES = ShapeCache.create(map ->
    {
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            ShapeUtils.makeHorizontalRotations(INNER_SHAPES.get(rot), Direction.NORTH, map, rot, ShapeKey::new);
        }
    });

    public static ShapeProvider generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            builder.put(state, FINAL_INNER_SHAPES.get(new ShapeKey(dir, rot)));
        }

        return ShapeProvider.of(builder.build());
    }
}
