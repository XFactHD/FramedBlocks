package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.render.outline.OutlineRender;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.Utils;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, value = Dist.CLIENT)
public class BlockOutlineRenderer
{
    private static final Map<BlockType, OutlineRender> OUTLINE_RENDERERS = new HashMap<>();

    @SubscribeEvent
    public static void onRenderBlockHighlight(final DrawHighlightEvent.HighlightBlock event)
    {
        if (!ClientConfig.fancyHitboxes) { return; }

        BlockRayTraceResult result = event.getTarget();
        //noinspection ConstantConditions
        BlockState state = Minecraft.getInstance().world.getBlockState(result.getPos());
        if (!(state.getBlock() instanceof IFramedBlock)) { return; }

        BlockType type = ((IFramedBlock) state.getBlock()).getBlockType();
        if (type.hasSpecialHitbox())
        {
            MatrixStack mstack = event.getMatrix();
            Vector3d offset = Vector3d.copy(result.getPos()).subtract(event.getInfo().getProjectedView());
            IVertexBuilder builder = event.getBuffers().getBuffer(RenderType.getLines());
            OutlineRender render = OUTLINE_RENDERERS.get(type);

            mstack.push();
            mstack.translate(offset.x, offset.y, offset.z);
            mstack.translate(.5, .5, .5);
            render.rotateMatrix(mstack, state);
            mstack.translate(-.5, -.5, -.5);

            render.draw(state, Minecraft.getInstance().world, result.getPos(), mstack, builder);

            mstack.pop();

            event.setCanceled(true);
        }
    }

    public static void registerOutlineRender(BlockType type, OutlineRender render)
    {
        if (!type.hasSpecialHitbox())
        {
            throw new IllegalArgumentException(String.format("Type %s doesn't return true from IBlockType#hasSpecialHitbox()", type));
        }

        OUTLINE_RENDERERS.put(type, render);
    }



    public static void drawSlopeBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        SlopeType type = Utils.getSlopeType(state);

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

    public static void drawCornerSlopeBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);
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

    public static void drawInnerCornerSlopeBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);

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

            //Right face
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 1, 0, 0, 1, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 1, 0);

            //Bottom face
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, mstack, 0, 0, 0, 1, 0, 0);

            //Slope edges
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 0, 1, 0);
            OutlineRender.drawLine(builder, mstack, 1, 0, 0, 0, 1, 1);
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

    public static void drawPrismCornerBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        boolean top = state.get(PropertyHolder.TOP);

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

    public static void drawInnerPrismCornerBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        boolean top = state.get(PropertyHolder.TOP);

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

    public static void drawThreewayCornerBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        boolean top = state.get(PropertyHolder.TOP);

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

    public static void drawInnerThreewayCornerBox(BlockState state, MatrixStack mstack, IVertexBuilder builder)
    {
        boolean top = state.get(PropertyHolder.TOP);

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

        //Right face
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, mstack, 0, 1, 0, 0, 1, 1);
        OutlineRender.drawLine(builder, mstack, 0, 0, 0, 0, 1, 0);

        //Slope edges
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 1, 1, 1);
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, 0, 1, 0);
        OutlineRender.drawLine(builder, mstack, 0, 1, 0, 1, 1, 1);

        //Cross
        OutlineRender.drawLine(builder, mstack, 1, 0, 0, .5, .5, .5);
        OutlineRender.drawLine(builder, mstack, .5, .5, .5, 1, 1, 1);
        OutlineRender.drawLine(builder, mstack, 0, 1, 0, .5, .5, .5);
    }
}