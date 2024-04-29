package xfacthd.framedblocks.common.data;

import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.blueprint.*;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;

public final class BlueprintBehaviours
{
    public static void register()
    {
        FramedBlueprintItem.registerBehaviour(
                new DoubleSlabCopyBehaviour(),
                FBContent.BLOCK_FRAMED_DOUBLE_SLAB.value()
        );

        FramedBlueprintItem.registerBehaviour(
                new DoublePanelCopyBehaviour(),
                FBContent.BLOCK_FRAMED_DOUBLE_PANEL.value()
        );

        FramedBlueprintItem.registerBehaviour(
                new DoorCopyBehaviour(),
                FBContent.BLOCK_FRAMED_DOOR.value(),
                FBContent.BLOCK_FRAMED_IRON_DOOR.value()
        );

        FramedBlueprintItem.registerBehaviour(
                new CollapsibleBlockCopyBehaviour(),
                FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.value()
        );

        FramedBlueprintItem.registerBehaviour(
                new CollapsibleCopycatBlockCopyBehaviour(),
                FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK.value()
        );

        FramedBlueprintItem.registerBehaviour(
                new FlowerPotCopyBehaviour(),
                FBContent.BLOCK_FRAMED_FLOWER_POT.value()
        );
    }



    private BlueprintBehaviours() { }
}
