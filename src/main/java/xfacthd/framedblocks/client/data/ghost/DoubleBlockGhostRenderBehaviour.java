package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.ghost.CamoPair;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;

public class DoubleBlockGhostRenderBehaviour implements GhostRenderBehaviour
{
    @Override
    public CamoPair readCamo(ItemStack stack, ItemStack proxiedStack, boolean secondPass)
    {
        //noinspection ConstantConditions
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
        {
            CompoundTag tag = stack.getTag().getCompound("BlockEntityTag").getCompound("camo");
            CamoContainer camo = CamoContainer.load(tag);

            tag = stack.getTag().getCompound("BlockEntityTag").getCompound("camo_two");
            CamoContainer camoTwo = CamoContainer.load(tag);

            return new CamoPair(camo.getState(), camoTwo.getState());
        }
        return CamoPair.EMPTY;
    }
}
