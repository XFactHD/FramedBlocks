package xfacthd.framedblocks.client.render;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.util.client.ClientUtils;
import xfacthd.framedblocks.client.model.FramedChestLidModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.blockentity.FramedChestBlockEntity;

import java.util.*;

public class FramedChestRenderer implements BlockEntityRenderer<FramedChestBlockEntity>
{
    private static final RenderType[] RENDER_TYPES = new RenderType[]
            {
                    RenderType.solid(),
                    RenderType.cutout(),
                    RenderType.cutoutMipped(),
                    RenderType.translucent()
            };
    private static final Table<Direction, LatchType, BakedModel> LID_MODELS = HashBasedTable.create(4, 3);

    @SuppressWarnings("unused")
    public FramedChestRenderer(BlockEntityRendererProvider.Context ctx) { }

    @Override
    public void render(FramedChestBlockEntity be, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int light, int overlay)
    {
        BlockState state = be.getBlockState();

        ChestState chestState = state.getValue(PropertyHolder.CHEST_STATE);
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);

        long lastChange = be.getLastChangeTime(chestState);

        if (chestState == ChestState.CLOSED) { return; }

        BakedModel model = LID_MODELS.get(dir, state.getValue(PropertyHolder.LATCH_TYPE));
        //noinspection ConstantConditions
        IModelData data = model.getModelData(be.getLevel(), be.getBlockPos(), state, EmptyModelData.INSTANCE);

        float angle = calculateAngle(be, chestState, dir, lastChange, partialTicks);

        float xOff = dir.getAxis() == Direction.Axis.X ? (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1F/16F : 15F/16F) : 0;
        float zOff = dir.getAxis() == Direction.Axis.Z ? (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1F/16F : 15F/16F) : 0;

        matrix.pushPose();

        matrix.translate(xOff, 9F/16F, zOff);
        matrix.mulPose(dir.getAxis() == Direction.Axis.X ? Vector3f.ZP.rotationDegrees(angle) : Vector3f.XN.rotationDegrees(angle));
        matrix.translate(-xOff, -9F/16F, -zOff);

        renderLidModel(be, state, matrix, buffer, model, data);

        matrix.popPose();
    }

    private static void renderLidModel(FramedChestBlockEntity be, BlockState state, PoseStack matrix, MultiBufferSource buffer, BakedModel model, IModelData data)
    {
        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

        for (RenderType type : RENDER_TYPES)
        {
            ForgeHooksClient.setRenderType(type);

            //noinspection ConstantConditions
            renderer.tesselateWithAO(
                    be.getLevel(),
                    model,
                    state,
                    be.getBlockPos(),
                    matrix,
                    buffer.getBuffer(type),
                    false,
                    be.getLevel().getRandom(),
                    be.getBlockPos().asLong(),
                    OverlayTexture.NO_OVERLAY,
                    data
            );
        }
        ForgeHooksClient.setRenderType(null);
    }

    private static float calculateAngle(FramedChestBlockEntity be, ChestState chestState, Direction dir, long lastChange, float partialTicks)
    {
        //noinspection ConstantConditions
        float diff = (float) (be.getLevel().getGameTime() - lastChange) + partialTicks;

        float factor = Mth.lerp(diff / 10F, 0, 1);
        if (chestState == ChestState.CLOSING) { factor = 1F - factor; }

        factor = 1.0F - factor;
        factor = 1.0F - factor * factor * factor;

        float angle = Mth.clamp(factor * 90F, 0F, 90F);
        if (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE) { angle *= -1F; }

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
                BlockState state = FBContent.blockFramedChest.get().defaultBlockState()
                        .setValue(PropertyHolder.FACING_HOR, dir)
                        .setValue(PropertyHolder.LATCH_TYPE, latch);

                ResourceLocation location = BlockModelShaper.stateToModelLocation(state);

                LID_MODELS.put(dir, latch, new FramedChestLidModel(
                        state,
                        registry.get(location)
                ));
            }
        }
    }
}