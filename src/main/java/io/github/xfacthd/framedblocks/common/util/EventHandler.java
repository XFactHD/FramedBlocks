package io.github.xfacthd.framedblocks.common.util;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.util.ClientAccess;
import io.github.xfacthd.framedblocks.common.config.ServerConfig;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

public final class EventHandler {
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IFramedBlock block) {
            if (block.handleBlockLeftClick(state, level, pos, event.getEntity())) {
                event.setCanceled(true);

                if (Utils.CLIENT_DIST && level.isClientSide()) {
                    ClientAccess.resetDestroyDelay();
                }
            }

            if (ServerConfig.VIEW.enableIntangibility() && !event.isCanceled() && block.getBlockType().allowMakingIntangible()) {
                if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be && be.isIntangible(null)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemInHand = event.getEntity().getItemInHand(event.getHand());
        if (!itemInHand.is(Items.BRUSH)) {
            return;
        }

        BlockPos pos = event.getHitVec().getBlockPos();
        if (event.getLevel().getBlockState(pos).getBlock() instanceof IFramedBlock) {
            event.setUseBlock(TriState.TRUE);
            event.setUseItem(TriState.FALSE);
        }
    }

    public static void onServerShutdown(@SuppressWarnings("unused") ServerStoppedEvent event) {
        FramingSawRecipeCache.get(false).clear();
    }

    private EventHandler() { }
}
