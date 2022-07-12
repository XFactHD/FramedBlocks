package xfacthd.framedblocks.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedConstants;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EventHandler
{
    @SubscribeEvent
    public static void onBlockLeftClick(final PlayerInteractEvent.LeftClickBlock event)
    {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IFramedBlock block)
        {
            if (block.handleBlockLeftClick(state, level, pos, event.getEntity()))
            {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.CONSUME);
            }

            if (ServerConfig.enableIntangibleFeature && !event.isCanceled() && block.getBlockType().allowMakingIntangible())
            {
                if (level.getBlockEntity(pos) instanceof FramedBlockEntity be && be.isIntangible(null))
                {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                }
            }
        }
    }



    private EventHandler() { }
}