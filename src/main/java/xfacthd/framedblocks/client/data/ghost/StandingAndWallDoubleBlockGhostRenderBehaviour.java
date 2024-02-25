package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.CamoPair;

public final class StandingAndWallDoubleBlockGhostRenderBehaviour extends StandingAndWallBlockGhostRenderBehaviour
{
    @Override
    public CamoPair readCamo(ItemStack stack, @Nullable ItemStack proxiedStack, int renderPass)
    {
        return DoubleBlockGhostRenderBehaviour.readDoubleCamo(stack);
    }

    @Override
    public ModelData buildModelData(ItemStack stack, ItemStack proxiedStack, BlockPlaceContext ctx, BlockState renderState, int renderPass, CamoPair camo)
    {
        return DoubleBlockGhostRenderBehaviour.buildModelData(camo);
    }
}
