package xfacthd.framedblocks.common.util;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedCollapsibleBlock;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler
{
    @SubscribeEvent
    public static void onBlockLeftClick(final PlayerInteractEvent.LeftClickBlock event)
    {
        BlockState state = event.getWorld().getBlockState(event.getPos());
        if (state.is(FBContent.blockFramedCollapsibleBlock.get()))
        {
            boolean success = FramedCollapsibleBlock.onLeftClick(event.getWorld(), event.getPos(), event.getPlayer());
            if (success)
            {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.CONSUME);
            }
        }
    }
}