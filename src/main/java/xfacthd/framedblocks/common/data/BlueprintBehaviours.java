package xfacthd.framedblocks.common.data;

import xfacthd.framedblocks.api.blueprint.RegisterBlueprintCopyBehavioursEvent;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.blueprint.*;
import xfacthd.framedblocks.common.data.component.AdjustableDoubleBlockData;

import java.util.List;

public final class BlueprintBehaviours
{
    public static void onRegisterBlueprintCopyBehaviours(RegisterBlueprintCopyBehavioursEvent event)
    {
        event.register(new DoubleSlabCopyBehaviour(), FBContent.BLOCK_FRAMED_DOUBLE_SLAB);
        event.register(new DoublePanelCopyBehaviour(), FBContent.BLOCK_FRAMED_DOUBLE_PANEL);
        event.register(new DoorCopyBehaviour(), List.of(FBContent.BLOCK_FRAMED_DOOR, FBContent.BLOCK_FRAMED_IRON_DOOR));
        event.register(new CollapsibleBlockCopyBehaviour(), FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK);
        event.register(new CollapsibleCopycatBlockCopyBehaviour(), FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK);
        event.register(new FlowerPotCopyBehaviour(), FBContent.BLOCK_FRAMED_FLOWER_POT);
        event.register(
                new DummyDataHandlingCopyBehaviour<>(
                        FBContent.DC_TYPE_ADJ_DOUBLE_BLOCK_DATA.value(),
                        AdjustableDoubleBlockData.EMPTY
                ),
                List.of(
                        FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_SLAB
                )
        );
    }



    private BlueprintBehaviours() { }
}
