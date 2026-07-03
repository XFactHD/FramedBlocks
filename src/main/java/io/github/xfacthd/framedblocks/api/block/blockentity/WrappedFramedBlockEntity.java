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

/// Extended [FramedBlockEntity] implementation for wrapping on "foreign" BEs to avoid
/// having to re-implement their functionality on top of [FramedBlockEntity].
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

    /// Serialize this BE and its owning BE to a [CompoundTag] for initial chunk sync while protecting this BE
    /// against having its disk format written to the update tag.
    ///
    /// @param registries     The registries used for serialization
    /// @param superTagWriter A reference to the owning BE's [#getUpdateTag(HolderLookup.Provider)] method
    /// @return The final update tag
    public CompoundTag getUpdateTag(HolderLookup.Provider registries, Function<HolderLookup.Provider, CompoundTag> superTagWriter) {
        suppressSave = true;
        CompoundTag tag = superTagWriter.apply(registries);
        suppressSave = false;
        return appendUpdateTag(tag, registries);
    }

    /// Deserialize this BE and its owning BE from the given update tag [ValueInput] while
    /// protecting this BE against trying to read its disk format from the update tag.
    ///
    /// @param input          The update tag to read from
    /// @param superTagReader A reference to the owning BE's [#handleUpdateTag(ValueInput)]
    public void handleUpdateTag(ValueInput input, Consumer<ValueInput> superTagReader) {
        suppressLoad = true;
        superTagReader.accept(input);
        suppressLoad = false;
        handleUpdateTag(input);
    }

    /// Deserialize this BE and its owning BE from the given update packet [ValueInput] while
    /// protecting this BE against trying to read its disk format from the update tag.
    ///
    /// @param con            The network connection of the local player
    /// @param input          The update tag to read from
    /// @param superTagReader A reference to the owning BE's [#onDataPacket(Connection, ValueInput)]
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

    /// Deserialize this BE from disk while protecting it against calls from network packet serialization.
    ///
    /// @param valueInput The input to read from
    @Override
    public void loadAdditionalInternal(ValueInput valueInput) {
        // Ensure loadAdditional() called from handleUpdateTag() in the wrapping BE does not attempt a "from-disk load" from a network tag
        if (!suppressLoad) {
            super.loadAdditionalInternal(valueInput);
        }
    }

    /// Serialize this BE to disk while protecting it against calls from network packet serialization.
    ///
    /// @param valueOutput The output to write to
    @Override
    public void saveAdditionalInternal(ValueOutput valueOutput) {
        // Ensure saveAdditional() called from getUpdateTag() in the wrapping BE does not attempt a "to-disk save" into a network tag
        if (!suppressSave) {
            super.saveAdditionalInternal(valueOutput);
        }
    }
}
