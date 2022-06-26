package xfacthd.framedblocks.common.data;

import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.blueprint.*;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;

public final class BlueprintBehaviours
{
    public static void register()
    {
        BlueprintCopyBehaviour doubleBlockBehaviour = new DoubleBlockCopyBehaviour();

        FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(IFramedBlock.class::isInstance)
                .filter(b -> ((IFramedBlock) b).getBlockType().isDoubleBlock())
                .forEach(block ->
                {
                    if (block == FBContent.blockFramedDoubleSlab.get())
                    {
                        FramedBlueprintItem.registerBehaviour(new DoubleSlabCopyBehaviour(), block);
                    }
                    else if (block == FBContent.blockFramedDoublePanel.get())
                    {
                        FramedBlueprintItem.registerBehaviour(new DoublePanelCopyBehaviour(), block);
                    }
                    else
                    {
                        FramedBlueprintItem.registerBehaviour(doubleBlockBehaviour, block);
                    }
                });

        FramedBlueprintItem.registerBehaviour(
                new DoorCopyBehaviour(),
                FBContent.blockFramedDoor.get(),
                FBContent.blockFramedIronDoor.get()
        );

        FramedBlueprintItem.registerBehaviour(
                new CollapsibleBlockCopyBehaviour(),
                FBContent.blockFramedCollapsibleBlock.get()
        );
    }



    private BlueprintBehaviours() { }
}
