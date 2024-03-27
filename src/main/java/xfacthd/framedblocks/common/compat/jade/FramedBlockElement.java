package xfacthd.framedblocks.common.compat.jade;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import snownee.jade.api.ui.Element;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.common.config.ClientConfig;

final class FramedBlockElement extends Element
{
    private static final float SIZE = 18F;
    private static final Vec2 SIZE_VEC = new Vec2(SIZE, SIZE);
    private static final float Z_OFFSET = 15F;
    private static final float RENDER_SIZE = 16F;
    private static final Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.2F, 1.0F, -0.7F).rotate(Quaternions.XN_90).normalize();
    private static final Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-0.2F, 1.0F, 0.7F).rotate(Quaternions.XN_90).normalize();
    private static final RandomSource RANDOM = RandomSource.create();

    private final BlockState state;
    private final SingleBlockFakeLevel fakeLevel;
    private final BakedModel model;
    private final ModelData modelData;
    private final RenderType renderType;
    private final float scale;

    FramedBlockElement(BlockState state, FramedBlockEntity blockEntity)
    {
        IFramedBlock block = (IFramedBlock) state.getBlock();
        this.state = block.getJadeRenderState(state);
        this.fakeLevel = new SingleBlockFakeLevel(blockEntity.getBlockPos(), this.state, blockEntity);
        this.model = Minecraft.getInstance().getBlockRenderer().getBlockModel(this.state);
        boolean renderCamo = ClientConfig.VIEW.shouldRenderCamoInJade();
        this.modelData = renderCamo ? blockEntity.getModelData(false) : ModelData.EMPTY;
        this.renderType = renderCamo ? Sheets.translucentCullBlockSheet() : Sheets.cutoutBlockSheet();
        this.scale = block.getJadeRenderScale(this.state) * .625F;
    }

    @Override
    public Vec2 getSize()
    {
        return SIZE_VEC;
    }

    @Override
    public void render(GuiGraphics graphics, float x, float y, float maxX, float maxY)
    {
        if (!state.isAir())
        {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();

            poseStack.translate(x + (SIZE / 2F), y + (SIZE / 2F), Z_OFFSET);
            poseStack.scale(RENDER_SIZE * scale, -RENDER_SIZE * scale, RENDER_SIZE * scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(30F));
            poseStack.mulPose(Axis.YP.rotationDegrees(225F));
            poseStack.translate(-.5F, -.5F, -.5F);

            long seed = state.getSeed(BlockPos.ZERO);
            RANDOM.setSeed(seed);
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer consumer = buffer.getBuffer(renderType);
            for (RenderType renderType : model.getRenderTypes(state, RANDOM, modelData))
            {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
                        fakeLevel,
                        model,
                        state,
                        BlockPos.ZERO,
                        poseStack,
                        consumer,
                        false,
                        RANDOM,
                        seed,
                        OverlayTexture.NO_OVERLAY,
                        modelData,
                        renderType
                );
            }

            RenderSystem.setupGuiFlatDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);
            buffer.endBatch();
            Lighting.setupFor3DItems();

            poseStack.popPose();
        }
    }

    @Override
    @Nullable
    public String getMessage()
    {
        return state.isAir() ? null : state.getBlock().getDescriptionId();
    }
}
