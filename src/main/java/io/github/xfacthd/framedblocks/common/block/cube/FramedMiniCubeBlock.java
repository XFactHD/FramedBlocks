package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class FramedMiniCubeBlock extends FramedBlock
{
    public FramedMiniCubeBlock(Properties props)
    {
        super(BlockType.FRAMED_MINI_CUBE, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16, FramedProperties.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) -> state.setValue(
                        BlockStateProperties.ROTATION_16,
                        RotationSegment.convertToSegment(modCtx.getRotation() + 180F)
                ))
                .withTop()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        rotation += switch (direction)
        {
            case CLOCKWISE -> 1;
            case COUNTERCLOCKWISE -> 15;
        };
        return state.setValue(BlockStateProperties.ROTATION_16, rotation % 16);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, rotation.rotate(rot, 16));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, mirror.mirror(rot, 16));
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        return Direction.from2DDataValue(rotation / 4);
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
