package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.ghost.CamoPair;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.client.render.GhostBlockRenderer;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;

import java.util.Iterator;
import java.util.Set;

public final class BlueprintGhostRenderBehaviour implements GhostRenderBehaviour
{
    @Override
    @Nullable
    public ItemStack getProxiedStack(ItemStack stack)
    {
        Item item = FramedBlueprintItem.getTargetBlock(stack).asItem();
        if (item instanceof BlockItem)
        {
            ItemStack proxied = new ItemStack(item);
            //noinspection ConstantConditions
            if (stack.hasTag() && stack.getTag().contains("blueprint_data"))
            {
                proxied.getOrCreateTag().put("BlockEntityTag", stack.getTag().getCompound("blueprint_data").getCompound("camo_data"));
            }
            return proxied;
        }
        return null;
    }

    @Override
    public boolean mayRender(ItemStack stack, @Nullable ItemStack proxiedStack)
    {
        return proxiedStack != null && proxyBehaviour(proxiedStack).mayRender(proxiedStack, null);
    }

    @Override
    public boolean hasSecondBlock(ItemStack stack, ItemStack proxiedStack)
    {
        return proxyBehaviour(proxiedStack).hasSecondBlock(proxiedStack, null);
    }

    @Override
    @Nullable
    public BlockState getRenderState(ItemStack stack, ItemStack proxiedStack, BlockHitResult hit, BlockPlaceContext ctx, BlockState hitState, boolean secondPass)
    {
        if (proxiedStack == null) { return null; }

        return proxyBehaviour(proxiedStack).getRenderState(proxiedStack, null, hit, ctx, hitState, secondPass);
    }

    @Override
    public BlockPos getRenderPos(ItemStack stack, ItemStack proxiedStack, BlockHitResult hit, BlockPlaceContext ctx, BlockState hitState, BlockPos defaultPos, boolean secondPass)
    {
        return proxyBehaviour(proxiedStack).getRenderPos(proxiedStack, null, hit, ctx, hitState, defaultPos, secondPass);
    }

    @Override
    public boolean canRenderAt(ItemStack stack, ItemStack proxiedStack, BlockHitResult hit, BlockPlaceContext ctx, BlockState hitState, BlockState renderState, BlockPos renderPos)
    {
        return proxyBehaviour(proxiedStack).canRenderAt(proxiedStack, null, hit, ctx, hitState, renderState, renderPos);
    }

    @Override
    public CamoPair readCamo(ItemStack stack, ItemStack proxiedStack, boolean secondPass)
    {
        //noinspection ConstantConditions
        if (stack.hasTag() && stack.getTag().contains("blueprint_data"))
        {
            CompoundTag tag = stack.getOrCreateTagElement("blueprint_data");
            Set<CamoContainer> camos = FramedBlueprintItem.getCamoContainers((BlockItem) proxiedStack.getItem(), tag);

            Iterator<CamoContainer> it = camos.iterator();
            BlockState camoState = it.next().getState();
            BlockState camoStateTwo = null;
            if (it.hasNext())
            {
                camoStateTwo = it.next().getState();
            }
            return new CamoPair(camoState, camoStateTwo);
        }
        return CamoPair.EMPTY;
    }

    @Override
    public CamoPair postProcessCamo(ItemStack stack, ItemStack proxiedStack, BlockPlaceContext ctx, BlockState renderState, boolean secondPass, CamoPair camo)
    {
        return proxyBehaviour(proxiedStack).postProcessCamo(proxiedStack, null, ctx, renderState, secondPass, camo);
    }

    @Override
    public ModelData appendModelData(ItemStack stack, ItemStack proxiedStack, BlockPlaceContext ctx, BlockState renderState, boolean secondPass, ModelData data)
    {
        return proxyBehaviour(proxiedStack).appendModelData(proxiedStack, null, ctx, renderState, secondPass, data);
    }

    private static GhostRenderBehaviour proxyBehaviour(ItemStack proxiedStack)
    {
        return GhostBlockRenderer.getBehaviour(proxiedStack.getItem());
    }
}
