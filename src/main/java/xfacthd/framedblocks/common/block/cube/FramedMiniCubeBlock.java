package xfacthd.framedblocks.common.block.cube;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.Set;

@SuppressWarnings("deprecation")
public class FramedMiniCubeBlock extends FramedBlock
{
    public FramedMiniCubeBlock()
    {
        super(BlockType.FRAMED_MINI_CUBE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) -> state.setValue(
                        BlockStateProperties.ROTATION_16,
                        RotationSegment.convertToSegment(modCtx.getRotation() + 180F)
                ))
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rotation)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        if (rotation == Rotation.CLOCKWISE_90)
        {
            rot = (rot + 1) % 16;
        }
        else
        {
            rot = (rot + 15) % 16;
        }
        return state.setValue(BlockStateProperties.ROTATION_16, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, rotation.rotate(rot, 16));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, mirror.mirror(rot, 16));
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }



    public static final class MiniCubeStateMerger implements StateMerger
    {
        public static final MiniCubeStateMerger INSTANCE = new MiniCubeStateMerger();

        private final StateMerger ignoringMerger = StateMerger.ignoring(WrapHelper.IGNORE_WATERLOGGED);

        private MiniCubeStateMerger() { }

        @Override
        public BlockState apply(BlockState state)
        {
            state = ignoringMerger.apply(state);
            int rot = state.getValue(BlockStateProperties.ROTATION_16);
            if (rot > 3)
            {
                state = state.setValue(BlockStateProperties.ROTATION_16, rot % 4);
            }
            return state;
        }

        @Override
        public Set<Property<?>> getHandledProperties(Holder<Block> block)
        {
            return Utils.concat(
                    ignoringMerger.getHandledProperties(block),
                    Set.of(BlockStateProperties.ROTATION_16)
            );
        }
    }
}
