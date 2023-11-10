package xfacthd.framedblocks.common.block.slopepanelcorner;

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
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedStackedCornerSlopePanelWallBlockEntity;
import xfacthd.framedblocks.common.blockentity.doubled.FramedStackedInnerCornerSlopePanelWallBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

@SuppressWarnings("deprecation")
public class FramedStackedCornerSlopePanelWallBlock extends AbstractFramedDoubleBlock
{
    public FramedStackedCornerSlopePanelWallBlock(BlockType blockType)
    {
        super(blockType);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.ROTATION,
                FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return FramedCornerSlopePanelWallBlock.getStateForPlacement(
                this, ctx, getBlockType() == BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W
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
        switch ((BlockType) getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL_W ->
            {
                if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
                {
                    side = dir;
                }
            }
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W ->
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
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, state.getValue(FramedProperties.FACING_HOR), rot);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return FramedCornerSlopePanelWallBlock.mirrorCornerPanel(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return switch ((BlockType) getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL_W -> new FramedStackedCornerSlopePanelWallBlockEntity(pos, state);
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> new FramedStackedInnerCornerSlopePanelWallBlockEntity(pos, state);
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        Direction otherDir;
        boolean top;
        switch (rot)
        {
            case UP ->
            {
                otherDir = dir.getCounterClockWise();
                top = true;
            }
            case DOWN ->
            {
                otherDir = dir.getClockWise();
                top = false;
            }
            case RIGHT ->
            {
                otherDir = dir.getClockWise();
                top = true;
            }
            case LEFT ->
            {
                otherDir = dir.getCounterClockWise();
                top = false;
            }
            default -> throw new IncompatibleClassChangeError();
        }

        return switch ((BlockType) getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL_W -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_SLAB_EDGE.get()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, otherDir)
                            .setValue(FramedProperties.TOP, top),
                    FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL.get()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(PropertyHolder.ROTATION, rot)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_STAIRS.get()
                            .defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, otherDir.getOpposite())
                            .setValue(BlockStateProperties.HALF, top ? Half.BOTTOM : Half.TOP),
                    FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL.get()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(PropertyHolder.ROTATION, rot)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
            default -> throw new IllegalArgumentException("Invalid type for this block: " + getBlockType());
        };
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        if (rot == HorizontalRotation.UP || rot == HorizontalRotation.RIGHT)
        {
            return DoubleBlockTopInteractionMode.EITHER;
        }
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        return switch ((BlockType) getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL_W ->
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                if ((side == rotDir && edge == perpRotDir) || (side == perpRotDir && edge == rotDir))
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dir && (edge == rotDir.getOpposite() || edge == perpRotDir.getOpposite()))
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W ->
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                if (side.getAxis() == dir.getAxis() && (edge == rotDir.getOpposite() || edge == perpRotDir.getOpposite()))
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == rotDir && edge == perpRotDir.getOpposite())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == perpRotDir && edge == rotDir.getOpposite())
                {
                    yield CamoGetter.FIRST;
                }
                yield CamoGetter.NONE;
            }
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        return switch ((BlockType) getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL_W ->
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                if (side == dir)
                {
                    yield SolidityCheck.BOTH;
                }
                yield SolidityCheck.NONE;
            }
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W ->
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                if (side == dir)
                {
                    yield SolidityCheck.BOTH;
                }

                HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                if (side == rot.withFacing(dir).getOpposite() || side == perpRotDir.getOpposite())
                {
                    yield SolidityCheck.FIRST;
                }
                yield SolidityCheck.NONE;
            }
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
    }
}
