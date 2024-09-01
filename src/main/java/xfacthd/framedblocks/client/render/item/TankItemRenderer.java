package xfacthd.framedblocks.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import xfacthd.framedblocks.api.camo.CamoContent;
import xfacthd.framedblocks.api.model.ErrorModel;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.client.model.FramedTankItemModel;
import xfacthd.framedblocks.client.render.block.FramedTankRenderer;
import xfacthd.framedblocks.common.FBContent;

public final class TankItemRenderer extends BlockEntityWithoutLevelRenderer
{
    public TankItemRenderer()
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        // Pop off the transformations applied by ItemRenderer before calling this
        poseStack.popPose();
        poseStack.pushPose();

        boolean validModel = true;
        BakedModel model = renderer.getModel(stack, null, null, 0);
        if (model instanceof FramedTankItemModel tankModel)
        {
            model = tankModel.getBaseModel();
        }
        else
        {
            model = ErrorModel.get();
            validModel = false;
        }
        model = model.applyTransform(ctx, poseStack, ClientUtils.isLeftHand(ctx));
        poseStack.translate(-.5, -.5, -.5); // Replicate ItemRenderer's translation

        boolean glint = stack.hasFoil();
        for (BakedModel pass : model.getRenderPasses(stack, true))
        {
            for (RenderType type : pass.getRenderTypes(stack, true))
            {
                VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(buffer, type, true, glint);
                renderer.renderModelLists(pass, stack, light, overlay, poseStack, consumer);
            }
        }

        if (!validModel) return;

        CamoContent<?> camo = stack.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY).getCamo(0).getContent();
        if (camo.isSolid(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) return;

        SimpleFluidContent content = stack.getOrDefault(FBContent.DC_TYPE_TANK_CONTENTS, SimpleFluidContent.EMPTY);
        if (content.isEmpty()) return;

        IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(content.getFluid());
        ResourceLocation stillTex = fluidExt.getStillTexture();
        ResourceLocation flowTex = fluidExt.getFlowingTexture();
        int tint = fluidExt.getTintColor();
        RenderType renderType = ItemBlockRenderTypes.getRenderLayer(content.getFluid().defaultFluidState());

        FramedTankRenderer.renderContents(poseStack, buffer, renderType, light, content.getAmount(), stillTex, flowTex, tint);
    }
}
