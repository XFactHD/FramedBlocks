package io.github.xfacthd.framedblocks.common.block.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner.FramedExtendedDoubleCornerSlopePanelWallBlockEntity;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner.FramedExtendedInnerDoubleCornerSlopePanelWallBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedExtendedDoubleCornerSlopePanelWallBlock extends FramedDoubleBlock implements SlopeToggleBlock
{
    private final Holder<Block> nonWallBlock;

    public FramedExtendedDoubleCornerSlopePanelWallBlock(BlockType type, Properties props)
    {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.ALT_SLOPE, true));
        this.nonWallBlock = switch (type)
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL;
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL;
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
                this, ctx, getBlockType() == BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W
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
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return switch (getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W -> new FramedExtendedDoubleCornerSlopePanelWallBlockEntity(pos, state);
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> new FramedExtendedInnerDoubleCornerSlopePanelWallBlockEntity(pos, state);
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        HorizontalRotation backRot = rot.rotate(rot.isVertical() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        return switch (getBlockType())
        {
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W -> new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(PropertyHolder.ROTATION, rot)
                            .setValue(FramedProperties.ALT_SLOPE, altSlope),
                    FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getOpposite())
                            .setValue(PropertyHolder.ROTATION, backRot)
                            .setValue(FramedProperties.ALT_SLOPE, altSlope)
            );
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir)
                            .setValue(PropertyHolder.ROTATION, rot)
                            .setValue(FramedProperties.ALT_SLOPE, altSlope),
                    FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getOpposite())
                            .setValue(PropertyHolder.ROTATION, backRot)
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
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W ->
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                if (side == dir)
                {
                    yield CamoGetter.FIRST;
                }

                HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
                {
                    yield CamoGetter.SECOND;
                }
                else if (side == dir.getOpposite() && (edge == rotDir.getOpposite() || edge == perpRotDir.getOpposite()))
                {
                    yield CamoGetter.SECOND;
                }
                else if (side == rotDir)
                {
                    if (edge == dir || edge == perpRotDir)
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == perpRotDir.getOpposite())
                    {
                        yield CamoGetter.SECOND;
                    }
                }
                else if (side == perpRotDir)
                {
                    if (edge == dir || edge == rotDir)
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == rotDir.getOpposite())
                    {
                        yield CamoGetter.SECOND;
                    }
                }
                yield CamoGetter.NONE;
            }
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W ->
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                if (side == dir || side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dir.getOpposite() && (edge == rotDir.getOpposite() || edge == perpRotDir.getOpposite()))
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == rotDir)
                {
                    if (edge == dir || edge == perpRotDir.getOpposite())
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == perpRotDir)
                    {
                        yield CamoGetter.SECOND;
                    }
                }
                else if (side == perpRotDir)
                {
                    if (edge == dir || edge == rotDir.getOpposite())
                    {
                        yield CamoGetter.FIRST;
                    }
                    else if (edge == rotDir)
                    {
                        yield CamoGetter.SECOND;
                    }
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
            case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W ->
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                if (side == dir)
                {
                    yield SolidityCheck.FIRST;
                }

                HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                if (side == dir.getOpposite() || side == rot.withFacing(dir) || side == perpRotDir)
                {
                    yield SolidityCheck.BOTH;
                }
                yield SolidityCheck.SECOND;
            }
            case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W ->
            {
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
                Direction rotDir = rot.withFacing(dir);
                Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);

                if (side == dir || side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
                {
                    yield SolidityCheck.FIRST;
                }
                else if (side == dir.getOpposite() || side == rotDir || side == perpRotDir)
                {
                    yield SolidityCheck.BOTH;
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
