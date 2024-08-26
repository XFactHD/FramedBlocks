package xfacthd.framedblocks.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.blockentity.IFramedDoubleBlockEntity;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.api.util.Utils;

public class DoubleBlockItemModelInfo implements ItemModelInfo
{
    public static final DoubleBlockItemModelInfo INSTANCE = new DoubleBlockItemModelInfo();
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final DoubleBlockItemModelInfo Y_HALF_UP = new DoubleBlockItemModelInfo.HandTranslated(0F, .5F, 0F);

    @Override
    public final ModelData buildItemModelData(BlockState state, CamoList camos)
    {
        FramedBlockData fbDataOne = new FramedBlockData(camos.getCamo(0).getContent(), false);
        ModelData.Builder builderOne = ModelData.builder().with(FramedBlockData.PROPERTY, fbDataOne);
        appendItemModelPartData(builderOne, state, false);

        FramedBlockData fbDataTwo = new FramedBlockData(camos.getCamo(1).getContent(), true);
        ModelData.Builder builderTwo = ModelData.builder().with(FramedBlockData.PROPERTY, fbDataTwo);
        appendItemModelPartData(builderTwo, state, true);

        ModelData.Builder builder = ModelData.builder()
                .with(IFramedDoubleBlockEntity.DATA_ONE, builderOne.build())
                .with(IFramedDoubleBlockEntity.DATA_TWO, builderTwo.build());
        appendItemModelData(builder);

        return builder.build();
    }

    protected void appendItemModelData(ModelData.Builder builder) { }

    protected void appendItemModelPartData(ModelData.Builder builder, BlockState state, boolean second) { }



    public static class HandTranslated extends DoubleBlockItemModelInfo
    {
        private final float tx;
        private final float ty;
        private final float tz;

        public HandTranslated(float tx, float ty, float tz)
        {
            this.tx = tx;
            this.ty = ty;
            this.tz = tz;
        }

        @Override
        public void applyItemTransform(PoseStack poseStack, ItemDisplayContext ctx, boolean leftHand)
        {
            if (Utils.isHandContext(ctx))
            {
                poseStack.translate(tx, ty, tz);
            }
        }
    }
}
