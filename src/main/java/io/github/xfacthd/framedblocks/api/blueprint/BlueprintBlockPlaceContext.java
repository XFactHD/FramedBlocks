package io.github.xfacthd.framedblocks.api.blueprint;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.ApiStatus;

/// Special [BlockPlaceContext] used for placing blocks with a Framed Blueprint.
public final class BlueprintBlockPlaceContext extends BlockPlaceContext {
    private final BlueprintData blueprintData;

    @ApiStatus.Internal
    public BlueprintBlockPlaceContext(UseOnContext context, ItemStack dummyStack, BlueprintData blueprintData) {
        BlockHitResult hitResult = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside());
        super(context.getLevel(), context.getPlayer(), context.getHand(), dummyStack, hitResult);
        this.blueprintData = blueprintData;
    }

    /// {@return the blueprint data the block is being placed with}
    public BlueprintData getBlueprintData() {
        return blueprintData;
    }
}
