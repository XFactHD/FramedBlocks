package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedTargetBlockEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public final class TargetGhostRenderBehaviour implements GhostRenderBehaviour {
    @Override
    public ModelData appendModelData(ItemStack stack, @Nullable ItemStack proxiedStack, BlockPlaceContext ctx, BlockState renderState, int renderPass, ModelData data) {
        DyeColor targetColor = stack.getOrDefault(FBContent.DC_TYPE_TARGET_COLOR, FramedTargetBlockEntity.DEFAULT_COLOR);
        return data.derive().with(FramedTargetBlockEntity.COLOR_PROPERTY, targetColor).build();
    }
}
