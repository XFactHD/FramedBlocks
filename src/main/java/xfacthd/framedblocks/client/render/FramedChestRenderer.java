package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.client.model.FramedChestLidModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.ChestState;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.blockentity.FramedChestBlockEntity;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.ModelBlockRenderer;

public class FramedChestRenderer implements BlockEntityRenderer<FramedChestBlockEntity>
{
    private static final RenderType[] RENDER_TYPES = new RenderType[]
            {
                    RenderType.solid(),
                    RenderType.cutout(),
                    RenderType.cutoutMipped(),
                    RenderType.translucent()
            };
    private static final Map<Direction, BakedModel> LID_MODELS = new EnumMap<>(Direction.class);

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

        BakedModel model = LID_MODELS.get(dir);
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

    private void renderLidModel(FramedChestBlockEntity be, BlockState state, PoseStack matrix, MultiBufferSource buffer, BakedModel model, IModelData data)
    {
        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

        for (RenderType type : RENDER_TYPES)
        {
            ForgeHooksClient.setRenderLayer(type);

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
        ForgeHooksClient.setRenderLayer(null);
    }

    private float calculateAngle(FramedChestBlockEntity be, ChestState chestState, Direction dir, long lastChange, float partialTicks)
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

    public static void onModelsLoaded(Map<ResourceLocation, BakedModel> registry)
    {
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            BlockState state = FBContent.blockFramedChest.get().defaultBlockState().setValue(PropertyHolder.FACING_HOR, dir);

            ResourceLocation location = BlockModelShaper.stateToModelLocation(state);

            LID_MODELS.put(dir, new FramedChestLidModel(
                    state,
                    registry.get(location)
            ));
        }
    }
}