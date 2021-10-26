package xfacthd.framedblocks.common;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fmllegacy.network.IContainerFactory;
import net.minecraftforge.registries.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.menu.FramedChestMenu;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.FramedToolType;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.item.FramedToolItem;
import xfacthd.framedblocks.common.blockentity.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FBContent
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FramedBlocks.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FramedBlocks.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BE_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, FramedBlocks.MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, FramedBlocks.MODID);

    /** BLOCKS */
    public static final RegistryObject<Block> blockFramedCube = registerBlock(FramedBlock::new, BlockType.FRAMED_CUBE);
    public static final RegistryObject<Block> blockFramedSlope = registerBlock(FramedSlopeBlock::new, BlockType.FRAMED_SLOPE);
    public static final RegistryObject<Block> blockFramedCornerSlope = registerBlock(FramedCornerSlopeBlock::new, BlockType.FRAMED_CORNER_SLOPE);
    public static final RegistryObject<Block> blockFramedInnerCornerSlope = registerBlock(FramedCornerSlopeBlock::new, BlockType.FRAMED_INNER_CORNER_SLOPE);
    public static final RegistryObject<Block> blockFramedPrismCorner = registerBlock(FramedPrismCornerBlock::new, BlockType.FRAMED_PRISM_CORNER);
    public static final RegistryObject<Block> blockFramedInnerPrismCorner = registerBlock(FramedPrismCornerBlock::new, BlockType.FRAMED_INNER_PRISM_CORNER);
    public static final RegistryObject<Block> blockFramedThreewayCorner = registerBlock(FramedThreewayCornerBlock::new, BlockType.FRAMED_THREEWAY_CORNER);
    public static final RegistryObject<Block> blockFramedInnerThreewayCorner = registerBlock(FramedThreewayCornerBlock::new, BlockType.FRAMED_INNER_THREEWAY_CORNER);
    public static final RegistryObject<Block> blockFramedSlab = registerBlock(FramedSlabBlock::new, BlockType.FRAMED_SLAB);
    public static final RegistryObject<Block> blockFramedSlabEdge = registerBlock(FramedSlabEdgeBlock::new, BlockType.FRAMED_SLAB_EDGE);
    public static final RegistryObject<Block> blockFramedSlabCorner = registerBlock(FramedSlabCornerBlock::new, BlockType.FRAMED_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedPanel = registerBlock(FramedPanelBlock::new, BlockType.FRAMED_PANEL);
    public static final RegistryObject<Block> blockFramedCornerPillar = registerBlock(FramedCornerPillarBlock::new, BlockType.FRAMED_CORNER_PILLAR);
    public static final RegistryObject<Block> blockFramedStairs = registerBlock(FramedStairsBlock::new, BlockType.FRAMED_STAIRS);
    public static final RegistryObject<Block> blockFramedWall = registerBlock(FramedWallBlock::new, BlockType.FRAMED_WALL);
    public static final RegistryObject<Block> blockFramedFence = registerBlock(FramedFenceBlock::new, BlockType.FRAMED_FENCE);
    public static final RegistryObject<Block> blockFramedGate = registerBlock(FramedGateBlock::new, BlockType.FRAMED_GATE);
    public static final RegistryObject<Block> blockFramedDoor = registerBlock(FramedDoorBlock::new, BlockType.FRAMED_DOOR);
    public static final RegistryObject<Block> blockFramedTrapDoor = registerBlock(FramedTrapDoorBlock::new, BlockType.FRAMED_TRAPDOOR);
    public static final RegistryObject<Block> blockFramedPressurePlate = registerBlock(FramedPressurePlateBlock::new, BlockType.FRAMED_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedLadder = registerBlock(FramedLadderBlock::new, BlockType.FRAMED_LADDER);
    public static final RegistryObject<Block> blockFramedButton = registerBlock(FramedButtonBlock::new, BlockType.FRAMED_BUTTON);
    public static final RegistryObject<Block> blockFramedLever = registerBlock(FramedLeverBlock::new, BlockType.FRAMED_LEVER);
    public static final RegistryObject<Block> blockFramedSign = registerBlock(FramedSignBlock::new, BlockType.FRAMED_SIGN);
    public static final RegistryObject<Block> blockFramedWallSign = registerBlock(FramedWallSignBlock::new, BlockType.FRAMED_WALL_SIGN);
    public static final RegistryObject<Block> blockFramedDoubleSlab = registerBlock(FramedDoubleSlabBlock::new, BlockType.FRAMED_DOUBLE_SLAB);
    public static final RegistryObject<Block> blockFramedDoublePanel = registerBlock(FramedDoublePanelBlock::new, BlockType.FRAMED_DOUBLE_PANEL);
    public static final RegistryObject<Block> blockFramedDoubleSlope = registerBlock(FramedDoubleSlopeBlock::new, BlockType.FRAMED_DOUBLE_SLOPE);
    public static final RegistryObject<Block> blockFramedDoubleCorner = registerBlock(FramedDoubleCornerBlock::new, BlockType.FRAMED_DOUBLE_CORNER);
    public static final RegistryObject<Block> blockFramedDoublePrismCorner = registerBlock(FramedDoublePrismCornerBlock::new, BlockType.FRAMED_DOUBLE_PRISM_CORNER);
    public static final RegistryObject<Block> blockFramedDoubleThreewayCorner = registerBlock(FramedDoubleThreewayCornerBlock::new, BlockType.FRAMED_DOUBLE_THREEWAY_CORNER);
    public static final RegistryObject<Block> blockFramedTorch = registerBlock(FramedTorchBlock::new, BlockType.FRAMED_TORCH);
    public static final RegistryObject<Block> blockFramedWallTorch = registerBlock(FramedWallTorchBlock::new, BlockType.FRAMED_WALL_TORCH);
    public static final RegistryObject<Block> blockFramedSoulTorch = registerBlock(FramedSoulTorchBlock::new, BlockType.FRAMED_SOUL_TORCH);
    public static final RegistryObject<Block> blockFramedSoulWallTorch = registerBlock(FramedSoulWallTorchBlock::new, BlockType.FRAMED_SOUL_WALL_TORCH);
    public static final RegistryObject<Block> blockFramedFloor = registerBlock(FramedFloorBlock::new, BlockType.FRAMED_FLOOR_BOARD);
    public static final RegistryObject<Block> blockFramedLattice = registerBlock(FramedLatticeBlock::new, BlockType.FRAMED_LATTICE_BLOCK);
    public static final RegistryObject<Block> blockFramedVerticalStairs = registerBlock(FramedVerticalStairs::new, BlockType.FRAMED_VERTICAL_STAIRS);
    public static final RegistryObject<Block> blockFramedChest = registerBlock(FramedChestBlock::new, BlockType.FRAMED_CHEST);
    public static final RegistryObject<Block> blockFramedBars = registerBlock(FramedPaneBlock::new, BlockType.FRAMED_BARS);
    public static final RegistryObject<Block> blockFramedPane = registerBlock(FramedPaneBlock::new, BlockType.FRAMED_PANE);
    public static final RegistryObject<Block> blockFramedRailSlope = registerBlock(FramedRailSlopeBlock::new, BlockType.FRAMED_RAIL_SLOPE);
    public static final RegistryObject<Block> blockFramedFlowerPot = registerBlock(FramedFlowerPotBlock::new, BlockType.FRAMED_FLOWER_POT);
    public static final RegistryObject<Block> blockFramedPillar = registerBlock(FramedPillarBlock::new, BlockType.FRAMED_PILLAR);
    public static final RegistryObject<Block> blockFramedHalfPillar = registerBlock(FramedHalfPillarBlock::new, BlockType.FRAMED_HALF_PILLAR);
    public static final RegistryObject<Block> blockFramedPost = registerBlock(FramedPillarBlock::new, BlockType.FRAMED_POST);
    //public static final RegistryObject<Block> blockFramedCollapsibleBlock = register(FramedCollapsibleBlock::new, BlockType.FRAMED_COLLAPSIBLE_BLOCK); //STATUS: Not implemented

    /** ITEMS */
    public static final RegistryObject<Item> itemFramedHammer = registerToolItem(FramedToolItem::new, FramedToolType.HAMMER);
    public static final RegistryObject<Item> itemFramedWrench = registerToolItem(FramedToolItem::new, FramedToolType.WRENCH);
    public static final RegistryObject<Item> itemFramedBlueprint = registerToolItem(FramedBlueprintItem::new, FramedToolType.BLUEPRINT);

    /** TILE ENTITY TYPES */
    public static final RegistryObject<BlockEntityType<FramedBlockEntity>> blockEntityTypeFramedBlock = createBlockEntityType(
            FramedBlockEntity::new,
            "framed_tile",
            getDefaultEntityBlocks()
    );
    public static final RegistryObject<BlockEntityType<FramedSignBlockEntity>> blockEntityTypeFramedSign = createBlockEntityType(
            FramedSignBlockEntity::new,
            "framed_sign",
            blockFramedSign, blockFramedWallSign
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleSlabBlockEntity>> blockEntityTypeDoubleFramedSlab = createBlockEntityType(
            FramedDoubleSlabBlockEntity::new,
            "framed_double_slab",
            blockFramedDoubleSlab
    );
    public static final RegistryObject<BlockEntityType<FramedDoublePanelBlockEntity>> blockEntityTypeDoubleFramedPanel = createBlockEntityType(
            FramedDoublePanelBlockEntity::new,
            "framed_double_panel",
            blockFramedDoublePanel
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleSlopeBlockEntity>> blockEntityTypeDoubleFramedSlope = createBlockEntityType(
            FramedDoubleSlopeBlockEntity::new,
            "framed_double_slope",
            blockFramedDoubleSlope
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleCornerBlockEntity>> blockEntityTypeDoubleFramedCorner = createBlockEntityType(
            FramedDoubleCornerBlockEntity::new,
            "framed_double_corner",
            blockFramedDoubleCorner
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleThreewayCornerBlockEntity>> blockEntityTypeDoubleFramedThreewayCorner = createBlockEntityType(
            FramedDoubleThreewayCornerBlockEntity::new,
            "framed_double_threeway_corner",
            blockFramedDoublePrismCorner, blockFramedDoubleThreewayCorner
    );
    public static final RegistryObject<BlockEntityType<FramedChestBlockEntity>> blockEntityTypeFramedChest = createBlockEntityType(
            FramedChestBlockEntity::new,
            "framed_chest",
            blockFramedChest
    );
    public static final RegistryObject<BlockEntityType<FramedFlowerPotBlockEntity>> blockEntityTypeFramedFlowerPot = createBlockEntityType(
            FramedFlowerPotBlockEntity::new,
            "framed_flower_pot",
            blockFramedFlowerPot
    );
    public static final RegistryObject<BlockEntityType<FramedCollapsibleBlockEntity>> blockEntityTypeFramedCollapsibleBlock = null/*createBlockEntityType(
            FramedCollapsibleBlockEntity::new,
            "framed_collapsible_block",
            blockFramedCollapsibleBlock
    )*/;

    /** CONTAINER TYPES */
    public static final RegistryObject<MenuType<FramedChestMenu>> menuTypeFramedChest = createMenuType(FramedChestMenu::new, "framed_chest");



    public static void init()
    {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINER_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static Collection<RegistryObject<Block>> getRegisteredBlocks() { return BLOCKS.getEntries(); }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        BLOCKS.getEntries()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .filter(block -> ((IFramedBlock)block).getBlockType().hasBlockItem())
                .map(block -> ((IFramedBlock)block).createItemBlock())
                .forEach(registry::register);
    }

    private static Supplier<Block[]> getDefaultEntityBlocks()
    {
        return () -> BLOCKS.getEntries()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .filter(block -> !((IFramedBlock)block).getBlockType().hasSpecialTile())
                .toArray(Block[]::new);
    }

    private static RegistryObject<Block> registerBlock(Function<BlockType, Block> blockFactory, BlockType type)
    {
        return registerBlock(() -> blockFactory.apply(type), type);
    }

    private static RegistryObject<Block> registerBlock(Supplier<Block> blockFactory, BlockType type)
    {
        return BLOCKS.register(type.getName(), blockFactory);
    }

    private static RegistryObject<Item> registerToolItem(Function<FramedToolType, Item> itemFactory, FramedToolType type)
    {
        return ITEMS.register(type.getName(), () -> itemFactory.apply(type));
    }

    @SafeVarargs
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> createBlockEntityType(
            BlockEntityType.BlockEntitySupplier<T> factory, String name, RegistryObject<Block>... roBlocks
    )
    {
        return createBlockEntityType(factory, name, () -> Arrays.stream(roBlocks).map(RegistryObject::get).toArray(Block[]::new));
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> createBlockEntityType(
            BlockEntityType.BlockEntitySupplier<T> factory, String name, Supplier<Block[]> blocks
    )
    {
        return BE_TYPES.register(name, () ->
        {
            //noinspection ConstantConditions
            return BlockEntityType.Builder.of(factory, blocks.get()).build(null);
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> createMenuType(IContainerFactory<T> factory, String name)
    {
        return CONTAINER_TYPES.register(name, () -> IForgeContainerType.create(factory));
    }
}