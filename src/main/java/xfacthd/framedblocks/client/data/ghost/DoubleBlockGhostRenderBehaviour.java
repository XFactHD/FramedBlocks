package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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
            CompoundTag tag = stack.getTag().getCompound("BlockEntityTag").getCompound("camo_state");
            BlockState camoState = NbtUtils.readBlockState(tag);

            tag = stack.getTag().getCompound("BlockEntityTag").getCompound("camo_state_two");
            BlockState camoStateTwo = NbtUtils.readBlockState(tag);

            return new CamoPair(camoState, camoStateTwo);
        }
        return CamoPair.EMPTY;
    }
}
