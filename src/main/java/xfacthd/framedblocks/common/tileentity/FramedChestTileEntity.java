package xfacthd.framedblocks.common.tileentity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.ChestState;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedChestTileEntity extends FramedStorageTileEntity implements ITickableTileEntity
{
    public static final ITextComponent TITLE = new TranslationTextComponent("title.framedblocks:framed_chest");

    private int openCount = 0;
    private long closeStart = 0;

    //Client-only
    private long lastChangeTime = 0;
    private ChestState lastState = ChestState.CLOSED;

    public FramedChestTileEntity() { super(FBContent.tileTypeFramedChest.get()); }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote() && (world.getGameTime() - closeStart) >= 10 && getBlockState().get(PropertyHolder.CHEST_STATE) == ChestState.CLOSING)
        {
            closeStart = 0;
            world.setBlockState(pos, getBlockState().with(PropertyHolder.CHEST_STATE, ChestState.CLOSED));
        }
    }

    @Override
    public void open(ServerPlayerEntity player)
    {
        if (getBlockState().get(PropertyHolder.CHEST_STATE) != ChestState.OPENING)
        {
            //noinspection ConstantConditions
            world.setBlockState(pos, getBlockState().with(PropertyHolder.CHEST_STATE, ChestState.OPENING));
            world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
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
                world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
                world.setBlockState(pos, getBlockState().with(PropertyHolder.CHEST_STATE, ChestState.CLOSING));

                closeStart = world.getGameTime();
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
                long diff = world.getGameTime() - lastChangeTime;
                lastChangeTime = world.getGameTime() - (diff < 10 ? 10 - diff : 0);
            }
            else
            {
                //noinspection ConstantConditions
                lastChangeTime = world.getGameTime();
            }
            lastState = state;
        }
        return lastChangeTime;
    }

    @Override
    public ITextComponent getDisplayName() { return TITLE; }
}