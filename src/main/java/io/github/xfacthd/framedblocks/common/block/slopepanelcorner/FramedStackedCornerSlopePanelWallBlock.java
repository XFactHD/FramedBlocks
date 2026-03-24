package io.github.xfacthd.framedblocks.common.block.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import org.jspecify.annotations.Nullable;

public class FramedStackedCornerSlopePanelWallBlock extends FramedDoubleBlock implements SlopeToggleBlock
{
    private final Holder<Block> nonWallBlock;

    public FramedStackedCornerSlopePanelWallBlock(BlockType type, Properties props)
    {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.ALT_SLOPE, true));
        this.nonWallBlock = switch (type)
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL;
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL;
            default -> throw new IllegalArgumentException("Unknown corner slope panel type: " + type);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return FramedCornerSlopePanelWallBlock.getStateForPlacement(
                this, ctx, getBlockType() == BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W
        );
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return switch (mode)
        {
            case PRIMARY -> HorizontalRotation.rotate(state, direction);
            case SECONDARY -> super.rotate(state, direction, mode);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return FramedCornerSlopePanelWallBlock.mirrorCornerPanel(state, mirror);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

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

        return switch (getBlockType())
        {
            case FRAMED_STACKED_CORNER_SLOPE_PANEL_W -> new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_SLAB_EDGE.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, otherDir)
                            .setValue(FramedProperties.TOP, top),
                    FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(PropertyHolder.ROTATION, rot)
                            .setValue(FramedProperties.ALT_SLOPE, altSlope)
            );
            case FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_STAIRS.value()
                            .defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, otherDir.getOpposite())
                            .setValue(BlockStateProperties.HALF, top ? Half.BOTTOM : Half.TOP),
                    FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(PropertyHolder.ROTATION, rot)
                            .setValue(FramedProperties.ALT_SLOPE, altSlope)
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
        return switch (getBlockType())
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
        return switch (getBlockType())
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

    @Override
    @Nullable
    public BlockState getItemModelSource()
    {
        return null;
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return ((IFramedBlock) nonWallBlock.value()).getJadeRenderState(state);
    }
}
