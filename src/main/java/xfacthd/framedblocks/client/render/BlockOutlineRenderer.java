package xfacthd.framedblocks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
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
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, value = Dist.CLIENT)
public class BlockOutlineRenderer
{
    private static final Map<IBlockType, OutlineRender> OUTLINE_RENDERERS = new HashMap<>();

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
            PoseStack mstack = event.getMatrix();
            Vec3 offset = Vec3.atLowerCornerOf(result.getBlockPos()).subtract(event.getInfo().getPosition());
            VertexConsumer builder = event.getBuffers().getBuffer(RenderType.lines());
            OutlineRender render = OUTLINE_RENDERERS.get(type);

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
        boolean top = state.getValue(PropertyHolder.TOP);

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
        boolean top = state.getValue(PropertyHolder.TOP);

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
        boolean top = state.getValue(PropertyHolder.TOP);

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
        boolean top = state.getValue(PropertyHolder.TOP);

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
}