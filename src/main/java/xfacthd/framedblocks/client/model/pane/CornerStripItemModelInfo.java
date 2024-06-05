package xfacthd.framedblocks.client.model.pane;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.Utils;

public final class CornerStripItemModelInfo implements ItemModelInfo
{
    public static final CornerStripItemModelInfo INSTANCE = new CornerStripItemModelInfo();

    private CornerStripItemModelInfo() { }

    @Override
    public void applyItemTransform(PoseStack poseStack, ItemDisplayContext ctx, boolean leftHand)
    {
        if (Utils.isHandContext(ctx))
        {
            if (ctx.firstPerson())
            {
                poseStack.mulPose(Quaternions.YP_90);
            }
            poseStack.translate(0, .5, 0);
        }
    }
}
