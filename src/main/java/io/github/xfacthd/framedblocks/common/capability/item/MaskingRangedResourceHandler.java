package io.github.xfacthd.framedblocks.common.capability.item;

import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class MaskingRangedResourceHandler<T extends Resource> extends RangedResourceHandler<T> {
    private final boolean canInsert;
    private final boolean canExtract;

    private MaskingRangedResourceHandler(ResourceHandler<T> delegate, int start, int end, boolean canInsert, boolean canExtract) {
        super(delegate, start, end);
        this.canInsert = canInsert;
        this.canExtract = canExtract;
    }

    public static <T extends Resource> ResourceHandler<T> insertOnly(ResourceHandler<T> delegate, int start, int end) {
        return of(delegate, start, end, true, false);
    }

    public static <T extends Resource> ResourceHandler<T> extractOnly(ResourceHandler<T> delegate, int start, int end) {
        return of(delegate, start, end, false, true);
    }

    public static <T extends Resource> ResourceHandler<T> of(ResourceHandler<T> delegate, int start, int end, boolean canInsert, boolean canExtract) {
        if (canInsert && canExtract) {
            return RangedResourceHandler.of(delegate, start, end);
        }
        return new MaskingRangedResourceHandler<>(delegate, start, end, canInsert, canExtract);
    }

    @Override
    public int insert(T resource, int amount, TransactionContext transaction) {
        return canInsert ? super.insert(resource, amount, transaction) : 0;
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        return canInsert ? super.insert(index, resource, amount, transaction) : 0;
    }

    @Override
    public int extract(T resource, int amount, TransactionContext transaction) {
        return canExtract ? super.extract(resource, amount, transaction) : 0;
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        return canExtract ? super.extract(index, resource, amount, transaction) : 0;
    }
}
