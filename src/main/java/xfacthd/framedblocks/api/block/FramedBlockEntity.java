package xfacthd.framedblocks.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.ClientUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.ServerConfig;

import java.util.List;

@SuppressWarnings("deprecation")
public class FramedBlockEntity extends BlockEntity
{
    public static final TranslatableComponent MSG_BLACKLISTED = Utils.translate("msg", "blacklisted");
    public static final TranslatableComponent MSG_BLOCK_ENTITY = Utils.translate("msg", "block_entity");
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int DATA_VERSION = 2;

    private final FramedBlockData modelData = new FramedBlockData(false);
    private CamoContainer camoContainer = EmptyCamoContainer.EMPTY;
    private boolean glowing = false;
    private boolean intangible = false;
    private boolean recheckStates = false;

    public FramedBlockEntity(BlockPos pos, BlockState state) { this(FramedBlocksAPI.getInstance().defaultBlockEntity(), pos, state); }

    protected FramedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    public final InteractionResult handleInteraction(Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        boolean secondary = hitSecondary(hit);
        CamoContainer camo = getCamo(secondary);

        if (camo.getType().isBlock() && FramedBlocksAPI.getInstance().isFramedHammer(stack))
        {
            return clearBlockCamo(player, camo, stack, secondary);
        }
        else if (camo.getType().isFluid() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            if (true) { return InteractionResult.PASS; }
            return clearFluidCamo(player, camo, stack, secondary);
        }
        else if (camo.isEmpty() && stack.getItem() instanceof BlockItem)
        {
            return setBlockCamo(player, stack, secondary);
        }
        else if (camo.isEmpty() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            if (true) { return InteractionResult.PASS; }
            return setFluidCamo(player, stack, secondary);
        }
        else if (stack.is(Tags.Items.DUSTS_GLOWSTONE) && !glowing)
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative()) { stack.shrink(1); }

                int light = getLightValue();
                glowing = true;
                if (!updateDynamicStates(false, true))
                {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }

                boolean lightUpdate = getLightValue() != light;

