package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.model.data.FramedBlockData;

public class FramedCubeModel extends FramedCubeBaseModel
{
    public FramedCubeModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
    {
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (fbData == null || fbData.getCamoState().isAir())
        {
            return baseModel.getRenderTypes(state, rand, data);
        }
        return super.getRenderTypes(state, rand, data);
    }

    @Override
    protected BlockState getNoCamoModelState(BlockState camoState, FramedBlockData fbData)
    {
        return super.getNoCamoModelState(this.state, fbData);
    }
}
