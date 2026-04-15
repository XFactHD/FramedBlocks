package io.github.xfacthd.framedblocks.common;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainerFactory;
import io.github.xfacthd.framedblocks.api.component.FrameConfig;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.datagen.loot.objects.RetainCamoLootCondition;
import io.github.xfacthd.framedblocks.api.datagen.loot.objects.SplitCamoLootFunction;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.registration.*;
import io.github.xfacthd.framedblocks.common.block.cube.*;
import io.github.xfacthd.framedblocks.common.block.door.*;
import io.github.xfacthd.framedblocks.common.block.interactive.*;
import io.github.xfacthd.framedblocks.common.block.interactive.button.*;
import io.github.xfacthd.framedblocks.common.block.interactive.pressureplate.*;
import io.github.xfacthd.framedblocks.common.block.pane.*;
import io.github.xfacthd.framedblocks.common.block.pillar.*;
import io.github.xfacthd.framedblocks.common.block.prism.*;
import io.github.xfacthd.framedblocks.common.block.rail.fancy.*;
import io.github.xfacthd.framedblocks.common.block.rail.vanillaslope.*;
import io.github.xfacthd.framedblocks.common.block.sign.*;
import io.github.xfacthd.framedblocks.common.block.slab.*;
import io.github.xfacthd.framedblocks.common.block.slope.*;
import io.github.xfacthd.framedblocks.common.block.slopeedge.*;
import io.github.xfacthd.framedblocks.common.block.slopepanel.*;
import io.github.xfacthd.framedblocks.common.block.slopepanelcorner.*;
import io.github.xfacthd.framedblocks.common.block.slopeslab.*;
import io.github.xfacthd.framedblocks.common.block.special.*;
import io.github.xfacthd.framedblocks.common.block.stairs.standard.*;
import io.github.xfacthd.framedblocks.common.block.stairs.vertical.*;
import io.github.xfacthd.framedblocks.common.block.torch.*;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.prism.*;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.rail.*;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slab.*;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slope.*;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopeedge.*;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanel.*;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner.*;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopeslab.*;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.stairs.*;
import io.github.xfacthd.framedblocks.common.blockentity.special.*;
import io.github.xfacthd.framedblocks.common.compat.jei.camo.JeiCamoApplicationDummyIngredient;
import io.github.xfacthd.framedblocks.common.compat.jei.camo.JeiCamoApplicationRecipe;
import io.github.xfacthd.framedblocks.common.crafting.camo.CamoApplicationRecipe;
import io.github.xfacthd.framedblocks.common.crafting.rotation.ShapeRotationRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeDisplay;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.FramedRegistries;
import io.github.xfacthd.framedblocks.common.data.FramedToolType;
import io.github.xfacthd.framedblocks.common.data.camo.block.BlockCamoContainerFactory;
import io.github.xfacthd.framedblocks.common.data.camo.fluid.FluidCamoContainerFactory;
import io.github.xfacthd.framedblocks.common.data.component.*;
import io.github.xfacthd.framedblocks.common.data.loot.BoardAdditionalItemCountNumberProvider;
import io.github.xfacthd.framedblocks.common.data.loot.LayeredCubeAdditionalItemCountNumberProvider;
import io.github.xfacthd.framedblocks.common.item.FramedAxeItem;
import io.github.xfacthd.framedblocks.common.item.FramedBlueprintItem;
import io.github.xfacthd.framedblocks.common.item.PaintRollerItem;
import io.github.xfacthd.framedblocks.common.item.FramedToolItem;
import io.github.xfacthd.framedblocks.common.item.FramedWrenchItem;
import io.github.xfacthd.framedblocks.common.item.PhantomPasteItem;
import io.github.xfacthd.framedblocks.common.menu.FramedStorageMenu;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import io.github.xfacthd.framedblocks.common.menu.PaintRollerMenu;
import io.github.xfacthd.framedblocks.common.menu.PoweredFramingSawMenu;
import io.github.xfacthd.framedblocks.common.particle.BlockOverlayParticleOptions;
import io.github.xfacthd.framedblocks.common.particle.FluidParticleOptions;
import io.github.xfacthd.framedblocks.common.util.FramedCreativeTab;
import io.github.xfacthd.framedblocks.common.util.registration.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class FBContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FramedConstants.MOD_ID);
    private static final DeferredDataComponentTypeRegister DATA_COMPONENTS = DeferredDataComponentTypeRegister.create(FramedConstants.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FramedConstants.MOD_ID);
    private static final DeferredBlockEntityRegister BE_TYPES = DeferredBlockEntityRegister.create(FramedConstants.MOD_ID);
    private static final DeferredMenuTypeRegister MENU_TYPES = DeferredMenuTypeRegister.create(FramedConstants.MOD_ID);
    private static final DeferredRecipeTypeRegister RECIPE_TYPES = DeferredRecipeTypeRegister.create(FramedConstants.MOD_ID);
    private static final DeferredRecipeSerializerRegister RECIPE_SERIALIZERS = DeferredRecipeSerializerRegister.create(FramedConstants.MOD_ID);
    private static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = register(Registries.RECIPE_BOOK_CATEGORY);
    private static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAY_TYPES = register(Registries.RECIPE_DISPLAY);
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = register(NeoForgeRegistries.Keys.INGREDIENT_TYPES);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = register(Registries.CREATIVE_MODE_TAB);
    private static final DeferredParticleTypeRegister PARTICLE_TYPES = DeferredParticleTypeRegister.create(FramedConstants.MOD_ID);
    private static final DeferredMapCodecRegister<LootItemCondition> LOOT_CONDITIONS = mapCodecRegister(Registries.LOOT_CONDITION_TYPE);
    private static final DeferredMapCodecRegister<LootItemFunction> LOOT_FUNCTIONS = mapCodecRegister(Registries.LOOT_FUNCTION_TYPE);
    private static final DeferredMapCodecRegister<NumberProvider> LOOT_NUMBER_PROVIDERS = mapCodecRegister(Registries.LOOT_NUMBER_PROVIDER_TYPE);
    private static final DeferredRegister<CamoContainerFactory<?>> CAMO_CONTAINER_FACTORIES = register(FramedConstants.Registries.CAMO_CONTAINER_FACTORY_REGISTRY_KEY);

    private static final Map<BlockType, Holder<Block>> BLOCKS_BY_TYPE = new EnumMap<>(BlockType.class);
    private static final Map<FramedToolType, Holder<Item>> TOOLS_BY_TYPE = new EnumMap<>(FramedToolType.class);
    private static final Map<BlockType, DeferredBlockEntity<? extends IFramedBlockEntity>> BLOCK_ENTITIES_BY_TYPE = new EnumMap<>(BlockType.class);

    // region Blocks
    public static final Holder<Block> BLOCK_FRAMED_CUBE = registerBlock(FramedCubeBlock::new, BlockType.FRAMED_CUBE);
    public static final Holder<Block> BLOCK_FRAMED_SLOPE = registerBlock(FramedSlopeBlock::new, BlockType.FRAMED_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_SLOPE = registerBlock(FramedDoubleSlopeBlock::new, BlockType.FRAMED_DOUBLE_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_HALF_SLOPE = registerBlock(FramedHalfSlopeBlock::new, BlockType.FRAMED_HALF_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_HALF_SLOPE = registerBlock(FramedVerticalHalfSlopeBlock::new, BlockType.FRAMED_VERTICAL_HALF_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_DIVIDED_SLOPE = registerBlock(FramedDividedSlopeBlock::new, BlockType.FRAMED_DIVIDED_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_HALF_SLOPE = registerBlock(FramedDoubleHalfSlopeBlock::new, BlockType.FRAMED_DOUBLE_HALF_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE = registerBlock(FramedVerticalDoubleHalfSlopeBlock::new, BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_CORNER_SLOPE = registerBlock(FramedCornerSlopeBlock::new, BlockType.FRAMED_CORNER_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_INNER_CORNER_SLOPE = registerBlock(FramedCornerSlopeBlock::new, BlockType.FRAMED_INNER_CORNER_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_CORNER = registerBlock(FramedDoubleCornerBlock::new, BlockType.FRAMED_DOUBLE_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_PRISM_CORNER = registerBlock(FramedPrismCornerBlock::new, BlockType.FRAMED_PRISM_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_INNER_PRISM_CORNER = registerBlock(FramedPrismCornerBlock::new, BlockType.FRAMED_INNER_PRISM_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_PRISM_CORNER = registerBlock(FramedDoublePrismCornerBlock::new, BlockType.FRAMED_DOUBLE_PRISM_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_THREEWAY_CORNER = registerBlock(FramedThreewayCornerBlock::new, BlockType.FRAMED_THREEWAY_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_INNER_THREEWAY_CORNER = registerBlock(FramedThreewayCornerBlock::new, BlockType.FRAMED_INNER_THREEWAY_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER = registerBlock(FramedDoubleThreewayCornerBlock::new, BlockType.FRAMED_DOUBLE_THREEWAY_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_SLOPE_EDGE = registerBlock(FramedSlopeEdgeBlock::new, BlockType.FRAMED_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_SLOPE_EDGE = registerBlock(FramedElevatedSlopeEdgeBlock::new, BlockType.FRAMED_ELEVATED_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE = registerBlock(FramedElevatedDoubleSlopeEdgeBlock::new, BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_SLOPE_EDGE = registerBlock(FramedStackedSlopeEdgeBlock::new, BlockType.FRAMED_STACKED_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_CORNER_SLOPE_EDGE = registerBlock(FramedCornerSlopeEdgeBlock::new, BlockType.FRAMED_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_INNER_CORNER_SLOPE_EDGE = registerBlock(FramedCornerSlopeEdgeBlock::new, BlockType.FRAMED_INNER_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_CORNER_SLOPE_EDGE = registerBlock(FramedElevatedCornerSlopeEdgeBlock::new, BlockType.FRAMED_ELEVATED_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE = registerBlock(FramedElevatedCornerSlopeEdgeBlock::new, BlockType.FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_DOUBLE_CORNER_SLOPE_EDGE = registerBlock(FramedElevatedDoubleCornerSlopeEdgeBlock::new, BlockType.FRAMED_ELEV_DOUBLE_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_DOUBLE_INNER_CORNER_SLOPE_EDGE = registerBlock(FramedElevatedDoubleInnerCornerSlopeEdgeBlock::new, BlockType.FRAMED_ELEV_DOUBLE_INNER_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_CORNER_SLOPE_EDGE = registerBlock(FramedStackedCornerSlopeEdgeBlock::new, BlockType.FRAMED_STACKED_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE = registerBlock(FramedStackedInnerCornerSlopeEdgeBlock::new, BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_THREEWAY_CORNER_SLOPE_EDGE = registerBlock(FramedThreewayCornerSlopeEdgeBlock::new, BlockType.FRAMED_THREEWAY_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE = registerBlock(FramedThreewayCornerSlopeEdgeBlock::new, BlockType.FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_SLOPE_EDGE_SLAB = registerBlock(FramedSlopeEdgeSlabBlock::new, BlockType.FRAMED_SLOPE_EDGE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_SLOPE_EDGE_PANEL = registerBlock(FramedSlopeEdgePanelBlock::new, BlockType.FRAMED_SLOPE_EDGE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_SLAB = registerBlock(FramedSlabBlock::new, BlockType.FRAMED_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_SLAB = registerBlock(FramedDoubleSlabBlock::new, BlockType.FRAMED_DOUBLE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_ADJ_DOUBLE_SLAB = registerBlock(FramedAdjustableDoubleSlabBlock::standard, BlockType.FRAMED_ADJ_DOUBLE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_SLAB = registerBlock(FramedAdjustableDoubleSlabBlock::copycat, BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_DIVIDED_SLAB = registerBlock(FramedDividedSlabBlock::new, BlockType.FRAMED_DIVIDED_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_SLAB_EDGE = registerBlock(FramedSlabEdgeBlock::new, BlockType.FRAMED_SLAB_EDGE);
    public static final Holder<Block> BLOCK_FRAMED_SLAB_CORNER = registerBlock(FramedSlabCornerBlock::new, BlockType.FRAMED_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_PANEL = registerBlock(FramedPanelBlock::new, BlockType.FRAMED_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_PANEL = registerBlock(FramedDoublePanelBlock::new, BlockType.FRAMED_DOUBLE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_ADJ_DOUBLE_PANEL = registerBlock(FramedAdjustableDoublePanelBlock::standard, BlockType.FRAMED_ADJ_DOUBLE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_PANEL = registerBlock(FramedAdjustableDoublePanelBlock::copycat, BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_DIVIDED_PANEL_HOR = registerBlock(FramedDividedPanelBlock::new, BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL);
    public static final Holder<Block> BLOCK_FRAMED_DIVIDED_PANEL_VERT = registerBlock(FramedDividedPanelBlock::new, BlockType.FRAMED_DIVIDED_PANEL_VERTICAL);
    public static final Holder<Block> BLOCK_FRAMED_CORNER_PILLAR = registerBlock(FramedCornerPillarBlock::new, BlockType.FRAMED_CORNER_PILLAR);
    public static final Holder<Block> BLOCK_FRAMED_STAIRS = registerBlock(FramedStairsBlock::new, BlockType.FRAMED_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_STAIRS = registerBlock(FramedDoubleStairsBlock::new, BlockType.FRAMED_DOUBLE_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_HALF_STAIRS = registerBlock(FramedHalfStairsBlock::new, BlockType.FRAMED_HALF_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_DIVIDED_STAIRS = registerBlock(FramedDividedStairsBlock::new, BlockType.FRAMED_DIVIDED_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_HALF_STAIRS = registerBlock(FramedDoubleHalfStairsBlock::new, BlockType.FRAMED_DOUBLE_HALF_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_SLICED_STAIRS_SLAB = registerBlock(FramedSlicedStairsSlabBlock::new, BlockType.FRAMED_SLICED_STAIRS_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_SLICED_STAIRS_PANEL = registerBlock(FramedSlicedStairsPanelBlock::new, BlockType.FRAMED_SLICED_STAIRS_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_SLOPED_STAIRS = registerBlock(FramedSlopedStairsBlock::new, BlockType.FRAMED_SLOPED_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_SLOPED_DOUBLE_STAIRS = registerBlock(FramedSlopedDoubleStairsBlock::new, BlockType.FRAMED_SLOPED_DOUBLE_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLAB = registerBlock(FramedSlicedSlopedStairsSlabBlock::new, BlockType.FRAMED_SLICED_SLOPED_STAIRS_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLOPE = registerBlock(FramedSlicedSlopedStairsSlopeBlock::new, BlockType.FRAMED_SLICED_SLOPED_STAIRS_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_STAIRS = registerBlock(FramedVerticalStairsBlock::new, BlockType.FRAMED_VERTICAL_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS = registerBlock(FramedVerticalDoubleStairsBlock::new, BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_HALF_STAIRS = registerBlock(FramedVerticalHalfStairsBlock::new, BlockType.FRAMED_VERTICAL_HALF_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS = registerBlock(FramedVerticalDividedStairsBlock::new, BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS = registerBlock(FramedVerticalDoubleHalfStairsBlock::new, BlockType.FRAMED_VERTICAL_DOUBLE_HALF_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_SLICED_STAIRS = registerBlock(FramedVerticalSlicedStairsBlock::new, BlockType.FRAMED_VERTICAL_SLICED_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS = registerBlock(FramedVerticalSlopedStairsBlock::new, BlockType.FRAMED_VERTICAL_SLOPED_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS = registerBlock(FramedVerticalSlopedDoubleStairsBlock::new, BlockType.FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_PANEL = registerBlock(FramedVerticalSlicedSlopedStairsPanelBlock::new, BlockType.FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE = registerBlock(FramedVerticalSlicedSlopedStairsSlopeBlock::new, BlockType.FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_THREEWAY_CORNER_PILLAR = registerBlock(FramedThreewayCornerPillarBlock::new, BlockType.FRAMED_THREEWAY_CORNER_PILLAR);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR = registerBlock(FramedDoubleThreewayCornerPillarBlock::new, BlockType.FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR);
    public static final Holder<Block> BLOCK_FRAMED_WALL = registerBlock(FramedWallBlock::new, BlockType.FRAMED_WALL);
    public static final Holder<Block> BLOCK_FRAMED_FENCE = registerBlock(FramedFenceBlock::new, BlockType.FRAMED_FENCE);
    public static final Holder<Block> BLOCK_FRAMED_FENCE_GATE = registerBlock(FramedFenceGateBlock::new, BlockType.FRAMED_FENCE_GATE);
    public static final Holder<Block> BLOCK_FRAMED_DOOR = registerBlock(FramedDoorBlock::wood, BlockType.FRAMED_DOOR);
    public static final Holder<Block> BLOCK_FRAMED_IRON_DOOR = registerBlock(FramedDoorBlock::iron, BlockType.FRAMED_IRON_DOOR);
    public static final Holder<Block> BLOCK_FRAMED_TRAP_DOOR = registerBlock(FramedTrapDoorBlock::wood, BlockType.FRAMED_TRAPDOOR);
    public static final Holder<Block> BLOCK_FRAMED_IRON_TRAP_DOOR = registerBlock(FramedTrapDoorBlock::iron, BlockType.FRAMED_IRON_TRAPDOOR);
    public static final Holder<Block> BLOCK_FRAMED_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::wood, BlockType.FRAMED_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::woodWaterloggable, BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_STONE_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::stone, BlockType.FRAMED_STONE_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::stoneWaterloggable, BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::obsidian, BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::obsidianWaterloggable, BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_GOLD_PRESSURE_PLATE = registerBlock(FramedWeightedPressurePlateBlock::gold, BlockType.FRAMED_GOLD_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE = registerBlock(FramedWeightedPressurePlateBlock::goldWaterloggable, BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_IRON_PRESSURE_PLATE = registerBlock(FramedWeightedPressurePlateBlock::iron, BlockType.FRAMED_IRON_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE = registerBlock(FramedWeightedPressurePlateBlock::ironWaterloggable, BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE);
    public static final Holder<Block> BLOCK_FRAMED_LADDER = registerBlock(FramedLadderBlock::new, BlockType.FRAMED_LADDER);
    public static final Holder<Block> BLOCK_FRAMED_BUTTON = registerBlock(FramedButtonBlock::wood, BlockType.FRAMED_BUTTON);
    public static final Holder<Block> BLOCK_FRAMED_STONE_BUTTON = registerBlock(FramedButtonBlock::stone, BlockType.FRAMED_STONE_BUTTON);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_BUTTON = registerBlock(FramedLargeButtonBlock::largeWood, BlockType.FRAMED_LARGE_BUTTON);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_STONE_BUTTON = registerBlock(FramedLargeButtonBlock::largeStone, BlockType.FRAMED_LARGE_STONE_BUTTON);
    public static final Holder<Block> BLOCK_FRAMED_LEVER = registerBlock(FramedLeverBlock::new, BlockType.FRAMED_LEVER);
    public static final Holder<Block> BLOCK_FRAMED_SIGN = registerBlock(FramedStandingSignBlock::new, BlockType.FRAMED_SIGN);
    public static final Holder<Block> BLOCK_FRAMED_WALL_SIGN = registerBlock(FramedWallSignBlock::new, BlockType.FRAMED_WALL_SIGN);
    public static final Holder<Block> BLOCK_FRAMED_HANGING_SIGN = registerBlock(FramedCeilingHangingSignBlock::new, BlockType.FRAMED_HANGING_SIGN);
    public static final Holder<Block> BLOCK_FRAMED_WALL_HANGING_SIGN = registerBlock(FramedWallHangingSignBlock::new, BlockType.FRAMED_WALL_HANGING_SIGN);
    public static final Holder<Block> BLOCK_FRAMED_TORCH = registerBlock(FramedTorchBlock::normal, BlockType.FRAMED_TORCH);
    public static final Holder<Block> BLOCK_FRAMED_WALL_TORCH = registerBlock(FramedWallTorchBlock::normal, BlockType.FRAMED_WALL_TORCH);
    public static final Holder<Block> BLOCK_FRAMED_SOUL_TORCH = registerBlock(FramedTorchBlock::soul, BlockType.FRAMED_SOUL_TORCH);
    public static final Holder<Block> BLOCK_FRAMED_SOUL_WALL_TORCH = registerBlock(FramedWallTorchBlock::soul, BlockType.FRAMED_SOUL_WALL_TORCH);
    public static final Holder<Block> BLOCK_FRAMED_COPPER_TORCH = registerBlock(FramedTorchBlock::copper, BlockType.FRAMED_COPPER_TORCH);
    public static final Holder<Block> BLOCK_FRAMED_COPPER_WALL_TORCH = registerBlock(FramedWallTorchBlock::copper, BlockType.FRAMED_COPPER_WALL_TORCH);
    public static final Holder<Block> BLOCK_FRAMED_REDSTONE_TORCH = registerBlock(FramedRedstoneTorchBlock::new, BlockType.FRAMED_REDSTONE_TORCH);
    public static final Holder<Block> BLOCK_FRAMED_REDSTONE_WALL_TORCH = registerBlock(FramedRedstoneWallTorchBlock::new, BlockType.FRAMED_REDSTONE_WALL_TORCH);
    public static final Holder<Block> BLOCK_FRAMED_BOARD = registerBlock(FramedBoardBlock::new, BlockType.FRAMED_BOARD);
    public static final Holder<Block> BLOCK_FRAMED_HALF_BOARD = registerBlock(FramedPartialBoardBlock::new, BlockType.FRAMED_HALF_BOARD);
    public static final Holder<Block> BLOCK_FRAMED_DIVIDED_BOARD = registerBlock(FramedDividedBoardBlock::new, BlockType.FRAMED_DIVIDED_BOARD);
    public static final Holder<Block> BLOCK_FRAMED_CORNER_BOARD = registerBlock(FramedPartialBoardBlock::new, BlockType.FRAMED_CORNER_BOARD);
    public static final Holder<Block> BLOCK_FRAMED_INNER_CORNER_BOARD = registerBlock(FramedPartialBoardBlock::new, BlockType.FRAMED_INNER_CORNER_BOARD);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_CORNER_BOARD = registerBlock(FramedDoubleCornerBoardBlock::new, BlockType.FRAMED_DOUBLE_CORNER_BOARD);
    public static final Holder<Block> BLOCK_FRAMED_CORNER_STRIP = registerBlock(FramedCornerStripBlock::new, BlockType.FRAMED_CORNER_STRIP);
    public static final Holder<Block> BLOCK_FRAMED_LATTICE = registerBlock(FramedLatticeBlock::new, BlockType.FRAMED_LATTICE_BLOCK);
    public static final Holder<Block> BLOCK_FRAMED_THICK_LATTICE = registerBlock(FramedLatticeBlock::new, BlockType.FRAMED_THICK_LATTICE);
    public static final Holder<Block> BLOCK_FRAMED_CHEST = registerBlock(FramedChestBlock::new, BlockType.FRAMED_CHEST);
    public static final Holder<Block> BLOCK_FRAMED_SECRET_STORAGE = registerBlock(FramedStorageBlock::new, BlockType.FRAMED_SECRET_STORAGE);
    public static final Holder<Block> BLOCK_FRAMED_TANK = registerBlock(FramedTankBlock::new, BlockType.FRAMED_TANK);
    public static final Holder<Block> BLOCK_FRAMED_BARS = registerBlock(FramedPaneBlock::new, BlockType.FRAMED_BARS);
    public static final Holder<Block> BLOCK_FRAMED_PANE = registerBlock(FramedPaneBlock::new, BlockType.FRAMED_PANE);
    public static final Holder<Block> BLOCK_FRAMED_HORIZONTAL_PANE = registerBlock(FramedHorizontalPaneBlock::new, BlockType.FRAMED_HORIZONTAL_PANE);
    public static final Holder<Block> BLOCK_FRAMED_RAIL_SLOPE = registerBlock(FramedRailSlopeBlock::normal, BlockType.FRAMED_RAIL_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_POWERED_RAIL_SLOPE = registerBlock(FramedPoweredRailSlopeBlock::powered, BlockType.FRAMED_POWERED_RAIL_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_DETECTOR_RAIL_SLOPE = registerBlock(FramedDetectorRailSlopeBlock::normal, BlockType.FRAMED_DETECTOR_RAIL_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE = registerBlock(FramedPoweredRailSlopeBlock::activator, BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_FANCY_RAIL = registerBlock(FramedFancyRailBlock::new, BlockType.FRAMED_FANCY_RAIL);
    public static final Holder<Block> BLOCK_FRAMED_FANCY_POWERED_RAIL = registerBlock(FramedFancyPoweredRailBlock::powered, BlockType.FRAMED_FANCY_POWERED_RAIL);
    public static final Holder<Block> BLOCK_FRAMED_FANCY_DETECTOR_RAIL = registerBlock(FramedFancyDetectorRailBlock::new, BlockType.FRAMED_FANCY_DETECTOR_RAIL);
    public static final Holder<Block> BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL = registerBlock(FramedFancyPoweredRailBlock::activator, BlockType.FRAMED_FANCY_ACTIVATOR_RAIL);
    public static final Holder<Block> BLOCK_FRAMED_FANCY_RAIL_SLOPE = registerBlock(FramedRailSlopeBlock::fancy, BlockType.FRAMED_FANCY_RAIL_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE = registerBlock(FramedPoweredRailSlopeBlock::poweredFancy, BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE = registerBlock(FramedDetectorRailSlopeBlock::fancy, BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE = registerBlock(FramedPoweredRailSlopeBlock::activatorFancy, BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE);
    public static final Holder<Block> BLOCK_FRAMED_FLOWER_POT = registerBlock(FramedFlowerPotBlock::new, BlockType.FRAMED_FLOWER_POT);
    public static final Holder<Block> BLOCK_FRAMED_PILLAR = registerBlock(FramedPillarBlock::new, BlockType.FRAMED_PILLAR);
    public static final Holder<Block> BLOCK_FRAMED_HALF_PILLAR = registerBlock(FramedHalfPillarBlock::new, BlockType.FRAMED_HALF_PILLAR);
    public static final Holder<Block> BLOCK_FRAMED_PILLAR_SOCKET = registerBlock(FramedPillarSocketBlock::new, BlockType.FRAMED_PILLAR_SOCKET);
    public static final Holder<Block> BLOCK_FRAMED_SPLIT_PILLAR_SOCKET = registerBlock(FramedSplitPillarSocketBlock::new, BlockType.FRAMED_SPLIT_PILLAR_SOCKET);
    public static final Holder<Block> BLOCK_FRAMED_POST = registerBlock(FramedPillarBlock::new, BlockType.FRAMED_POST);
    public static final Holder<Block> BLOCK_FRAMED_COLLAPSIBLE_BLOCK = registerBlock(FramedCollapsibleBlock::new, BlockType.FRAMED_COLLAPSIBLE_BLOCK);
    public static final Holder<Block> BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK = registerBlock(FramedCollapsibleCopycatBlock::new, BlockType.FRAMED_COLLAPSIBLE_COPYCAT_BLOCK);
    public static final Holder<Block> BLOCK_FRAMED_BOUNCY_CUBE = registerBlock(FramedBouncyCubeBlock::new, BlockType.FRAMED_BOUNCY_CUBE);
    public static final Holder<Block> BLOCK_FRAMED_REDSTONE_BLOCK = registerBlock(FramedRedstoneBlock::new, BlockType.FRAMED_REDSTONE_BLOCK);
    public static final Holder<Block> BLOCK_FRAMED_PRISM = registerBlock(FramedPrismBlock::new, BlockType.FRAMED_PRISM);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_INNER_PRISM = registerBlock(FramedElevatedPrismBlock::new, BlockType.FRAMED_ELEVATED_INNER_PRISM);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_PRISM = registerBlock(FramedElevatedDoublePrismBlock::new, BlockType.FRAMED_ELEVATED_INNER_DOUBLE_PRISM);
    public static final Holder<Block> BLOCK_FRAMED_SLOPED_PRISM = registerBlock(FramedSlopedPrismBlock::new, BlockType.FRAMED_SLOPED_PRISM);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_INNER_SLOPED_PRISM = registerBlock(FramedElevatedSlopedPrismBlock::new, BlockType.FRAMED_ELEVATED_INNER_SLOPED_PRISM);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM = registerBlock(FramedElevatedDoubleSlopedPrismBlock::new, BlockType.FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM);
    public static final Holder<Block> BLOCK_FRAMED_SLOPE_SLAB = registerBlock(FramedSlopeSlabBlock::new, BlockType.FRAMED_SLOPE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_SLOPE_SLAB = registerBlock(FramedElevatedSlopeSlabBlock::new, BlockType.FRAMED_ELEVATED_SLOPE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_COMPOUND_SLOPE_SLAB = registerBlock(FramedCompoundSlopeSlabBlock::new, BlockType.FRAMED_COMPOUND_SLOPE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_SLOPE_SLAB = registerBlock(FramedDoubleSlopeSlabBlock::new, BlockType.FRAMED_DOUBLE_SLOPE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB = registerBlock(FramedInverseDoubleSlopeSlabBlock::new, BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB = registerBlock(FramedElevatedDoubleSlopeSlabBlock::new, BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_SLOPE_SLAB = registerBlock(FramedStackedSlopeSlabBlock::new, BlockType.FRAMED_STACKED_SLOPE_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER = registerBlock(FramedFlatSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER = registerBlock(FramedFlatSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER = registerBlock(FramedFlatElevatedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER = registerBlock(FramedFlatElevatedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER = registerBlock(FramedFlatDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER = registerBlock(FramedFlatInverseDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER = registerBlock(FramedFlatElevatedDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER = registerBlock(FramedFlatElevatedDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER = registerBlock(FramedFlatStackedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER = registerBlock(FramedFlatStackedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_SLOPE_PANEL = registerBlock(FramedSlopePanelBlock::new, BlockType.FRAMED_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_SLOPE_PANEL = registerBlock(FramedExtendedSlopePanelBlock::new, BlockType.FRAMED_EXTENDED_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_COMPOUND_SLOPE_PANEL = registerBlock(FramedCompoundSlopePanelBlock::new, BlockType.FRAMED_COMPOUND_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_DOUBLE_SLOPE_PANEL = registerBlock(FramedDoubleSlopePanelBlock::new, BlockType.FRAMED_DOUBLE_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL = registerBlock(FramedInverseDoubleSlopePanelBlock::new, BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL = registerBlock(FramedExtendedDoubleSlopePanelBlock::new, BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_SLOPE_PANEL = registerBlock(FramedStackedSlopePanelBlock::new, BlockType.FRAMED_STACKED_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER = registerBlock(FramedFlatSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER = registerBlock(FramedFlatSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER = registerBlock(FramedFlatExtendedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER = registerBlock(FramedFlatExtendedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER = registerBlock(FramedFlatDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER = registerBlock(FramedFlatInverseDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER = registerBlock(FramedFlatExtendedDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER = registerBlock(FramedFlatExtendedDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER = registerBlock(FramedFlatStackedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER = registerBlock(FramedFlatStackedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL = registerBlock(FramedCornerSlopePanelBlock::new, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedCornerSlopePanelWallBlock::new, BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL = registerBlock(FramedCornerSlopePanelBlock::new, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedCornerSlopePanelWallBlock::new, BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL = registerBlock(FramedCornerSlopePanelBlock::new, BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedCornerSlopePanelWallBlock::new, BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL = registerBlock(FramedCornerSlopePanelBlock::new, BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedCornerSlopePanelWallBlock::new, BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL = registerBlock(FramedExtendedCornerSlopePanelBlock::new, BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedExtendedCornerSlopePanelWallBlock::new, BlockType.FRAMED_EXT_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL = registerBlock(FramedExtendedCornerSlopePanelBlock::new, BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedExtendedCornerSlopePanelWallBlock::new, BlockType.FRAMED_EXT_INNER_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL = registerBlock(FramedDoubleCornerSlopePanelBlock::new, BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedDoubleCornerSlopePanelWallBlock::new, BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL = registerBlock(FramedDoubleCornerSlopePanelBlock::new, BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedDoubleCornerSlopePanelWallBlock::new, BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL = registerBlock(FramedInverseDoubleCornerSlopePanelBlock::new, BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedInverseDoubleCornerSlopePanelWallBlock::new, BlockType.FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL = registerBlock(FramedExtendedDoubleCornerSlopePanelBlock::new, BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedExtendedDoubleCornerSlopePanelWallBlock::new, BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL = registerBlock(FramedExtendedDoubleCornerSlopePanelBlock::new, BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedExtendedDoubleCornerSlopePanelWallBlock::new, BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL = registerBlock(FramedStackedCornerSlopePanelBlock::new, BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedStackedCornerSlopePanelWallBlock::new, BlockType.FRAMED_STACKED_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL = registerBlock(FramedStackedCornerSlopePanelBlock::new, BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_WALL = registerBlock(FramedStackedCornerSlopePanelWallBlock::new, BlockType.FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER = registerBlock(FramedPrismSlopePanelCornerBlock::new, BlockType.FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER_WALL = registerBlock(FramedPrismSlopePanelCornerWallBlock::new, BlockType.FRAMED_SMALL_PRISM_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER = registerBlock(FramedPrismSlopePanelCornerBlock::new, BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER_WALL = registerBlock(FramedPrismSlopePanelCornerWallBlock::new, BlockType.FRAMED_LARGE_PRISM_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER = registerBlock(FramedPrismSlopePanelCornerBlock::new, BlockType.FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER_WALL = registerBlock(FramedPrismSlopePanelCornerWallBlock::new, BlockType.FRAMED_SMALL_INNER_PRISM_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER = registerBlock(FramedPrismSlopePanelCornerBlock::new, BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER_WALL = registerBlock(FramedPrismSlopePanelCornerWallBlock::new, BlockType.FRAMED_LARGE_INNER_PRISM_CORNER_SLOPE_PANEL_W);
    public static final Holder<Block> BLOCK_FRAMED_PYRAMID = registerBlock(FramedConnectingPyramidBlock::new, BlockType.FRAMED_PYRAMID);
    public static final Holder<Block> BLOCK_FRAMED_PYRAMID_SLAB = registerBlock(FramedPyramidBlock::new, BlockType.FRAMED_PYRAMID_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_ELEVATED_PYRAMID_SLAB = registerBlock(FramedConnectingPyramidBlock::new, BlockType.FRAMED_ELEVATED_PYRAMID_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_UPPER_PYRAMID_SLAB = registerBlock(FramedConnectingPyramidBlock::new, BlockType.FRAMED_UPPER_PYRAMID_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_STACKED_PYRAMID_SLAB = registerBlock(FramedStackedPyramidSlabBlock::new, BlockType.FRAMED_STACKED_PYRAMID_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_TARGET = registerBlock(FramedTargetBlock::new, BlockType.FRAMED_TARGET);
    public static final Holder<Block> BLOCK_FRAMED_GATE = registerBlock(FramedGateBlock::wood, BlockType.FRAMED_GATE);
    public static final Holder<Block> BLOCK_FRAMED_IRON_GATE = registerBlock(FramedGateBlock::iron, BlockType.FRAMED_IRON_GATE);
    public static final Holder<Block> BLOCK_FRAMED_ITEM_FRAME = registerBlock(FramedItemFrameBlock::new, BlockType.FRAMED_ITEM_FRAME);
    public static final Holder<Block> BLOCK_FRAMED_GLOWING_ITEM_FRAME = registerBlock(FramedItemFrameBlock::new, BlockType.FRAMED_GLOWING_ITEM_FRAME);
    public static final Holder<Block> BLOCK_FRAMED_MINI_CUBE = registerBlock(FramedMiniCubeBlock::new, BlockType.FRAMED_MINI_CUBE);
    public static final Holder<Block> BLOCK_FRAMED_ONE_WAY_WINDOW = registerBlock(FramedOneWayWindowBlock::new, BlockType.FRAMED_ONE_WAY_WINDOW);
    public static final Holder<Block> BLOCK_FRAMED_BOOKSHELF = registerBlock(FramedBookshelfBlock::new, BlockType.FRAMED_BOOKSHELF);
    public static final Holder<Block> BLOCK_FRAMED_CHISELED_BOOKSHELF = registerBlock(FramedChiseledBookshelfBlock::new, BlockType.FRAMED_CHISELED_BOOKSHELF);
    public static final Holder<Block> BLOCK_FRAMED_CENTERED_SLAB = registerBlock(FramedCenteredSlabBlock::new, BlockType.FRAMED_CENTERED_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_CENTERED_PANEL = registerBlock(FramedCenteredPanelBlock::new, BlockType.FRAMED_CENTERED_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_MASONRY_CORNER_SEGMENT = registerBlock(FramedMasonryCornerSegmentBlock::new, BlockType.FRAMED_MASONRY_CORNER_SEGMENT);
    public static final Holder<Block> BLOCK_FRAMED_MASONRY_CORNER = registerBlock(FramedMasonryCornerBlock::new, BlockType.FRAMED_MASONRY_CORNER);
    public static final Holder<Block> BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT = registerBlock(FramedCheckeredCubeSegmentBlock::new, BlockType.FRAMED_CHECKERED_CUBE_SEGMENT);
    public static final Holder<Block> BLOCK_FRAMED_CHECKERED_CUBE = registerBlock(FramedCheckeredCubeBlock::new, BlockType.FRAMED_CHECKERED_CUBE);
    public static final Holder<Block> BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT = registerBlock(FramedCheckeredSlabSegmentBlock::new, BlockType.FRAMED_CHECKERED_SLAB_SEGMENT);
    public static final Holder<Block> BLOCK_FRAMED_CHECKERED_SLAB = registerBlock(FramedCheckeredSlabBlock::new, BlockType.FRAMED_CHECKERED_SLAB);
    public static final Holder<Block> BLOCK_FRAMED_CHECKERED_PANEL_SEGMENT = registerBlock(FramedCheckeredPanelSegmentBlock::new, BlockType.FRAMED_CHECKERED_PANEL_SEGMENT);
    public static final Holder<Block> BLOCK_FRAMED_CHECKERED_PANEL = registerBlock(FramedCheckeredPanelBlock::new, BlockType.FRAMED_CHECKERED_PANEL);
    public static final Holder<Block> BLOCK_FRAMED_TUBE = registerBlock(FramedTubeBlock::new, BlockType.FRAMED_TUBE);
    public static final Holder<Block> BLOCK_FRAMED_CORNER_TUBE = registerBlock(FramedCornerTubeBlock::new, BlockType.FRAMED_CORNER_TUBE);
    public static final Holder<Block> BLOCK_FRAMED_CHAIN = registerBlock(FramedChainBlock::new, BlockType.FRAMED_CHAIN);
    public static final Holder<Block> BLOCK_FRAMED_LANTERN = registerBlock(FramedLanternBlock::new, BlockType.FRAMED_LANTERN);
    public static final Holder<Block> BLOCK_FRAMED_SOUL_LANTERN = registerBlock(FramedLanternBlock::new, BlockType.FRAMED_SOUL_LANTERN);
    public static final Holder<Block> BLOCK_FRAMED_COPPER_LANTERN = registerBlock(FramedLanternBlock::new, BlockType.FRAMED_COPPER_LANTERN);
    public static final Holder<Block> BLOCK_FRAMED_HOPPER = registerBlock(FramedHopperBlock::new, BlockType.FRAMED_HOPPER);
    public static final Holder<Block> BLOCK_FRAMED_LAYERED_CUBE = registerBlock(FramedLayeredCubeBlock::new, BlockType.FRAMED_LAYERED_CUBE);
    public static final Holder<Block> BLOCK_FRAMED_LIGHTNING_ROD = registerBlock(FramedLightningRodBlock::new, BlockType.FRAMED_LIGHTNING_ROD);
    public static final Holder<Block> BLOCK_FRAMED_PATH = registerBlock(FramedPathBlock::new, BlockType.FRAMED_PATH);
    public static final Holder<Block> BLOCK_FRAMED_SHELF = registerBlock(FramedShelfBlock::new, BlockType.FRAMED_SHELF);
    // endregion

    // region Special Blocks
    public static final Holder<Block> BLOCK_FRAMING_SAW = registerBlock("framing_saw", FramingSawBlock::new);
    public static final Holder<Block> BLOCK_POWERED_FRAMING_SAW = registerBlock("powered_framing_saw", PoweredFramingSawBlock::new);
    // endregion

    // region DataComponentTypes
    public static final DeferredDataComponentType<CamoList> DC_TYPE_CAMO_LIST = DATA_COMPONENTS.registerCachedComponentType(
            "camo_list", CamoList.CODEC, CamoList.STREAM_CODEC
    );
    public static final DeferredDataComponentType<FrameConfig> DC_TYPE_FRAME_CONFIG = DATA_COMPONENTS.registerSimpleComponentType(
            "frame_config", FrameConfig.CODEC, FrameConfig.STREAM_CODEC
    );
    public static final DeferredDataComponentType<BlueprintData> DC_TYPE_BLUEPRINT_DATA = DATA_COMPONENTS.registerCachedComponentType(
            "blueprint_data", BlueprintData.CODEC, BlueprintData.STREAM_CODEC
    );
    public static final DeferredDataComponentType<FramedMap> DC_TYPE_FRAMED_MAP = DATA_COMPONENTS.registerSimpleComponentType(
            "framed_map", FramedMap.CODEC, FramedMap.STREAM_CODEC
    );
    public static final DeferredDataComponentType<CollapsibleBlockData> DC_TYPE_COLLAPSIBLE_BLOCK_DATA = DATA_COMPONENTS.registerSimpleComponentType(
            "collapsible_block", CollapsibleBlockData.CODEC, CollapsibleBlockData.STREAM_CODEC
    );
    public static final DeferredDataComponentType<CollapsibleCopycatBlockData> DC_TYPE_COLLAPSIBLE_COPYCAT_BLOCK_DATA = DATA_COMPONENTS.registerSimpleComponentType(
            "collapsible_copycat_block", CollapsibleCopycatBlockData.CODEC, CollapsibleCopycatBlockData.STREAM_CODEC
    );
    public static final DeferredDataComponentType<PottedFlower> DC_TYPE_POTTED_FLOWER = DATA_COMPONENTS.registerSimpleComponentType(
            "potted_flower", PottedFlower.CODEC, PottedFlower.STREAM_CODEC
    );
    public static final DeferredDataComponentType<DyeColor> DC_TYPE_TARGET_COLOR = DATA_COMPONENTS.registerSimpleComponentType(
            "target_color", DyeColor.CODEC, DyeColor.STREAM_CODEC
    );
    public static final DeferredDataComponentType<AdjustableDoubleBlockData> DC_TYPE_ADJ_DOUBLE_BLOCK_DATA = DATA_COMPONENTS.registerSimpleComponentType(
            "adjustable_double_block", AdjustableDoubleBlockData.CODEC, AdjustableDoubleBlockData.STREAM_CODEC
    );
    public static final DeferredDataComponentType<SimpleFluidContent> DC_TYPE_TANK_CONTENTS = DATA_COMPONENTS.registerSimpleComponentType(
            "tank_contents", SimpleFluidContent.CODEC, SimpleFluidContent.STREAM_CODEC
    );
    public static final DeferredDataComponentType<WrenchRotationMode> DC_TYPE_WRENCH_MODE = DATA_COMPONENTS.registerSimpleComponentType(
            "wrench_mode", WrenchRotationMode.CODEC, WrenchRotationMode.STREAM_CODEC
    );
    public static final DeferredDataComponentType<Unit> DC_TYPE_RETAIN_CAMO = DATA_COMPONENTS.registerSimpleComponentType(
            "retain_camo", Unit.CODEC, Unit.STREAM_CODEC
    );
    public static final DeferredDataComponentType<Holder<BlockOverlay>> DC_TYPE_BLOCK_OVERLAY = DATA_COMPONENTS.registerSimpleComponentType(
            "block_overlay", BlockOverlay.CODEC, BlockOverlay.STREAM_CODEC
    );
    public static final DeferredDataComponentType<PaintRollerContents> DC_TYPE_PAINT_ROLLER_CONTENTS = DATA_COMPONENTS.registerComponentType(
            "paint_roller_contents",
            builder -> builder.persistent(PaintRollerContents.CODEC).networkSynchronized(PaintRollerContents.STREAM_CODEC).ignoreSwapAnimation()
    );
    // endregion

    // region Items
    public static final Holder<Item> ITEM_FRAMED_HAMMER = registerToolItem(FramedToolItem::new, FramedToolType.HAMMER);
    public static final Holder<Item> ITEM_FRAMED_WRENCH = registerToolItem(FramedWrenchItem::new, FramedToolType.WRENCH);
    public static final Holder<Item> ITEM_FRAMED_BLUEPRINT = registerToolItem(FramedBlueprintItem::new, FramedToolType.BLUEPRINT);
    public static final Holder<Item> ITEM_FRAMED_KEY = registerToolItem(FramedToolItem::new, FramedToolType.KEY);
    public static final Holder<Item> ITEM_FRAMED_SCREWDRIVER = registerToolItem(FramedToolItem::new, FramedToolType.SCREWDRIVER);
    public static final Holder<Item> ITEM_FRAMED_AXE = registerToolItem(FramedAxeItem::new, FramedToolType.AXE);
    public static final Holder<Item> ITEM_PAINT_ROLLER = registerToolItem(PaintRollerItem::new, FramedToolType.PAINT_ROLLER);
    public static final Holder<Item> ITEM_FRAMED_REINFORCEMENT = ITEMS.registerSimpleItem("framed_reinforcement");
    public static final Holder<Item> ITEM_PHANTOM_PASTE = ITEMS.registerItem("phantom_paste", PhantomPasteItem::new);
    public static final Holder<Item> ITEM_GLOW_PASTE = ITEMS.registerSimpleItem("glow_paste");
    // endregion

    // region BlockEntityTypes
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_BLOCK = registerBlockEntity(
            "framed_tile",
            getDefaultBlockEntityFactory(),
            false,
            getDefaultEntityBlockTypes(false)
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_DOUBLE_BLOCK = registerBlockEntity(
            "framed_double_tile",
            getDefaultDoubleBlockEntityFactory(),
            false,
            getDefaultEntityBlockTypes(true)
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_DOUBLE_FRAMED_SLOPE = registerBlockEntity(
            FramedDoubleSlopeBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPE
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_DOUBLE_HALF_SLOPE = registerBlockEntity(
            FramedDoubleHalfSlopeBlockEntity::new,
            BlockType.FRAMED_DOUBLE_HALF_SLOPE
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE = registerBlockEntity(
            FramedVerticalDoubleHalfSlopeBlockEntity::new,
            BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_DOUBLE_FRAMED_CORNER = registerBlockEntity(
            FramedDoubleCornerBlockEntity::new,
            BlockType.FRAMED_DOUBLE_CORNER
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_DOUBLE_FRAMED_THREEWAY_CORNER = registerBlockEntity(
            FramedDoubleThreewayCornerBlockEntity::new,
            BlockType.FRAMED_DOUBLE_THREEWAY_CORNER, BlockType.FRAMED_DOUBLE_PRISM_CORNER
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE = registerBlockEntity(
            FramedElevatedDoubleSlopeEdgeBlockEntity::new,
            BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_ELEVATED_DOUBLE_CORNER_SLOPE_EDGE = registerBlockEntity(
            FramedElevatedDoubleCornerSlopeEdgeBlockEntity::new,
            BlockType.FRAMED_ELEV_DOUBLE_CORNER_SLOPE_EDGE
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_ELEVATED_DOUBLE_INNER_CORNER_SLOPE_EDGE = registerBlockEntity(
            FramedElevatedDoubleInnerCornerSlopeEdgeBlockEntity::new,
            BlockType.FRAMED_ELEV_DOUBLE_INNER_CORNER_SLOPE_EDGE
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_ADJ_DOUBLE_BLOCK = registerBlockEntity(
            FramedAdjustableDoubleBlockEntity::standard,
            BlockType.FRAMED_ADJ_DOUBLE_SLAB, BlockType.FRAMED_ADJ_DOUBLE_PANEL
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_ADJ_DOUBLE_COPYCAT_BLOCK = registerBlockEntity(
            FramedAdjustableDoubleBlockEntity::copycat,
            BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_SLAB, BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_PANEL
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_SLOPED_DOUBLE_STAIRS = registerBlockEntity(
            FramedSlopedDoubleStairsBlockEntity::new,
            BlockType.FRAMED_SLOPED_DOUBLE_STAIRS
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_SLICED_SLOPED_DOUBLE_STAIRS_SLAB = registerBlockEntity(
            FramedSlicedSlopedStairsSlabBlockEntity::new,
            BlockType.FRAMED_SLICED_SLOPED_STAIRS_SLAB
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_SLICED_SLOPED_DOUBLE_STAIRS_SLOPE = registerBlockEntity(
            FramedSlicedSlopedStairsSlopeBlockEntity::new,
            BlockType.FRAMED_SLICED_SLOPED_STAIRS_SLOPE
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS = registerBlockEntity(
            FramedVerticalSlopedDoubleStairsBlockEntity::new,
            BlockType.FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_VERTICAL_SLICED_SLOPED_DOUBLE_STAIRS_PANEL = registerBlockEntity(
            FramedVerticalSlicedSlopedStairsPanelBlockEntity::new,
            BlockType.FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_PANEL
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_VERTICAL_SLICED_SLOPED_DOUBLE_STAIRS_SLOPE = registerBlockEntity(
            FramedVerticalSlicedSlopedStairsSlopeBlockEntity::new,
            BlockType.FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_DOOR = registerBlockEntity(
            FramedDoorBlockEntity::new,
            BlockType.FRAMED_DOOR, BlockType.FRAMED_IRON_DOOR
    );
    public static final DeferredBlockEntity<FramedSignBlockEntity> BE_TYPE_FRAMED_SIGN = registerBlockEntity(
            FramedSignBlockEntity::new,
            true,
            BlockType.FRAMED_SIGN, BlockType.FRAMED_WALL_SIGN
    );
    public static final DeferredBlockEntity<FramedSignBlockEntity> BE_TYPE_FRAMED_HANGING_SIGN = registerBlockEntity(
            FramedSignBlockEntity.Hanging::new,
            true,
            BlockType.FRAMED_HANGING_SIGN, BlockType.FRAMED_WALL_HANGING_SIGN
    );
    public static final DeferredBlockEntity<FramedChestBlockEntity> BE_TYPE_FRAMED_CHEST = registerBlockEntity(
            FramedChestBlockEntity::new,
            BlockType.FRAMED_CHEST
    );
    public static final DeferredBlockEntity<FramedStorageBlockEntity> BE_TYPE_FRAMED_SECRET_STORAGE = registerBlockEntity(
            FramedStorageBlockEntity::new,
            BlockType.FRAMED_SECRET_STORAGE
    );
    public static final DeferredBlockEntity<FramedTankBlockEntity> BE_TYPE_FRAMED_TANK = registerBlockEntity(
            FramedTankBlockEntity::new,
            BlockType.FRAMED_TANK
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_FANCY_RAIL_SLOPE = registerBlockEntity(
            FramedFancyRailSlopeBlockEntity::new,
            BlockType.FRAMED_FANCY_RAIL_SLOPE,
            BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE,
            BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
            BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_FLOWER_POT = registerBlockEntity(
            FramedFlowerPotBlockEntity::new,
            BlockType.FRAMED_FLOWER_POT
    );
    public static final DeferredBlockEntity<FramedCollapsibleBlockEntity> BE_TYPE_FRAMED_COLLAPSIBLE_BLOCK = registerBlockEntity(
            FramedCollapsibleBlockEntity::new,
            BlockType.FRAMED_COLLAPSIBLE_BLOCK
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK = registerBlockEntity(
            FramedCollapsibleCopycatBlockEntity::new,
            BlockType.FRAMED_COLLAPSIBLE_COPYCAT_BLOCK
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_ELEVATED_DOUBLE_PRISM = registerBlockEntity(
            FramedElevatedDoublePrismBlockEntity::new,
            BlockType.FRAMED_ELEVATED_INNER_DOUBLE_PRISM
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_ELEVATED_DOUBLE_SLOPED_PRISM = registerBlockEntity(
            FramedElevatedDoubleSlopedPrismBlockEntity::new,
            BlockType.FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_DOUBLE_SLOPE_SLAB = registerBlockEntity(
            FramedDoubleSlopeSlabBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPE_SLAB
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB = registerBlockEntity(
            FramedElevatedDoubleSlopeSlabBlockEntity::new,
            BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER = registerBlockEntity(
            FramedFlatDoubleSlopeSlabCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER = registerBlockEntity(
            FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER, BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_DOUBLE_SLOPE_PANEL = registerBlockEntity(
            FramedDoubleSlopePanelBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPE_PANEL
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL = registerBlockEntity(
            FramedExtendedDoubleSlopePanelBlockEntity::new,
            BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER = registerBlockEntity(
            FramedFlatDoubleSlopePanelCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER = registerBlockEntity(
            FramedFlatExtendedDoubleSlopePanelCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER, BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL = registerBlockEntity(
            FramedSmallDoubleCornerSlopePanelBlockEntity::new,
            BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_WALL = registerBlockEntity(
            FramedSmallDoubleCornerSlopePanelWallBlockEntity::new,
            BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL = registerBlockEntity(
            FramedLargeDoubleCornerSlopePanelBlockEntity::new,
            BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL = registerBlockEntity(
            FramedLargeDoubleCornerSlopePanelWallBlockEntity::new,
            BlockType.FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_W
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL = registerBlockEntity(
            FramedExtendedDoubleCornerSlopePanelBlockEntity::new,
            BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL = registerBlockEntity(
            FramedExtendedDoubleCornerSlopePanelWallBlockEntity::new,
            BlockType.FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL = registerBlockEntity(
            FramedExtendedInnerDoubleCornerSlopePanelBlockEntity::new,
            BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL = registerBlockEntity(
            FramedExtendedInnerDoubleCornerSlopePanelWallBlockEntity::new,
            BlockType.FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_TARGET = registerBlockEntity(
            FramedTargetBlockEntity::new,
            BlockType.FRAMED_TARGET
    );
    public static final DeferredBlockEntity<FramedItemFrameBlockEntity> BE_TYPE_FRAMED_ITEM_FRAME = registerBlockEntity(
            FramedItemFrameBlockEntity::new,
            BlockType.FRAMED_ITEM_FRAME, BlockType.FRAMED_GLOWING_ITEM_FRAME
    );
    public static final Holder<BlockEntityType<?>> BE_TYPE_FRAMED_OWNABLE_BLOCK = registerBlockEntity(
            FramedOwnableBlockEntity::new,
            BlockType.FRAMED_ONE_WAY_WINDOW
    );
    public static final DeferredBlockEntity<FramedChiseledBookshelfBlockEntity> BE_TYPE_FRAMED_CHISELED_BOOKSHELF = registerBlockEntity(
            FramedChiseledBookshelfBlockEntity::new,
            BlockType.FRAMED_CHISELED_BOOKSHELF
    );
    public static final DeferredBlockEntity<FramedHopperBlockEntity> BE_TYPE_FRAMED_HOPPER = registerBlockEntity(
            FramedHopperBlockEntity::new,
            BlockType.FRAMED_HOPPER
    );
    public static final DeferredBlockEntity<FramedShelfBlockEntity> BE_TYPE_FRAMED_SHELF = registerBlockEntity(
            FramedShelfBlockEntity::new,
            BlockType.FRAMED_SHELF
    );
    // endregion

    // region Special BlockEntities
    public static final DeferredBlockEntity<PoweredFramingSawBlockEntity> BE_TYPE_POWERED_FRAMING_SAW = BE_TYPES.registerBlockEntity(
            "powered_framing_saw",
            PoweredFramingSawBlockEntity::new,
            () -> Set.of(BLOCK_POWERED_FRAMING_SAW.value()),
            false
    );
    // endregion

    // region MenuTypes
    public static final DeferredMenuType<FramedStorageMenu> MENU_TYPE_FRAMED_STORAGE = MENU_TYPES.registerSimpleMenuType(
            "framed_chest", FramedStorageMenu::createSingle
    );
    public static final DeferredMenuType<FramedStorageMenu> MENU_TYPE_FRAMED_DOUBLE_CHEST = MENU_TYPES.registerSimpleMenuType(
            "framed_double_chest", FramedStorageMenu::createDouble
    );
    public static final DeferredMenuType<FramingSawMenu> MENU_TYPE_FRAMING_SAW = MENU_TYPES.registerSimpleMenuType(
            "framing_saw", FramingSawMenu::createClient
    );
    public static final DeferredMenuType<PoweredFramingSawMenu> MENU_TYPE_POWERED_FRAMING_SAW = MENU_TYPES.registerAdvancedMenuType(
            "powered_framing_saw", PoweredFramingSawMenu::new
    );
    public static final DeferredMenuType<PaintRollerMenu> MENU_TYPE_PAINT_ROLLER = MENU_TYPES.registerSimpleMenuType(
            "paint_roller", PaintRollerMenu::createClient
    );
    // endregion

    // region RecipeTypes
    public static final DeferredRecipeType<FramingSawRecipe> RECIPE_TYPE_FRAMING_SAW_RECIPE = RECIPE_TYPES.registerRecipeType("frame");
    // endregion

    // region RecipeSerializers
    public static final DeferredRecipeSerializer<FramingSawRecipe> RECIPE_SERIALIZER_FRAMING_SAW_RECIPE = RECIPE_SERIALIZERS.registerRecipeSerializer(
            "frame", FramingSawRecipe.CODEC, FramingSawRecipe.STREAM_CODEC
    );
    public static final DeferredRecipeSerializer<CamoApplicationRecipe> RECIPE_SERIALIZER_APPLY_CAMO = RECIPE_SERIALIZERS.registerRecipeSerializer(
            "apply_camo", CamoApplicationRecipe.CODEC, CamoApplicationRecipe.STREAM_CODEC
    );
    public static final DeferredRecipeSerializer<JeiCamoApplicationRecipe> RECIPE_SERIALIZER_JEI_CAMO = RECIPE_SERIALIZERS.registerRecipeSerializer(
            "jei_camo", JeiCamoApplicationRecipe.CODEC, JeiCamoApplicationRecipe.STREAM_CODEC
    );
    public static final DeferredRecipeSerializer<ShapeRotationRecipe> RECIPE_SERIALIZER_SHAPE_ROTATION = RECIPE_SERIALIZERS.registerRecipeSerializer(
            "rotate_shape", ShapeRotationRecipe.CODEC, ShapeRotationRecipe.STREAM_CODEC
    );
    // endregion

    // region RecipeBookCategories
    public static final Holder<RecipeBookCategory> RECIPE_BOOK_CATEGORY_FRAMING_SAW = RECIPE_BOOK_CATEGORIES.register(
            "framing_saw", RecipeBookCategory::new
    );
    // endregion

    // region RecipeDisplay.Types
    public static final Holder<RecipeDisplay.Type<?>> RECIPE_DISPLAY_TYPE_FRAMING_SAW = RECIPE_DISPLAY_TYPES.register(
            "framing_saw", () -> new RecipeDisplay.Type<>(FramingSawRecipeDisplay.CODEC, FramingSawRecipeDisplay.STREAM_CODEC)
    );
    // endregion

    // region IngredientTypes
    public static final Holder<IngredientType<?>> INGREDIENT_TYPE_JEI_CAMO_DUMMY = INGREDIENT_TYPES.register(
            "jei_camo_dummy", () -> new IngredientType<>(JeiCamoApplicationDummyIngredient.CODEC, JeiCamoApplicationDummyIngredient.STREAM_CODEC)
    );
    // endregion

    // region CreativeModeTabs
    public static final Holder<CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register(
            "framed_blocks", FramedCreativeTab::makeTab
    );
    // endregion

    // region ParticleTypes
    public static final DeferredParticleType<FluidParticleOptions> FLUID_PARTICLE = PARTICLE_TYPES.registerParticleType(
            "fluid", false, FluidParticleOptions.CODEC, FluidParticleOptions.STREAM_CODEC
    );
    public static final DeferredParticleType<BlockOverlayParticleOptions> BLOCK_OVERLAY_PARTICLE = PARTICLE_TYPES.registerParticleType(
            "block_overlay", false, BlockOverlayParticleOptions.CODEC, BlockOverlayParticleOptions.STREAM_CODEC
    );
    // endregion

    // region LootItemConditions
    public static final DeferredMapCodec<RetainCamoLootCondition> NON_TRIVIAL_CAMO_LOOT_CONDITION = LOOT_CONDITIONS.registerCodec(
            "retain_camo", RetainCamoLootCondition.MAP_CODEC
    );
    // endregion

    // region LootItemFunctions
    public static final DeferredMapCodec<SplitCamoLootFunction> SPLIT_CAMO_LOOT_FUNCTION = LOOT_FUNCTIONS.registerCodec(
            "split_camo", SplitCamoLootFunction.MAP_CODEC
    );
    // endregion

    // region LootNumberProviderTypes
    public static final DeferredMapCodec<BoardAdditionalItemCountNumberProvider> BOARD_ADDITIONAL_ITEM_COUNT_NUMBER_PROVIDER = LOOT_NUMBER_PROVIDERS.registerCodec(
            "board", MapCodec.unit(BoardAdditionalItemCountNumberProvider.INSTANCE)
    );
    public static final DeferredMapCodec<LayeredCubeAdditionalItemCountNumberProvider> LAYERED_CUBE_ADDITIONAL_ITEM_COUNT_NUMBER_PROVIDER = LOOT_NUMBER_PROVIDERS.registerCodec(
            "layered_cube", MapCodec.unit(LayeredCubeAdditionalItemCountNumberProvider.INSTANCE)
    );
    // endregion

    // region CamoContainer.Factories
    public static final DeferredHolder<CamoContainerFactory<?>, EmptyCamoContainerFactory> FACTORY_EMPTY = CAMO_CONTAINER_FACTORIES.register(
            "empty", EmptyCamoContainerFactory::new
    );
    public static final DeferredHolder<CamoContainerFactory<?>, BlockCamoContainerFactory> FACTORY_BLOCK = CAMO_CONTAINER_FACTORIES.register(
            "block", BlockCamoContainerFactory::new
    );
    public static final DeferredHolder<CamoContainerFactory<?>, FluidCamoContainerFactory> FACTORY_FLUID = CAMO_CONTAINER_FACTORIES.register(
            "fluid", FluidCamoContainerFactory::new
    );
    // endregion

    public static void init(IEventBus modBus) {
        modBus.addListener(FramedRegistries::onRegisterNewRegistries);
        modBus.addListener(FramedRegistries::onModifyRegistries);

        BLOCKS.register(modBus);
        DATA_COMPONENTS.register(modBus);
        ITEMS.register(modBus);
        BE_TYPES.register(modBus);
        MENU_TYPES.register(modBus);
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
        RECIPE_BOOK_CATEGORIES.register(modBus);
        RECIPE_DISPLAY_TYPES.register(modBus);
        INGREDIENT_TYPES.register(modBus);
        CREATIVE_TABS.register(modBus);
        PARTICLE_TYPES.register(modBus);
        LOOT_CONDITIONS.register(modBus);
        LOOT_FUNCTIONS.register(modBus);
        LOOT_NUMBER_PROVIDERS.register(modBus);
        CAMO_CONTAINER_FACTORIES.register(modBus);
    }

    public static Collection<DeferredHolder<Block, ? extends Block>> getRegisteredBlocks() {
        return BLOCKS.getEntries();
    }

    public static Block byType(BlockType type) {
        return BLOCKS_BY_TYPE.get(type).value();
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getRegisteredItems() {
        return ITEMS.getEntries();
    }

    public static Item toolByType(FramedToolType type) {
        return TOOLS_BY_TYPE.get(type).value();
    }

    public static Stream<DeferredBlockEntity<? extends IFramedBlockEntity>> getBlockEntities() {
        return BLOCK_ENTITIES_BY_TYPE.values().stream();
    }

    @SuppressWarnings("unchecked")
    public static Stream<DeferredBlockEntity<? extends FramedDoubleBlockEntity>> getDoubleBlockEntities() {
        return BLOCK_ENTITIES_BY_TYPE.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isDoubleBlock())
                .map(Map.Entry::getValue)
                .map(holder -> (DeferredBlockEntity<? extends FramedDoubleBlockEntity>) holder);
    }

    public static BlockEntityType.BlockEntitySupplier<FramedBlockEntity> getDefaultBlockEntityFactory() {
        return (pos, state) -> new FramedBlockEntity(BE_TYPE_FRAMED_BLOCK.value(), pos, state);
    }

    private static BlockEntityType.BlockEntitySupplier<FramedBlockEntity> getDefaultDoubleBlockEntityFactory() {
        return (pos, state) -> new FramedDoubleBlockEntity(BE_TYPE_FRAMED_DOUBLE_BLOCK.value(), pos, state);
    }

    private static BlockType[] getDefaultEntityBlockTypes(boolean _double) {
        return Arrays.stream(BlockType.values())
                .filter(type -> !type.hasSpecialTile() && (type.isDoubleBlock() == _double))
                .toArray(BlockType[]::new);
    }

    private static <T extends Block & IFramedBlock> Holder<Block> registerBlock(
            FramedBlockFactory<T> blockFactory, BlockType type
    ) {
        return registerBlock(props -> blockFactory.create(type, props), type);
    }

    private static <T extends Block & IFramedBlock> Holder<Block> registerBlock(
            Function<BlockBehaviour.Properties, T> blockFactory, BlockType type
    ) {
        Holder<Block> result = BLOCKS.registerBlock(type.getName(), props -> {
            T block;
            try {
                block = blockFactory.apply(props);
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to construct block of type " + type, t);
            }
            Preconditions.checkArgument(block.getBlockType() == type, "Inconsistent block type, expected %s, got %s", type, block.getBlockType());
            return block;
        });
        BLOCKS_BY_TYPE.put(type, result);

        if (type.hasBlockItem()) {
            ITEMS.registerItem(type.getName(), props -> {
                IFramedBlock block = (IFramedBlock) result.value();
                return (BlockItem) block.createBlockItem(props.useBlockDescriptionPrefix());
            });
        }

        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private static Holder<Block> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends Block> blockFactory) {
        Holder<Block> result = BLOCKS.registerBlock(name, blockFactory);
        ITEMS.registerSimpleBlockItem(result);
        return result;
    }

    private static Holder<Item> registerToolItem(ToolItemFactory itemFactory, FramedToolType type) {
        Holder<Item> result = ITEMS.registerItem(type.getName(), props -> itemFactory.create(type, props));
        TOOLS_BY_TYPE.put(type, result);
        return result;
    }

    private static <T extends BlockEntity & IFramedBlockEntity> DeferredBlockEntity<T> registerBlockEntity(
            BlockEntityType.BlockEntitySupplier<T> factory, BlockType... types
    ) {
        return registerBlockEntity(factory, false, types);
    }

    private static <T extends BlockEntity & IFramedBlockEntity> DeferredBlockEntity<T> registerBlockEntity(
            BlockEntityType.BlockEntitySupplier<T> factory, boolean opOnlyNbt, BlockType... types
    ) {
        return registerBlockEntity(types[0].getName(), factory, opOnlyNbt, types);
    }

    private static <T extends BlockEntity & IFramedBlockEntity> DeferredBlockEntity<T> registerBlockEntity(
            String name, BlockEntityType.BlockEntitySupplier<T> factory, boolean opOnlyNbt, BlockType... types
    ) {
        Supplier<Set<Block>> blocks = () -> Arrays.stream(types)
                .map(BLOCKS_BY_TYPE::get)
                .map(Holder::value)
                .collect(Collectors.toSet());
        DeferredBlockEntity<T> result = BE_TYPES.registerBlockEntity(name, factory, blocks, opOnlyNbt);
        for (BlockType type : types) {
            BLOCK_ENTITIES_BY_TYPE.put(type, result);
        }
        return result;
    }

    private static <T> DeferredRegister<T> register(ResourceKey<Registry<T>> key) {
        return DeferredRegister.create(key, FramedConstants.MOD_ID);
    }

    private static <T> DeferredMapCodecRegister<T> mapCodecRegister(ResourceKey<Registry<MapCodec<? extends T>>> key) {
        return DeferredMapCodecRegister.createMapCodecs(key, FramedConstants.MOD_ID);
    }

    @FunctionalInterface
    private interface FramedBlockFactory<T extends Block & IFramedBlock> {
        T create(BlockType type, BlockBehaviour.Properties properties);
    }

    @FunctionalInterface
    private interface ToolItemFactory {
        Item create(FramedToolType type, Item.Properties properties);
    }

    private FBContent() { }
}
