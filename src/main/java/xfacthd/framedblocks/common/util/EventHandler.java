package xfacthd.framedblocks.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedCollapsibleBlock;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler
{
    @SubscribeEvent
    public static void onBlockLeftClick(final PlayerInteractEvent.LeftClickBlock event)
    {
        boolean success = false;

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        PlayerEntity player = event.getPlayer();
        BlockState state = event.getWorld().getBlockState(event.getPos());
        Block block = state.getBlock();

        if (block == FBContent.blockFramedCollapsibleBlock.get())
        {
            success = FramedCollapsibleBlock.onLeftClick(event.getWorld(), event.getPos(), event.getPlayer());
        }
        else if (block == FBContent.blockFramedPrismCorner.get() ||
                 block == FBContent.blockFramedInnerPrismCorner.get() ||
                 block == FBContent.blockFramedDoublePrismCorner.get()
        )
        {
            if (player.getHeldItemMainhand().getItem() == FBContent.itemFramedHammer.get())
            {
                world.setBlockState(pos, state.with(PropertyHolder.OFFSET, !state.get(PropertyHolder.OFFSET)));
                success = true;
            }
        }

        if (ServerConfig.enableIntangibleFeature && !success && block instanceof IFramedBlock && ((IFramedBlock) block).getBlockType().allowMakingIntangible())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof FramedTileEntity && ((FramedTileEntity) te).isIntangible(null))
            {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.FAIL);
            }
        }

        if (success)
        {
            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.CONSUME);
        }
    }
}