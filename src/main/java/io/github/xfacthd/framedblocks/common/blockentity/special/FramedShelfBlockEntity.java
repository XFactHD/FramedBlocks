package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.DelegatingFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.WrappedFramedBlockEntity;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;

public class FramedShelfBlockEntity extends ShelfBlockEntity implements DelegatingFramedBlockEntity {
    private final WrappedFramedBlockEntity delegate;

    public FramedShelfBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.delegate = new WrappedFramedBlockEntity(this);
    }

    @Override
    public BlockEntityType<?> getType() {
        return FBContent.BE_TYPE_FRAMED_SHELF.value();
    }

    @Override
    public WrappedFramedBlockEntity unwrap() {
        return delegate;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        delegate.setRemoved();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        delegate.clearRemoved();
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        delegate.setLevel(level);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        delegate.setBlockState(state);
    }

    @Override
    public ModelData getModelData() {
        return delegate.getModelData();
    }

    @Override
    public void onLoad() {
        delegate.onLoadInternal();
        super.onLoad();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return delegate.getUpdateTag(registries, super::getUpdateTag);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        delegate.handleUpdateTag(input, super::handleUpdateTag);
    }

    @Override
    public void onDataPacket(Connection net, ValueInput input) {
        delegate.onDataPacket(net, input, super::onDataPacket);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        delegate.collectImplicitComponentsForDelegate(builder);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter getter) {
        super.applyImplicitComponents(getter);
        delegate.applyImplicitComponentsForDelegate(getter);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        delegate.removeComponentsFromTag(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        delegate.loadAdditionalInternal(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        delegate.saveAdditionalInternal(output);
    }
}
