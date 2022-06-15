package xfacthd.framedblocks.client.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.FBContent;

public class FramedIronTrapDoorModel extends FramedTrapDoorModel
{
    public FramedIronTrapDoorModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
    }

    @Override
    protected BakedModel getCamoModel(BlockState camoState)
    {
        if (camoState.is(FBContent.blockFramedCube.get()))
        {
            return baseModel;
        }
        return super.getCamoModel(camoState);
    }

    @Override
    protected boolean forceUngeneratedBaseModel() { return true; }
}