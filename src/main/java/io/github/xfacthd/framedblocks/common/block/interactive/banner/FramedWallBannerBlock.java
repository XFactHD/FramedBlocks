package io.github.xfacthd.framedblocks.common.block.interactive.banner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public final class FramedWallBannerBlock extends AbstractFramedBannerBlock {
    public FramedWallBannerBlock(Properties props) {
        super(BlockType.FRAMED_WALL_BANNER, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        for (Direction lookDir : context.getNearestLookingDirections()) {
            if (DirUtils.isY(lookDir)) {
                continue;
            }

            BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, lookDir.getOpposite());
            if (state.canSurvive(level, pos)) {
                return state;
            }
        }

        return null;
    }

    @Override
    protected Direction getAttachDir(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR).getOpposite();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FramedProperties.FACING_HOR, rotation.rotate(state.getValue(FramedProperties.FACING_HOR)));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FramedProperties.FACING_HOR)));
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }
}
