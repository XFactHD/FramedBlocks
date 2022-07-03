package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.*;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, value = Dist.CLIENT)
public final class BlockOutlineRenderer
{
    private static final Map<IBlockType, OutlineRender> OUTLINE_RENDERERS = new HashMap<>();
    private static final Set<IBlockType> ERRORED_TYPES = new HashSet<>();

    @SubscribeEvent
    public static void onRenderBlockHighlight(final DrawSelectionEvent.HighlightBlock event)
    {
        if (!ClientConfig.fancyHitboxes) { return; }

        BlockHitResult result = event.getTarget();
        //noinspection ConstantConditions
        BlockState state = Minecraft.getInstance().level.getBlockState(result.getBlockPos());
        if (!(state.getBlock() instanceof IFramedBlock block)) { return; }

        IBlockType type = block.getBlockType();
        if (type.hasSpecialHitbox())
        {
            PoseStack mstack = event.getPoseStack();
            Vec3 offset = Vec3.atLowerCornerOf(result.getBlockPos()).subtract(event.getCamera().getPosition());
            VertexConsumer builder = event.getMultiBufferSource().getBuffer(RenderType.lines());

            OutlineRender render = OUTLINE_RENDERERS.get(type);
            if (render == null)
            {
                if (ERRORED_TYPES.add(type))
                {
                    FramedBlocks.LOGGER.error("IBlockType '{}' requests custom outline rendering but no OutlineRender was registered!", type.getName());
                }
                return;
            }

            mstack.pushPose();
            mstack.translate(offset.x, offset.y, offset.z);
            mstack.translate(.5, .5, .5);
            render.rotateMatrix(mstack, state);
            mstack.translate(-.5, -.5, -.5);

            render.draw(state, Minecraft.getInstance().level, result.getBlockPos(), mstack, builder);

            mstack.popPose();

            event.setCanceled(true);
        }
    }

    public static synchronized void registerOutlineRender(IBlockType type, OutlineRender render)
    {
        if (!type.hasSpecialHitbox())
        {
            throw new IllegalArgumentException(String.format("Type %s doesn't return true from IBlockType#hasSpecialHitbox()", type));
        }

        OUTLINE_RENDERERS.put(type, render);
    }



    public static void drawSlopeBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        SlopeType type = FramedUtils.getSlopeType(state);

        if (type != SlopeType.HORIZONTAL)
        {
            if (type == SlopeType.TOP)
            {
                mstack.translate(.5, .5, .5);
                mstack.scale(1, -1, 1);
                mstack.translate(-.5, -.5, -.5);
            }

            //Back edges
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Bottom face
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

            //Top edge
            OutlineRender.drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);

