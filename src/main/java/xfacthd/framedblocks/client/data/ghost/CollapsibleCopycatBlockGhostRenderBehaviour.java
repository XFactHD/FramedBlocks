package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.component.CollapsibleCopycatBlockData;

public final class CollapsibleCopycatBlockGhostRenderBehaviour implements GhostRenderBehaviour
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
        CollapsibleCopycatBlockData blockData = stack.get(FBContent.DC_TYPE_COLLAPSIBLE_COPYCAT_BLOCK_DATA);
        if (state != null && blockData != null)
        {
            int solidFaces = FramedCollapsibleCopycatBlockEntity.computeSolidFaces(blockData.offsets());
            state = state.setValue(PropertyHolder.SOLID_FACES, solidFaces);
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
        CollapsibleCopycatBlockData blockData = stack.get(FBContent.DC_TYPE_COLLAPSIBLE_COPYCAT_BLOCK_DATA);
        if (blockData != null)
        {
            return data.derive().with(FramedCollapsibleCopycatBlockEntity.OFFSETS, blockData.offsets()).build();
        }
        return data;
    }
}
