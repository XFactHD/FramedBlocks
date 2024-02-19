package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;

public final class CollapsibleCopycatBlockCopyBehaviour implements BlueprintCopyBehaviour
{
    @Override
    public void postProcessPaste(
            Level level, BlockPos pos, Player player, CompoundTag blueprintData, ItemStack dummyStack
    )
    {
        if (level.getBlockEntity(pos) instanceof FramedCollapsibleCopycatBlockEntity be)
        {
            be.updateFaceSolidity();
        }
    }
}
