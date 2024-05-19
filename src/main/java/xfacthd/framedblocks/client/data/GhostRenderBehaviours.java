package xfacthd.framedblocks.client.data;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.ghost.RegisterGhostRenderBehavioursEvent;
import xfacthd.framedblocks.client.data.ghost.*;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;

public final class GhostRenderBehaviours
{
    public static void onRegisterGhostRenderBehaviours(RegisterGhostRenderBehavioursEvent event)
    {
        //noinspection SuspiciousToArrayCall
        event.registerBlocks(new DoubleBlockGhostRenderBehaviour(), FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(IFramedBlock.class::isInstance)
                .filter(b -> ((IFramedBlock) b).getBlockType().isDoubleBlock())
                .toArray(Block[]::new)
        );
        event.registerBlocks(new DoorGhostRenderBehaviour(), List.of(
                FBContent.BLOCK_FRAMED_DOOR,
                FBContent.BLOCK_FRAMED_IRON_DOOR
        ));
        event.registerBlocks(new SlabGhostRenderBehaviour(), FBContent.BLOCK_FRAMED_SLAB.value());
        event.registerBlocks(new PanelGhostRenderBehaviour(), FBContent.BLOCK_FRAMED_PANEL.value());
        event.registerBlocks(new StandingAndWallBlockGhostRenderBehaviour(), List.of(
                FBContent.BLOCK_FRAMED_SIGN,
                FBContent.BLOCK_FRAMED_HANGING_SIGN,
                FBContent.BLOCK_FRAMED_TORCH,
                FBContent.BLOCK_FRAMED_SOUL_TORCH,
                FBContent.BLOCK_FRAMED_REDSTONE_TORCH,
                FBContent.BLOCK_FRAMED_HALF_SLOPE,
                FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE,
                FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL
        ));
        event.registerBlocks(new StandingAndWallDoubleBlockGhostRenderBehaviour(), List.of(
                FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL,
                FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL
        ));
        event.registerBlock(new CollapsibleBlockGhostRenderBehaviour(), FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK);
        event.registerBlock(new CollapsibleCopycatBlockGhostRenderBehaviour(), FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK);
        event.registerItems(
                RailSlopeGhostRenderBehaviour.INSTANCE,
                Items.RAIL,
                Items.POWERED_RAIL,
                Items.DETECTOR_RAIL,
                Items.ACTIVATOR_RAIL
        );
        event.registerBlocks(new FancyRailGhostRenderBehaviour(), List.of(
                FBContent.BLOCK_FRAMED_FANCY_RAIL,
                FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL,
                FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL,
                FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL
        ));
        event.registerItem(new BlueprintGhostRenderBehaviour(), FBContent.ITEM_FRAMED_BLUEPRINT);
        event.registerBlock(new FlowerPotGhostRenderBehaviour(), FBContent.BLOCK_FRAMED_FLOWER_POT);
    }



    private GhostRenderBehaviours() { }
}
