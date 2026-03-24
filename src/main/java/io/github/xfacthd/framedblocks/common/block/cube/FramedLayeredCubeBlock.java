package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jspecify.annotations.Nullable;

public class FramedLayeredCubeBlock extends FramedBlock
{
    public FramedLayeredCubeBlock(Properties props)
    {
        super(BlockType.FRAMED_LAYERED_CUBE, props);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, BlockStateProperties.LAYERS);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return PlacementStateBuilder.of(this, context)
                .withTargetFacing(true)
                .withCustom((state, modCtx) ->
                {
                    BlockState prevState = modCtx.getLevel().getBlockState(modCtx.getClickedPos());
                    if (prevState.is(this))
                    {
                        int layers = prevState.getValue(BlockStateProperties.LAYERS);
                        return prevState.setValue(BlockStateProperties.LAYERS, Math.min(8, layers + 1));
                    }
                    return state;
                })
                .withWater()
                .build();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext)
    {
        int layers = state.getValue(BlockStateProperties.LAYERS);
        if (layers >= 8 || !useContext.getItemInHand().is(this.asItem()))
        {
            return false;
        }
        if (!useContext.getItemInHand().getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY).isEmpty())
        {
            return false;
        }
        if (useContext.replacingClickedOnBlock())
        {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            return useContext.getClickedFace() == facing;
        }
        return true;
    }

    @Override
    public BlockItem createBlockItem(Item.Properties props)
    {
        return new FramedSpecialBlockItem.Single(this, props)
        {
            @Nullable
            @Override
            protected BlockState getReplacementState(BlockPlaceContext ctx, BlockState originalState)
            {
                return FramedLayeredCubeBlock.this.getStateForPlacement(ctx);
            }
        };
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return type == PathComputationType.LAND && state.getValue(BlockStateProperties.LAYERS) < 5;
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
