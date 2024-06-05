package xfacthd.framedblocks.client.model.slopepanelcorner;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.Utils;

public final class SmallInnerCornerSlopePanelItemModelInfo implements ItemModelInfo
{
    public static final SmallInnerCornerSlopePanelItemModelInfo INSTANCE = new SmallInnerCornerSlopePanelItemModelInfo();

    private SmallInnerCornerSlopePanelItemModelInfo() { }

    @Override
    public void applyItemTransform(PoseStack poseStack, ItemDisplayContext ctx, boolean leftHand)
    {
        if (Utils.isHandContext(ctx))
        {
            poseStack.mulPose(Quaternions.YP_90);
        }
    }
}
