package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.capability.energy.EntityAwareEnergyHandler;
import io.github.xfacthd.framedblocks.common.capability.item.RecipeInputItemResourceHandler;
import io.github.xfacthd.framedblocks.common.capability.item.MaskingRangedResourceHandler;
import io.github.xfacthd.framedblocks.common.config.ServerConfig;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeAdditive;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCalculation;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeMatchResult;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PoweredFramingSawBlockEntity extends BlockEntity {
    private static final boolean INSERT_ENERGY_DEBUG = true;
    private static final long ACTIVE_TIMEOUT = 40;

    private final RecipeInputItemResourceHandler itemHandler = new RecipeInputItemResourceHandler(FramingSawMenu.SLOT_RESULT + 1) {
        @Override
        protected void onContentsChanged(int slot, ItemStack prevStack) {
            PoweredFramingSawBlockEntity.this.onContentsChanged(slot);
        }

        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return PoweredFramingSawBlockEntity.this.isValidItem(slot, resource);
        }
    };
    private final ResourceHandler<ItemResource> externalItemHandler = new CombinedResourceHandler<>(
            MaskingRangedResourceHandler.insertOnly(itemHandler, FramingSawMenu.SLOT_INPUT, FramingSawMenu.SLOT_RESULT),
            MaskingRangedResourceHandler.extractOnly(itemHandler, FramingSawMenu.SLOT_RESULT, FramingSawMenu.SLOT_RESULT + 1)
    );
    private final EntityAwareEnergyHandler energyStorage = new EntityAwareEnergyHandler(
            ServerConfig.VIEW.getPoweredSawEnergyCapacity(),
            ServerConfig.VIEW.getPoweredSawMaxInput(),
            0,
            () -> this.needSaving = true
    );
    private final int energyConsumption;
    private final int craftingDuration;
    @UnknownNullability
    private FramingSawRecipeCache cache = null;
    @Nullable
    private ResourceKey<Recipe<?>> selectedRecipeId = null;
    @Nullable
    private RecipeHolder<FramingSawRecipe> selectedRecipe = null;
    private boolean active = false;
    private long lastActive = 0;
    private boolean recipeSatisfied = false;
    @Nullable
    private FramingSawRecipeMatchResult matchResult = null;
    @Nullable
    private FramingSawRecipeCalculation calculation = null;
    private int outputCount = 0;
    private int progress = 0;
    private boolean needSaving = false;
    private boolean inhibitUpdate = false;
    private boolean internalAccess = false;

    public PoweredFramingSawBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(FBContent.BE_TYPE_POWERED_FRAMING_SAW.value(), pPos, pBlockState);
        this.energyConsumption = ServerConfig.VIEW.getPoweredSawConsumption();
        this.craftingDuration = ServerConfig.VIEW.getPoweredSawCraftingDuration();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PoweredFramingSawBlockEntity be) {
        if (!Utils.PRODUCTION && INSERT_ENERGY_DEBUG) {
            try (Transaction tx = Transaction.open(null)) {
                be.energyStorage.insert(be.energyStorage.getMaxReceive(), tx);
                tx.commit();
            }
        }

        if ((be.active || level.getGameTime() - be.lastActive > ACTIVE_TIMEOUT) && be.canRun()) {
            if (!be.active) {
                be.active = true;
                level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.ACTIVE, true));
            }

            be.energyStorage.extractEnergyInternal(be.energyConsumption);
            be.progress++;

            if (be.progress >= be.craftingDuration) {
                be.progress = 0;

                ItemResource result = ItemResource.of(Objects.requireNonNull(be.selectedRecipe).value().getResultStack());
                be.internalAccess = true;
                try (Transaction tx = Transaction.open(null)) {
                    be.inhibitUpdate = true;
                    for (int i = 0; i < be.selectedRecipe.value().getAdditives().size(); i++) {
                        int slot = i + FramingSawMenu.SLOT_ADDITIVE_FIRST;
                        int additiveCount = Objects.requireNonNull(be.calculation).getAdditiveCount(i);
                        be.itemHandler.extract(slot, be.itemHandler.getResource(slot), additiveCount, tx);
                    }
                    be.inhibitUpdate = false;
                    int inputCount = Objects.requireNonNull(be.calculation).getInputCount();
                    be.itemHandler.extract(FramingSawMenu.SLOT_INPUT, be.itemHandler.getResource(FramingSawMenu.SLOT_INPUT), inputCount, tx);
                    be.itemHandler.insert(FramingSawMenu.SLOT_RESULT, result, be.outputCount, tx);
                    tx.commit();
                }
                be.internalAccess = false;
            }
        } else if (be.active) {
            be.active = false;
            level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.ACTIVE, false));
            be.lastActive = level.getGameTime();

            if (!be.recipeSatisfied) {
                be.progress = 0;
            }
        }

        if (be.needSaving) {
            level.blockEntityChanged(be.worldPosition);
            be.needSaving = false;
        }
    }

    private boolean canRun() {
        if (selectedRecipe == null || !recipeSatisfied) {
            return false;
        }
        if (energyStorage.getAmountAsInt() < energyConsumption) {
            return false;
        }

        ItemResource output = itemHandler.getResource(FramingSawMenu.SLOT_RESULT);
        if (!output.isEmpty()) {
            ItemStack result = selectedRecipe.value().getResultStack();
            if (!output.equals(ItemResource.of(result))) {
                return false;
            }
            int count = itemHandler.getAmountAsInt(FramingSawMenu.SLOT_RESULT);
            return count + outputCount <= output.getMaxStackSize();
        }
        return true;
    }

    private void checkRecipeSatisfied() {
        if (selectedRecipe != null) {
            matchResult = selectedRecipe.value().matchWithResult(itemHandler, level());
            recipeSatisfied = matchResult.success();
        } else {
            matchResult = null;
            recipeSatisfied = false;
        }

        if (recipeSatisfied) {
            calculation = Objects.requireNonNull(selectedRecipe).value().makeCraftingCalculation(itemHandler, false);
            outputCount = calculation.getOutputCount();
        } else {
            calculation = null;
            outputCount = 0;
            progress = 0;
        }
    }

    private void onContentsChanged(int slot) {
        needSaving = true;
        if (slot != FramingSawMenu.SLOT_RESULT && !inhibitUpdate) {
            checkRecipeSatisfied();
        }
    }

    private boolean isValidItem(int slot, ItemResource resource) {
        if (slot == FramingSawMenu.SLOT_INPUT) {
            return cache.getMaterialValue(resource.getItem()) > 0;
        } else if (slot < FramingSawMenu.SLOT_RESULT) {
            if (selectedRecipe != null) {
                int idx = slot - FramingSawMenu.SLOT_ADDITIVE_FIRST;
                List<FramingSawRecipeAdditive> additives = selectedRecipe.value().getAdditives();
                if (!additives.isEmpty() && idx < additives.size()) {
                    return additives.get(idx).ingredient().test(resource.toStack());
                }
                return false;
            }
            return true;
        } else if (slot == FramingSawMenu.SLOT_RESULT) {
            return internalAccess;
        }
        throw new IllegalArgumentException("Invalid slot: " + slot);
    }

    public void selectRecipe(@Nullable RecipeHolder<FramingSawRecipe> recipe) {
        ResourceKey<Recipe<?>> lastId = selectedRecipeId;
        selectedRecipe = recipe;
        selectedRecipeId = recipe == null ? null : recipe.id();
        checkRecipeSatisfied();
        if (!Objects.equals(lastId, selectedRecipeId)) {
            needSaving = true;
        }
    }

    public @Nullable RecipeHolder<FramingSawRecipe> getSelectedRecipe() {
        return selectedRecipe;
    }

    public @Nullable FramingSawRecipeMatchResult getMatchResult() {
        return matchResult;
    }

    public int getProgress() {
        return progress;
    }

    public RecipeInputItemResourceHandler getItemHandler() {
        return itemHandler;
    }

    public ResourceHandler<ItemResource> getExternalItemHandler() {
        return externalItemHandler;
    }

    public EnergyHandler getEnergyStorage() {
        return energyStorage;
    }

    public int getEnergy() {
        return energyStorage.getAmountAsInt();
    }

    public int getEnergyCapacity() {
        return energyStorage.getCapacity();
    }

    public int getCraftingDuration() {
        return craftingDuration;
    }

    public boolean isInputEmpty() {
        for (int i = 0; i < FramingSawMenu.SLOT_RESULT; i++) {
            if (!itemHandler.getResource(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onLoad() {
        super.onLoad();
        cache = FramingSawRecipeCache.get(level().isClientSide());
        if (level() instanceof ServerLevel serverLevel && selectedRecipeId != null) {
            RecipeHolder<FramingSawRecipe> recipe = (RecipeHolder<FramingSawRecipe>) serverLevel.recipeAccess()
                    .byKey(selectedRecipeId)
                    .filter(h -> h.value() instanceof FramingSawRecipe)
                    .orElse(null);
            selectRecipe(recipe);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level != null) {
            inhibitUpdate = true;
            Utils.dropItemResourceHandlerContents(level, pos, itemHandler);
            inhibitUpdate = false;
        }
    }

    private Level level() {
        return Objects.requireNonNull(level, "BlockEntity#level accessed before it was set");
    }

    public boolean isUsableByPlayer(Player player) {
        if (level().getBlockEntity(worldPosition) != this) {
            return false;
        }
        return !(player.distanceToSqr((double) worldPosition.getX() + 0.5D, (double) worldPosition.getY() + 0.5D, (double) worldPosition.getZ() + 0.5D) > 64.0D);
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        if (selectedRecipe != null) {
            valueOutput.putString("recipe", selectedRecipe.id().toString());
        }
        itemHandler.serialize(valueOutput.child("inventory"));
        energyStorage.serialize(valueOutput.child("energy"));
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        Optional<String> optRecipe = valueInput.getString("recipe");
        if (optRecipe.isPresent()) {
            Identifier recipe = Identifier.tryParse(optRecipe.get());
            selectedRecipeId = recipe != null ? ResourceKey.create(Registries.RECIPE, recipe) : null;
        }
        itemHandler.deserialize(valueInput.childOrEmpty("inventory"));
        energyStorage.deserialize(valueInput.childOrEmpty("energy"));
    }
}
