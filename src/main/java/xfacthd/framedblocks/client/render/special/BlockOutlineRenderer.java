package xfacthd.framedblocks.client.render.special;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.render.RegisterOutlineRenderersEvent;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.config.DevToolsConfig;

import java.util.*;

public final class BlockOutlineRenderer
{
    private static final Map<IBlockType, OutlineRenderer> OUTLINE_RENDERERS = new IdentityHashMap<>();
    private static final Set<IBlockType> ERRORED_TYPES = new HashSet<>();

    public static void onRenderBlockHighlight(final RenderHighlightEvent.Block event)
    {
        if (!ClientConfig.VIEW.useFancySelectionBoxes() && !DevToolsConfig.VIEW.isOcclusionShapeDebugRenderingEnabled())
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

        if (DevToolsConfig.VIEW.isOcclusionShapeDebugRenderingEnabled())
        {
            VertexConsumer builder = event.getMultiBufferSource().getBuffer(RenderType.lines());
            VoxelShape shape = state.getOcclusionShape(Minecraft.getInstance().level, result.getBlockPos());
            Vec3 offset = Vec3.atLowerCornerOf(result.getBlockPos()).subtract(event.getCamera().getPosition());
            LevelRenderer.renderShape(event.getPoseStack(), builder, shape, offset.x, offset.y, offset.z, 0F, 0F, 0F, .4F);
            event.setCanceled(true);
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

    public static void init()
    {
        ModLoader.postEvent(new RegisterOutlineRenderersEvent((type, renderer) ->
        {
            Preconditions.checkArgument(
                    type.hasSpecialHitbox(),
                    "IBlockType %s doesn't return true from IBlockType#hasSpecialHitbox()",
                    type
            );
            OUTLINE_RENDERERS.put(type, renderer);
        }));
    }

    public static boolean hasOutlineRenderer(IBlockType type)
    {
        return OUTLINE_RENDERERS.containsKey(type);
    }



    private BlockOutlineRenderer() { }
}
