package xfacthd.framedblocks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.*;
import xfacthd.framedblocks.common.tileentity.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FBContent
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FramedBlocks.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FramedBlocks.MODID);
    private static final DeferredRegister<TileEntityType<?>> TILE_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, FramedBlocks.MODID);

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
    //public static final RegistryObject<Block> blockFramedCollapsibleBlock = register(FramedCollapsibleBlock::new, BlockType.FRAMED_COLLAPSIBLE_BLOCK); //STATUS: Not implemented
    public static final RegistryObject<Block> blockFramedGhostBlock = BLOCKS.register("framed_ghost_block", FramedGhostBlock::new);

    /** ITEMS */
    public static final RegistryObject<Item> itemFramedHammer = ITEMS.register("framed_hammer", FramedHammerItem::new);

    /** TILE ENTITY TYPES */
    public static final RegistryObject<TileEntityType<FramedTileEntity>> tileTypeFramedBlock = createTileType(FramedTileEntity::new, "framed_tile", getDefaultTileBlocks());
    public static final RegistryObject<TileEntityType<FramedSignTileEntity>> tileTypeFramedSign = createTileType(
            FramedSignTileEntity::new,
            "framed_sign",
            blockFramedSign, blockFramedWallSign
    );
    public static final RegistryObject<TileEntityType<FramedDoubleSlabTileEntity>> tileTypeDoubleFramedSlab = createTileType(
            FramedDoubleSlabTileEntity::new,
            "framed_double_slab",
            blockFramedDoubleSlab
    );
    public static final RegistryObject<TileEntityType<FramedDoublePanelTileEntity>> tileTypeDoubleFramedPanel = createTileType(
            FramedDoublePanelTileEntity::new,
            "framed_double_panel",
            blockFramedDoublePanel
    );
    public static final RegistryObject<TileEntityType<FramedDoubleSlopeTileEntity>> tileTypeDoubleFramedSlope = createTileType(
            FramedDoubleSlopeTileEntity::new,
            "framed_double_slope",
            blockFramedDoubleSlope
    );
    public static final RegistryObject<TileEntityType<FramedDoubleCornerTileEntity>> tileTypeDoubleFramedCorner = createTileType(
            FramedDoubleCornerTileEntity::new,
            "framed_double_corner",
            blockFramedDoubleCorner
    );
    public static final RegistryObject<TileEntityType<FramedDoubleThreewayCornerTileEntity>> tileTypeDoubleFramedThreewayCorner = createTileType(
            FramedDoubleThreewayCornerTileEntity::new,
            "framed_double_threeway_corner",
            blockFramedDoublePrismCorner, blockFramedDoubleThreewayCorner
    );



    public static void init()
    {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
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

    private static Supplier<Block[]> getDefaultTileBlocks()
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

    @SafeVarargs
    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> createTileType(Supplier<T> factory, String name, RegistryObject<Block>... roBlocks)
    {
        return createTileType(factory, name, () -> Arrays.stream(roBlocks).map(RegistryObject::get).toArray(Block[]::new));
    }

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> createTileType(Supplier<T> factory, String name, Supplier<Block[]> blocks)
    {
        return TILE_TYPES.register(name, () ->
        {
            //noinspection ConstantConditions
            return TileEntityType.Builder.create(factory, blocks.get()).build(null);
        });
    }
}