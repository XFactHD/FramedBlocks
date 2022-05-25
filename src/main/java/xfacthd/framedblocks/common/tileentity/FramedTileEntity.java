package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.*;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.FramedBlockData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.*;

import java.util.List;

@SuppressWarnings("deprecation")
public class FramedTileEntity extends TileEntity
{
    public static final TranslationTextComponent MSG_BLACKLISTED = new TranslationTextComponent("msg." + FramedBlocks.MODID + ".blacklisted");
    public static final TranslationTextComponent MSG_TILE_ENTITY = new TranslationTextComponent("msg." + FramedBlocks.MODID + ".tile_entity");

    private final FramedBlockData modelData = new FramedBlockData(true);
    private ItemStack camoStack = ItemStack.EMPTY;
    private BlockState camoState = Blocks.AIR.defaultBlockState();
    private boolean glowing = false;
    private boolean intangible = false;
    private boolean recheckStates = false;

    public FramedTileEntity() { this(FBContent.tileTypeFramedBlock.get()); }

    protected FramedTileEntity(TileEntityType<?> type) { super(type); }

    public ActionResultType handleInteraction(PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
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
        else if (stack.getItem().is(Tags.Items.DUSTS_GLOWSTONE) && !glowing)
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative()) { stack.shrink(1); }

                int light = getLightValue();
                glowing = true;
                if (updateDynamicStates(false, true))
                {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }

                boolean lightUpdate = getLightValue() != light;

