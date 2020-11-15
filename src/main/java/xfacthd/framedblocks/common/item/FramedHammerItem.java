package xfacthd.framedblocks.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import xfacthd.framedblocks.FramedBlocks;

public class FramedHammerItem extends Item
{
    public FramedHammerItem()
    {
        super(new Properties()
                .maxStackSize(1)
                .group(ItemGroup.TOOLS)
        );

        setRegistryName(FramedBlocks.MODID, "framed_hammer");
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player)
    {
        return true;
    }
}