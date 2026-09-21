package io.github.xfacthd.framedblocks.common.compat.jei;

import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCalculation;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import io.github.xfacthd.framedblocks.common.menu.IFramingSawMenu;
import io.github.xfacthd.framedblocks.common.menu.PoweredFramingSawMenu;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundSelectFramingSawRecipePayload;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferContext;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Adapts the recipe shown by JEI to the materials the player actually has.
 * The framing saw accepts many materials for one recipe, but JEI's basic transfer handler only sees the material
 * in the displayed recipe. Each material can also change the required material and additive counts, so this handler
 * groups available materials by material value and gives the basic handler one recalculated layout per value.
 */
public abstract sealed class FramingSawTransferHandler<C extends AbstractContainerMenu & IFramingSawMenu> implements IRecipeTransferHandler<C, FramingSawRecipe> {
    private final IRecipeTransferHandlerHelper transferHelper;
    private final IRecipeTransferInfo<C, FramingSawRecipe> transferInfo;
    private final IRecipeTransferHandler<C, FramingSawRecipe> basicHandler;

    private FramingSawTransferHandler(IRecipeTransferHandlerHelper transferHelper, Class<? extends C> menuClass, MenuType<C> menuType) {
        this.transferHelper = transferHelper;
        this.transferInfo = transferHelper.createBasicRecipeTransferInfo(
                menuClass,
                menuType,
                FramedJeiPlugin.FRAMING_SAW_RECIPE_TYPE,
                FramingSawMenu.SLOT_INPUT,
                FramingSawMenu.SLOT_RESULT,
                FramingSawMenu.SLOT_INV_FIRST,
                FramingSawMenu.INV_SLOT_COUNT
        );
        this.basicHandler = transferHelper.createUnregisteredRecipeTransferHandler(transferInfo);
    }

    @Override
    public Class<? extends C> getContainerClass() {
        return transferInfo.getContainerClass();
    }

    @Override
    public Optional<MenuType<C>> getMenuType() {
        return transferInfo.getMenuType();
    }

    @Override
    public IRecipeType<FramingSawRecipe> getRecipeType() {
        return transferInfo.getRecipeType();
    }

