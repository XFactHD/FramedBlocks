package xfacthd.framedblocks.client.data;

import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.client.data.ghost.*;
import xfacthd.framedblocks.client.render.GhostBlockRenderer;
import xfacthd.framedblocks.common.FBContent;

public final class GhostRenderBehaviours
{
    public static void register()
    {
        GhostRenderBehaviour doubleBlockBehaviour = new DoubleBlockGhostRenderBehaviour();

        FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(IFramedBlock.class::isInstance)
                .filter(b -> ((IFramedBlock) b).getBlockType().isDoubleBlock())
                .forEach(block ->
                {
                    if (block == FBContent.BLOCK_FRAMED_DOUBLE_PANEL.get())
                    {
                        GhostBlockRenderer.registerBehaviour(
                                new DoublePanelGhostRenderBehaviour(),
                                block
                        );
                    }
                    else
                    {
                        GhostBlockRenderer.registerBehaviour(
                                doubleBlockBehaviour,
                                block
                        );
                    }
                });

        GhostBlockRenderer.registerBehaviour(
                new DoorGhostRenderBehaviour(),
                FBContent.BLOCK_FRAMED_DOOR.get(), FBContent.BLOCK_FRAMED_IRON_DOOR.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new SlabGhostRenderBehaviour(),
                FBContent.BLOCK_FRAMED_SLAB.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new PanelGhostRenderBehaviour(),
                FBContent.BLOCK_FRAMED_PANEL.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new StandingAndWallBlockGhostRenderBehaviour(),
                FBContent.BLOCK_FRAMED_SIGN.get(),
                FBContent.BLOCK_FRAMED_TORCH.get(),
                FBContent.BLOCK_FRAMED_SOUL_TORCH.get(),
                FBContent.BLOCK_FRAMED_REDSTONE_TORCH.get(),
                FBContent.BLOCK_FRAMED_HALF_SLOPE.get(),
                FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new CollapsibleBlockGhostRenderBehaviour(),
                FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new RailSlopeGhostRenderBehaviour(),
                Items.RAIL,
                Items.POWERED_RAIL,
                Items.DETECTOR_RAIL,
                Items.ACTIVATOR_RAIL
        );

        GhostBlockRenderer.registerBehaviour(
                new FancyRailGhostRenderBehaviour(),
                FBContent.BLOCK_FRAMED_FANCY_RAIL.get().asItem(),
                FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.get().asItem(),
                FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.get().asItem(),
                FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.get().asItem()
        );

        GhostBlockRenderer.registerBehaviour(
                new BlueprintGhostRenderBehaviour(),
                FBContent.ITEM_FRAMED_BLUEPRINT.get()
        );
    }



    private GhostRenderBehaviours() { }
}