                setChanged();
                if (lightUpdate) { doLightUpdate(); }
            }
            return level.isClientSide() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
        }
        else if (!camo.isAir() && !player.isShiftKeyDown() && stack.getItem().is(Utils.WRENCH))
        {
            return rotateCamo(camo, hit);
        }
        else if (ServerConfig.enableIntangibleFeature && stack.getItem() == ServerConfig.intangibleMarkerItem && !intangible && getBlock().getBlockType().allowMakingIntangible())
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative()) { stack.shrink(1); }

                setIntangible(true);
            }
            return ActionResultType.sidedSuccess(level.isClientSide());
        }
        else if (intangible && player.isShiftKeyDown() && stack.getItem().is(Utils.WRENCH))
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                setIntangible(false);

                ItemStack result = new ItemStack(ServerConfig.intangibleMarkerItem);
                if (!player.inventory.add(result))
                {
                    player.drop(result, false);
                }
            }
            return ActionResultType.sidedSuccess(level.isClientSide());
        }

        return ActionResultType.PASS;
    }

    private ActionResultType clearBlockCamo(PlayerEntity player, BlockRayTraceResult hit)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            int light = getLightValue();

            ItemStack camoStack = getCamoStack(hit);
            if (!player.inventory.add(camoStack))
            {
                player.drop(camoStack, false);
            }

            applyCamo(ItemStack.EMPTY, Blocks.AIR.defaultBlockState(), hit);
            if (!updateDynamicStates(true, true))
            {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }

            boolean lightUpdate = getLightValue() != light;

            setChanged();
            if (lightUpdate) { doLightUpdate(); }
        }

        return level.isClientSide() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
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
                if (!level.isClientSide())
                {
                    if (!player.isCreative())
                    {
                        if (stack.getItem() == Items.BUCKET)
                        {
                            stack.shrink(1);

                            ItemStack result = new ItemStack(fluid.getFluid().getBucket());
                            if (!player.inventory.add(result))
                            {
                                player.drop(result, false);
                            }
                        }
                        else
                        {
                            handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }

                    int light = getLightValue();
                    applyCamo(ItemStack.EMPTY, Blocks.AIR.defaultBlockState(), hit);
                    if (!updateDynamicStates(true, true))
                    {
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                    }
                    boolean lightUpdate = getLightValue() != light;

                    setChanged();
                    if (lightUpdate) { doLightUpdate(); }
                }
                return level.isClientSide() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
            }
            return ActionResultType.FAIL;
        }).orElse(ActionResultType.FAIL);
    }

    private ActionResultType setBlockCamo(PlayerEntity player, ItemStack stack, BlockRayTraceResult hit)
    {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        if (isValidBlock(state, player))
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                int light = getLightValue();

                applyCamo(stack.split(1), state, hit);
                if (!updateDynamicStates(true, true))
                {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
                if (player.isCreative()) { stack.grow(1); }

                setChanged();
                if (getLightValue() != light) { doLightUpdate(); }

            }

            return level.isClientSide() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
        }
        return ActionResultType.FAIL;
    }

    private ActionResultType setFluidCamo(PlayerEntity player, ItemStack stack, BlockRayTraceResult hit)
    {
        LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        return cap.map(handler ->
        {
            FluidStack fluid = handler.getFluidInTank(0);

            BlockState state = fluid.getFluid().defaultFluidState().createLegacyBlock();
            if (!state.isAir())
            {
                ItemStack bucket = new ItemStack(fluid.getFluid().getBucket());
                if (fluid.getAmount() >= 1000 && !bucket.isEmpty() && handler.drain(1000, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1000)
                {
                    //noinspection ConstantConditions
                    if (!level.isClientSide())
                    {
                        if (!player.isCreative())
                        {
                            if (stack.getItem() instanceof BucketItem)
                            {
                                stack.shrink(1);

                                ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                                if (!player.inventory.add(emptyBucket))
                                {
                                    player.drop(emptyBucket, false);
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
                        if (!updateDynamicStates(true, true))
                        {
                            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                        }

                        setChanged();
                        if (getLightValue() != light) { doLightUpdate(); }
                    }
                    return level.isClientSide() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
                }
            }
            return ActionResultType.FAIL;
        }).orElse(ActionResultType.FAIL);
    }

    private ActionResultType rotateCamo(BlockState camo, BlockRayTraceResult hit)
    {
        Property<?> prop = getRotatableProperty(camo);
        if (prop != null)
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                applyCamo(getCamoStack(hit), camo.cycle(prop), hit);

                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return ActionResultType.sidedSuccess(level.isClientSide());
        }
        return ActionResultType.FAIL;
    }

    private Property<?> getRotatableProperty(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if (prop.getValueClass() == Direction.Axis.class)
            {
                return prop;
            }
            else if (prop instanceof DirectionProperty)
            {
                return prop;
            }
        }
        return null;
    }

    protected boolean isValidBlock(BlockState state, PlayerEntity player)
    {
        Block block = state.getBlock();
        if (block instanceof IFramedBlock) { return false; }

        if (state.is(Utils.BLACKLIST))
        {
            if (player != null)
            {
                player.displayClientMessage(MSG_BLACKLISTED, true);
            }
            return false;
        }
        if (block.hasTileEntity(state) && !ServerConfig.allowBlockEntities)
        {
            if (player != null)
            {
                player.displayClientMessage(MSG_TILE_ENTITY, true);
            }
            return false;
        }

        //noinspection ConstantConditions
        return state.isSolidRender(level, worldPosition) || state.is(Utils.FRAMEABLE);
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

        setChanged();
        if (getLightValue() != light)
        {
            doLightUpdate();
        }
        //noinspection ConstantConditions
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean isSolidSide(Direction side)
    {
        //noinspection ConstantConditions
        return getBlock().getCtmPredicate().test(getBlockState(), side) && camoState.isSolidRender(level, worldPosition);
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

    protected boolean isCamoSolid()
    {
        //noinspection ConstantConditions
        return !camoState.isAir() && camoState.isSolidRender(level, worldPosition);
    }

    public final void checkCamoSolid()
    {
        if (getBlock().getBlockType().canOccludeWithSolidCamo() && !camoState.isAir())
        {
            updateDynamicStates(true, false);
        }
    }

    protected final boolean updateDynamicStates(boolean updateSolid, boolean updateLight)
    {
        if (!getBlock().getBlockType().canOccludeWithSolidCamo()) { return false; }

        BlockState state = getBlockState();
        boolean changed = false;

        if (updateSolid)
        {
            boolean wasSolid = getBlockState().getValue(PropertyHolder.SOLID);
            boolean solid = !intangible && isCamoSolid();

            if (solid != wasSolid)
            {
                state = state.setValue(PropertyHolder.SOLID, solid);
                changed = true;
            }
        }

        if (updateLight)
        {
            boolean isGlowing = getLightValue() > 0;

            if (isGlowing != state.getValue(PropertyHolder.GLOWING))
            {
                state = state.setValue(PropertyHolder.GLOWING, isGlowing);
                changed = true;
            }
        }

        if (changed)
        {
            //noinspection ConstantConditions
            level.setBlock(worldPosition, state, Constants.BlockFlags.DEFAULT);
        }
        return changed;
    }

    public float getCamoBlastResistance(Explosion explosion)
    {
        return camoState.getExplosionResistance(level, worldPosition, explosion);
    }

    public boolean isCamoFlammable(Direction face)
    {
        return camoState.isAir() || camoState.isFlammable(level, worldPosition, face);
    }

    public int getCamoFlammability(Direction face)
    {
        return getCamoState().isAir() ? -1 : getCamoState().getFlammability(level, worldPosition, face);
    }

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

            setChanged();
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isGlowing() { return glowing; }

    public int getLightValue()
    {
        if (glowing) { return 15; }
        return camoState.getLightEmission();
    }

    public void setIntangible(boolean intangible)
    {
        if (this.intangible != intangible)
        {
            this.intangible = intangible;

            setChanged();

            if (!updateDynamicStates(true, false))
            {
                //noinspection ConstantConditions
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    public boolean isIntangible(ISelectionContext ctx)
    {
        if (!ServerConfig.enableIntangibleFeature || !intangible) { return false; }

        if (ctx instanceof EntitySelectionContext && ctx.getEntity() instanceof PlayerEntity)
        {
            ItemStack mainItem = ((PlayerEntity) ctx.getEntity()).getMainHandItem();
            return !mainItem.getItem().is(Utils.WRENCH) && mainItem.getItem() != FBContent.itemFramedHammer.get();
        }

        return true;
    }

    protected void doLightUpdate()
    {
        //noinspection ConstantConditions
        level.getChunkSource().getLightEngine().checkBlock(worldPosition);
    }

    public final IFramedBlock getBlock() { return (IFramedBlock) getBlockState().getBlock(); }

    public void addCamoDrops(List<ItemStack> drops)
    {
        if (!camoStack.isEmpty())
        {
            drops.add(camoStack);
        }
    }

    public MaterialColor getMapColor()
    {
        if (!camoState.isAir())
        {
            //noinspection ConstantConditions
            return camoState.getMapColor(level, worldPosition);
        }
        return null;
    }

    public void checkSolidStateOnLoad()
    {
        //noinspection ConstantConditions
        if (!level.isClientSide() && recheckStates)
        {
            checkCamoSolid();
        }
    }

    /*
     * Sync
     */

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT nbt = new CompoundNBT();
        writeToDataPacket(nbt);
        return new SUpdateTileEntityPacket(worldPosition, -1, nbt);
    }

    @Override
    public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        CompoundNBT nbt = pkt.getTag();
        if (readFromDataPacket(nbt))
        {
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    protected void writeToDataPacket(CompoundNBT nbt)
    {
        nbt.put("camo_stack", camoStack.save(new CompoundNBT()));
        nbt.put("camo_state", NBTUtil.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);
    }

    protected boolean readFromDataPacket(CompoundNBT nbt)
    {
        camoStack = ItemStack.of(nbt.getCompound("camo_stack"));

        boolean needUpdate = false;
        BlockState newState = NBTUtil.readBlockState(nbt.getCompound("camo_state"));
        if (newState != camoState)
        {
            int oldLight = getLightValue();
            camoState = newState;
            if (oldLight != getLightValue()) { doLightUpdate(); }

            modelData.setWorld(level);
            modelData.setPos(worldPosition);
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

        boolean newIntangible = nbt.getBoolean("intangible");
        if (newIntangible != intangible)
        {
            intangible = newIntangible;
            needUpdate = true;
        }

        return needUpdate;
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbt = super.getUpdateTag();

        nbt.put("camo_stack", camoStack.save(new CompoundNBT()));
        nbt.put("camo_state", NBTUtil.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);

        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt)
    {
        camoStack = ItemStack.of(nbt.getCompound("camo_stack"));

        BlockState newState = NBTUtil.readBlockState(nbt.getCompound("camo_state"));
        if (newState != camoState)
        {
            camoState = newState;

            modelData.setWorld(level);
            modelData.setPos(worldPosition);
            modelData.setCamoState(camoState);
        }

        glowing = nbt.getBoolean("glowing");
        intangible = nbt.getBoolean("intangible");
    }

    /*
     * Model data
     */

    @Override
    public IModelData getModelData() { return modelData; }

    /*
     * NBT stuff
     */

    public CompoundNBT writeToBlueprint() { return save(new CompoundNBT()); }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.put("camo_stack", camoStack.save(new CompoundNBT()));
        nbt.put("camo_state", NBTUtil.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);
        nbt.putByte("updated", (byte) 2);

        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);

        BlockState camoState = NBTUtil.readBlockState(nbt.getCompound("camo_state"));
        if (camoState.isAir() || isValidBlock(camoState, null))
        {
            recheckStates = nbt.getByte("updated") < 2;
            this.camoState = camoState;
            camoStack = ItemStack.of(nbt.getCompound("camo_stack"));
        }
        else
        {
            recheckStates = true;
            FramedBlocks.LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\", removing camo! This might be caused by a config or tag change!",
                    state.getBlock().getRegistryName(),
                    worldPosition,
                    camoState.getBlock().getRegistryName()
            );
        }
        glowing = nbt.getBoolean("glowing");
        intangible = nbt.getBoolean("intangible");

        if (EffectiveSide.get().isServer())
        {
            EventHandler.addNewTileEntity(this);
        }
    }
}