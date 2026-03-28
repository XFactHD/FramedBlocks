package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class FramedOwnableBlockEntity extends FramedBlockEntity {
    @Nullable
    private UUID owner;

    protected FramedOwnableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public FramedOwnableBlockEntity(BlockPos pos, BlockState state) {
        this(FBContent.BE_TYPE_FRAMED_OWNABLE_BLOCK.value(), pos, state);
    }

    public void setOwner(UUID owner, boolean forceSync) {
        this.owner = owner;
        setChangedWithoutSignalUpdate();

        if (forceSync) {
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public @Nullable UUID getOwner() {
        return owner;
    }

    @Override
    protected void writeToDataPacket(ValueOutput valueOutput) {
        super.writeToDataPacket(valueOutput);
        valueOutput.storeNullable("owner", UUIDUtil.CODEC, owner);
    }

    @Override
    protected void readFromDataPacket(NetworkValueInput input) {
        super.readFromDataPacket(input);
        owner = input.read("owner", UUIDUtil.CODEC).orElse(null);
    }

    @Override
    public void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.storeNullable("owner", UUIDUtil.CODEC, owner);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        owner = valueInput.read("owner", UUIDUtil.CODEC).orElse(null);
    }
}
