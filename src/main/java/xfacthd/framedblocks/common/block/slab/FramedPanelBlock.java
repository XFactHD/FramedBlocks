package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.Direction;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedSpecialDoubleBlockItem;

public class FramedPanelBlock extends FramedBlock
{
    public FramedPanelBlock()
    {
        super(BlockType.FRAMED_PANEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
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

            Direction innerFace = state.getValue(FramedProperties.FACING_HOR).getOpposite();
            return ctx.getClickedFace() == innerFace || Utils.fractionInDir(ctx.getClickLocation(), innerFace) > .5D;
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
                Direction facing = originalState.getValue(FramedProperties.FACING_HOR);
                return FBContent.BLOCK_FRAMED_DOUBLE_PANEL.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing);
            }

            @Override
            protected boolean shouldWriteToCamoTwo(BlockPlaceContext ctx, BlockState originalState)
            {
                return false;
            }
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
