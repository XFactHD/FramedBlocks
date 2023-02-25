package xfacthd.framedblocks.client.model.slopeslab;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedStackedSlopeSlabModel extends FramedDoubleBlockModel
{
    private final boolean top;

    public FramedStackedSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, true);
        this.top = state.getValue(FramedProperties.TOP);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data)
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



    public static BlockState itemSourceSlab()
    {
        return FBContent.blockFramedStackedSlopeSlab.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    public static BlockState itemSourceCorner()
    {
        return FBContent.blockFramedFlatStackedSlopeSlabCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    public static BlockState itemSourceInnerCorner()
    {
        return FBContent.blockFramedFlatStackedInnerSlopeSlabCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}