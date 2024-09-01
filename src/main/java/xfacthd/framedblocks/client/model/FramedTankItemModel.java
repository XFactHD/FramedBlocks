package xfacthd.framedblocks.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.Map;

public final class FramedTankItemModel extends BakedModelWrapper<BakedModel>
{
    private FramedTankItemModel(BakedModel baseModel)
    {
        super(baseModel);
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext ctx, PoseStack poseStack, boolean leftHand)
    {
        return this;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return true;
    }

    public BakedModel getBaseModel()
    {
        return originalModel;
    }



    public static void wrap(Map<ModelResourceLocation, BakedModel> registry)
    {
        ModelResourceLocation location = ModelResourceLocation.inventory(Utils.getKeyOrThrow(FBContent.BLOCK_FRAMED_TANK).location());
        registry.compute(location, (loc, model) -> model != null ? new FramedTankItemModel(model) : null);
    }
}
