package xfacthd.framedblocks.client.model.slopeslab;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedElevatedDoubleSlopeSlabModel extends FramedDoubleBlockModel
{
    private final boolean top;

    public FramedElevatedDoubleSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, true);
        this.top = state.getValue(FramedProperties.TOP);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        if (top)
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_LEFT, getModels().getA());
        }
        else
        {
            return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedElevatedDoubleSlopeSlab.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
