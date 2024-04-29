package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.blueprint.BlueprintData;
import xfacthd.framedblocks.common.FBContent;

public final class DoublePanelCopyBehaviour implements BlueprintCopyBehaviour
{
    @Override
    public ItemStack getBlockItem(BlueprintData blueprintData)
    {
        return new ItemStack(FBContent.BLOCK_FRAMED_PANEL.value(), 2);
    }
}
