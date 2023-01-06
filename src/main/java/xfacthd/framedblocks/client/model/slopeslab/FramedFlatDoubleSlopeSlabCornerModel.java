package xfacthd.framedblocks.client.model.slopeslab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
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

public class FramedFlatDoubleSlopeSlabCornerModel extends FramedDoubleBlockModel
{
    private final boolean top;

    public FramedFlatDoubleSlopeSlabCornerModel(BlockState state, BakedModel baseModel)
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

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemTransforms.TransformType type)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedFlatDoubleSlopeSlabCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
