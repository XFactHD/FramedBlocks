package xfacthd.framedblocks.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.client.util.ClientAccess;
import xfacthd.framedblocks.common.config.ServerConfig;

public final class EventHandler
{
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

                if (FMLEnvironment.dist.isClient() && level.isClientSide())
                {
                    ClientAccess.resetDestroyDelay();
                }
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