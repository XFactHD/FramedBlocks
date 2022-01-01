package xfacthd.framedblocks.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler
{
    @SubscribeEvent
    public static void onBlockLeftClick(final PlayerInteractEvent.LeftClickBlock event)
    {
        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IFramedBlock block)
        {
            if (block.handleBlockLeftClick(state, level, pos, event.getPlayer()))
            {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.CONSUME);
            }
        }
    }
}