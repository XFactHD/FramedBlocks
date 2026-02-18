package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import io.github.xfacthd.framedblocks.client.render.special.GhostBlockRenderer;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.item.FramedBlueprintItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public final class BlueprintGhostRenderBehaviour implements GhostRenderBehaviour
{
    @Override
    @Nullable
    public ItemStack getProxiedStack(ItemStack stack)
    {
        BlueprintData blueprintData = stack.getOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY);
        if (!blueprintData.isEmpty())
        {
            ItemStack proxied = new ItemStack(blueprintData.block());
            FramedBlueprintItem.getBehaviour(blueprintData.block()).attachDataToDummyRenderStack(proxied, blueprintData);
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
    public int getPassCount(ItemStack stack, @Nullable ItemStack proxiedStack)
    {
        return proxiedStack != null ? proxyBehaviour(proxiedStack).getPassCount(proxiedStack, null) : 0;
    }

    @Override
    @Nullable
    public BlockState getRenderState(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    )
    {
        if (proxiedStack == null)
        {
            return null;
        }
        BlockState state = proxyBehaviour(proxiedStack).getRenderState(proxiedStack, null, hit, ctx, hitState, renderPass);
        BlockItemStateProperties stateProps = stack.getOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY).blockState();
        if (state != null && !stateProps.isEmpty())
        {
            state = stateProps.apply(state);
        }
        return state;
    }

    @Override
    public BlockPos getRenderPos(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockPos defaultPos,
            int renderPass
    )
    {
        if (proxiedStack == null)
        {
            return defaultPos;
        }
        return proxyBehaviour(proxiedStack).getRenderPos(proxiedStack, null, hit, ctx, hitState, defaultPos, renderPass);
    }

    @Override
    public boolean canRenderAt(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockState renderState,
            BlockPos renderPos
    )
    {
        if (proxiedStack == null)
        {
            return false;
        }
        return proxyBehaviour(proxiedStack).canRenderAt(proxiedStack, null, hit, ctx, hitState, renderState, renderPos);
    }

    @Override
    public CamoList readCamo(ItemStack stack, @Nullable ItemStack proxiedStack, int renderPass)
    {
        if (proxiedStack == null) return CamoList.EMPTY;

        BlueprintData blueprintData = stack.getOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY);
        if (!blueprintData.isEmpty())
        {
            return FramedBlueprintItem.getCamoContainers(blueprintData);
        }
        return CamoList.EMPTY;
    }

    @Override
    public CamoList postProcessCamo(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo
    )
    {
        if (proxiedStack == null)
        {
            return CamoList.EMPTY;
        }
        return proxyBehaviour(proxiedStack).postProcessCamo(proxiedStack, null, ctx, renderState, renderPass, camo);
    }

    @Override
    @Nullable
    public Holder<BlockOverlay> readBlockOverlay(ItemStack stack, @Nullable ItemStack proxiedStack, int renderPass)
    {
        if (proxiedStack == null)
        {
            return null;
        }
        return proxyBehaviour(proxiedStack).readBlockOverlay(stack, proxiedStack, renderPass);
    }

    @Override
    public ModelData buildModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo,
            @Nullable Holder<BlockOverlay> overlay
    )
    {
        if (proxiedStack == null)
        {
            return ModelData.EMPTY;
        }
        return proxyBehaviour(proxiedStack).buildModelData(stack, proxiedStack, ctx, renderState, renderPass, camo, overlay);
    }

    @Override
    public ModelData appendModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    )
    {
        if (proxiedStack == null)
        {
            return data;
        }
        return proxyBehaviour(proxiedStack).appendModelData(proxiedStack, null, ctx, renderState, renderPass, data);
    }

    @Override
    public Vector3fc getRenderOffset(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    )
    {
        if (proxiedStack == null)
        {
            return OFFSET_ZERO;
        }
        return proxyBehaviour(proxiedStack).getRenderOffset(proxiedStack, null, ctx, renderState, renderPass, data);
    }

    private static GhostRenderBehaviour proxyBehaviour(ItemStack proxiedStack)
    {
        return GhostBlockRenderer.getBehaviour(proxiedStack.getItem());
    }
}
