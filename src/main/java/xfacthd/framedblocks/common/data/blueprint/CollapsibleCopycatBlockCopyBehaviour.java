package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xfacthd.framedblocks.api.blueprint.BlueprintData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;
import xfacthd.framedblocks.common.data.component.CollapsibleCopycatBlockData;

public final class CollapsibleCopycatBlockCopyBehaviour extends DummyDataHandlingCopyBehaviour<CollapsibleCopycatBlockData>
{
    public CollapsibleCopycatBlockCopyBehaviour()
    {
        super(FBContent.DC_TYPE_COLLAPSIBLE_COPYCAT_BLOCK_DATA.value(), CollapsibleCopycatBlockData.EMPTY);
    }

    @Override
    public void postProcessPaste(Level level, BlockPos pos, Player player, BlueprintData data, ItemStack dummyStack)
    {
        if (level.getBlockEntity(pos) instanceof FramedCollapsibleCopycatBlockEntity be)
        {
            be.updateFaceSolidity();
        }
    }
}
