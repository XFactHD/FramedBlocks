package io.github.xfacthd.framedblocks.common.item;

import io.github.xfacthd.framedblocks.common.data.FramedToolType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.ItemAbility;

public class FramedToolItem extends Item
{
    private final FramedToolType type;

    public FramedToolItem(FramedToolType type, Properties props)
    {
        super(props.stacksTo(1));
        this.type = type;
    }

    @Override
    public ItemStack getCraftingRemainder(ItemStack stack)
    {
        return stack.copyWithCount(1);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player)
    {
        return true;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability)
    {
        return type.hasAbility() && ability == type.getAbility();
    }

    public final FramedToolType getType()
    {
        return type;
    }
}
