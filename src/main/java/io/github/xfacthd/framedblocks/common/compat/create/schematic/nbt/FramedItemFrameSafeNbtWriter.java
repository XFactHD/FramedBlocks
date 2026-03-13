package io.github.xfacthd.framedblocks.common.compat.create.schematic.nbt;

import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.compat.create.FramedBlockSafeNbtWriter;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedItemFrameBlockEntity;
import net.createmod.catnip.nbt.NBTProcessors;
import net.minecraft.nbt.CompoundTag;

public final class FramedItemFrameSafeNbtWriter extends FramedBlockSafeNbtWriter
{
    @Override
    protected void cleanupTag(IFramedBlockEntity fbe, CompoundTag tag)
    {
        NBTProcessors.itemProcessor(FramedItemFrameBlockEntity.ITEM_NBT_KEY).apply(tag);
    }
}
