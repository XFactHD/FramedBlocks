package io.github.xfacthd.framedblocks.common.item;

import io.github.xfacthd.framedblocks.common.data.FramedToolType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
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
    public ItemStackTemplate getCraftingRemainder(ItemInstance inst)
    {
        // TODO: this sucks, the parameter should probably just be a stack instead of the super-interface
        DataComponentPatch patch = switch (inst)
        {
            case ItemStack stack -> stack.getComponentsPatch();
            case ItemStackTemplate template -> template.components();
            default -> DataComponentPatch.EMPTY;
        };
        return new ItemStackTemplate(inst.typeHolder(), 1, patch);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player)
    {
        return true;
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, ItemAbility ability)
    {
        return type.hasAbility() && ability == type.getAbility();
    }

    public final FramedToolType getType()
    {
        return type;
    }
}
