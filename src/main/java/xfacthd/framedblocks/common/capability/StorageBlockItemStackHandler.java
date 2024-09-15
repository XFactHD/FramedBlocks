package xfacthd.framedblocks.common.capability;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;

public final class StorageBlockItemStackHandler extends ItemStackHandler
{
    @Nullable
    private final FramedStorageBlockEntity be;

    public StorageBlockItemStackHandler(@Nullable FramedStorageBlockEntity be, int slots)
    {
        super(slots);
        this.be = be;
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        if (be != null)
        {
            be.setChanged();
        }
    }

    public boolean stillValid(Player player)
    {
        return be != null && be.isUsableByPlayer(player);
    }

    public void close()
    {
        if (be instanceof FramedChestBlockEntity chest)
        {
            chest.close();
        }
    }
}
