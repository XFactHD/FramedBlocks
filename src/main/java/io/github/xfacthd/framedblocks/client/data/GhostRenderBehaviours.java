package io.github.xfacthd.framedblocks.client.data;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.ghost.DoubleBlockGhostRenderBehaviour;
import io.github.xfacthd.framedblocks.api.ghost.RegisterGhostRenderBehavioursEvent;
import io.github.xfacthd.framedblocks.client.data.ghost.*;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.List;

public final class GhostRenderBehaviours {
    public static void onRegisterGhostRenderBehaviours(RegisterGhostRenderBehavioursEvent event) {
        //noinspection SuspiciousToArrayCall
        event.registerBlocks(DoubleBlockGhostRenderBehaviour.INSTANCE, FBContent.getRegisteredBlocks()
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
        event.registerBlocks(AdjustableDoubleBlockGhostRenderBehaviour.standard(), List.of(
                FBContent.BLOCK_FRAMED_ADJ_DOUBLE_SLAB,
                FBContent.BLOCK_FRAMED_ADJ_DOUBLE_PANEL
        ));
        event.registerBlocks(AdjustableDoubleBlockGhostRenderBehaviour.copycat(), List.of(
                FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_SLAB,
                FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_PANEL
        ));
        event.registerBlock(new BoardGhostRenderBehaviour(), FBContent.BLOCK_FRAMED_BOARD);
        event.registerBlock(new LayeredCubeGhostRenderBehaviour(), FBContent.BLOCK_FRAMED_LAYERED_CUBE);
        event.registerBlock(new TargetGhostRenderBehaviour(), FBContent.BLOCK_FRAMED_TARGET);
    }

    private GhostRenderBehaviours() { }
}