                setChanged();
                if (lightUpdate) { doLightUpdate(); }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        else if (!camo.isEmpty() && !player.isShiftKeyDown() && stack.is(Utils.WRENCH))
        {
            return rotateCamo(camo);
        }
        else if (ServerConfig.enableIntangibleFeature && stack.is(ServerConfig.intangibleMarkerItem) && !intangible && getBlock().getBlockType().allowMakingIntangible())
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative()) { stack.shrink(1); }

                setIntangible(true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        else if (intangible && player.isShiftKeyDown() && stack.is(Utils.WRENCH))
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                setIntangible(false);

                ItemStack result = new ItemStack(ServerConfig.intangibleMarkerItem);
                if (!player.getInventory().add(result))
                {
                    player.drop(result, false);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    private InteractionResult clearBlockCamo(Player player, CamoContainer camo, ItemStack stack, boolean secondary)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            ItemStack camoStack = camo.toItemStack(stack);
            if ((!player.isCreative() || !player.getInventory().contains(camoStack)) && !player.getInventory().add(camoStack))
            {
                player.drop(camoStack, false);
            }

            setCamo(EmptyCamoContainer.EMPTY, secondary);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private InteractionResult clearFluidCamo(Player player, CamoContainer camo, ItemStack stack, boolean secondary)
    {
        ItemStack result = camo.toItemStack(stack);
        if (!result.isEmpty())
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative())
                {
                    if (stack.getItem() == Items.BUCKET)
                    {
                        stack.shrink(1);

                        if (!player.getInventory().add(result))
                        {
                            player.drop(result, false);
                        }
                    }
                    else
                    {
                        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler ->
                        {
                            FluidStack fluid = new FluidStack(camo.getFluid(), FluidAttributes.BUCKET_VOLUME);
                            handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                        });
                    }
                }

                setCamo(EmptyCamoContainer.EMPTY, secondary);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult setBlockCamo(Player player, ItemStack stack, boolean secondary)
    {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        if (isValidBlock(state, player))
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                CamoContainer.Factory factory = FramedBlocksAPI.getInstance().getCamoContainerFactory(stack);
                setCamo(factory.fromItem(stack), secondary);

                if (!player.isCreative())
                {
                    stack.shrink(1);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    //TODO: sides are not hidden when next to a "falling" fluid block
    private InteractionResult setFluidCamo(Player player, ItemStack stack, boolean secondary)
    {
        CamoContainer.Factory factory = FramedBlocksAPI.getInstance().getCamoContainerFactory(stack);
        CamoContainer camo = factory.fromItem(stack);
        if (!camo.isEmpty())
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
                        if (!player.getInventory().add(emptyBucket))
                        {
                            player.drop(emptyBucket, false);
                        }
                    }
                    else
                    {
                        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler ->
                            handler.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                        );
                    }
                }

                setCamo(camo, secondary);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult rotateCamo(CamoContainer camo)
    {
        if (camo.rotateCamo())
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    protected final boolean isValidBlock(BlockState state, Player player)
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
        if (state.hasBlockEntity() && !FramedBlocksAPI.getInstance().allowBlockEntities() && !state.is(Utils.BE_WHITELIST))
        {
            if (player != null)
            {
                player.displayClientMessage(MSG_BLOCK_ENTITY, true);
            }
            return false;
        }

        //noinspection ConstantConditions
        return state.isSolidRender(level, worldPosition) || state.is(Utils.FRAMEABLE);
    }

    protected boolean hitSecondary(BlockHitResult hit) { return false; }

    public void setCamo(CamoContainer camo, boolean secondary)
    {
        int light = getLightValue();

        this.camoContainer = camo;

        setChanged();
        if (getLightValue() != light)
        {
            doLightUpdate();
        }

        if (!updateDynamicStates(true, true))
        {
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public boolean isSolidSide(Direction side)
    {
        if (camoContainer.isEmpty()) { return false; }

        //noinspection ConstantConditions
        return getBlock().getCtmPredicate().test(getBlockState(), side) && camoContainer.getState().isSolidRender(level, worldPosition);
    }

    /**
     * Used to return a different camo depending on the given side
     * @param side The blocks face, can return EMPTY if the face does not pass the CTM_PREDICATE
     */
    public CamoContainer getCamo(Direction side) { return camoContainer; }

    protected CamoContainer getCamo(boolean secondary) { return camoContainer; }

    public final CamoContainer getCamo() { return camoContainer; }

    protected boolean isCamoSolid()
    {
        //noinspection ConstantConditions
        return !camoContainer.isEmpty() && camoContainer.getState().isSolidRender(level, worldPosition);
    }

    public final void checkCamoSolid()
    {
        if (getBlock().getBlockType().canOccludeWithSolidCamo() && !camoContainer.isEmpty())
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
            boolean wasSolid = getBlockState().getValue(FramedProperties.SOLID);
            boolean solid = !intangible && isCamoSolid();

            if (solid != wasSolid)
            {
                state = state.setValue(FramedProperties.SOLID, solid);
                changed = true;
            }
        }

        if (updateLight)
        {
            boolean isGlowing = getLightValue() > 0;

            if (isGlowing != state.getValue(FramedProperties.GLOWING))
            {
                state = state.setValue(FramedProperties.GLOWING, isGlowing);
                changed = true;
            }
        }

        if (changed)
        {
            //noinspection ConstantConditions
            level.setBlock(worldPosition, state, Block.UPDATE_ALL);
        }
        return changed;
    }

    @SuppressWarnings("ConstantConditions")
    public final void updateCulling(boolean neighbors, boolean rerender)
    {
        boolean changed = false;
        for (Direction dir : DIRECTIONS)
        {
            changed |= updateCulling(dir, false);
            if (neighbors && level.getBlockEntity(worldPosition.relative(dir)) instanceof FramedBlockEntity be)
            {
                be.updateCulling(dir.getOpposite(), true);
            }
        }

        if (rerender && changed)
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public boolean updateCulling(Direction side, boolean rerender)
    {
        return updateCulling(modelData, getBlockState(), side, rerender);
    }

    protected final boolean updateCulling(FramedBlockData modelData, BlockState testState, Direction side, boolean rerender)
    {
        boolean wasHidden = modelData.isSideHidden(side);
        boolean hidden = ((IFramedBlock) testState.getBlock()).isSideHidden(level, worldPosition, testState, side);
        if (wasHidden != hidden)
        {
            modelData.setSideHidden(side, hidden);
            if (rerender)
            {
                //noinspection ConstantConditions
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
    }

    public float getCamoExplosionResistance(Explosion explosion)
    {
        return camoContainer.getState().getExplosionResistance(level, worldPosition, explosion);
    }

    public boolean isCamoFlammable(Direction face)
    {
        return camoContainer.isEmpty() || camoContainer.getState().isFlammable(level, worldPosition, face);
    }

    public int getCamoFlammability(Direction face)
    {
        return camoContainer.isEmpty() ? -1 : camoContainer.getState().getFlammability(level, worldPosition, face);
    }

    public final void setGlowing(boolean glowing)
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
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public final boolean isGlowing() { return glowing; }

    public int getLightValue()
    {
        if (glowing) { return 15; }
        return camoContainer.getState().getLightEmission();
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
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public boolean isIntangible(CollisionContext ctx)
    {
        if (!ServerConfig.enableIntangibleFeature || !intangible) { return false; }

        if (ctx instanceof EntityCollisionContext ectx && ectx.getEntity() instanceof Player player)
        {
            ItemStack mainItem = player.getMainHandItem();
            return !mainItem.is(Utils.WRENCH) && !mainItem.is(FBContent.itemFramedHammer.get());
        }

        return true;
    }

    protected final void doLightUpdate()
    {
        //noinspection ConstantConditions
        level.getChunkSource().getLightEngine().checkBlock(worldPosition);
    }

    public final IFramedBlock getBlock() { return (IFramedBlock) getBlockState().getBlock(); }

    public void addCamoDrops(List<ItemStack> drops)
    {
        if (!camoContainer.isEmpty())
        {
            drops.add(camoContainer.toItemStack(ItemStack.EMPTY));
        }
    }

    public MaterialColor getMapColor() { return camoContainer.getMapColor(level, worldPosition); }

    @Override
    public void onLoad()
    {
        //noinspection ConstantConditions
        if (!level.isClientSide() && recheckStates)
        {
            checkCamoSolid();
        }
        super.onLoad();
    }

    /*
     * Sync
     */

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null && readFromDataPacket(nbt))
        {
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    protected void writeToDataPacket(CompoundTag nbt)
    {
        nbt.put("camo", CamoContainer.save(camoContainer));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);
    }

    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        boolean needUpdate = false;
        boolean needCullingUpdate = false;

        CamoContainer newCamo = CamoContainer.load(nbt.getCompound("camo"));
        if (!newCamo.equals(camoContainer))
        {
            int oldLight = getLightValue();
            camoContainer = newCamo;
            if (oldLight != getLightValue()) { doLightUpdate(); }

            modelData.setCamoState(camoContainer.getState());

            needUpdate = true;
            needCullingUpdate = true;
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
            needCullingUpdate = true;
        }

        if (needCullingUpdate)
        {
            updateCulling(true, false);
        }

        return needUpdate;
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = super.getUpdateTag();

        nbt.put("camo", CamoContainer.save(camoContainer));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        CamoContainer newCamo = CamoContainer.load(nbt.getCompound("camo"));
        if (!newCamo.equals(camoContainer))
        {
            camoContainer = newCamo;

            modelData.setCamoState(camoContainer.getState());

            ClientUtils.enqueueClientTask(() -> updateCulling(true, true));
        }

        glowing = nbt.getBoolean("glowing");
        intangible = nbt.getBoolean("intangible");
    }

    /*
     * Model data
     */

    @Override
    public IModelData getModelData() { return modelData; }

    protected final FramedBlockData getModelDataInternal() { return modelData; }

    /*
     * NBT stuff
     */

    public CompoundTag writeToBlueprint() { return saveWithoutMetadata(); }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.put("camo", CamoContainer.save(camoContainer));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);
        nbt.putByte("updated", (byte) DATA_VERSION);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        CamoContainer camo = CamoContainer.load(nbt.getCompound("camo"));
        if (camo.isEmpty() || isValidBlock(camo.getState(), null))
        {
            recheckStates = nbt.getByte("updated") < DATA_VERSION;
            camoContainer = camo;
        }
        else
        {
            recheckStates = true;
            FramedBlocks.LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\", removing camo! This might be caused by a config or tag change!",
                    getBlockState().getBlock().getRegistryName(),
                    worldPosition,
                    camo.getState().getBlock().getRegistryName()
            );
        }
        glowing = nbt.getBoolean("glowing");
        intangible = nbt.getBoolean("intangible");
    }
}