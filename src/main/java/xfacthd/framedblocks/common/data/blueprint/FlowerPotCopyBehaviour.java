package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.blueprint.BlueprintData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.component.PottedFlower;

import java.util.List;

public final class FlowerPotCopyBehaviour implements BlueprintCopyBehaviour
{
    @Override
    public List<ItemStack> getAdditionalConsumedMaterials(BlueprintData data)
    {
        PottedFlower flower = data.getAuxDataOrDefault(PottedFlower.EMPTY);
        if (!flower.isEmpty())
        {
            return List.of(new ItemStack(flower.flower()));
        }
        return List.of();
    }

    @Override
    public void attachDataToDummyRenderStack(ItemStack stack, BlueprintData data)
    {
        PottedFlower flower = data.getAuxDataOrDefault(PottedFlower.EMPTY);
        stack.set(FBContent.DC_TYPE_POTTED_FLOWER, flower);
    }
}