            //Slope
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
        }
        else
        {
            //Back
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Left side
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 1, 1, 0, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 0);

            //Slope
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, mstack, 1, 1, 0, 0, 1, 1);
        }
    }

    public static void drawCornerSlopeBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (!type.isHorizontal())
        {
            if (type.isTop())
            {
                mstack.translate(.5, .5, .5);
                mstack.scale(1, -1, 1);
                mstack.translate(-.5, -.5, -.5);
            }

            //Back edge
            OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Bottom face
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

            //Slope
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 1, 1);
        }
        else
        {
            mstack.translate(.5, .5, .5);
            if (!type.isRight()) { mstack.scale(-1, 1, 1); }
            if (type.isTop()) { mstack.scale(1, -1, 1); }
            mstack.translate(-.5, -.5, -.5);

            //Back face
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Back edge
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);

            //Center slope edge
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 1, 1);

            //Side slope edges
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 0, 1);
        }
    }

    public static void drawInnerCornerSlopeBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (!type.isHorizontal())
        {
            mstack.translate(.5, .5, .5);
            if (type.isTop()) { mstack.scale(1, -1, 1); }
            mstack.translate(-.5, -.5, -.5);

            //Back face
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

            //Left face
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 1, 1, 0, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 0);

            //Bottom face
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);

            //Slope edges
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 1, 0);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 1, 1);
        }
        else
        {
            mstack.translate(.5, .5, .5);
            if (!type.isRight()) { mstack.scale(-1, 1, 1); }
            if (type.isTop()) { mstack.scale(1, -1, 1); }
            mstack.translate(-.5, -.5, -.5);

            //Top face
            OutlineRender.drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 0, 1, 0, 0, 1, 1);
            OutlineRender.drawLine(builder, mstack, 0, 1, 0, 1, 1, 1);

            //Bottom face
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);

            //Right face
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 1, 0);
            OutlineRender.drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);

            //Left face
            OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);

            //Slope edge
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 1, 1);
        }
    }

    public static void drawPrismCornerBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        boolean top = state.getValue(FramedProperties.TOP);

        mstack.translate(.5, .5, .5);
        if (top) { mstack.scale(1, -1, 1); }
        mstack.translate(-.5, -.5, -.5);

        //Back edge
        OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

        //Bottom edges
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

        //Front edge
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 0, 0, 1);

        //Slope edges
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 1, 1);
    }

    public static void drawInnerPrismCornerBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        boolean top = state.getValue(FramedProperties.TOP);

        mstack.translate(.5, .5, .5);
        if (top) { mstack.scale(1, -1, 1); }
        mstack.translate(-.5, -.5, -.5);

        //Bottom face
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

        //Back face
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 0);
        OutlineRender.drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
        OutlineRender.drawLine(builder, mstack, 1, 1, 0, 1, 1, 1);

        //Right face
        OutlineRender.drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
        OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

        //Slope edges
        OutlineRender.drawLine(builder, mstack, 1, 1, 0, 0, 1, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 1, 0);
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
    }

    public static void drawThreewayCornerBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        boolean top = state.getValue(FramedProperties.TOP);

        mstack.translate(.5, .5, .5);
        if (top) { mstack.scale(1, -1, 1); }
        mstack.translate(-.5, -.5, -.5);

        //Back edges
        OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

        //Slope edges
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 1, 1);

        //Cross
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, .5, .5, .5);
        OutlineRender.drawLine(builder, mstack, .5, .5, .5, 1, 1, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 1, .5, .5, .5);
    }

    public static void drawInnerThreewayCornerBox(BlockState state, PoseStack mstack, VertexConsumer builder)
    {
        boolean top = state.getValue(FramedProperties.TOP);

        mstack.translate(.5, .5, .5);
        if (top) { mstack.scale(1, -1, 1); }
        mstack.translate(-.5, -.5, -.5);

        //Bottom face
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 1, 1, 0, 1);

        //Back face
        OutlineRender.drawLine(builder, mstack, 0, 1, 1, 1, 1, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 1, 0, 1, 1);
        OutlineRender.drawLine(builder, mstack, 1, 0, 1, 1, 1, 1);

        //Left face
        OutlineRender.drawLine(builder, mstack, 1, 1, 0, 1, 1, 1);
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 0);

        //Slope edges
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 1, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 1, 0);
        OutlineRender.drawLine(builder, mstack, 0, 1, 1, 1, 1, 0);

        //Cross
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, .5, .5, .5);
        OutlineRender.drawLine(builder, mstack, .5, .5, .5, 0, 1, 1);
        OutlineRender.drawLine(builder, mstack, 1, 1, 0, .5, .5, .5);
    }

    public static void drawSlopeSlabBox(BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

        if (top)
        {
            pstack.translate(.5, .5, .5);
            pstack.scale(1, -1, 1);
            pstack.translate(-.5, -.5, -.5);
        }

        if (topHalf != top)
        {
            pstack.translate(0, .5, 0);
        }

        //Back edges
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 0, .5, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 1, 1, .5, 1);

        //Bottom face
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);

        //Top edge
        OutlineRender.drawLine(builder, pstack, 0, .5, 1, 1, .5, 1);

        //Slope
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, .5, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, .5, 1);
    }

    public static void drawElevatedSlopeSlabBox(BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        if (state.getValue(FramedProperties.TOP))
        {
            pstack.translate(.5, .5, .5);
            pstack.scale(1, -1, 1);
            pstack.translate(-.5, -.5, -.5);
        }

        //Back edges
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 0, 1, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 1, 1, 1, 1);

        //Front edges
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, .5, 0);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, .5, 0);
        OutlineRender.drawLine(builder, pstack, 0, .5, 0, 1, .5, 0);

        //Bottom face
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);

        //Top edge
        OutlineRender.drawLine(builder, pstack, 0, 1, 1, 1, 1, 1);

        //Slope
        OutlineRender.drawLine(builder, pstack, 0, .5, 0, 0, 1, 1);
        OutlineRender.drawLine(builder, pstack, 1, .5, 0, 1, 1, 1);
    }

    public static void drawInverseDoubleSlopeSlabBox(@SuppressWarnings("unused") BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        //Back vertical edges
        OutlineRender.drawLine(builder, pstack, 0, .5, 1, 0, 1, 1);
        OutlineRender.drawLine(builder, pstack, 1, .5, 1, 1, 1, 1);

        //Center horizontal edges
        OutlineRender.drawLine(builder, pstack, 0, .5, 0, 1, .5, 0);
        OutlineRender.drawLine(builder, pstack, 0, .5, 1, 1, .5, 1);

        //Top edge
        OutlineRender.drawLine(builder, pstack, 0, 1, 1, 1, 1, 1);

        //Top slope
        OutlineRender.drawLine(builder, pstack, 0, .5, 0, 0, 1, 1);
        OutlineRender.drawLine(builder, pstack, 1, .5, 0, 1, 1, 1);

        //Bottom edge
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);

        //Bottom slope
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, .5, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, .5, 1);

        //Front vertical edges
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, .5, 0);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, .5, 0);
    }



    private BlockOutlineRenderer() { }
}