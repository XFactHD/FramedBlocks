package xfacthd.framedblocks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.*;
import xfacthd.framedblocks.common.tileentity.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FBContent
{
    public static Block blockFramedCube;                //STATUS: Complete
    public static Block blockFramedSlope;               //STATUS: Complete
    public static Block blockFramedCornerSlope;         //STATUS: Complete
    public static Block blockFramedInnerCornerSlope;    //STATUS: Complete
    public static Block blockFramedPrismCorner;         //STATUS: Complete
    public static Block blockFramedInnerPrismCorner;    //STATUS: Complete
    public static Block blockFramedThreewayCorner;      //STATUS: Complete
    public static Block blockFramedInnerThreewayCorner; //STATUS: Complete
    public static Block blockFramedSlab;                //STATUS: Complete
    public static Block blockFramedSlabEdge;            //STATUS: Complete
    public static Block blockFramedSlabCorner;          //STATUS: Complete
    public static Block blockFramedPanel;               //STATUS: Complete
    public static Block blockFramedCornerPillar;        //STATUS: Complete
    public static Block blockFramedStairs;              //STATUS: Complete
    public static Block blockFramedWall;                //STATUS: Complete
    public static Block blockFramedFence;               //STATUS: Complete
    public static Block blockFramedGate;                //STATUS: Complete
    public static Block blockFramedDoor;                //STATUS: Complete
    public static Block blockFramedTrapDoor;            //STATUS: Complete
    public static Block blockFramedPressurePlate;       //STATUS: Complete
    public static Block blockFramedLadder;              //STATUS: Complete
    public static Block blockFramedButton;              //STATUS: Complete
    public static Block blockFramedLever;               //STATUS: Complete
    public static Block blockFramedSign;                //STATUS: Complete
    public static Block blockFramedWallSign;            //STATUS: Complete
    public static Block blockFramedDoubleSlab;          //STATUS: Complete
    public static Block blockFramedDoublePanel;         //STATUS: Complete
    public static Block blockFramedDoubleSlope;         //STATUS: Complete
    public static Block blockFramedTorch;               //STATUS: Complete
    public static Block blockFramedWallTorch;           //STATUS: Complete
    public static Block blockFramedFloor;               //STATUS: Complete
    public static Block blockFramedLattice;             //STATUS: Complete
    public static Block blockFramedCollapsibleBlock;    //STATUS: Not implemented
    public static Block blockFramedGhostBlock;          //STATUS: Complete

    public static Item itemFramedHammer;

    public static TileEntityType<FramedTileEntity> tileTypeFramedBlock;
    public static TileEntityType<FramedSignTileEntity> tileTypeFramedSign;
    public static TileEntityType<FramedDoubleSlabTileEntity> tileTypeDoubleFramedSlab;
    public static TileEntityType<FramedDoublePanelTileEntity> tileTypeDoubleFramedPanel;
    public static TileEntityType<FramedDoubleSlopeTileEntity> tileTypeDoubleFramedSlope;

    @SubscribeEvent
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(blockFramedCube = new FramedBlock("framed_cube", BlockType.FRAMED_CUBE));
        registry.register(blockFramedSlope = new FramedSlopeBlock());
        registry.register(blockFramedCornerSlope = new FramedCornerSlopeBlock("framed_corner_slope", BlockType.FRAMED_CORNER_SLOPE));
        registry.register(blockFramedInnerCornerSlope = new FramedCornerSlopeBlock("framed_inner_corner_slope", BlockType.FRAMED_INNER_CORNER_SLOPE));
        registry.register(blockFramedPrismCorner = new FramedThreewayCornerBlock("framed_prism_corner", BlockType.FRAMED_PRISM_CORNER));
        registry.register(blockFramedInnerPrismCorner = new FramedThreewayCornerBlock("framed_inner_prism_corner", BlockType.FRAMED_INNER_PRISM_CORNER));
        registry.register(blockFramedThreewayCorner = new FramedThreewayCornerBlock("framed_threeway_corner", BlockType.FRAMED_THREEWAY_CORNER));
        registry.register(blockFramedInnerThreewayCorner = new FramedThreewayCornerBlock("framed_inner_threeway_corner", BlockType.FRAMED_INNER_THREEWAY_CORNER));
        registry.register(blockFramedSlab = new FramedSlabBlock());
        registry.register(blockFramedSlabEdge = new FramedSlabEdgeBlock());
        registry.register(blockFramedSlabCorner = new FramedSlabCornerBlock());
        registry.register(blockFramedPanel = new FramedPanelBlock());
        registry.register(blockFramedCornerPillar = new FramedCornerPillarBlock());
        registry.register(blockFramedStairs = new FramedStairsBlock());
        registry.register(blockFramedWall = new FramedWallBlock());
        registry.register(blockFramedFence = new FramedFenceBlock());
        registry.register(blockFramedGate = new FramedGateBlock());
        registry.register(blockFramedDoor = new FramedDoorBlock());
        registry.register(blockFramedTrapDoor = new FramedTrapDoorBlock());
        registry.register(blockFramedPressurePlate = new FramedPressurePlateBlock());
        registry.register(blockFramedLadder = new FramedLadderBlock());
        registry.register(blockFramedButton = new FramedButtonBlock());
        registry.register(blockFramedLever = new FramedLeverBlock());
        registry.register(blockFramedSign = new FramedSignBlock());
        registry.register(blockFramedWallSign = new FramedWallSignBlock());
        registry.register(blockFramedDoubleSlab = new FramedDoubleSlabBlock());
        registry.register(blockFramedDoublePanel = new FramedDoublePanelBlock());
        registry.register(blockFramedDoubleSlope = new FramedDoubleSlopeBlock());
        registry.register(blockFramedTorch = new FramedTorchBlock());
        registry.register(blockFramedWallTorch = new FramedWallTorchBlock());
        registry.register(blockFramedFloor = new FramedFloorBlock());
        registry.register(blockFramedLattice = new FramedLatticeBlock());
        //registry.register(blockFramedCollapsibleBlock = new FramedCollapsibleBlock());
        registry.register(blockFramedGhostBlock = new FramedGhostBlock());
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        //noinspection ConstantConditions
        ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block.getRegistryName().getNamespace().equals(FramedBlocks.MODID))
                .filter(block -> block instanceof IFramedBlock)
                .filter(block -> ((IFramedBlock)block).getBlockType().hasBlockItem())
                .forEach(block -> registry.register(((IFramedBlock)block).createItemBlock()));

        registry.register(itemFramedHammer = new FramedHammerItem());
    }

    @SubscribeEvent
    public static void onRegisterTiles(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        //noinspection ConstantConditions
        Block[] validBlocks = ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block.getRegistryName().getNamespace().equals(FramedBlocks.MODID))
                .filter(block -> block instanceof IFramedBlock)
                .filter(block -> !((IFramedBlock)block).getBlockType().hasSpecialTile())
                .toArray(Block[]::new);

        event.getRegistry().registerAll(
            tileTypeFramedBlock = createTileType(FramedTileEntity::new, "framed_tile", validBlocks),
            tileTypeFramedSign = createTileType(FramedSignTileEntity::new, "framed_sign", blockFramedSign, blockFramedWallSign),
            tileTypeDoubleFramedSlab = createTileType(FramedDoubleSlabTileEntity::new, "framed_double_slab", blockFramedDoubleSlab),
            tileTypeDoubleFramedPanel = createTileType(FramedDoublePanelTileEntity::new, "framed_double_panel", blockFramedDoublePanel),
            tileTypeDoubleFramedSlope = createTileType(FramedDoubleSlopeTileEntity::new, "framed_double_slope", blockFramedDoubleSlope)
        );
    }

    private static <T extends TileEntity> TileEntityType<T> createTileType(Supplier<T> factory, String name, Block... blocks)
    {
        //noinspection ConstantConditions
        TileEntityType<T> type = TileEntityType.Builder.create(factory, blocks).build(null);
        type.setRegistryName(FramedBlocks.MODID, name);
        return type;
    }
}