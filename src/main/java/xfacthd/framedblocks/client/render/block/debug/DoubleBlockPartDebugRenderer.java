package xfacthd.framedblocks.client.render.block.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.camo.block.BlockCamoContent;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import xfacthd.framedblocks.api.util.TestProperties;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public class DoubleBlockPartDebugRenderer implements BlockDebugRenderer<FramedDoubleBlockEntity>
{
    public static final DoubleBlockPartDebugRenderer INSTANCE = new DoubleBlockPartDebugRenderer();
    private static final ModelData MODEL_DATA = ModelData.builder().with(
            FramedBlockData.PROPERTY,
            new FramedBlockData(new BlockCamoContent(Blocks.STONE.defaultBlockState()), new boolean[6], false, false)
    ).build();

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
        Tuple<BlockState, BlockState> blockPair = be.getBlockPair();
        boolean secondary = be.debugHitSecondary(blockHit);
        BlockState state = secondary ? blockPair.getB() : blockPair.getA();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

        OutlineBufferSource outlineBuffer = Minecraft.getInstance().renderBuffers().outlineBufferSource();
        outlineBuffer.setColor(
                secondary ? 0x00 : 0xFF,
                secondary ? 0xFF : 0x00,
                0x00,
                0xFF
        );

        //noinspection deprecation
        VertexConsumer consumer = outlineBuffer.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                consumer,
                state,
                model,
                1F, 1F, 1F,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                MODEL_DATA,
                RenderType.solid()
        );
    }

    @Override
    public boolean isEnabled()
    {
        return TestProperties.ENABLE_DOUBLE_BLOCK_PART_HIT_DEBUG_RENDERER;
    }
}
