package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedRailSlopeBlock;

public final class RailSlopeGhostRenderBehaviour implements GhostRenderBehaviour
{
    @Override
    public boolean mayRender(ItemStack stack, @Nullable ItemStack proxiedStack)
    {
        return stack.getItem() == Items.RAIL;
    }

    @Override
    @Nullable
    public BlockState getRenderState(ItemStack stack, ItemStack proxiedStack, BlockHitResult hit, BlockPlaceContext ctx, BlockState hitState, boolean secondPass)
    {
        if (hitState.getBlock() == FBContent.blockFramedSlope.get())
        {
            RailShape shape = FramedRailSlopeBlock.shapeFromDirection(hitState.getValue(FramedProperties.FACING_HOR));
            return Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, shape);
        }
        return null;
    }

    @Override
    public BlockPos getRenderPos(ItemStack stack, ItemStack proxiedStack, BlockHitResult hit, BlockPlaceContext ctx, BlockState hitState, BlockPos defaultPos, boolean secondPass)
    {
        return hit.getBlockPos();
    }

    @Override
    public boolean canRenderAt(ItemStack stack, ItemStack proxiedStack, BlockHitResult hit, BlockPlaceContext ctx, BlockState hitState, BlockState renderState, BlockPos renderPos)
    {
        return true;
    }
}
