package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.ghost.SimpleGhostRenderBehaviour;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedTargetBlockEntity;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

public final class TargetGhostRenderBehaviour implements SimpleGhostRenderBehaviour {
    @Override
    public ModelData appendModelData(ItemStack stack, Unit context, BlockPlaceContext ctx, BlockState renderState, int renderPass, ModelData data) {
        DyeColor targetColor = stack.getOrDefault(FBContent.DC_TYPE_TARGET_COLOR, FramedTargetBlockEntity.DEFAULT_COLOR);
        return data.derive().with(FramedTargetBlockEntity.COLOR_PROPERTY, targetColor).build();
    }
}
