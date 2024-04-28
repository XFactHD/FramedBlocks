package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

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
        var beData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        //noinspection ConstantConditions
        if (state != null && beData != null)
        {
            int offsets = beData.getUnsafe().getInt("offsets");
            int solidFaces = FramedCollapsibleCopycatBlockEntity.computeSolidFaces(offsets);
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
        var beData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        //noinspection ConstantConditions
        if (beData != null)
        {
            int offsets = beData.getUnsafe().getInt("offsets");
            return data.derive().with(FramedCollapsibleCopycatBlockEntity.OFFSETS, offsets).build();
        }
        return data;
    }
}
