package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedCheckeredCubeSegmentBlock extends FramedBlock {
    public FramedCheckeredCubeSegmentBlock(Properties props) {
        super(BlockType.FRAMED_CHECKERED_CUBE_SEGMENT, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.SECOND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.SECOND);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) -> state.setValue(
                        PropertyHolder.SECOND, DirUtils.isX(modCtx.getHorizontalDirection())
                ))
                .withWater()
                .build();
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return DirUtils.isNinetyDegree(rotation) ? state.cycle(PropertyHolder.SECOND) : state;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror != Mirror.NONE) {
            return state.cycle(PropertyHolder.SECOND);
        }
        return state;
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public @Nullable Direction getHorizontalOrientation(BlockState state) {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return state;
    }
}
