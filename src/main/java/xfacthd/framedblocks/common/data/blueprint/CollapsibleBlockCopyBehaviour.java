package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class CollapsibleBlockCopyBehaviour implements BlueprintCopyBehaviour
{
    @Override
    public void postProcessPaste(
            Level level, BlockPos pos, Player player, CompoundTag blueprintData, ItemStack dummyStack
    )
    {
        if (level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)
        {
            NullableDirection face = NullableDirection.fromDirection(be.getCollapsedFace());
            level.setBlockAndUpdate(
                    pos,
                    level.getBlockState(pos).setValue(PropertyHolder.NULLABLE_FACE, face)
            );
        }
    }
}
