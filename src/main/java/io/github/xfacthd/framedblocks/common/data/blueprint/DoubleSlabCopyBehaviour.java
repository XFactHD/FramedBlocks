package io.github.xfacthd.framedblocks.common.data.blueprint;

import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.world.item.ItemStack;

public final class DoubleSlabCopyBehaviour implements BlueprintCopyBehaviour {
    @Override
    public ItemStack getBlockItem(BlueprintData blueprintData) {
        return new ItemStack(FBContent.BLOCK_FRAMED_SLAB.value(), 2);
    }
}
