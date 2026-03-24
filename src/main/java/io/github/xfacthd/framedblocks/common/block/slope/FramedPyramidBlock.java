package io.github.xfacthd.framedblocks.common.block.slope;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedPyramidBlock extends FramedBlock implements SlopeToggleBlock
{
    public FramedPyramidBlock(BlockType type, Properties props)
    {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetFacing(true)
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return direction.cycle(state, BlockStateProperties.FACING);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, BlockStateProperties.FACING, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return BlockUtils.mirrorFaceBlock(state, BlockStateProperties.FACING, mirror);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return DirUtils.isY(facing) ? Direction.NORTH : facing;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }
}
