package xfacthd.framedblocks.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.data.FramedToolType;

public class FramedToolItem extends Item
{
    private final FramedToolType type;

    public FramedToolItem(FramedToolType type)
    {
        super(new Properties()
                .maxStackSize(1)
                .group(FramedBlocks.FRAMED_GROUP)
        );
        this.type = type;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) { return true; }

    @Override
    public ItemStack getContainerItem(ItemStack stack) { return stack.copy(); }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) { return true; }

    public FramedToolType getType() { return type; }
}