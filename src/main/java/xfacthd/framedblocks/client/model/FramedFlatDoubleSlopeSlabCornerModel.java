package xfacthd.framedblocks.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedFlatDoubleSlopeSlabCornerBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedFlatDoubleSlopeSlabCornerModel extends FramedDoubleBlockModel
{
    private final Direction facing;
    private final boolean topHalf;
    private final boolean top;

    public FramedFlatDoubleSlopeSlabCornerModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel, true);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.topHalf = state.getValue(PropertyHolder.TOP_HALF);
        this.top = state.getValue(FramedProperties.TOP);
    }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        return FramedFlatDoubleSlopeSlabCornerBlockEntity.getBlockPair(facing, topHalf, top);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
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
