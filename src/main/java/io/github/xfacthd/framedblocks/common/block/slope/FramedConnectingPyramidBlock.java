package io.github.xfacthd.framedblocks.common.block.slope;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.block.PillarLikeBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedConnectingPyramidBlock extends FramedPyramidBlock {
    public FramedConnectingPyramidBlock(BlockType type, Properties props) {
        super(type, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.PILLAR_CONNECTION);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, super.getStateForPlacement(ctx), ctx)
                .withCustom((state, modCtx) -> {
                    Direction side = state.getValue(BlockStateProperties.FACING);
                    PillarConnection connection = computeConnection(modCtx.getLevel(), modCtx.getClickedPos(), side);
                    return state.setValue(PropertyHolder.PILLAR_CONNECTION, connection);
                })
                .build();
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random
    ) {
        if (side == state.getValue(BlockStateProperties.FACING)) {
            PillarConnection connection = computeConnection(level, pos, side);
            state = state.setValue(PropertyHolder.PILLAR_CONNECTION, connection);
        }
        return super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
    }

    public static PillarConnection computeConnection(BlockGetter level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos.relative(side));
        if (state.getBlock() instanceof PillarLikeBlock block) {
            return block.getPillarConnection(state, side.getOpposite());
        } else if (DirUtils.isY(side)) {
            if (state.is(BlockTags.FENCES)) {
                return PillarConnection.POST;
            } else if (state.is(BlockTags.WALLS) && state.hasProperty(WallBlock.UP) && state.getValue(WallBlock.UP)) {
                return PillarConnection.PILLAR;
            }
        }
        return PillarConnection.NONE;
    }
}
