package xfacthd.framedblocks.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedCollapsibleBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler
{
    @SubscribeEvent
    public static void onBlockLeftClick(final PlayerInteractEvent.LeftClickBlock event)
    {
        boolean success = false;

        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        Player player = event.getPlayer();
        BlockState state = level.getBlockState(pos);

        if (state.is(FBContent.blockFramedCollapsibleBlock.get()))
        {
            success = FramedCollapsibleBlock.onLeftClick(level, pos, player);
        }
        else if (state.is(FBContent.blockFramedPrismCorner.get()) ||
                state.is(FBContent.blockFramedInnerPrismCorner.get()) ||
                state.is(FBContent.blockFramedDoublePrismCorner.get())
        )
        {
            if (player.getMainHandItem().getItem() == FBContent.itemFramedHammer.get())
            {
                level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.OFFSET, !state.getValue(PropertyHolder.OFFSET)));
                success = true;
            }
        }

        if (success)
        {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }
}