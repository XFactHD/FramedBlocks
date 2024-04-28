package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.common.FBContent;

import java.util.UUID;

public class FramedOwnableBlockEntity extends FramedBlockEntity
{
    private UUID owner;

    protected FramedOwnableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public FramedOwnableBlockEntity(BlockPos pos, BlockState state)
    {
        this(FBContent.BE_TYPE_FRAMED_OWNABLE_BLOCK.value(), pos, state);
    }

    public void setOwner(UUID owner, boolean forceSync)
    {
        this.owner = owner;
        setChangedWithoutSignalUpdate();

        if (forceSync)
        {
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public UUID getOwner()
    {
        return owner;
    }

    @Override
    protected void writeToDataPacket(CompoundTag nbt)
    {
        super.writeToDataPacket(nbt);
        if (owner != null)
        {
            nbt.put("owner", NbtUtils.createUUID(owner));
        }
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        if (nbt.contains("owner"))
        {
            //noinspection ConstantConditions
            owner = NbtUtils.loadUUID(nbt.get("owner"));
        }
        return super.readFromDataPacket(nbt);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag tag = super.getUpdateTag(provider);
        if (owner != null)
        {
            tag.putUUID("owner", owner);
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.handleUpdateTag(nbt, provider);
        if (nbt.contains("owner"))
        {
            owner = nbt.getUUID("owner");
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        if (owner != null)
        {
            tag.putUUID("owner", owner);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        if (tag.contains("owner"))
        {
            owner = tag.getUUID("owner");
        }
    }
}
