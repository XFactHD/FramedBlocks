package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.property.ChestState;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedChestBlockEntity extends FramedStorageBlockEntity
{
    public static final Component TITLE = Utils.translate("title", "framed_chest");

    private int openCount = 0;
    private long closeStart = 0;

    //Client-only
    private long lastChangeTime = 0;
    private ChestState lastState = ChestState.CLOSED;

    public FramedChestBlockEntity(BlockPos pos, BlockState state) { super(FBContent.blockEntityTypeFramedChest.get(), pos, state); }

    public static void tick(Level level, BlockPos pos, BlockState state, FramedChestBlockEntity tile)
    {
        if (!level.isClientSide() && (level.getGameTime() - tile.closeStart) >= 10 && state.getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSING)
        {
            tile.closeStart = 0;
            level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.CHEST_STATE, ChestState.CLOSED));
        }
    }

    @Override
    public void open(ServerPlayer player)
    {
        if (getBlockState().getValue(PropertyHolder.CHEST_STATE) != ChestState.OPENING)
        {
            //noinspection ConstantConditions
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.CHEST_STATE, ChestState.OPENING));
            level.playSound(null, worldPosition, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }

        openCount++;

        super.open(player);
    }

    public void close()
    {
        if (openCount > 0)
        {
            openCount--;
            if (openCount == 0)
            {
                //noinspection ConstantConditions
                level.playSound(null, worldPosition, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.CHEST_STATE, ChestState.CLOSING));

                closeStart = level.getGameTime();
            }
        }
    }

    public long getLastChangeTime(ChestState state)
    {
        if (lastChangeTime == 0 || state != lastState)
        {
            if ((lastState == ChestState.CLOSING && state == ChestState.OPENING) || (lastState == ChestState.OPENING && state == ChestState.CLOSING))
            {
                //noinspection ConstantConditions
                long diff = level.getGameTime() - lastChangeTime;
                lastChangeTime = level.getGameTime() - (diff < 10 ? 10 - diff : 0);
            }
            else
            {
                //noinspection ConstantConditions
                lastChangeTime = level.getGameTime();
            }
            lastState = state;
        }
        return lastChangeTime;
    }

    @Override
    protected Component getDefaultName() { return TITLE; }
}