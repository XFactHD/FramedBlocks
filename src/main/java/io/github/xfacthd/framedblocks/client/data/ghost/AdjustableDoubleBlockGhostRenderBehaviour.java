package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.ghost.DoubleBlockGhostRenderBehaviour;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.component.AdjustableDoubleBlockData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public final class AdjustableDoubleBlockGhostRenderBehaviour implements DoubleBlockGhostRenderBehaviour {
    @Override
    public ModelData appendModelData(ItemStack stack, @Nullable ItemStack proxiedStack, BlockPlaceContext ctx, BlockState renderState, int renderPass, ModelData data) {
        AdjustableDoubleBlockData blockData = stack.get(FBContent.DC_TYPE_ADJ_DOUBLE_BLOCK_DATA);
        int firstHeight = blockData != null ? blockData.firstHeight() : FramedAdjustableDoubleBlockEntity.CENTER_PART_HEIGHT;
        return data.derive().with(PackedCollapsibleBlockOffsets.PROPERTY, FramedAdjustableDoubleBlockEntity.packDoubleOffsets(renderState, firstHeight)).build();
    }
}
