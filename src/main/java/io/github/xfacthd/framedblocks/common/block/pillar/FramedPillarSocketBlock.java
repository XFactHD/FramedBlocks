package io.github.xfacthd.framedblocks.common.block.pillar;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedPillarSocketBlock extends FramedBlock {
    public FramedPillarSocketBlock(BlockType type, Properties props) {
        super(type, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return PlacementStateBuilder.of(this, context)
                .withTargetFacing()
                .withWater()
                .build();
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(BlockStateProperties.FACING, Direction.DOWN);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return DirUtils.isY(facing) ? Direction.NORTH : facing;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, Direction.DOWN);
    }
}
