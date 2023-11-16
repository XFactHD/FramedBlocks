package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.CamoPair;
import xfacthd.framedblocks.api.block.FramedProperties;

public final class DoublePanelGhostRenderBehaviour extends DoubleBlockGhostRenderBehaviour
{
    @Override
    public CamoPair postProcessCamo(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoPair camo
    )
    {
        //noinspection ConstantConditions
        if (renderState.getValue(FramedProperties.FACING_NE) != ctx.getPlayer().getDirection())
        {
            return camo.swap();
        }
        return camo;
    }
}
