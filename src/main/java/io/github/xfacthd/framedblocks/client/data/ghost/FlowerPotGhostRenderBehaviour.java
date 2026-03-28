package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedFlowerPotBlockEntity;
import io.github.xfacthd.framedblocks.common.data.component.PottedFlower;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public final class FlowerPotGhostRenderBehaviour implements GhostRenderBehaviour {
    @Override
    public ModelData appendModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    ) {
        PottedFlower flower = stack.getOrDefault(FBContent.DC_TYPE_POTTED_FLOWER, PottedFlower.EMPTY);
        if (!flower.isEmpty()) {
            return data.derive().with(FramedFlowerPotBlockEntity.FLOWER_BLOCK, flower.flower()).build();
        }
        return data;
    }
}
