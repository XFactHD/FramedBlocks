package xfacthd.framedblocks.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.net.OpenSignScreenPacket;
import xfacthd.framedblocks.common.tileentity.FramedSignTileEntity;

import javax.annotation.Nullable;

public class FramedSignItem extends StandingAndWallBlockItem
{
    public FramedSignItem()
    {
        super(FBContent.blockFramedSign.get(), FBContent.blockFramedWallSign.get(),
                new Properties().tab(FramedBlocks.FRAMED_GROUP).stacksTo(16));
        setRegistryName(FramedBlocks.MODID, "framed_sign");
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, @Nullable Player player, ItemStack stack, BlockState state)
    {
        boolean hadNBT = super.updateCustomBlockEntityTag(pos, world, player, stack, state);
        if (!world.isClientSide() && !hadNBT && player != null)
        {
            if (world.getBlockEntity(pos) instanceof FramedSignTileEntity te)
            {
                te.setEditingPlayer(player);

                FramedBlocks.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new OpenSignScreenPacket(pos)
                );
            }
        }
        return hadNBT;
    }
}