package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.cube.FramedChestBlock;
import xfacthd.framedblocks.common.capability.IStorageBlockItemHandler;
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

    public FramedChestBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_CHEST.value(), pos, state);
    }

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
        IStorageBlockItemHandler handler = getChestItemHandler(false);
        if (handler != null)
        {
            handler.open();
            super.open(player);
        }
    }

    public void doOpen()
    {
        openCount++;
        if (getBlockState().getValue(PropertyHolder.CHEST_STATE) != ChestState.OPENING)
        {
            level().setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.CHEST_STATE, ChestState.OPENING));
            playSound(level(), worldPosition, getBlockState(), SoundEvents.CHEST_OPEN);
        }
    }

    public void close()
    {
        if (openCount > 0)
        {
            openCount--;
            if (openCount == 0)
            {
                playSound(level(), worldPosition, getBlockState(), SoundEvents.CHEST_CLOSE);
                level().setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.CHEST_STATE, ChestState.CLOSING));

                closeStart = level().getGameTime();
            }
        }
    }

    private static void playSound(Level level, BlockPos pos, BlockState state, SoundEvent sound)
    {
        ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
        if (type != ChestType.LEFT)
        {
            double x = (double)pos.getX() + 0.5;
            double y = (double)pos.getY() + 0.5;
            double z = (double)pos.getZ() + 0.5;
            if (type == ChestType.RIGHT)
            {
                Direction side = FramedChestBlock.getConnectionDirection(state);
                x += (double) side.getStepX() * 0.5;
                z += (double) side.getStepZ() * 0.5;
            }

            level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    public long getLastChangeTime(ChestState state)
    {
        if (lastChangeTime == 0 || state != lastState)
        {
            if ((lastState == ChestState.CLOSING && state == ChestState.OPENING) || (lastState == ChestState.OPENING && state == ChestState.CLOSING))
            {
                long diff = level().getGameTime() - lastChangeTime;
                lastChangeTime = level().getGameTime() - (diff < 10 ? 10 - diff : 0);
            }
            else
            {
                lastChangeTime = level().getGameTime();
            }
            lastState = state;
        }
        return lastChangeTime;
    }

    public IStorageBlockItemHandler getChestItemHandler(boolean override)
    {
        return FramedChestBlock.combine(this, override).apply(FramedChestBlock.CHEST_COMBINER).orElse(null);
    }

    @Override
    public int getAnalogOutputSignal()
    {
        return getAnalogOutputSignal(getChestItemHandler(false));
    }

    @Override
    protected Component getDefaultName()
    {
        return TITLE;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
    {
        IStorageBlockItemHandler handler = getChestItemHandler(false);
        return handler == null ? null : handler.createMenu(windowId, inv);
    }

    @Override
    public Component getDisplayName()
    {
        return FramedChestBlock.combine(this, true).apply(FramedChestBlock.TITLE_COMBINER);
    }

    @Override
    public void setBlockState(BlockState state)
    {
        BlockState oldState = getBlockState();
        super.setBlockState(state);
        if (state.getValue(FramedProperties.FACING_HOR) != oldState.getValue(FramedProperties.FACING_HOR) ||
            state.getValue(BlockStateProperties.CHEST_TYPE) != oldState.getValue(BlockStateProperties.CHEST_TYPE))
        {
            invalidateCapabilities();
        }
    }
}
