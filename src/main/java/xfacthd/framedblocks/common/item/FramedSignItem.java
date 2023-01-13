package xfacthd.framedblocks.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.net.OpenSignScreenPacket;
import xfacthd.framedblocks.common.blockentity.FramedSignBlockEntity;

import javax.annotation.Nullable;

public class FramedSignItem extends StandingAndWallBlockItem
{
    public FramedSignItem()
    {
        super(FBContent.blockFramedSign.get(), FBContent.blockFramedWallSign.get(), new Properties(), Direction.DOWN);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state)
    {
        boolean hadNBT = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (!level.isClientSide() && !hadNBT && player != null)
        {
            if (level.getBlockEntity(pos) instanceof FramedSignBlockEntity be)
            {
                be.setEditingPlayer(player);

                FramedBlocks.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new OpenSignScreenPacket(pos)
                );
            }
        }
        return hadNBT;
    }
}