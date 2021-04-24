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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.Tags;
import xfacthd.framedblocks.client.util.FramedBlockData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.Utils;

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

    public ActionResultType handleInteraction(PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!getCamoState(hit).isAir() && stack.getItem() == FBContent.itemFramedHammer)
        {
            //noinspection ConstantConditions
            if (!world.isRemote())
            {
                int light = getLightValue();

                ItemStack camoStack = getCamoStack(hit);
                if (!player.inventory.addItemStackToInventory(camoStack))
                {
                    player.dropItem(camoStack, false);
                }

                boolean lightUpdate = getLightValue() != light;

                applyCamo(ItemStack.EMPTY, Blocks.AIR.getDefaultState(), hit);

                markDirty();
                if (lightUpdate) { doLightUpdate(); }
                world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
            }

            return world.isRemote() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
        }
        else if (getCamoState(hit).isAir() && stack.getItem() instanceof BlockItem)
        {
            BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            if (isValidBlock(state))
            {
                //noinspection ConstantConditions
                if (!world.isRemote())
                {
                    int light = getLightValue();

                    applyCamo(stack.split(1), state, hit);
                    if (player.isCreative()) { stack.grow(1); }

                    markDirty();
                    if (getLightValue() != light) { doLightUpdate(); }
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

    protected boolean isValidBlock(BlockState state)
    {
        Block block = state.getBlock();

        if (BLOCK_BLACKLIST.contains(block)) { return false; }
        if (block.hasTileEntity(state) && !TILE_ENTITY_WHITELIST.contains(block)) { return false; }

        //noinspection ConstantConditions
        return state.isOpaqueCube(world, pos) || state.isIn(Utils.FRAMEABLE);
    }

    protected void applyCamo(ItemStack camoStack, BlockState camoState, BlockRayTraceResult hit)
    {
        this.camoStack = camoStack;
        this.camoState = camoState;
    }

    public void setCamo(ItemStack camoStack, BlockState camoState, boolean secondary)
    {
        int light = getLightValue();

        this.camoStack = camoStack;
        this.camoState = camoState;

        markDirty();
        if (getLightValue() != light)
        {
            doLightUpdate();
        }
        //noinspection ConstantConditions
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
    }

    /**
     * Checks if this block hides the given face on the given adjacent block, assumes the caller has
     * checked that the corresponding face on this block fills the whole face (can be checked via the CTM_PREDICATE)
     * @param adjState The adjacent BlockState
     * @param face The face on the adjacent block
     */
    public boolean hidesAdjacentFace(BlockState adjState, Direction face) { return getCamoState(face.getOpposite()) == adjState; }

    /**
     * Used to return a different camo state depending on the given side
     * @param side The blocks face, can return AIR if the face does not pass the CTM_PREDICATE
     */
    public BlockState getCamoState(Direction side) { return camoState; }

    protected BlockState getCamoState(BlockRayTraceResult hit) { return camoState; }

    public BlockState getCamoState() { return camoState; }

    protected ItemStack getCamoStack(BlockRayTraceResult hit) { return camoStack; }

    public ItemStack getCamoStack() { return camoStack; }

    public void setGlowing(boolean glowing)
    {
        if (this.glowing != glowing)
        {
            int oldLight = getLightValue();

            this.glowing = glowing;

            if (oldLight != getLightValue())
            {
                doLightUpdate();
            }
            //noinspection ConstantConditions
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isGlowing() { return glowing; }

    public int getLightValue()
    {
        if (glowing) { return 15; }
        return camoState.getLightValue();
    }

    protected void doLightUpdate()
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
        writeToDataPacket(nbt);
        return new SUpdateTileEntityPacket(pos, -1, nbt);
    }

    @Override
    public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        CompoundNBT nbt = pkt.getNbtCompound();
        if (readFromDataPacket(nbt))
        {
            requestModelDataUpdate();

            //noinspection ConstantConditions
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }
    }

    protected void writeToDataPacket(CompoundNBT nbt)
    {
        nbt.put("camo_stack", camoStack.write(new CompoundNBT()));
        nbt.put("camo_state", NBTUtil.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);
    }

    protected boolean readFromDataPacket(CompoundNBT nbt)
    {
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

            needUpdate = true;
        }

        boolean newGlow = nbt.getBoolean("glowing");
        if (newGlow != glowing)
        {
            glowing = newGlow;
            needUpdate = true;

            doLightUpdate();
        }

        return needUpdate;
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
    public void handleUpdateTag(BlockState state, CompoundNBT nbt)
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
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);

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