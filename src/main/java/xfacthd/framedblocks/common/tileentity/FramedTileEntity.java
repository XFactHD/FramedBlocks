package xfacthd.framedblocks.common.tileentity;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.FramedBlockData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.util.Utils;

@SuppressWarnings("deprecation")
public class FramedTileEntity extends TileEntity
{
    public static final TranslationTextComponent MSG_BLACKLISTED = new TranslationTextComponent("msg." + FramedBlocks.MODID + ".blacklisted");
    public static final TranslationTextComponent MSG_TILE_ENTITY = new TranslationTextComponent("msg." + FramedBlocks.MODID + ".tile_entity");
    private static final ImmutableList<Block> TILE_ENTITY_WHITELIST = buildTileEntityWhitelist();

    private final FramedBlockData modelData = new FramedBlockData();
    private ItemStack camoStack = ItemStack.EMPTY;
    private BlockState camoState = Blocks.AIR.getDefaultState();
    private boolean glowing = false;

    public FramedTileEntity() { this(FBContent.tileTypeFramedBlock.get()); }

    protected FramedTileEntity(TileEntityType<?> type) { super(type); }

    public ActionResultType handleInteraction(PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(hand);
        BlockState camo = getCamoState(hit);
        if (!camo.isAir() && !(camo.getBlock() instanceof FlowingFluidBlock) && stack.getItem() == FBContent.itemFramedHammer.get())
        {
            return clearBlockCamo(player, hit);
        }
        else if (!camo.isAir() && camo.getBlock() instanceof FlowingFluidBlock && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            if (true) { return ActionResultType.PASS; }
            return clearFluidCamo(player, camo, stack, hit);
        }
        else if (camo.isAir() && stack.getItem() instanceof BlockItem)
        {
            return setBlockCamo(player, stack, hit);
        }
        else if (camo.isAir() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            if (true) { return ActionResultType.PASS; }
            return setFluidCamo(player, stack, hit);
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

    private ActionResultType clearBlockCamo(PlayerEntity player, BlockRayTraceResult hit)
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

            applyCamo(ItemStack.EMPTY, Blocks.AIR.getDefaultState(), hit);

            boolean lightUpdate = getLightValue() != light;

            markDirty();
            if (lightUpdate) { doLightUpdate(); }
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }

        return world.isRemote() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
    }

    private ActionResultType clearFluidCamo(PlayerEntity player, BlockState camo, ItemStack stack, BlockRayTraceResult hit)
    {
        LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        return cap.map(handler ->
        {
            FluidStack fluid = new FluidStack(((FlowingFluidBlock) camo.getBlock()).getFluid(), 1000);
            if (handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE) == 1000)
            {
                //noinspection ConstantConditions
                if (!world.isRemote())
                {
                    if (!player.isCreative())
                    {
                        if (stack.getItem() == Items.BUCKET)
                        {
                            stack.shrink(1);

                            ItemStack result = new ItemStack(fluid.getFluid().getFilledBucket());
                            if (!player.inventory.addItemStackToInventory(result))
                            {
                                player.dropItem(result, false);
                            }
                        }
                        else
                        {
                            handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }

                    int light = getLightValue();
                    applyCamo(ItemStack.EMPTY, Blocks.AIR.getDefaultState(), hit);
                    boolean lightUpdate = getLightValue() != light;

                    markDirty();
                    if (lightUpdate) { doLightUpdate(); }
                    world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
                }
                return world.isRemote() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
            }
            return ActionResultType.FAIL;
        }).orElse(ActionResultType.FAIL);
    }

    private ActionResultType setBlockCamo(PlayerEntity player, ItemStack stack, BlockRayTraceResult hit)
    {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
        if (isValidBlock(state, player))
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
        return ActionResultType.FAIL;
    }

    private ActionResultType setFluidCamo(PlayerEntity player, ItemStack stack, BlockRayTraceResult hit)
    {
        LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        return cap.map(handler ->
        {
            FluidStack fluid = handler.getFluidInTank(0);

            BlockState state = fluid.getFluid().getDefaultState().getBlockState();
            if (!state.isAir())
            {
                ItemStack bucket = new ItemStack(fluid.getFluid().getFilledBucket());
                if (fluid.getAmount() >= 1000 && !bucket.isEmpty() && handler.drain(1000, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1000)
                {
                    //noinspection ConstantConditions
                    if (!world.isRemote())
                    {
                        if (!player.isCreative())
                        {
                            if (stack.getItem() instanceof BucketItem)
                            {
                                stack.shrink(1);

                                ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                                if (!player.inventory.addItemStackToInventory(emptyBucket))
                                {
                                    player.dropItem(emptyBucket, false);
                                }
                            }
                            else
                            {
                                handler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                            }
                        }

                        int light = getLightValue();

                        //Setting the bucket as the camo stack would allow duping buckets
                        applyCamo(ItemStack.EMPTY, state, hit);

                        markDirty();
                        if (getLightValue() != light) { doLightUpdate(); }
                        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
                    }
                    return world.isRemote() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
                }
            }
            return ActionResultType.FAIL;
        }).orElse(ActionResultType.FAIL);
    }

    protected boolean isValidBlock(BlockState state, PlayerEntity player)
    {
        Block block = state.getBlock();
        if (block instanceof IFramedBlock) { return false; }

        if (state.isIn(Utils.BLACKLIST))
        {
            player.sendStatusMessage(MSG_BLACKLISTED, true);
            return false;
        }
        if (block.hasTileEntity(state) && !TILE_ENTITY_WHITELIST.contains(block))
        {
            player.sendStatusMessage(MSG_TILE_ENTITY, true);
            return false;
        }

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

    public boolean isSolidSide(Direction side)
    {
        return getBlock().getCtmPredicate().test(getBlockState(), side) && camoState.isSolid();
    }

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

    protected final IFramedBlock getBlock() { return (IFramedBlock) getBlockState().getBlock(); }

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

            modelData.setWorld(world);
            modelData.setPos(pos);
            modelData.setCamoState(camoState);

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

            modelData.setWorld(world);
            modelData.setPos(pos);
            modelData.setCamoState(camoState);
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
}