package xfacthd.framedblocks.common.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

import javax.annotation.Nullable;

public class FramedSignItem extends WallOrFloorItem
{
    public FramedSignItem()
    {
        super(FBContent.blockFramedSign, FBContent.blockFramedWallSign,
                new Properties().group(FramedBlocks.FRAMED_GROUP).maxStackSize(16));
        setRegistryName(FramedBlocks.MODID, "framed_sign");
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        boolean hadNBT = super.onBlockPlaced(pos, world, player, stack, state);
        if (!world.isRemote && !hadNBT && player != null)
        {
            //TODO: implement
        }
        return hadNBT;
    }
}