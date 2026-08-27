package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCubeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.component.CollapsibleCubeData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public final class CollapsibleCubeGhostRenderBehaviour implements GhostRenderBehaviour {
    @Override
    public @Nullable BlockState getRenderState(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    ) {
        BlockState state = GhostRenderBehaviour.super.getRenderState(stack, proxiedStack, hit, ctx, hitState, renderPass);
        CollapsibleCubeData blockData = stack.get(FBContent.DC_TYPE_COLLAPSIBLE_CUBE_DATA);
        if (state != null && blockData != null) {
            int solidFaces = FramedCollapsibleCubeBlockEntity.computeSolidFaces(blockData.offsets());
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
    ) {
        CollapsibleCubeData blockData = stack.get(FBContent.DC_TYPE_COLLAPSIBLE_CUBE_DATA);
        if (blockData != null) {
            PackedCollapsibleBlockOffsets.Single offsets = new PackedCollapsibleBlockOffsets.Single(blockData.offsets());
            return data.derive().with(PackedCollapsibleBlockOffsets.PROPERTY, offsets).build();
        }
        return data;
    }
}
