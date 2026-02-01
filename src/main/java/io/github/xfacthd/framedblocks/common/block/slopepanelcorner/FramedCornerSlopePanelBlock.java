package io.github.xfacthd.framedblocks.common.block.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.item.block.VerticalAndWallBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedCornerSlopePanelBlock extends FramedBlock
{
    private final boolean invertFacing;
    private final boolean invertFracDir;

    public FramedCornerSlopePanelBlock(BlockType type, Properties props)
    {
        super(type, props);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
        this.invertFacing = type == BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL ||
                            type == BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL;
        this.invertFracDir = type == BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL ||
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
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return getStateForPlacement(this, ctx, invertFacing, invertFracDir);
    }

    @Nullable
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
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return switch (mode)
        {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> state.cycle(FramedProperties.TOP);
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
        return BlockUtils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockItem createBlockItem(Item.Properties props)
    {
        Block other = switch (getBlockType())
        {
            case FRAMED_SMALL_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_LARGE_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL.value();
            case FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER_WALL.value();
            case FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER_WALL.value();
            case FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER_WALL.value();
            case FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL -> FBContent.BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER_WALL.value();
            default -> throw new IllegalStateException("Unexpected type: " + getBlockType());
        };
        return new VerticalAndWallBlockItem(this, other, props);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, invertFacing ? Direction.EAST : Direction.WEST);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
