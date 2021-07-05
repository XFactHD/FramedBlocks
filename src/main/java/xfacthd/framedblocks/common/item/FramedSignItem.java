package xfacthd.framedblocks.common.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.net.OpenSignScreenPacket;
import xfacthd.framedblocks.common.tileentity.FramedSignTileEntity;

import javax.annotation.Nullable;

public class FramedSignItem extends WallOrFloorItem
{
    public FramedSignItem()
    {
        super(FBContent.blockFramedSign.get(), FBContent.blockFramedWallSign.get(),
                new Properties().group(FramedBlocks.FRAMED_GROUP).maxStackSize(16));
        setRegistryName(FramedBlocks.MODID, "framed_sign");
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
    {
        boolean hadNBT = super.onBlockPlaced(pos, world, player, stack, state);
        if (!world.isRemote() && !hadNBT && player != null)
        {
            if (world.getTileEntity(pos) instanceof FramedSignTileEntity te)
            {
                te.setEditingPlayer(player);

                FramedBlocks.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                        new OpenSignScreenPacket(pos)
                );
            }
        }
        return hadNBT;
    }
}