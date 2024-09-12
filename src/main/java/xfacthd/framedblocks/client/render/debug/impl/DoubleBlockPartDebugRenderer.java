package xfacthd.framedblocks.client.render.debug.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.block.BlockCamoContent;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.config.DevToolsConfig;

import java.util.Objects;

public class DoubleBlockPartDebugRenderer implements BlockDebugRenderer<FramedDoubleBlockEntity>
{
    public static final DoubleBlockPartDebugRenderer INSTANCE = new DoubleBlockPartDebugRenderer();
    private static final FramedBlockData MODEL_DATA = new FramedBlockData(new BlockCamoContent(Blocks.STONE.defaultBlockState()), false);

    private DoubleBlockPartDebugRenderer() { }

    @Override
    public void render(
            FramedDoubleBlockEntity be,
            BlockHitResult blockHit,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    )
    {
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof IFramedBlock)) return;

        Tuple<BlockState, BlockState> blockPair = be.getBlockPair();
        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        boolean secondary = be.debugHitSecondary(blockHit, player);
        BlockState partState = secondary ? blockPair.getB() : blockPair.getA();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(partState);

        OutlineBufferSource outlineBuffer = Minecraft.getInstance().renderBuffers().outlineBufferSource();
        outlineBuffer.setColor(
                secondary ? 0x00 : 0xFF,
                secondary ? 0xFF : 0x00,
                0x00,
                0xFF
        );

        ModelData modelData = ((IFramedBlock) state.getBlock()).unpackNestedModelData(be.getModelData(), state, partState);
        modelData = modelData.derive().with(FramedBlockData.PROPERTY, MODEL_DATA).build();

        //noinspection deprecation
        VertexConsumer consumer = outlineBuffer.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                consumer,
                partState,
                model,
                1F, 1F, 1F,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                modelData,
                RenderType.solid()
        );
    }

    @Override
    public boolean isEnabled()
    {
        return DevToolsConfig.VIEW.isDoubleBlockPartHitDebugRendererEnabled();
    }
}
