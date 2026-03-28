package io.github.xfacthd.framedblocks.common.data.blueprint;

import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.component.PottedFlower;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class FlowerPotCopyBehaviour implements BlueprintCopyBehaviour {
    @Override
    public List<ItemStack> getAdditionalConsumedMaterials(BlueprintData data) {
        PottedFlower flower = data.getCustomDataOrDefault(FBContent.DC_TYPE_POTTED_FLOWER, PottedFlower.EMPTY);
        if (!flower.isEmpty()) {
            return List.of(new ItemStack(flower.flower()));
        }
        return List.of();
    }

    @Override
    public void attachDataToDummyRenderStack(ItemStack stack, BlueprintData data) {
        PottedFlower flower = data.getCustomDataOrDefault(FBContent.DC_TYPE_POTTED_FLOWER, PottedFlower.EMPTY);
        stack.set(FBContent.DC_TYPE_POTTED_FLOWER, flower);
    }
}
