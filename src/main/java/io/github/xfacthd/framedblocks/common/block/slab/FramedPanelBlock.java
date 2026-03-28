package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.item.block.FramedSpecialBlockItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedPanelBlock extends FramedBlock {
    public FramedPanelBlock(Properties props) {
        super(BlockType.FRAMED_PANEL, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
                .withWater()
                .build();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        if (ctx.getPlayer() != null && !ctx.getPlayer().isShiftKeyDown() && ctx.getItemInHand().is(asItem())) {
            if (!ctx.replacingClickedOnBlock()) {
                return true;
            }

            Direction innerFace = state.getValue(FramedProperties.FACING_HOR).getOpposite();
            return ctx.getClickedFace() == innerFace || MathUtils.fractionInDir(ctx.getClickLocation(), innerFace) > .5D;
        }
        return false;
    }

    @Override
    public BlockItem createBlockItem(Item.Properties props) {
        return new FramedSpecialBlockItem(this, true, props) {
            @Override
            protected BlockState getReplacementState(BlockPlaceContext ctx, BlockState originalState) {
                Direction facing = originalState.getValue(FramedProperties.FACING_HOR);
                return FBContent.BLOCK_FRAMED_DOUBLE_PANEL.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing);
            }

            @Override
            protected boolean shouldWriteToCamoTwo(BlockPlaceContext ctx, BlockState originalState) {
                return false;
            }
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return BlockUtils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
