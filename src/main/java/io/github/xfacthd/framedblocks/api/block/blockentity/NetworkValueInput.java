package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.util.serdes.DelegateValueInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;

/// Extended [ValueInput] used during deserialization of BE update network packets.
/// Provides the ability to request various types of updates to perform after
/// deserializing the packet data.
public final class NetworkValueInput extends DelegateValueInput {
    private final FramedBlockEntity blockEntity;
    boolean needRenderUpdate = false;
    boolean needCullingUpdate = false;
    boolean needLightUpdate = false;

    private NetworkValueInput(FramedBlockEntity blockEntity, ValueInput input) {
        super(input);
        this.blockEntity = blockEntity;
    }

    /// Read the camo at the given key from the packet data, apply it to the "slot" indicated
    /// by the `secondary` flag and request relevant updates.
    ///
    /// @param key       The NBT key of the camo to read
    /// @param secondary Whether the camo should be applied to the first or second camo "slot"
    public CamoContainer<?, ?> readCamo(String key, boolean secondary) {
        CamoContainer<?, ?> newCamo = CamoContainerHelper.readFromNetwork(delegate.child(key));
        if (!newCamo.equals(blockEntity.getCamo(secondary))) {
            int oldLight = blockEntity.getLightValue();
            blockEntity.setCamoNoUpdate(newCamo, secondary);
            if (oldLight != blockEntity.getLightValue()) {
                requestLightUpdate();
            }

            requestRenderUpdate();
            requestCullingUpdate();
        }
        return newCamo;
    }

    /// Request the chunk section containing this block to be re-rendered.
    ///
    /// Ignored when reading the [update tag][BlockEntity#handleUpdateTag(ValueInput)] as vanilla
    /// already performs a re-render after handling it.
    public void requestRenderUpdate() {
        needRenderUpdate = true;
    }

    /// Request the occlusion state of the block to be recomputed.
    public void requestCullingUpdate() {
        needCullingUpdate = true;
    }

    /// Request the "published" dynamic light value to be updated.
    public void requestLightUpdate() {
        needLightUpdate = true;
    }

    private void finishUpdateTag() {
        if (needCullingUpdate) {
            blockEntity.clientData.markCullStateDirty();
        }
        blockEntity.requestModelDataUpdate();
    }

    private void finishUpdatePacket() {
        if (needLightUpdate) {
            blockEntity.doLightUpdate();
        }
        if (needCullingUpdate) {
            blockEntity.updateCulling(true, false);
        }
        if (needRenderUpdate) {
            blockEntity.requestModelDataUpdate();
            blockEntity.clientData.markSectionRangeDirty();
        }
    }

    static void handleUpdateTag(FramedBlockEntity blockEntity, ValueInput input) {
        NetworkValueInput netInput = new NetworkValueInput(blockEntity, input);
        blockEntity.readFromDataPacket(netInput);
        netInput.finishUpdateTag();
    }

    static void handleUpdatePacket(FramedBlockEntity blockEntity, ValueInput input) {
        NetworkValueInput netInput = new NetworkValueInput(blockEntity, input);
        blockEntity.readFromDataPacket(netInput);
        netInput.finishUpdatePacket();
    }
}
