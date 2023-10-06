package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.util.Utils;

import java.lang.invoke.MethodHandle;

public sealed class StandingAndWallBlockGhostRenderBehaviour implements GhostRenderBehaviour
        permits StandingAndWallDoubleBlockGhostRenderBehaviour
{
    private static final MethodHandle BLOCKITEM_GETPLACESTATE = Utils.unreflectMethod(
            BlockItem.class, "getPlacementState", BlockPlaceContext.class
    );

    @Override
    @Nullable
    public BlockState getRenderState(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            boolean secondPass
    )
    {
        try
        {
            return (BlockState) BLOCKITEM_GETPLACESTATE.invokeExact((BlockItem) stack.getItem(), ctx);
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Failed to invoke BlockItem#getPlacementState on '%s'".formatted(stack.getItem()), e);
        }
    }
}
