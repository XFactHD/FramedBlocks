package xfacthd.framedblocks.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedCollapsibleBlock;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import java.util.ArrayList;
import java.util.List;

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
            if (player.getMainHandItem().getItem() == FBContent.itemFramedHammer.get())
            {
                world.setBlockAndUpdate(pos, state.setValue(PropertyHolder.OFFSET, !state.getValue(PropertyHolder.OFFSET)));
                success = true;
            }
        }

        if (ServerConfig.enableIntangibleFeature && !success && block instanceof IFramedBlock && ((IFramedBlock) block).getBlockType().allowMakingIntangible())
        {
            TileEntity te = world.getBlockEntity(pos);
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

    private static boolean iteratingNewTEs = false;
    private static final List<FramedTileEntity> NEW_TILE_ENTITIES = new ArrayList<>();
    private static final List<FramedTileEntity> PENDING_NEW_TILE_ENTITIES = new ArrayList<>();

    @SubscribeEvent
    public static void onServerTick(final TickEvent.ServerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) { return; }

        if (!NEW_TILE_ENTITIES.isEmpty())
        {
            iteratingNewTEs = true;

            NEW_TILE_ENTITIES.forEach(FramedTileEntity::checkSolidStateOnLoad);
            NEW_TILE_ENTITIES.clear();

            iteratingNewTEs = false;

            if (!PENDING_NEW_TILE_ENTITIES.isEmpty())
            {
                NEW_TILE_ENTITIES.addAll(PENDING_NEW_TILE_ENTITIES);
                PENDING_NEW_TILE_ENTITIES.clear();
            }
        }
    }

    public static void addNewTileEntity(FramedTileEntity te)
    {
        if (iteratingNewTEs)
        {
            PENDING_NEW_TILE_ENTITIES.add(te);
        }
        else
        {
            NEW_TILE_ENTITIES.add(te);
        }
    }
}