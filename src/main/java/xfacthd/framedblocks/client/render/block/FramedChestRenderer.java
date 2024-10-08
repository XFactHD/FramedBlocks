package xfacthd.framedblocks.client.render.block;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.client.model.cube.FramedChestLidModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import xfacthd.framedblocks.common.data.property.ChestState;
import xfacthd.framedblocks.common.data.property.LatchType;

import java.util.*;

public class FramedChestRenderer implements BlockEntityRenderer<FramedChestBlockEntity>
{
    private static final Table<Direction, LatchType, FramedBlockModel> LID_MODELS = HashBasedTable.create(4, 3);
    private static final RandomSource RANDOM = RandomSource.create();

    @SuppressWarnings("unused")
    public FramedChestRenderer(BlockEntityRendererProvider.Context ctx) { }

    @Override
    public void render(
            FramedChestBlockEntity be,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    )
    {
        BlockState state = be.getBlockState();

        ChestState chestState = state.getValue(PropertyHolder.CHEST_STATE);
        if (chestState == ChestState.CLOSED)
        {
            return;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        long lastChange = be.getLastChangeTime(chestState);

        BakedModel model = LID_MODELS.get(dir, state.getValue(PropertyHolder.LATCH_TYPE));
        //noinspection ConstantConditions
        ModelData data = model.getModelData(be.getLevel(), be.getBlockPos(), state, be.getModelData());

        float angle = calculateAngle(be, chestState, dir, lastChange, partialTicks);

        float xOff = Utils.isX(dir) ? (Utils.isPositive(dir) ? 1F/16F : 15F/16F) : 0;
        float zOff = Utils.isZ(dir) ? (Utils.isPositive(dir) ? 1F/16F : 15F/16F) : 0;

        poseStack.pushPose();

        poseStack.translate(xOff, 9F/16F, zOff);
        poseStack.mulPose(Utils.isX(dir) ? Axis.ZP.rotationDegrees(angle) : Axis.XN.rotationDegrees(angle));
        poseStack.translate(-xOff, -9F/16F, -zOff);

        renderLidModel(be, state, poseStack, buffer, model, data);

        poseStack.popPose();
    }

    private static void renderLidModel(
            FramedChestBlockEntity be,
            BlockState state,
            PoseStack matrix,
            MultiBufferSource buffer,
            BakedModel model,
            ModelData data
    )
    {
        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

        int color = Minecraft.getInstance().getBlockColors().getColor(state, be.getLevel(), be.getBlockPos(), 0);
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        //noinspection ConstantConditions
        int light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos());

        RANDOM.setSeed(42);
        for (RenderType type : model.getRenderTypes(state, RANDOM, data))
        {
            RenderType bufferType = type == RenderType.solid() ? Sheets.solidBlockSheet() : RenderTypeHelper.getEntityRenderType(type, false);

            renderer.renderModel(
                    matrix.last(),
                    buffer.getBuffer(bufferType),
                    state,
                    model,
                    red, green, blue,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    data,
                    type
            );
        }
    }

    private static float calculateAngle(
            FramedChestBlockEntity be, ChestState chestState, Direction dir, long lastChange, float partialTicks
    )
    {
        //noinspection ConstantConditions
        float diff = (float) (be.getLevel().getGameTime() - lastChange) + partialTicks;

        float factor = Mth.lerp(diff / 10F, 0, 1);
        if (chestState == ChestState.CLOSING) { factor = 1F - factor; }

        factor = 1.0F - factor;
        factor = 1.0F - factor * factor * factor;

        float angle = Mth.clamp(factor * 90F, 0F, 90F);
        if (!Utils.isPositive(dir)) { angle *= -1F; }

        return angle;
    }

    @Override
    public boolean shouldRender(FramedChestBlockEntity be, Vec3 camera)
    {
        return !ClientUtils.OPTIFINE_LOADED.get() && BlockEntityRenderer.super.shouldRender(be, camera);
    }



    public static void onModelsLoaded(Map<ResourceLocation, BakedModel> registry)
    {
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            for (LatchType latch : LatchType.values())
            {
                BlockState state = FBContent.BLOCK_FRAMED_CHEST.get().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir)
                        .setValue(PropertyHolder.LATCH_TYPE, latch);

                ResourceLocation location = BlockModelShaper.stateToModelLocation(state);

                BakedModel model = registry.get(location);
                if (model instanceof FramedBlockModel fbModel)
                {
                    model = fbModel.getBaseModel();
                }
                LID_MODELS.put(dir, latch, new FramedChestLidModel(state, model));
            }
        }
    }

    public static void clearModelCaches()
    {
        for (FramedBlockModel model : LID_MODELS.values())
        {
            if (model != null)
            {
                model.clearCache();
            }
        }
    }
}