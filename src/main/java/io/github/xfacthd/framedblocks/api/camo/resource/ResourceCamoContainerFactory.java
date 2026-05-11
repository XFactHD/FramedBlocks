package io.github.xfacthd.framedblocks.api.camo.resource;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public abstract class ResourceCamoContainerFactory<R extends Resource, C extends ResourceCamoContent<R, C>, T extends ResourceCamoContainer<R, C, T>> extends CamoContainerFactory<T> {
    protected final ItemCapability<ResourceHandler<R>, ItemAccess> itemCapability;
    protected final int resourceAmount;
    protected final ResourceCamoCraftingHandler<R, C, T> craftingHandler;

    protected ResourceCamoContainerFactory(ItemCapability<ResourceHandler<R>, ItemAccess> itemCapability, int resourceAmount, TagKey<Item> craftingBlockContainers) {
        this.itemCapability = itemCapability;
        this.resourceAmount = resourceAmount;
        this.craftingHandler = new ResourceCamoCraftingHandler<>(this, craftingBlockContainers);
    }

    @Override
    public final @Nullable T applyCamo(Level level, BlockPos pos, Player player, ItemAccess itemAccess) {
        return applyCamo(itemAccess, player, !player.isCreative(), !player.level().isClientSide());
    }

    protected @Nullable T applyCamo(ItemAccess itemAccess, @Nullable Player player, boolean consume, boolean commit) {
        ResourceHandler<R> handler = itemAccess.getCapability(itemCapability);
        if (handler == null || handler.size() <= 0) {
            return null;
        }

        for (int tank = 0; tank < handler.size(); tank++) {
            R resource = handler.getResource(tank);
            if (!isValidResource(resource, player)) {
                continue;
            }

            if (consume && ConfigView.Server.INSTANCE.shouldConsumeCamoItem()) {
                try (Transaction tx = Transaction.open(null)) {
                    if (handler.extract(tank, resource, FluidType.BUCKET_VOLUME, tx) != FluidType.BUCKET_VOLUME) {
                        continue;
                    }
                    if (commit) {
                        tx.commit();
                    }
                }
            }

            return createContainer(resource);
        }
        return null;
    }

    @Override
    public boolean removeCamo(Level level, BlockPos pos, Player player, ItemAccess itemAccess, T container) {
        if (itemAccess.getResource().isEmpty()) {
            return false;
        }

        ResourceHandler<R> handler = itemAccess.getCapability(itemCapability);
        if (handler == null) {
            return false;
        }

        R resource = container.getResource();
        if (!isValidForHandler(handler, resource)) {
            return false;
        }
        if (!player.isCreative() && ConfigView.Server.INSTANCE.shouldConsumeCamoItem()) {
            try (Transaction tx = Transaction.open(null)) {
                if (handler.insert(resource, resourceAmount, tx) != resourceAmount) {
                    return false;
                }
                if (!level.isClientSide()) {
                    tx.commit();
                }
            }
        }
        return true;
    }

    protected final boolean isValidForHandler(ResourceHandler<R> handler, R resource) {
        for (int tank = 0; tank < handler.size(); tank++) {
            if (!handler.isValid(tank, resource)) {
                continue;
            }

            R inTank = handler.getResource(tank);
            if (inTank.isEmpty() || inTank.equals(resource)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean canTriviallyConvertToItemStack() {
        return false;
    }

    @Override
    public final ItemStack dropCamo(T container) {
        return ItemStack.EMPTY;
    }

    @Override
    public final boolean validateCamo(T container) {
        return isValidResource(container.getResource(), null);
    }

    protected abstract T createContainer(R resource);

    protected abstract boolean isValidResource(R resource, @Nullable Player player);

    @Override
    public final ResourceCamoCraftingHandler<R, C, T> getCraftingHandler() {
        return craftingHandler;
    }
}
