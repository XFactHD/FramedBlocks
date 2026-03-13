package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.util.serdes.DelegateValueInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

public final class NetworkValueInput extends DelegateValueInput
{
    private final FramedBlockEntity blockEntity;
    boolean needRenderUpdate = false;
    boolean needCullingUpdate = false;
    boolean needLightUpdate = false;

    private NetworkValueInput(FramedBlockEntity blockEntity, ValueInput input)
    {
        super(input);
        this.blockEntity = blockEntity;
    }

    public CamoContainer<?, ?> readCamo(String key, boolean secondary)
    {
        CamoContainer<?, ?> newCamo = CamoContainerHelper.readFromNetwork(delegate.child(key));
        if (!newCamo.equals(blockEntity.getCamo(secondary)))
        {
            int oldLight = blockEntity.getLightValue();
            blockEntity.setCamoNoUpdate(newCamo, secondary);
            if (oldLight != blockEntity.getLightValue())
            {
                requestLightUpdate();
            }

            requestRenderUpdate();
            requestCullingUpdate();
        }
        return newCamo;
    }

    public void requestRenderUpdate()
    {
        needRenderUpdate = true;
    }

    public void requestCullingUpdate()
    {
        needCullingUpdate = true;
    }

    public void requestLightUpdate()
    {
        needLightUpdate = true;
    }

    private void finishUpdateTag()
    {
        if (needCullingUpdate)
        {
            blockEntity.markCullStateDirty();
        }
        blockEntity.requestModelDataUpdate();
    }

    private void finishUpdatePacket()
    {
        if (needLightUpdate)
        {
            blockEntity.doLightUpdate();
        }
        if (needCullingUpdate)
        {
            blockEntity.updateCulling(true, false);
        }
        if (needRenderUpdate)
        {
            blockEntity.requestModelDataUpdate();

            BlockState state = blockEntity.getBlockState();
            blockEntity.level().sendBlockUpdated(blockEntity.getBlockPos(), state, state, Block.UPDATE_ALL);
        }
    }

    static void handleUpdateTag(FramedBlockEntity blockEntity, ValueInput input)
    {
        NetworkValueInput netInput = new NetworkValueInput(blockEntity, input);
        blockEntity.readFromDataPacket(netInput);
        netInput.finishUpdateTag();
    }

    static void handleUpdatePacket(FramedBlockEntity blockEntity, ValueInput input)
    {
        NetworkValueInput netInput = new NetworkValueInput(blockEntity, input);
        blockEntity.readFromDataPacket(netInput);
        netInput.finishUpdatePacket();
    }
}
