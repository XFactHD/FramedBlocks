package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.ghost.CamoPair;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;

public sealed class DoubleBlockGhostRenderBehaviour implements GhostRenderBehaviour
        permits DoublePanelGhostRenderBehaviour, AdjustableDoubleBlockGhostRenderBehaviour
{
    @Override
    public CamoPair readCamo(ItemStack stack, @Nullable ItemStack proxiedStack, boolean secondPass)
    {
        return readDoubleCamo(stack);
    }

    public static CamoPair readDoubleCamo(ItemStack stack)
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
