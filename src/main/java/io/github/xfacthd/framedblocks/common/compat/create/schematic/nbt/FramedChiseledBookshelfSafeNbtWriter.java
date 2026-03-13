package io.github.xfacthd.framedblocks.common.compat.create.schematic.nbt;

import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.compat.create.FramedBlockSafeNbtWriter;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedChiseledBookshelfBlockEntity;
import net.minecraft.nbt.CompoundTag;

public final class FramedChiseledBookshelfSafeNbtWriter extends FramedBlockSafeNbtWriter
{
    @Override
    protected void cleanupTag(IFramedBlockEntity fbe, CompoundTag tag)
    {
        tag.remove(FramedChiseledBookshelfBlockEntity.INVENTORY_NBT_KEY);
        tag.putInt(FramedChiseledBookshelfBlockEntity.LAST_SLOT_NBT_KEY, -1);
    }
}
