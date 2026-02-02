package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.component.CollapsibleBlockData;

public final class CollapsibleBlockGhostRenderBehaviour implements GhostRenderBehaviour
{
    @Override
    @Nullable
    public BlockState getRenderState(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    )
    {
        BlockState state = GhostRenderBehaviour.super.getRenderState(stack, proxiedStack, hit, ctx, hitState, renderPass);
        BlockItemStateProperties properties = stack.get(DataComponents.BLOCK_STATE);
        if (state != null && properties != null && !properties.isEmpty())
        {
            state = properties.apply(state);
        }
        return state;
    }

    @Override
    public ModelData appendModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    )
    {
        CollapsibleBlockData blockData = stack.get(FBContent.DC_TYPE_COLLAPSIBLE_BLOCK_DATA);
        if (blockData != null)
        {
            return data.derive().with(FramedCollapsibleBlockEntity.OFFSETS, blockData.offsets()).build();
        }
        return data;
    }
}
