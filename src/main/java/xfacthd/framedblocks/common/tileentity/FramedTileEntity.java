package xfacthd.framedblocks.common.tileentity;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.Tags;
import xfacthd.framedblocks.client.util.FramedBlockData;
import xfacthd.framedblocks.common.FBContent;

@SuppressWarnings("deprecation")
public class FramedTileEntity extends TileEntity
{
    private static final ImmutableList<Block> TILE_ENTITY_WHITELIST = buildTileEntityWhitelist();
    private static final ImmutableList<Block> BLOCK_BLACKLIST = buildBlockBlacklist();

    private final IModelData modelData = new FramedBlockData();
    private ItemStack camoStack = ItemStack.EMPTY;
    private BlockState camoState = Blocks.AIR.getDefaultState();
    private boolean glowing = false;

    public FramedTileEntity() { this(FBContent.tileTypeFramedBlock); }

    protected FramedTileEntity(TileEntityType<?> type) { super(type); }

    public ActionResultType handleInteraction(PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!camoState.isAir() && stack.getItem() == FBContent.itemFramedHammer)
        {
            //noinspection ConstantConditions
            if (!world.isRemote())
            {
                if (!player.inventory.addItemStackToInventory(camoStack))
                {
                    player.dropItem(camoStack, false);
                }

                boolean lightUpdate = getLightValue() != 0;

                camoStack = ItemStack.EMPTY;
                camoState = Blocks.AIR.getDefaultState();

                markDirty();
                if (lightUpdate) { doLightUpdate(); }
                world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
            }

            return world.isRemote() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
        }
        else if (camoState.isAir() && stack.getItem() instanceof BlockItem)
        {
            BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            if (isValidBlock(state))
            {
                //noinspection ConstantConditions
                if (!world.isRemote())
                {
                    camoStack = stack.split(1);
                    if (player.isCreative()) { stack.grow(1); }
                    camoState = state;

                    markDirty();
                    if (getLightValue() != 0) { doLightUpdate(); }
                    world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
                }

                return world.isRemote() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
            }
        }
        else if (stack.getItem().isIn(Tags.Items.DUSTS_GLOWSTONE) && !glowing)
        {
            //noinspection ConstantConditions
            if (!world.isRemote())
            {
                if (!player.isCreative()) { stack.shrink(1); }

                glowing = true;

                markDirty();
                doLightUpdate();
                world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
            }
            return world.isRemote() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
        }

        return ActionResultType.FAIL;
    }

    private boolean isValidBlock(BlockState state)
    {
        Block block = state.getBlock();

        if (BLOCK_BLACKLIST.contains(block)) { return false; }
        if (block.hasTileEntity(state) && !TILE_ENTITY_WHITELIST.contains(block)) { return false; }

        //noinspection ConstantConditions
        return block.isOpaqueCube(state, world, pos);
    }

    public BlockState getCamoState() { return camoState; }

    public ItemStack getCamoStack() { return camoStack; }

    public int getLightValue()
    {
        if (glowing) { return 15; }
        return camoState.getLightValue();
    }

    private void doLightUpdate()
    {
        //noinspection ConstantConditions
        world.getChunkProvider().getLightManager().checkBlock(pos);
    }

    /*
     * Sync
     */

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT nbt = new CompoundNBT();

        nbt.put("camo_stack", camoStack.write(new CompoundNBT()));
        nbt.put("camo_state", NBTUtil.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);

        return new SUpdateTileEntityPacket(pos, -1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        CompoundNBT nbt = pkt.getNbtCompound();
        camoStack = ItemStack.read(nbt.getCompound("camo_stack"));

        boolean needUpdate = false;
        BlockState newState = NBTUtil.readBlockState(nbt.getCompound("camo_state"));
        if (newState != camoState)
        {
            int oldLight = getLightValue();
            camoState = newState;
            if (oldLight != getLightValue()) { doLightUpdate(); }

            modelData.setData(FramedBlockData.WORLD, world);
            modelData.setData(FramedBlockData.POS, pos);
            modelData.setData(FramedBlockData.CAMO, camoState);
            requestModelDataUpdate();

            needUpdate = true;
        }

        boolean newGlow = nbt.getBoolean("glowing");
        if (newGlow != glowing)
        {
            glowing = newGlow;
            needUpdate = true;

            doLightUpdate();
        }

        if (needUpdate)
        {
            //noinspection ConstantConditions
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbt = super.getUpdateTag();

        nbt.put("camo_stack", camoStack.write(new CompoundNBT()));
        nbt.put("camo_state", NBTUtil.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundNBT nbt)
    {
        camoStack = ItemStack.read(nbt.getCompound("camo_stack"));

        BlockState newState = NBTUtil.readBlockState(nbt.getCompound("camo_state"));
        if (newState != camoState)
        {
            camoState = newState;

            modelData.setData(FramedBlockData.WORLD, world);
            modelData.setData(FramedBlockData.POS, pos);
            modelData.setData(FramedBlockData.CAMO, camoState);
            requestModelDataUpdate();
        }

        glowing = nbt.getBoolean("glowing");
    }

    /*
     * Model data
     */

    @Override
    public IModelData getModelData() { return modelData; }

    /*
     * NBT stuff
     */

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("camo_stack", camoStack.write(new CompoundNBT()));
        nbt.put("camo_state", NBTUtil.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);

        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);

        camoStack = ItemStack.read(nbt.getCompound("camo_stack"));
        camoState = NBTUtil.readBlockState(nbt.getCompound("camo_state"));
        glowing = nbt.getBoolean("glowing");
    }

    private static ImmutableList<Block> buildTileEntityWhitelist()
    {
        return ImmutableList.<Block>builder()
                .add(Blocks.JUKEBOX)
                .build();
    }

    private static ImmutableList<Block> buildBlockBlacklist()
    {
        return ImmutableList.<Block>builder()
                .add(Blocks.PISTON)
                .add(Blocks.STICKY_PISTON)
                .add(Blocks.COMPOSTER)
                .build();
    }
}