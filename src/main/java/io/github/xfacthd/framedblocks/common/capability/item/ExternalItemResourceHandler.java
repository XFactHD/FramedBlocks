package io.github.xfacthd.framedblocks.common.capability.item;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public abstract class ExternalItemResourceHandler extends DelegatingResourceHandler<ItemResource> {
    public ExternalItemResourceHandler(ResourceHandler<ItemResource> delegate) {
        super(delegate);
    }

    @Override
    public final int extract(int slot, ItemResource resource, int amount, TransactionContext ctx) {
        if (!canExtract(slot)) {
            return 0;
        }
        return delegate.get().extract(slot, resource, amount, ctx);
    }

    protected abstract boolean canExtract(int slot);
}
