package io.github.xfacthd.framedblocks.common.block.interactive.banner;

import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.item.block.FramedStandingAndWallBlockItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public final class FramedBannerBlock extends AbstractFramedBannerBlock {
    public FramedBannerBlock(Properties props) {
        super(BlockType.FRAMED_BANNER, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int rotation = RotationSegment.convertToSegment(context.getRotation() + 180);
        return this.defaultBlockState().setValue(BlockStateProperties.ROTATION_16, rotation);
    }

    @Override
    protected Direction getAttachDir(BlockState state) {
        return Direction.DOWN;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.ROTATION_16, rotation.rotate(state.getValue(BlockStateProperties.ROTATION_16), 16));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(BlockStateProperties.ROTATION_16, mirror.mirror(state.getValue(BlockStateProperties.ROTATION_16), 16));
    }

    @Override
    public IFramedBlockItem createBlockItem(Item.Properties props) {
        return new FramedStandingAndWallBlockItem(this, FBContent.BLOCK_FRAMED_WALL_BANNER.value(), Direction.DOWN, props);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        return Direction.from2DDataValue(rotation / 4);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }
}
