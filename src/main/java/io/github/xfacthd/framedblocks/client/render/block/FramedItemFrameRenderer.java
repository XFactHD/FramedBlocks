package io.github.xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.client.render.block.state.FramedItemFrameRenderState;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedItemFrameBlockEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FramedItemFrameRenderer implements BlockEntityRenderer<FramedItemFrameBlockEntity, FramedItemFrameRenderState>
{
    private static final double ITEM_Z_OFF = 0.4375D;
    private static final float DIR_OFF_MULT = 0.49875F;
    private static final float MAP_SCALE = 0.0078125F;
    private static final double MAX_NAMETAG_DIST_SQR = 64D * 64D;

    private final ItemModelResolver itemModelResolver;
    private final MapRenderState mapRenderState = new MapRenderState();
    private final MapRenderer mapRenderer;

    public FramedItemFrameRenderer(BlockEntityRendererProvider.Context ctx)
    {
        this.itemModelResolver = ctx.itemModelResolver();
        this.mapRenderer = Minecraft.getInstance().getMapRenderer();
    }

    @Override
    public void submit(FramedItemFrameRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        Direction dir = renderState.direction;
        float dirOff = DirUtils.isPositive(dir) ? 0 : 1;
        poseStack.translate(
                dir.getStepX() * DIR_OFF_MULT + (DirUtils.isX(dir) ? dirOff : .5F),
                dir.getStepY() * DIR_OFF_MULT + (DirUtils.isY(dir) ? dirOff : .5F),
                dir.getStepZ() * DIR_OFF_MULT + (DirUtils.isZ(dir) ? dirOff : .5F)
        );

        boolean vert = DirUtils.isY(dir);
        float yRot = vert ? 0 : dir.toYRot();
        if (vert)
        {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90F * dir.getAxisDirection().getStep()));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yRot));

        //noinspection ConstantConditions
        poseStack.translate(0.0D, 0.0D, ITEM_Z_OFF);
        float itemRotation = renderState.mapId != null ? (renderState.rotation % 4 * 2) : renderState.rotation;
        poseStack.mulPose(Axis.ZP.rotationDegrees(itemRotation * 360.0F / 8.0F));

        if (renderState.mapId != null)
        {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

            poseStack.scale(MAP_SCALE, MAP_SCALE, MAP_SCALE);
            poseStack.translate(-64.0D, -64.0D, -1.0D);

            int mapLight = renderState.isGlowFrame ? 0x00F000D2 : renderState.lightCoords;
            mapRenderer.render(mapRenderState, poseStack, submitNodeCollector, true, mapLight);
        }
        else if (!renderState.item.isEmpty())
        {
            poseStack.scale(0.5F, 0.5F, 0.5F);

            int itemLight = renderState.isGlowFrame ? 0x00F000F0 : renderState.lightCoords;
            renderState.item.submit(poseStack, submitNodeCollector, itemLight, OverlayTexture.NO_OVERLAY, 0);
        }

        poseStack.popPose();

        if (renderState.nameTag != null)
        {
            renderCustomItemName(renderState, renderState.nameTag, poseStack, submitNodeCollector, camera);
        }
    }

    @Override
    public FramedItemFrameRenderState createRenderState()
    {
        return new FramedItemFrameRenderState();
    }

    @Override
    public void extractRenderState(
            FramedItemFrameBlockEntity blockEntity,
            FramedItemFrameRenderState renderState,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    )
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

        renderState.direction = blockEntity.getBlockState().getValue(BlockStateProperties.FACING).getOpposite();
        renderState.rotation = blockEntity.getRotation();
        renderState.isGlowFrame = blockEntity.isGlowingFrame();

        ItemStack stack = blockEntity.getItem();
        this.itemModelResolver.updateForTopItem(renderState.item, stack, ItemDisplayContext.FIXED, blockEntity.level(), blockEntity, 0);

        if (stack.isEmpty()) return;

        extractMapData(blockEntity, renderState, stack);
        extractNameTag(blockEntity, renderState, stack);
    }

    private void extractMapData(FramedItemFrameBlockEntity blockEntity, FramedItemFrameRenderState renderState, ItemStack stack)
    {
        MapId mapId = stack.get(DataComponents.MAP_ID);
        if (mapId == null) return;

        MapItemSavedData mapData = MapItem.getSavedData(stack, blockEntity.level());
        if (mapData == null) return;

        mapRenderer.extractRenderState(mapId, mapData, renderState.mapRenderState);
        renderState.mapId = mapId;
    }

    private static void extractNameTag(FramedItemFrameBlockEntity blockEntity, FramedItemFrameRenderState renderState, ItemStack stack)
    {
        if (!Minecraft.renderNames() || !stack.has(DataComponents.CUSTOM_NAME)) return;
        if (!(Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult)) return;
        if (!blockEntity.getBlockPos().equals(hitResult.getBlockPos())) return;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double dist = camera.position().distanceToSqr(hitResult.getLocation());
        if (dist <= MAX_NAMETAG_DIST_SQR)
        {
            renderState.nameTag = stack.getHoverName();
            renderState.distanceToCameraSq = dist;
        }
    }

    private static void renderCustomItemName(
            FramedItemFrameRenderState renderState,
            Component nameTag,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    )
    {
        Direction dir = renderState.direction.getOpposite();
        float dx = .5F + (dir.getStepX() * .46875F) - (dir.getStepX() * .3F);
        float dz = .5F + (dir.getStepZ() * .46875F) - (dir.getStepZ() * .3F);

        poseStack.pushPose();
        poseStack.translate(dx, .75F, dz);

        submitNodeCollector.submitNameTag(poseStack, Vec3.ZERO, 0, nameTag, true, renderState.lightCoords, renderState.distanceToCameraSq, camera);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(FramedItemFrameBlockEntity be, Vec3 cameraPos)
    {
        return be.hasItem() && BlockEntityRenderer.super.shouldRender(be, cameraPos);
    }
}
