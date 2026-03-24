package io.github.xfacthd.framedblocks.common.block.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
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
import org.jspecify.annotations.Nullable;

public class FramedCornerSlopePanelWallBlock extends FramedBlock implements SlopeToggleBlock
{
    private final boolean large;
    private final Holder<Block> nonWallBlock;

    public FramedCornerSlopePanelWallBlock(BlockType type, Properties props)
    {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.ALT_SLOPE, true));
        this.large = type == BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W ||
                     type == BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W;
        this.nonWallBlock = switch (type)
        {
            case FRAMED_SMALL_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL;
            case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL;
            case FRAMED_LARGE_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL;
            case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL;
            case FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER;
            case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER;
            case FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER;
            case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W -> FBContent.BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER;
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
        return getStateForPlacement(this, ctx, large);
    }

    @Nullable
    public static BlockState getStateForPlacement(Block block, BlockPlaceContext ctx, boolean invert)
    {
        return ExtPlacementStateBuilder.of(block, ctx)
                .withHorizontalTargetFacing()
                .withCornerRotation(!invert)
                .tryWithWater()
                .build();
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
        return mirrorCornerPanel(state, mirror);
    }

    public static BlockState mirrorCornerPanel(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }

        BlockState newState = BlockUtils.mirrorFaceBlock(state, mirror);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        rot = rot.rotate(rot.isVertical() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
        return newState.setValue(PropertyHolder.ROTATION, rot);
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
