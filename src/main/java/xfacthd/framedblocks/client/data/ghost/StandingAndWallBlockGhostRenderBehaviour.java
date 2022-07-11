package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class StandingAndWallBlockGhostRenderBehaviour implements GhostRenderBehaviour
{
    private static final MethodHandle BLOCKITEM_GETPLACESTATE;

    @Override
    @Nullable
    public BlockState getRenderState(ItemStack stack, ItemStack proxiedStack, BlockHitResult hit, BlockPlaceContext ctx, BlockState hitState, boolean secondPass)
    {
        try
        {
            return (BlockState) BLOCKITEM_GETPLACESTATE.invokeExact((BlockItem) stack.getItem(), ctx);
        }
        catch (Throwable e)
        {
            FramedBlocks.LOGGER.error("Encountered an error while getting placement state of ", e);
            return null;
        }
    }

    static
    {
        Method method = ObfuscationReflectionHelper.findMethod(BlockItem.class, "m_5965_", BlockPlaceContext.class);
        try
        {
            BLOCKITEM_GETPLACESTATE = MethodHandles.publicLookup().unreflect(method);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect 'BlockItem#getStateForPlacement'", e);
        }
    }
}
