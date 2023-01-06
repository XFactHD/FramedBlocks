package xfacthd.framedblocks.client.model.slopeslab;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedFlatElevatedInnerDoubleSlopeSlabCornerModel extends FramedDoubleBlockModel
{
    private final boolean top;

    public FramedFlatElevatedInnerDoubleSlopeSlabCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, true);
        this.top = state.getValue(FramedProperties.TOP);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data)
    {
        return getSpriteOrDefault(
                data,
                top ? FramedDoubleBlockEntity.DATA_LEFT : FramedDoubleBlockEntity.DATA_RIGHT,
                top ? getModels().getA() : getModels().getB()
        );
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
