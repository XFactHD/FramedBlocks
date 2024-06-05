package xfacthd.framedblocks.api.model.wrapping.itemmodel;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import xfacthd.framedblocks.api.util.Utils;

import java.util.function.Predicate;

public final class TranslatedItemModelInfo implements ItemModelInfo
{
    public static final TranslatedItemModelInfo HAND_Y_HALF_UP = TranslatedItemModelInfo.hand(0F, .5F, 0F);

    private final Predicate<ItemDisplayContext> ctxSelector;
    private final float tx;
    private final float ty;
    private final float tz;

    public TranslatedItemModelInfo(Predicate<ItemDisplayContext> ctxSelector, float tx, float ty, float tz)
    {
        this.ctxSelector = ctxSelector;
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }

    @Override
    public void applyItemTransform(PoseStack poseStack, ItemDisplayContext ctx, boolean leftHand)
    {
        if (ctxSelector.test(ctx))
        {
            poseStack.translate(tx, ty, tz);
        }
    }



    public static TranslatedItemModelInfo hand(float tx, float ty, float tz)
    {
        return new TranslatedItemModelInfo(Utils::isHandContext, tx, ty, tz);
    }

    public static TranslatedItemModelInfo handOrGui(float tx, float ty, float tz)
    {
        return new TranslatedItemModelInfo(ctx -> Utils.isHandContext(ctx) || ctx == ItemDisplayContext.GUI, tx, ty, tz);
    }

    public static TranslatedItemModelInfo handGuiOrFixed(float tx, float ty, float tz)
    {
        return new TranslatedItemModelInfo(
                ctx -> Utils.isHandContext(ctx) || ctx == ItemDisplayContext.GUI || ctx == ItemDisplayContext.FIXED,
                tx, ty, tz
        );
    }
}
