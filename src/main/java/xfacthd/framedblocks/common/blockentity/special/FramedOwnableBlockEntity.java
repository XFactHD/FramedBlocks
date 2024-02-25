package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
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
        setChanged();

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
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();
        if (owner != null)
        {
            tag.putUUID("owner", owner);
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        super.handleUpdateTag(nbt);
        if (nbt.contains("owner"))
        {
            owner = nbt.getUUID("owner");
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        if (owner != null)
        {
            tag.putUUID("owner", owner);
        }
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        if (tag.contains("owner"))
        {
            owner = tag.getUUID("owner");
        }
    }
}
