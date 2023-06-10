package xfacthd.framedblocks.client.render;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHighlightEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.client.util.ClientConfig;

import java.util.*;

public final class BlockOutlineRenderer
{
    private static final Map<IBlockType, OutlineRenderer> OUTLINE_RENDERERS = new HashMap<>();
    private static final Set<IBlockType> ERRORED_TYPES = new HashSet<>();
    private static boolean locked = false;

    public static void onRenderBlockHighlight(final RenderHighlightEvent.Block event)
    {
        if (!ClientConfig.fancyHitboxes)
        {
            return;
        }

        BlockHitResult result = event.getTarget();
        //noinspection ConstantConditions
        BlockState state = Minecraft.getInstance().level.getBlockState(result.getBlockPos());
        if (!(state.getBlock() instanceof IFramedBlock block))
        {
            return;
        }

        IBlockType type = block.getBlockType();
        if (type.hasSpecialHitbox())
        {
            OutlineRenderer renderer = OUTLINE_RENDERERS.get(type);
            if (renderer == null)
            {
                if (ERRORED_TYPES.add(type))
                {
                    FramedBlocks.LOGGER.error("IBlockType '{}' requests custom outline rendering but no OutlineRender was registered!", type.getName());
                }
                return;
            }

            PoseStack mstack = event.getPoseStack();
            Vec3 offset = Vec3.atLowerCornerOf(result.getBlockPos()).subtract(event.getCamera().getPosition());
            VertexConsumer builder = event.getMultiBufferSource().getBuffer(RenderType.lines());

            mstack.pushPose();
            mstack.translate(offset.x, offset.y, offset.z);
            mstack.translate(.5, .5, .5);
            renderer.rotateMatrix(mstack, state);
            mstack.translate(-.5, -.5, -.5);

            renderer.draw(state, Minecraft.getInstance().level, result.getBlockPos(), mstack, builder);

            mstack.popPose();

            event.setCanceled(true);
        }
    }

    public static synchronized void registerOutlineRender(IBlockType type, OutlineRenderer render)
    {
        Preconditions.checkState(!locked, "OutlineRenderer registry is locked!");

        if (!type.hasSpecialHitbox())
        {
            throw new IllegalArgumentException(String.format(
                    "Type %s doesn't return true from IBlockType#hasSpecialHitbox()", type
            ));
        }

        OUTLINE_RENDERERS.put(type, render);
    }

    public static void lockRegistration()
    {
        locked = true;
    }



    private BlockOutlineRenderer() { }
}