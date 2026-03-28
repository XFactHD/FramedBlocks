package io.github.xfacthd.framedblocks.api.block.blockentity;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class WrappedFramedBlockEntity extends FramedBlockEntity {
    private boolean suppressLoad = false;
    private boolean suppressSave = false;

    public WrappedFramedBlockEntity(BlockEntity owner) {
        super(owner.getType(), owner.getBlockPos(), owner.getBlockState());
    }

    @Override
    public void setRemoved() {
        remove = true;
    }

    @Override
    public void clearRemoved() {
        remove = false;
    }

    @Override
    public void onLoadInternal() {
        super.onLoadInternal();
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries, Function<HolderLookup.Provider, CompoundTag> superTagWriter) {
        suppressSave = true;
        CompoundTag tag = superTagWriter.apply(registries);
        suppressSave = false;
        return appendUpdateTag(tag, registries);
    }

    public void handleUpdateTag(ValueInput input, Consumer<ValueInput> superTagReader) {
        suppressLoad = true;
        superTagReader.accept(input);
        suppressLoad = false;
        handleUpdateTag(input);
    }

    public void onDataPacket(Connection con, ValueInput input, BiConsumer<Connection, ValueInput> superTagReader) {
        suppressLoad = true;
        superTagReader.accept(con, input);
        suppressLoad = false;
        onDataPacket(con, input);
    }

    public void collectImplicitComponentsForDelegate(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
    }

    public void applyImplicitComponentsForDelegate(DataComponentGetter getter) {
        super.applyImplicitComponents(getter);
    }

    @Override
    public void loadAdditionalInternal(ValueInput valueInput) {
        // Ensure loadAdditional() called from handleUpdateTag() in the wrapping BE does not attempt a "from-disk load" from a network tag
        if (!suppressLoad)
        {
            super.loadAdditionalInternal(valueInput);
        }
    }

    @Override
    public void saveAdditionalInternal(ValueOutput valueOutput) {
        // Ensure saveAdditional() called from getUpdateTag() in the wrapping BE does not attempt a "to-disk save" into a network tag
        if (!suppressSave)
        {
            super.saveAdditionalInternal(valueOutput);
        }
    }
}
