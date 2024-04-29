package xfacthd.framedblocks.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import xfacthd.framedblocks.common.data.FramedToolType;

public class FramedToolItem extends Item
{
    private final FramedToolType type;

    public FramedToolItem(FramedToolType type)
    {
        this(type, new Properties());
    }

    public FramedToolItem(FramedToolType type, Properties props)
    {
        super(props.stacksTo(1));
        this.type = type;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack)
    {
        return stack.copy();
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player)
    {
        return true;
    }

    public final FramedToolType getType()
    {
        return type;
    }
}