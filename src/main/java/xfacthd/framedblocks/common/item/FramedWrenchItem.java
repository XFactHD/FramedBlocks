package xfacthd.framedblocks.common.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.FramedToolType;

public class FramedWrenchItem extends FramedToolItem
{
    public FramedWrenchItem(FramedToolType type)
    {
        super(type);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility action)
    {
        return action == Utils.ACTION_WRENCH;
    }
}
