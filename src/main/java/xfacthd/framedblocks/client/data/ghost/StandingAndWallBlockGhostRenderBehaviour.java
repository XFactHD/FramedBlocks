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

public class StandingAndWallBlockGhostRenderBehaviour implements GhostRenderBehaviour
{
    private static final MethodHandle BLOCKITEM_GETPLACESTATE = Utils.unreflectMethod(BlockItem.class, "m_5965_", BlockPlaceContext.class);

    @Override
    @Nullable
    @SuppressWarnings("RedundantCast") //Cast is needed for invokeExact()
    public BlockState getRenderState(ItemStack stack, ItemStack proxiedStack, BlockHitResult hit, BlockPlaceContext ctx, BlockState hitState, boolean secondPass)
    {
        return Utils.invokeMethodHandle(BLOCKITEM_GETPLACESTATE, (BlockItem) stack.getItem(), ctx);
    }
}
