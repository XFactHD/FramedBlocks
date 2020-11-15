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
    public static Block blockFramedCube;
    public static Block blockFramedSlope;
    public static Block blockFramedCornerSlope;
    public static Block blockFramedInnerCornerSlope;
    public static Block blockFramedPrismCorner;
    public static Block blockFramedInnerPrismCorner;
    public static Block blockFramedThreewayCorner;
    public static Block blockFramedInnerThreewayCorner;
    public static Block blockFramedSlab;
    public static Block blockFramedPanel;
    public static Block blockFramedCornerPillar;
    public static Block blockFramedStairs;
    public static Block blockFramedWall;
    public static Block blockFramedFence;
    public static Block blockFramedGate;
    public static Block blockFramedDoor;
    public static Block blockFramedTrapDoor;
    public static Block blockFramedPressurePlate;
    public static Block blockFramedLadder;
    public static Block blockFramedButton;
    public static Block blockFramedLever;
    public static Block blockFramedSign;
    public static Block blockFramedWallSign;
    public static Block blockFramedCollapsibleBlock;

    public static Item itemFramedHammer;

    public static TileEntityType<FramedTileEntity> tileTypeFramedBlock;
    public static TileEntityType<FramedSignTileEntity> tileTypeFramedSign;

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
        registry.register(blockFramedPanel = new FramedPanelBlock());
        //registry.register(blockFramedCornerPillar = new FramedCornerPillarBlock());
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
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        //noinspection ConstantConditions
        ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block.getRegistryName().getNamespace().equals(FramedBlocks.MODID))
                .filter(block -> block != blockFramedWallSign)
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
                .filter(block -> block != blockFramedSign && block != blockFramedWallSign)
                .toArray(Block[]::new);

        event.getRegistry().registerAll(
            tileTypeFramedBlock = createTileType(FramedTileEntity::new, "framed_tile", validBlocks),
            tileTypeFramedSign = createTileType(FramedSignTileEntity::new, "framed_sign", blockFramedSign, blockFramedWallSign)
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