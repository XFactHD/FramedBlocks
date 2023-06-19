package xfacthd.framedblocks.common;

import com.google.common.base.Preconditions;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.*;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramingSawBlock;
import xfacthd.framedblocks.common.block.cube.*;
import xfacthd.framedblocks.common.block.door.*;
import xfacthd.framedblocks.common.block.interactive.*;
import xfacthd.framedblocks.common.block.pane.*;
import xfacthd.framedblocks.common.block.pillar.*;
import xfacthd.framedblocks.common.block.prism.*;
import xfacthd.framedblocks.common.block.rail.*;
import xfacthd.framedblocks.common.block.slab.*;
import xfacthd.framedblocks.common.block.slope.*;
import xfacthd.framedblocks.common.block.slopepanel.*;
import xfacthd.framedblocks.common.block.slopeslab.*;
import xfacthd.framedblocks.common.block.stairs.*;
import xfacthd.framedblocks.common.block.torch.*;
import xfacthd.framedblocks.common.blockentity.doubled.*;
import xfacthd.framedblocks.common.blockentity.special.*;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeSerializer;
import xfacthd.framedblocks.common.data.camo.BlockCamoContainer;
import xfacthd.framedblocks.common.data.camo.FluidCamoContainer;
import xfacthd.framedblocks.common.menu.FramingSawMenu;
import xfacthd.framedblocks.common.menu.FramedStorageMenu;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.FramedToolType;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.item.FramedToolItem;
import xfacthd.framedblocks.common.blockentity.*;
import xfacthd.framedblocks.common.util.FramedCreativeTab;
import xfacthd.framedblocks.common.util.RegisteredBE;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FBContent
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FramedConstants.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FramedConstants.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BE_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FramedConstants.MOD_ID);
    private static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FramedConstants.MOD_ID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, FramedConstants.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FramedConstants.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FramedConstants.MOD_ID);

    private static final DeferredRegister<CamoContainer.Factory> CAMO_CONTAINER_FACTORIES = DeferredRegister.create(
            FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_NAME,
            FramedConstants.MOD_ID
    );
    public static final Supplier<IForgeRegistry<CamoContainer.Factory>> CAMO_CONTAINER_FACTORY_REGISTRY = CAMO_CONTAINER_FACTORIES.makeRegistry(
            () ->
            {
                RegistryBuilder<CamoContainer.Factory> builder = new RegistryBuilder<>();
                builder.disableOverrides().setDefaultKey(Utils.rl("empty"));
                return builder;
            }
    );

    private static final Map<BlockType, RegistryObject<Block>> BLOCKS_BY_TYPE = new EnumMap<>(BlockType.class);
    private static final Map<FramedToolType, RegistryObject<Item>> TOOLS_BY_TYPE = new EnumMap<>(FramedToolType.class);
    private static final List<RegistryObject<BlockEntityType<? extends FramedDoubleBlockEntity>>> DOUBLE_BLOCK_ENTITIES = new ArrayList<>();

    // region Blocks
    public static final RegistryObject<Block> BLOCK_FRAMED_CUBE = registerBlock(FramedCube::new, BlockType.FRAMED_CUBE);
    public static final RegistryObject<Block> BLOCK_FRAMED_SLOPE = registerBlock(FramedSlopeBlock::new, BlockType.FRAMED_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_CORNER_SLOPE = registerBlock(FramedCornerSlopeBlock::new, BlockType.FRAMED_CORNER_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_INNER_CORNER_SLOPE = registerBlock(FramedCornerSlopeBlock::new, BlockType.FRAMED_INNER_CORNER_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_PRISM_CORNER = registerBlock(FramedPrismCornerBlock::new, BlockType.FRAMED_PRISM_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_INNER_PRISM_CORNER = registerBlock(FramedPrismCornerBlock::new, BlockType.FRAMED_INNER_PRISM_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_THREEWAY_CORNER = registerBlock(FramedThreewayCornerBlock::new, BlockType.FRAMED_THREEWAY_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_INNER_THREEWAY_CORNER = registerBlock(FramedThreewayCornerBlock::new, BlockType.FRAMED_INNER_THREEWAY_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_SLAB = registerBlock(FramedSlabBlock::new, BlockType.FRAMED_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_SLAB_EDGE = registerBlock(FramedSlabEdgeBlock::new, BlockType.FRAMED_SLAB_EDGE);
    public static final RegistryObject<Block> BLOCK_FRAMED_SLAB_CORNER = registerBlock(FramedSlabCornerBlock::new, BlockType.FRAMED_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_DIVIDED_SLAB = registerBlock(FramedDividedSlabBlock::new, BlockType.FRAMED_DIVIDED_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_PANEL = registerBlock(FramedPanelBlock::new, BlockType.FRAMED_PANEL);
    public static final RegistryObject<Block> BLOCK_FRAMED_CORNER_PILLAR = registerBlock(FramedCornerPillarBlock::new, BlockType.FRAMED_CORNER_PILLAR);
    public static final RegistryObject<Block> BLOCK_FRAMED_DIVIDED_PANEL_HOR = registerBlock(FramedDividedPanelBlock::new, BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL);
    public static final RegistryObject<Block> BLOCK_FRAMED_DIVIDED_PANEL_VERT = registerBlock(FramedDividedPanelBlock::new, BlockType.FRAMED_DIVIDED_PANEL_VERTICAL);
    public static final RegistryObject<Block> BLOCK_FRAMED_STAIRS = registerBlock(FramedStairsBlock::new, BlockType.FRAMED_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_WALL = registerBlock(FramedWallBlock::new, BlockType.FRAMED_WALL);
    public static final RegistryObject<Block> BLOCK_FRAMED_FENCE = registerBlock(FramedFenceBlock::new, BlockType.FRAMED_FENCE);
    public static final RegistryObject<Block> BLOCK_FRAMED_FENCE_GATE = registerBlock(FramedFenceGateBlock::new, BlockType.FRAMED_FENCE_GATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOOR = registerBlock(FramedDoorBlock::wood, BlockType.FRAMED_DOOR);
    public static final RegistryObject<Block> BLOCK_FRAMED_IRON_DOOR = registerBlock(FramedDoorBlock::iron, BlockType.FRAMED_IRON_DOOR);
    public static final RegistryObject<Block> BLOCK_FRAMED_TRAP_DOOR = registerBlock(FramedTrapDoorBlock::wood, BlockType.FRAMED_TRAPDOOR);
    public static final RegistryObject<Block> BLOCK_FRAMED_IRON_TRAP_DOOR = registerBlock(FramedTrapDoorBlock::iron, BlockType.FRAMED_IRON_TRAPDOOR);
    public static final RegistryObject<Block> BLOCK_FRAMED_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::wood, BlockType.FRAMED_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::woodWaterloggable, BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_STONE_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::stone, BlockType.FRAMED_STONE_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::stoneWaterloggable, BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::obsidian, BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE = registerBlock(FramedPressurePlateBlock::obsidianWaterloggable, BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_GOLD_PRESSURE_PLATE = registerBlock(FramedWeightedPressurePlateBlock::gold, BlockType.FRAMED_GOLD_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE = registerBlock(FramedWeightedPressurePlateBlock::goldWaterloggable, BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_IRON_PRESSURE_PLATE = registerBlock(FramedWeightedPressurePlateBlock::iron, BlockType.FRAMED_IRON_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE = registerBlock(FramedWeightedPressurePlateBlock::ironWaterloggable, BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_LADDER = registerBlock(FramedLadderBlock::new, BlockType.FRAMED_LADDER);
    public static final RegistryObject<Block> BLOCK_FRAMED_BUTTON = registerBlock(FramedButtonBlock::wood, BlockType.FRAMED_BUTTON);
    public static final RegistryObject<Block> BLOCK_FRAMED_STONE_BUTTON = registerBlock(FramedButtonBlock::stone, BlockType.FRAMED_STONE_BUTTON);
    public static final RegistryObject<Block> BLOCK_FRAMED_LEVER = registerBlock(FramedLeverBlock::new, BlockType.FRAMED_LEVER);
    public static final RegistryObject<Block> BLOCK_FRAMED_SIGN = registerBlock(FramedSignBlock::new, BlockType.FRAMED_SIGN);
    public static final RegistryObject<Block> BLOCK_FRAMED_WALL_SIGN = registerBlock(FramedWallSignBlock::new, BlockType.FRAMED_WALL_SIGN);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_SLAB = registerBlock(FramedDoubleSlabBlock::new, BlockType.FRAMED_DOUBLE_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_PANEL = registerBlock(FramedDoublePanelBlock::new, BlockType.FRAMED_DOUBLE_PANEL);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_SLOPE = registerBlock(FramedDoubleSlopeBlock::new, BlockType.FRAMED_DOUBLE_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_CORNER = registerBlock(FramedDoubleCornerBlock::new, BlockType.FRAMED_DOUBLE_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_PRISM_CORNER = registerBlock(FramedDoublePrismCornerBlock::new, BlockType.FRAMED_DOUBLE_PRISM_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER = registerBlock(FramedDoubleThreewayCornerBlock::new, BlockType.FRAMED_DOUBLE_THREEWAY_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_TORCH = registerBlock(FramedTorchBlock::new, BlockType.FRAMED_TORCH);
    public static final RegistryObject<Block> BLOCK_FRAMED_WALL_TORCH = registerBlock(FramedWallTorchBlock::new, BlockType.FRAMED_WALL_TORCH);
    public static final RegistryObject<Block> BLOCK_FRAMED_SOUL_TORCH = registerBlock(FramedSoulTorchBlock::new, BlockType.FRAMED_SOUL_TORCH);
    public static final RegistryObject<Block> BLOCK_FRAMED_SOUL_WALL_TORCH = registerBlock(FramedSoulWallTorchBlock::new, BlockType.FRAMED_SOUL_WALL_TORCH);
    public static final RegistryObject<Block> BLOCK_FRAMED_REDSTONE_TORCH = registerBlock(FramedRedstoneTorchBlock::new, BlockType.FRAMED_REDSTONE_TORCH);
    public static final RegistryObject<Block> BLOCK_FRAMED_REDSTONE_WALL_TORCH = registerBlock(FramedRedstoneWallTorchBlock::new, BlockType.FRAMED_REDSTONE_WALL_TORCH);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLOOR = registerBlock(FramedFloorBlock::new, BlockType.FRAMED_FLOOR_BOARD);
    public static final RegistryObject<Block> BLOCK_FRAMED_LATTICE = registerBlock(FramedLatticeBlock::new, BlockType.FRAMED_LATTICE_BLOCK);
    public static final RegistryObject<Block> BLOCK_FRAMED_VERTICAL_STAIRS = registerBlock(FramedVerticalStairsBlock::new, BlockType.FRAMED_VERTICAL_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_CHEST = registerBlock(FramedChestBlock::new, BlockType.FRAMED_CHEST);
    public static final RegistryObject<Block> BLOCK_FRAMED_BARS = registerBlock(FramedPaneBlock::new, BlockType.FRAMED_BARS);
    public static final RegistryObject<Block> BLOCK_FRAMED_PANE = registerBlock(FramedPaneBlock::new, BlockType.FRAMED_PANE);
    public static final RegistryObject<Block> BLOCK_FRAMED_RAIL_SLOPE = registerBlock(FramedRailSlopeBlock::normal, BlockType.FRAMED_RAIL_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_POWERED_RAIL_SLOPE = registerBlock(FramedPoweredRailSlopeBlock::powered, BlockType.FRAMED_POWERED_RAIL_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_DETECTOR_RAIL_SLOPE = registerBlock(FramedDetectorRailSlopeBlock::normal, BlockType.FRAMED_DETECTOR_RAIL_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE = registerBlock(FramedPoweredRailSlopeBlock::activator, BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLOWER_POT = registerBlock(FramedFlowerPotBlock::new, BlockType.FRAMED_FLOWER_POT);
    public static final RegistryObject<Block> BLOCK_FRAMED_PILLAR = registerBlock(FramedPillarBlock::new, BlockType.FRAMED_PILLAR);
    public static final RegistryObject<Block> BLOCK_FRAMED_HALF_PILLAR = registerBlock(FramedHalfPillarBlock::new, BlockType.FRAMED_HALF_PILLAR);
    public static final RegistryObject<Block> BLOCK_FRAMED_POST = registerBlock(FramedPillarBlock::new, BlockType.FRAMED_POST);
    public static final RegistryObject<Block> BLOCK_FRAMED_COLLAPSIBLE_BLOCK = registerBlock(FramedCollapsibleBlock::new, BlockType.FRAMED_COLLAPSIBLE_BLOCK);
    public static final RegistryObject<Block> BLOCK_FRAMED_HALF_STAIRS = registerBlock(FramedHalfStairsBlock::new, BlockType.FRAMED_HALF_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_DIVIDED_STAIRS = registerBlock(FramedDividedStairsBlock::new, BlockType.FRAMED_DIVIDED_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_BOUNCY_CUBE = registerBlock(FramedBouncyCubeBlock::new, BlockType.FRAMED_BOUNCY_CUBE);
    public static final RegistryObject<Block> BLOCK_FRAMED_SECRET_STORAGE = registerBlock(FramedStorageBlock::new, BlockType.FRAMED_SECRET_STORAGE);
    public static final RegistryObject<Block> BLOCK_FRAMED_REDSTONE_BLOCK = registerBlock(FramedRedstoneBlock::new, BlockType.FRAMED_REDSTONE_BLOCK);
    public static final RegistryObject<Block> BLOCK_FRAMED_PRISM = registerBlock(FramedPrismBlock::new, BlockType.FRAMED_PRISM);
    public static final RegistryObject<Block> BLOCK_FRAMED_INNER_PRISM = registerBlock(FramedPrismBlock::new, BlockType.FRAMED_INNER_PRISM);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_PRISM = registerBlock(FramedDoublePrismBlock::new, BlockType.FRAMED_DOUBLE_PRISM);
    public static final RegistryObject<Block> BLOCK_FRAMED_SLOPED_PRISM = registerBlock(FramedSlopedPrismBlock::new, BlockType.FRAMED_SLOPED_PRISM);
    public static final RegistryObject<Block> BLOCK_FRAMED_INNER_SLOPED_PRISM = registerBlock(FramedSlopedPrismBlock::new, BlockType.FRAMED_INNER_SLOPED_PRISM);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_SLOPED_PRISM = registerBlock(FramedDoubleSlopedPrismBlock::new, BlockType.FRAMED_DOUBLE_SLOPED_PRISM);
    public static final RegistryObject<Block> BLOCK_FRAMED_SLOPE_SLAB = registerBlock(FramedSlopeSlabBlock::new, BlockType.FRAMED_SLOPE_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_ELEVATED_SLOPE_SLAB = registerBlock(FramedElevatedSlopeSlabBlock::new, BlockType.FRAMED_ELEVATED_SLOPE_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_SLOPE_SLAB = registerBlock(FramedDoubleSlopeSlabBlock::new, BlockType.FRAMED_DOUBLE_SLOPE_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB = registerBlock(FramedInverseDoubleSlopeSlabBlock::new, BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB = registerBlock(FramedElevatedDoubleSlopeSlabBlock::new, BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_STACKED_SLOPE_SLAB = registerBlock(FramedStackedSlopeSlabBlock::new, BlockType.FRAMED_STACKED_SLOPE_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER = registerBlock(FramedFlatSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER = registerBlock(FramedFlatSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER = registerBlock(FramedFlatElevatedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER = registerBlock(FramedFlatElevatedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER = registerBlock(FramedFlatDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER = registerBlock(FramedFlatInverseDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER = registerBlock(FramedFlatElevatedDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER = registerBlock(FramedFlatElevatedDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER = registerBlock(FramedFlatStackedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER = registerBlock(FramedFlatStackedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_VERTICAL_HALF_STAIRS = registerBlock(FramedVerticalHalfStairsBlock::new, BlockType.FRAMED_VERTICAL_HALF_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS = registerBlock(FramedVerticalDividedStairsBlock::new, BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_SLOPE_PANEL = registerBlock(FramedSlopePanelBlock::new, BlockType.FRAMED_SLOPE_PANEL);
    public static final RegistryObject<Block> BLOCK_FRAMED_EXTENDED_SLOPE_PANEL = registerBlock(FramedExtendedSlopePanelBlock::new, BlockType.FRAMED_EXTENDED_SLOPE_PANEL);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_SLOPE_PANEL = registerBlock(FramedDoubleSlopePanelBlock::new, BlockType.FRAMED_DOUBLE_SLOPE_PANEL);
    public static final RegistryObject<Block> BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL = registerBlock(FramedInverseDoubleSlopePanelBlock::new, BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL);
    public static final RegistryObject<Block> BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL = registerBlock(FramedExtendedDoubleSlopePanelBlock::new, BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL);
    public static final RegistryObject<Block> BLOCK_FRAMED_STACKED_SLOPE_PANEL = registerBlock(FramedStackedSlopePanelBlock::new, BlockType.FRAMED_STACKED_SLOPE_PANEL);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER = registerBlock(FramedFlatSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER = registerBlock(FramedFlatSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER = registerBlock(FramedFlatExtendedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER = registerBlock(FramedFlatExtendedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER = registerBlock(FramedFlatDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER = registerBlock(FramedFlatInverseDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER = registerBlock(FramedFlatExtendedDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER = registerBlock(FramedFlatExtendedDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER = registerBlock(FramedFlatStackedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER = registerBlock(FramedFlatStackedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_STAIRS = registerBlock(FramedDoubleStairsBlock::new, BlockType.FRAMED_DOUBLE_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS = registerBlock(FramedVerticalDoubleStairsBlock::new, BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_WALL_BOARD = registerBlock(FramedWallBoardBlock::new, BlockType.FRAMED_WALL_BOARD);
    public static final RegistryObject<Block> BLOCK_FRAMED_GLOWING_CUBE = registerBlock(FramedGlowingCube::new, BlockType.FRAMED_GLOWING_CUBE);
    public static final RegistryObject<Block> BLOCK_FRAMED_PYRAMID = registerBlock(FramedPyramidBlock::new, BlockType.FRAMED_PYRAMID);
    public static final RegistryObject<Block> BLOCK_FRAMED_PYRAMID_SLAB = registerBlock(FramedPyramidBlock::new, BlockType.FRAMED_PYRAMID_SLAB);
    public static final RegistryObject<Block> BLOCK_FRAMED_HORIZONTAL_PANE = registerBlock(FramedHorizontalPaneBlock::new, BlockType.FRAMED_HORIZONTAL_PANE);
    public static final RegistryObject<Block> BLOCK_FRAMED_LARGE_BUTTON = registerBlock(FramedLargeButtonBlock::wood, BlockType.FRAMED_LARGE_BUTTON);
    public static final RegistryObject<Block> BLOCK_FRAMED_LARGE_STONE_BUTTON = registerBlock(FramedLargeButtonBlock::stone, BlockType.FRAMED_LARGE_STONE_BUTTON);
    public static final RegistryObject<Block> BLOCK_FRAMED_TARGET = registerBlock(FramedTargetBlock::new, BlockType.FRAMED_TARGET);
    public static final RegistryObject<Block> BLOCK_FRAMED_GATE = registerBlock(FramedGateBlock::wood, BlockType.FRAMED_GATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_IRON_GATE = registerBlock(FramedGateBlock::iron, BlockType.FRAMED_IRON_GATE);
    public static final RegistryObject<Block> BLOCK_FRAMED_ITEM_FRAME = registerBlock(FramedItemFrameBlock::new, BlockType.FRAMED_ITEM_FRAME);
    public static final RegistryObject<Block> BLOCK_FRAMED_GLOWING_ITEM_FRAME = registerBlock(FramedItemFrameBlock::new, BlockType.FRAMED_GLOWING_ITEM_FRAME);
    public static final RegistryObject<Block> BLOCK_FRAMED_FANCY_RAIL = registerBlock(FramedFancyRailBlock::new, BlockType.FRAMED_FANCY_RAIL);
    public static final RegistryObject<Block> BLOCK_FRAMED_FANCY_POWERED_RAIL = registerBlock(FramedFancyPoweredRailBlock::powered, BlockType.FRAMED_FANCY_POWERED_RAIL);
    public static final RegistryObject<Block> BLOCK_FRAMED_FANCY_DETECTOR_RAIL = registerBlock(FramedFancyDetectorRailBlock::new, BlockType.FRAMED_FANCY_DETECTOR_RAIL);
    public static final RegistryObject<Block> BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL = registerBlock(FramedFancyPoweredRailBlock::activator, BlockType.FRAMED_FANCY_ACTIVATOR_RAIL);
    public static final RegistryObject<Block> BLOCK_FRAMED_FANCY_RAIL_SLOPE = registerBlock(FramedRailSlopeBlock::fancy, BlockType.FRAMED_FANCY_RAIL_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE = registerBlock(FramedPoweredRailSlopeBlock::poweredFancy, BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE = registerBlock(FramedDetectorRailSlopeBlock::fancy, BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE = registerBlock(FramedPoweredRailSlopeBlock::activatorFancy, BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_HALF_SLOPE = registerBlock(FramedHalfSlopeBlock::new, BlockType.FRAMED_HALF_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_VERTICAL_HALF_SLOPE = registerBlock(FramedVerticalHalfSlopeBlock::new, BlockType.FRAMED_VERTICAL_HALF_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_DIVIDED_SLOPE = registerBlock(FramedDividedSlopeBlock::new, BlockType.FRAMED_DIVIDED_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_DOUBLE_HALF_SLOPE = registerBlock(FramedDoubleHalfSlopeBlock::new, BlockType.FRAMED_DOUBLE_HALF_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE = registerBlock(FramedVerticalDoubleHalfSlopeBlock::new, BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE);
    public static final RegistryObject<Block> BLOCK_FRAMED_SLOPED_STAIRS = registerBlock(FramedSlopedStairsBlock::new, BlockType.FRAMED_SLOPED_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS = registerBlock(FramedVerticalSlopedStairsBlock::new, BlockType.FRAMED_VERTICAL_SLOPED_STAIRS);
    public static final RegistryObject<Block> BLOCK_FRAMED_MINI_CUBE = registerBlock(FramedMiniCubeBlock::new, BlockType.FRAMED_MINI_CUBE);
    public static final RegistryObject<Block> BLOCK_FRAMED_ONE_WAY_WINDOW = registerBlock(FramedOneWayWindowBlock::new, BlockType.FRAMED_ONE_WAY_WINDOW);
    // endregion

    // region Special Blocks
    public static final RegistryObject<Block> BLOCK_FRAMING_SAW = registerBlock("framing_saw", FramingSawBlock::new);
    // endregion

    // region Items
    public static final RegistryObject<Item> ITEM_FRAMED_HAMMER = registerToolItem(FramedToolItem::new, FramedToolType.HAMMER);
    public static final RegistryObject<Item> ITEM_FRAMED_WRENCH = registerToolItem(FramedToolItem::new, FramedToolType.WRENCH);
    public static final RegistryObject<Item> ITEM_FRAMED_BLUEPRINT = registerToolItem(FramedBlueprintItem::new, FramedToolType.BLUEPRINT);
    public static final RegistryObject<Item> ITEM_FRAMED_KEY = registerToolItem(FramedToolItem::new, FramedToolType.KEY);
    public static final RegistryObject<Item> ITEM_FRAMED_SCREWDRIVER = registerToolItem(FramedToolItem::new, FramedToolType.SCREWDRIVER);
    public static final RegistryObject<Item> ITEM_FRAMED_REINFORCEMENT = ITEMS.register("framed_reinforcement", () ->
            new Item(new Item.Properties())
    );
    // endregion

    // region BlockEntityTypes
    public static final RegisteredBE<FramedBlockEntity> BE_TYPE_FRAMED_BLOCK = createBlockEntityType(
            FramedBlockEntity::new,
            "framed_tile",
            getDefaultEntityBlocks()
    );
    public static final RegisteredBE<FramedDividedSlabBlockEntity> BE_TYPE_FRAMED_DIVIDED_SLAB = createBlockEntityType(
            FramedDividedSlabBlockEntity::new,
            BlockType.FRAMED_DIVIDED_SLAB
    );
    public static final RegisteredBE<FramedDividedPanelBlockEntity> BE_TYPE_FRAMED_DIVIDED_PANEL = createBlockEntityType(
            FramedDividedPanelBlockEntity::new,
            BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL, BlockType.FRAMED_DIVIDED_PANEL_VERTICAL
    );
    public static final RegisteredBE<FramedSignBlockEntity> BE_TYPE_FRAMED_SIGN = createBlockEntityType(
            FramedSignBlockEntity::new,
            BlockType.FRAMED_SIGN, BlockType.FRAMED_WALL_SIGN
    );
    public static final RegisteredBE<FramedDoubleSlabBlockEntity> BE_TYPE_DOUBLE_FRAMED_SLAB = createBlockEntityType(
            FramedDoubleSlabBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLAB
    );
    public static final RegisteredBE<FramedDoublePanelBlockEntity> BE_TYPE_DOUBLE_FRAMED_PANEL = createBlockEntityType(
            FramedDoublePanelBlockEntity::new,
            BlockType.FRAMED_DOUBLE_PANEL
    );
    public static final RegisteredBE<FramedDoubleSlopeBlockEntity> BE_TYPE_DOUBLE_FRAMED_SLOPE = createBlockEntityType(
            FramedDoubleSlopeBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPE
    );
    public static final RegisteredBE<FramedDoubleCornerBlockEntity> BE_TYPE_DOUBLE_FRAMED_CORNER = createBlockEntityType(
            FramedDoubleCornerBlockEntity::new,
            BlockType.FRAMED_DOUBLE_CORNER
    );
    public static final RegisteredBE<FramedDoubleThreewayCornerBlockEntity> BE_TYPE_DOUBLE_FRAMED_THREEWAY_CORNER = createBlockEntityType(
            FramedDoubleThreewayCornerBlockEntity::new,
            BlockType.FRAMED_DOUBLE_THREEWAY_CORNER, BlockType.FRAMED_DOUBLE_PRISM_CORNER
    );
    public static final RegisteredBE<FramedChestBlockEntity> blockEntityTypeFramedChest = createBlockEntityType(
            FramedChestBlockEntity::new,
            BlockType.FRAMED_CHEST
    );
    public static final RegisteredBE<FramedFlowerPotBlockEntity> BE_TYPE_FRAMED_FLOWER_POT = createBlockEntityType(
            FramedFlowerPotBlockEntity::new,
            BlockType.FRAMED_FLOWER_POT
    );
    public static final RegisteredBE<FramedCollapsibleBlockEntity> BE_TYPE_FRAMED_COLLAPSIBLE_BLOCK = createBlockEntityType(
            FramedCollapsibleBlockEntity::new,
            BlockType.FRAMED_COLLAPSIBLE_BLOCK
    );
    public static final RegisteredBE<FramedDividedStairsBlockEntity> BE_TYPE_FRAMED_DIVIDED_STAIRS = createBlockEntityType(
            FramedDividedStairsBlockEntity::new,
            BlockType.FRAMED_DIVIDED_STAIRS
    );
    public static final RegisteredBE<FramedStorageBlockEntity> BE_TYPE_FRAMED_SECRET_STORAGE = createBlockEntityType(
            FramedStorageBlockEntity::new,
            BlockType.FRAMED_SECRET_STORAGE
    );
    public static final RegisteredBE<FramedDoublePrismBlockEntity> BE_TYPE_FRAMED_DOUBLE_PRISM = createBlockEntityType(
            FramedDoublePrismBlockEntity::new,
            BlockType.FRAMED_DOUBLE_PRISM
    );
    public static final RegisteredBE<FramedDoubleSlopedPrismBlockEntity> BE_TYPE_FRAMED_DOUBLE_SLOPED_PRISM = createBlockEntityType(
            FramedDoubleSlopedPrismBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPED_PRISM
    );
    public static final RegisteredBE<FramedDoubleSlopeSlabBlockEntity> BE_TYPE_FRAMED_DOUBLE_SLOPE_SLAB = createBlockEntityType(
            FramedDoubleSlopeSlabBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPE_SLAB
    );
    public static final RegisteredBE<FramedInverseDoubleSlopeSlabBlockEntity> BE_TYPE_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB = createBlockEntityType(
            FramedInverseDoubleSlopeSlabBlockEntity::new,
            BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB
    );
    public static final RegisteredBE<FramedElevatedDoubleSlopeSlabBlockEntity> BE_TYPE_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB = createBlockEntityType(
            FramedElevatedDoubleSlopeSlabBlockEntity::new,
            BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB
    );
    public static final RegisteredBE<FramedStackedSlopeSlabBlockEntity> BE_TYPE_FRAMED_STACKED_SLOPE_SLAB = createBlockEntityType(
            FramedStackedSlopeSlabBlockEntity::new,
            BlockType.FRAMED_STACKED_SLOPE_SLAB, BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER
    );
    public static final RegisteredBE<FramedVerticalDividedStairsBlockEntity> BE_TYPE_FRAMED_VERTICAL_DIVIDED_STAIRS = createBlockEntityType(
            FramedVerticalDividedStairsBlockEntity::new,
            BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS
    );
    public static final RegisteredBE<FramedFlatDoubleSlopeSlabCornerBlockEntity> BE_TYPE_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER = createBlockEntityType(
            FramedFlatDoubleSlopeSlabCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER
    );
    public static final RegisteredBE<FramedFlatInverseDoubleSlopeSlabCornerBlockEntity> BE_TYPE_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER = createBlockEntityType(
            FramedFlatInverseDoubleSlopeSlabCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER
    );
    public static final RegisteredBE<FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity> BE_TYPE_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER = createBlockEntityType(
            FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER, BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER
    );
    public static final RegisteredBE<FramedDoubleSlopePanelBlockEntity> BE_TYPE_FRAMED_DOUBLE_SLOPE_PANEL = createBlockEntityType(
            FramedDoubleSlopePanelBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPE_PANEL
    );
    public static final RegisteredBE<FramedInverseDoubleSlopePanelBlockEntity> BE_TYPE_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL = createBlockEntityType(
            FramedInverseDoubleSlopePanelBlockEntity::new,
            BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL
    );
    public static final RegisteredBE<FramedExtendedDoubleSlopePanelBlockEntity> BE_TYPE_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL = createBlockEntityType(
            FramedExtendedDoubleSlopePanelBlockEntity::new,
            BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL
    );
    public static final RegisteredBE<FramedStackedSlopePanelBlockEntity> BE_TYPE_FRAMED_STACKED_SLOPE_PANEL = createBlockEntityType(
            FramedStackedSlopePanelBlockEntity::new,
            BlockType.FRAMED_STACKED_SLOPE_PANEL, BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER
    );
    public static final RegisteredBE<FramedFlatDoubleSlopePanelCornerBlockEntity> BE_TYPE_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER = createBlockEntityType(
            FramedFlatDoubleSlopePanelCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER
    );
    public static final RegisteredBE<FramedFlatInverseDoubleSlopePanelCornerBlockEntity> BE_TYPE_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER = createBlockEntityType(
            FramedFlatInverseDoubleSlopePanelCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER
    );
    public static final RegisteredBE<FramedFlatExtendedDoubleSlopePanelCornerBlockEntity> BE_TYPE_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER = createBlockEntityType(
            FramedFlatExtendedDoubleSlopePanelCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER, BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER
    );
    public static final RegisteredBE<FramedDoubleStairsBlockEntity> BE_TYPE_FRAMED_DOUBLE_STAIRS = createBlockEntityType(
            FramedDoubleStairsBlockEntity::new,
            BlockType.FRAMED_DOUBLE_STAIRS
    );
    public static final RegisteredBE<FramedVerticalDoubleStairsBlockEntity> BE_TYPE_FRAMED_VERTICAL_DOUBLE_STAIRS = createBlockEntityType(
            FramedVerticalDoubleStairsBlockEntity::new,
            BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS
    );
    public static final RegisteredBE<FramedTargetBlockEntity> BE_TYPE_FRAMED_TARGET = createBlockEntityType(
            FramedTargetBlockEntity::new,
            BlockType.FRAMED_TARGET
    );
    public static final RegisteredBE<FramedItemFrameBlockEntity> BE_TYPE_FRAMED_ITEM_FRAME = createBlockEntityType(
            FramedItemFrameBlockEntity::new,
            BlockType.FRAMED_ITEM_FRAME, BlockType.FRAMED_GLOWING_ITEM_FRAME
    );
    public static final RegisteredBE<FramedFancyRailSlopeBlockEntity> BE_TYPE_FRAMED_FANCY_RAIL_SLOPE = createBlockEntityType(
            FramedFancyRailSlopeBlockEntity::new,
            BlockType.FRAMED_FANCY_RAIL_SLOPE,
            BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE,
            BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
            BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE
    );
    public static final RegisteredBE<FramedDividedSlopeBlockEntity> BE_TYPE_FRAMED_DIVIDED_SLOPE = createBlockEntityType(
            FramedDividedSlopeBlockEntity::new,
            BlockType.FRAMED_DIVIDED_SLOPE
    );
    public static final RegisteredBE<FramedDoubleHalfSlopeBlockEntity> BE_TYPE_FRAMED_DOUBLE_HALF_SLOPE = createBlockEntityType(
            FramedDoubleHalfSlopeBlockEntity::new,
            BlockType.FRAMED_DOUBLE_HALF_SLOPE
    );
    public static final RegisteredBE<FramedVerticalDoubleHalfSlopeBlockEntity> BE_TYPE_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE = createBlockEntityType(
            FramedVerticalDoubleHalfSlopeBlockEntity::new,
            BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE
    );
    public static final RegisteredBE<FramedOwnableBlockEntity> BE_TYPE_FRAMED_OWNABLE_BLOCK = createBlockEntityType(
            FramedOwnableBlockEntity::new,
            BlockType.FRAMED_ONE_WAY_WINDOW
    );
    // endregion

    // region MenuTypes
    public static final RegistryObject<MenuType<FramedStorageMenu>> MENU_TYPE_FRAMED_STORAGE = createMenuType(FramedStorageMenu::new, "framed_chest");
    public static final RegistryObject<MenuType<FramingSawMenu>> MENU_TYPE_FRAMING_SAW = createMenuType(
            (id, inv, buf) -> new FramingSawMenu(id, inv, ContainerLevelAccess.NULL),
            "framing_saw"
    );
    // endregion

    // region RecipeTypes
    public static final RegistryObject<RecipeType<FramingSawRecipe>> RECIPE_TYPE_FRAMING_SAW_RECIPE = createRecipeType("frame");
    // endregion

    // region RecipeSerializers
    public static final RegistryObject<RecipeSerializer<FramingSawRecipe>> RECIPE_SERIALIZER_FRAMING_SAW_RECIPE = RECIPE_SERIALIZERS.register(
            "frame",
            FramingSawRecipeSerializer::new
    );
    // endregion

    // region CreativeModeTabs
    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register(
            "framed_blocks", FramedCreativeTab::makeTab
    );
    // endregion

    // region CamoContainer.Factories
    public static final RegistryObject<CamoContainer.Factory> FACTORY_EMPTY = CAMO_CONTAINER_FACTORIES.register(
            "empty",
            EmptyCamoContainer.Factory::new
    );
    public static final RegistryObject<CamoContainer.Factory> FACTORY_BLOCK = CAMO_CONTAINER_FACTORIES.register(
            "block",
            BlockCamoContainer.Factory::new
    );
    public static final RegistryObject<CamoContainer.Factory> FACTORY_FLUID = CAMO_CONTAINER_FACTORIES.register(
            "fluid",
            FluidCamoContainer.Factory::new
    );
    // endregion



    public static void init(IEventBus modBus)
    {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BE_TYPES.register(modBus);
        CONTAINER_TYPES.register(modBus);
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
        CREATIVE_TABS.register(modBus);
        CAMO_CONTAINER_FACTORIES.register(modBus);
    }

    public static Collection<RegistryObject<Block>> getRegisteredBlocks()
    {
        return BLOCKS.getEntries();
    }

    public static Block byType(BlockType type)
    {
        return BLOCKS_BY_TYPE.get(type).get();
    }

    public static Item toolByType(FramedToolType type)
    {
        return TOOLS_BY_TYPE.get(type).get();
    }

    public static List<RegistryObject<BlockEntityType<? extends FramedDoubleBlockEntity>>> getDoubleBlockEntities()
    {
        return DOUBLE_BLOCK_ENTITIES;
    }

    private static Supplier<Block[]> getDefaultEntityBlocks()
    {
        //noinspection SuspiciousToArrayCall
        return () -> BLOCKS.getEntries()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .map(IFramedBlock.class::cast)
                .filter(block -> !block.getBlockType().hasSpecialTile())
                .toArray(Block[]::new);
    }

    private static <T extends Block & IFramedBlock> RegistryObject<Block> registerBlock(
            Function<BlockType, T> blockFactory, BlockType type
    )
    {
        return registerBlock(() -> blockFactory.apply(type), type);
    }

    private static <T extends Block & IFramedBlock> RegistryObject<Block> registerBlock(
            Supplier<T> blockFactory, BlockType type
    )
    {
        RegistryObject<Block> result = BLOCKS.register(type.getName(), () ->
        {
            T block = blockFactory.get();
            Preconditions.checkArgument(block.getBlockType() == type);
            return block;
        });
        BLOCKS_BY_TYPE.put(type, result);

        if (type.hasBlockItem())
        {
            ITEMS.register(type.getName(), () ->
                    ((IFramedBlock) result.get()).createBlockItem()
            );
        }

        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private static RegistryObject<Block> registerBlock(String name, Supplier<? extends Block> blockFactory)
    {
        RegistryObject<Block> result = BLOCKS.register(name, blockFactory);
        ITEMS.register(name, () -> new BlockItem(result.get(), new Item.Properties()));
        return result;
    }

    private static RegistryObject<Item> registerToolItem(Function<FramedToolType, Item> itemFactory, FramedToolType type)
    {
        RegistryObject<Item> result = ITEMS.register(type.getName(), () -> itemFactory.apply(type));
        TOOLS_BY_TYPE.put(type, result);
        return result;
    }

    private static <T extends BlockEntity> RegisteredBE<T> createBlockEntityType(
            BlockEntityType.BlockEntitySupplier<T> factory, BlockType... types
    )
    {
        Supplier<Block[]> blocks = () -> Arrays.stream(types)
                .map(BLOCKS_BY_TYPE::get)
                .map(RegistryObject::get)
                .toArray(Block[]::new);

        RegisteredBE<T> result = createBlockEntityType(factory, types[0].getName(), blocks);
        if (!FMLEnvironment.production && Arrays.stream(types).anyMatch(BlockType::isDoubleBlock))
        {
            //noinspection unchecked
            DOUBLE_BLOCK_ENTITIES.add((RegistryObject<BlockEntityType<? extends FramedDoubleBlockEntity>>)(Object) result.value());
        }
        return result;
    }

    private static <T extends BlockEntity> RegisteredBE<T> createBlockEntityType(
            BlockEntityType.BlockEntitySupplier<T> factory, String name, Supplier<Block[]> blocks
    )
    {
        return new RegisteredBE<>(BE_TYPES.register(name, () ->
        {
            //noinspection ConstantConditions
            return BlockEntityType.Builder.of(factory, blocks.get()).build(null);
        }));
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> createMenuType(IContainerFactory<T> factory, String name)
    {
        return CONTAINER_TYPES.register(name, () -> IForgeMenuType.create(factory));
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> createRecipeType(String name)
    {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(Utils.rl(name)));
    }



    private FBContent() { }
}