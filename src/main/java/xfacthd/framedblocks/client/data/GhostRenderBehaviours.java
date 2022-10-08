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
                    if (block == FBContent.blockFramedDoublePanel.get())
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
                FBContent.blockFramedDoor.get(), FBContent.blockFramedIronDoor.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new SlabGhostRenderBehaviour(),
                FBContent.blockFramedSlab.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new PanelGhostRenderBehaviour(),
                FBContent.blockFramedPanel.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new StandingAndWallBlockGhostRenderBehaviour(),
                FBContent.blockFramedSign.get(),
                FBContent.blockFramedTorch.get(),
                FBContent.blockFramedSoulTorch.get(),
                FBContent.blockFramedRedstoneTorch.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new CollapsibleBlockGhostRenderBehaviour(),
                FBContent.blockFramedCollapsibleBlock.get()
        );

        GhostBlockRenderer.registerBehaviour(
                new RailSlopeGhostRenderBehaviour(),
                Items.RAIL,
                Items.POWERED_RAIL,
                Items.DETECTOR_RAIL,
                Items.ACTIVATOR_RAIL
        );

        GhostBlockRenderer.registerBehaviour(
                new BlueprintGhostRenderBehaviour(),
                FBContent.itemFramedBlueprint.get()
        );
    }



    private GhostRenderBehaviours() { }
}
