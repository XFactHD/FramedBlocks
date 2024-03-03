package xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.special.FramedItemFrameBlockEntity;

public class FramedItemFrameRenderer implements BlockEntityRenderer<FramedItemFrameBlockEntity>
{
    private static final double ITEM_Z_OFF = 0.4375D;
    private static final float DIR_OFF_MULT = 0.49875F;
    private static final float MAP_SCALE = 0.0078125F;
    private static final double MAX_NAMETAG_DIST_SQR = 64D * 64D;

    private final ItemRenderer itemRenderer;

    public FramedItemFrameRenderer(BlockEntityRendererProvider.Context ctx)
    {
        itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(
            FramedItemFrameBlockEntity be,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    )
    {
        poseStack.pushPose();

        Direction dir = be.getBlockState().getValue(BlockStateProperties.FACING).getOpposite();
        float dirOff = Utils.isPositive(dir) ? 0 : 1;
        poseStack.translate(
                dir.getStepX() * DIR_OFF_MULT + (Utils.isX(dir) ? dirOff : .5F),
                dir.getStepY() * DIR_OFF_MULT + (Utils.isY(dir) ? dirOff : .5F),
                dir.getStepZ() * DIR_OFF_MULT + (Utils.isZ(dir) ? dirOff : .5F)
        );

        boolean vert = Utils.isY(dir);
        float yRot = vert ? 0 : dir.toYRot();
        if (vert)
        {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90F * dir.getAxisDirection().getStep()));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yRot));

        ItemStack item = be.getItem();
        //noinspection ConstantConditions
        MapItemSavedData mapData = MapItem.getSavedData(item, be.getLevel());

        poseStack.translate(0.0D, 0.0D, ITEM_Z_OFF);
        float itemRotation = mapData != null ? (be.getRotation() % 4 * 2) : be.getRotation();
        poseStack.mulPose(Axis.ZP.rotationDegrees(itemRotation * 360.0F / 8.0F));

        if (mapData != null)
        {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

            poseStack.scale(MAP_SCALE, MAP_SCALE, MAP_SCALE);
            poseStack.translate(-64.0D, -64.0D, -1.0D);

            int mapLight = be.isGlowingFrame() ? 0x00F000D2 : packedLight;
            //noinspection ConstantConditions
            int mapId = MapItem.getMapId(item);
            Minecraft.getInstance().gameRenderer.getMapRenderer().render(poseStack, buffer, mapId, mapData, true, mapLight);
        }
        else
        {
            poseStack.scale(0.5F, 0.5F, 0.5F);

            int itemLight = be.isGlowingFrame() ? 0x00F000F0 : packedLight;
            itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, itemLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, be.getLevel(), 0);
        }

        poseStack.popPose();

        renderCustomItemName(be, poseStack, buffer, packedLight);
    }

    private static void renderCustomItemName(FramedItemFrameBlockEntity be, PoseStack poseStack, MultiBufferSource buffer, int packedLight)
    {
        ItemStack stack = be.getItem();

        if (!Minecraft.renderNames() || stack.isEmpty() || !stack.hasCustomHoverName()) return;
        if (!(Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult)) return;
        if (!be.getBlockPos().equals(hitResult.getBlockPos())) return;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double dist = camera.getPosition().distanceToSqr(hitResult.getLocation());
        if (dist > MAX_NAMETAG_DIST_SQR) return;

        Direction dir = be.getBlockState().getValue(BlockStateProperties.FACING);
        float dx = .5F + (dir.getStepX() * .46875F) - (dir.getStepX() * .3F);
        float dz = .5F + (dir.getStepZ() * .46875F) - (dir.getStepZ() * .3F);

        poseStack.pushPose();
        poseStack.translate(dx, 1.25F, dz);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        Font font = Minecraft.getInstance().font;
        Component name = stack.getHoverName();

        Matrix4f pose = poseStack.last().pose();
        int alpha = (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
        float x = -font.width(name) / 2F;

        font.drawInBatch(name, x, 0F, 553648127, false, pose, buffer, Font.DisplayMode.SEE_THROUGH, alpha, packedLight);
        font.drawInBatch(name, x, 0F, -1, false, pose, buffer, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(FramedItemFrameBlockEntity be, Vec3 cameraPos)
    {
        return be.hasItem() && BlockEntityRenderer.super.shouldRender(be, cameraPos);
    }
}
