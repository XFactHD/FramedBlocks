package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedSpecialDoubleBlockItem;

public class FramedSlabBlock extends FramedBlock
{
    public FramedSlabBlock()
    {
        super(BlockType.FRAMED_SLAB);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.TOP, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTop()
                .withWater()
                .build();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext ctx)
    {
        if (ctx.getPlayer() != null && !ctx.getPlayer().isShiftKeyDown() && ctx.getItemInHand().is(asItem()))
        {
            if (!ctx.replacingClickedOnBlock())
            {
                return true;
            }

            boolean top = state.getValue(FramedProperties.TOP);
            Direction side = ctx.getClickedFace();
            if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
            {
                return true;
            }
            return Utils.fractionInDir(ctx.getClickLocation(), top ? Direction.DOWN : Direction.UP) > .5D;
        }
        return false;
    }

    @Override
    public BlockItem createBlockItem()
    {
        return new FramedSpecialDoubleBlockItem(this, new Item.Properties())
        {
            @Override
            protected BlockState getReplacementState(BlockPlaceContext ctx, BlockState originalState)
            {
                return FBContent.BLOCK_FRAMED_DOUBLE_SLAB.value().defaultBlockState();
            }

            @Override
            protected boolean shouldWriteToCamoTwo(BlockPlaceContext ctx, BlockState originalState)
            {
                return originalState.getValue(FramedProperties.TOP);
            }
        };
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type)
    {
        return type == PathComputationType.WATER && state.getFluidState().is(FluidTags.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, Direction side, Rotation rot)
    {
        if (rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }
}