    @Override
    @SuppressWarnings("removal")
    public @Nullable IRecipeTransferError transferRecipe(C menu, FramingSawRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        return null;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(IRecipeTransferContext<FramingSawRecipe, C> context, boolean doTransfer) {
        int idx = FramingSawRecipeCache.get(true).getRecipeIndex(context.getRecipe());
        C menu = context.getContainer();
        if (idx == -1 || !menu.isValidRecipeIndex(idx)) {
            return transferHelper.createUserErrorWithTooltip(JeiCompat.MSG_INVALID_RECIPE);
        }

        List<IRecipeSlotsView> inputAlternatives = getRecipeInputAlternatives(context);
        IRecipeTransferError error = transferHelper.transferRecipeWithInputAlternatives(
                basicHandler,
                context,
                inputAlternatives,
                doTransfer
        );
        if (error != null) {
            return error;
        }

        if (doTransfer && menu.clickMenuButton(context.getPlayer(), idx)) {
            ClientPacketDistributor.sendToServer(new ServerboundSelectFramingSawRecipePayload(menu.containerId, idx));
        }
        return null;
    }

    private List<IRecipeSlotsView> getRecipeInputAlternatives(IRecipeTransferContext<FramingSawRecipe, C> context) {
        IRecipeSlotsView recipeSlots = context.getRecipeSlots();
        return findMaterialCandidates(context).stream()
                .map(candidate -> createRecipeInputAlternative(recipeSlots, candidate))
                .toList();
    }

    private IRecipeSlotsView createRecipeInputAlternative(
            IRecipeSlotsView recipeSlots,
            MaterialCandidate candidate
    ) {
        IRecipeSlotView materialSlot = recipeSlots.findSlotByName(FramingSawRecipeCategory.MATERIAL_SLOT_NAME).orElseThrow();
        List<IRecipeSlotView> candidateSlots = new ArrayList<>(recipeSlots.getSlotViews().size());
        int additiveIndex = 0;
        for (IRecipeSlotView slot : recipeSlots.getSlotViews()) {
            if (slot == materialSlot) {
                candidateSlots.add(transferHelper.copyWithIngredients(slot, candidate.materials()));
            } else if (slot.getRole() == RecipeIngredientRole.INPUT) {
                int count = candidate.calculation().getAdditiveCount(additiveIndex++);
                candidateSlots.add(copyWithCount(slot, count));
            } else {
                candidateSlots.add(slot);
            }
        }
        return transferHelper.createRecipeSlotsView(candidateSlots);
    }

    private IRecipeSlotView copyWithCount(IRecipeSlotView slot, int count) {
        List<ItemStack> ingredients = slot.getItemStacks()
                .map(stack -> copyWithCount(stack, count))
                .toList();
        return ingredients.isEmpty() ? slot : transferHelper.copyWithIngredients(slot, ingredients);
    }

    private List<MaterialCandidate> findMaterialCandidates(IRecipeTransferContext<FramingSawRecipe, C> context) {
        C menu = context.getContainer();
        FramingSawRecipe recipe = context.getRecipe();
        FramingSawRecipeCache cache = FramingSawRecipeCache.get(true);
        Map<Integer, List<ItemStack>> materialsByValue = new LinkedHashMap<>();
        Stream.concat(
                transferInfo.getRecipeSlots(menu, recipe).stream(),
                transferInfo.getInventorySlots(menu, recipe).stream()
                )
                .map(Slot::getItem)
                .filter(stack -> isMaterial(stack, cache))
                .forEach(stack -> materialsByValue
                        .computeIfAbsent(cache.getMaterialValue(stack.getItem()), ignored -> new ArrayList<>())
                        .add(stack));

        return materialsByValue.values().stream()
                .flatMap(materials -> MaterialCandidate.create(recipe, materials).stream())
                .toList();
    }

    private static boolean isMaterial(ItemStack stack, FramingSawRecipeCache cache) {
        return !stack.isEmpty() &&
               cache.getMaterialValue(stack.getItem()) > 0 &&
               stack.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY).isEmptyOrContentsEmpty();
    }

    private static ItemStack copyWithCount(ItemStack stack, int count) {
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }

    private record MaterialCandidate(List<ItemStack> materials, FramingSawRecipeCalculation calculation) {
        private static Optional<MaterialCandidate> create(FramingSawRecipe recipe, List<ItemStack> availableMaterials) {
            FramingSawRecipeCalculation calculation = recipe.makeCraftingCalculation(
                    new SingleRecipeInput(availableMaterials.getFirst()),
                    true
            );
            if (calculation.getOutputCount() > recipe.getResult().getMaxStackSize()) {
                return Optional.empty();
            }

            int inputCount = calculation.getInputCount();
            List<ItemStack> materials = availableMaterials.stream()
                    .filter(stack -> inputCount <= stack.getMaxStackSize())
                    .map(stack -> copyWithCount(stack, inputCount))
                    .toList();
            return materials.isEmpty() ? Optional.empty() : Optional.of(new MaterialCandidate(materials, calculation));
        }
    }

    public static final class FramingSaw extends FramingSawTransferHandler<FramingSawMenu> {
        public FramingSaw(IRecipeTransferHandlerHelper transferHelper) {
            super(transferHelper, FramingSawMenu.class, FBContent.MENU_TYPE_FRAMING_SAW.get());
        }
    }

    public static final class PoweredFramingSaw extends FramingSawTransferHandler<PoweredFramingSawMenu> {
        public PoweredFramingSaw(IRecipeTransferHandlerHelper transferHelper) {
            super(transferHelper, PoweredFramingSawMenu.class, FBContent.MENU_TYPE_POWERED_FRAMING_SAW.get());
        }
    }
}
