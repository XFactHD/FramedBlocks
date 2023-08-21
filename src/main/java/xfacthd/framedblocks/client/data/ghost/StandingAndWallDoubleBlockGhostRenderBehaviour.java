package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.CamoPair;

public final class StandingAndWallDoubleBlockGhostRenderBehaviour extends StandingAndWallBlockGhostRenderBehaviour
{
    @Override
    public CamoPair readCamo(ItemStack stack, @Nullable ItemStack proxiedStack, boolean secondPass)
    {
        return DoubleBlockGhostRenderBehaviour.readDoubleCamo(stack);
    }
}
