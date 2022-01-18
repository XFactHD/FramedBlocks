package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.FramedChestBlockEntity;
import xfacthd.framedblocks.api.block.FramedBlockEntity;

public class FramedLanguageProvider extends LanguageProvider
{
    public FramedLanguageProvider(DataGenerator gen) { super(gen, FramedBlocks.MODID, "en_us"); }

    @Override
    protected void addTranslations()
    {
        add(FBContent.blockFramedCube.get(), "Framed Cube");
        add(FBContent.blockFramedSlope.get(), "Framed Slope");
        add(FBContent.blockFramedCornerSlope.get(), "Framed Corner Slope");
        add(FBContent.blockFramedInnerCornerSlope.get(), "Framed Inner Corner Slope");
        add(FBContent.blockFramedPrismCorner.get(), "Framed Prism Corner");
        add(FBContent.blockFramedInnerPrismCorner.get(), "Framed Inner Prism Corner");
        add(FBContent.blockFramedThreewayCorner.get(), "Framed Threeway Corner");
        add(FBContent.blockFramedInnerThreewayCorner.get(), "Framed Inner Threeway Corner");
        add(FBContent.blockFramedSlab.get(), "Framed Slab");
        add(FBContent.blockFramedSlabEdge.get(), "Framed Slab Edge");
        add(FBContent.blockFramedSlabCorner.get(), "Framed Slab Corner");
        add(FBContent.blockFramedPanel.get(), "Framed Panel");
        add(FBContent.blockFramedCornerPillar.get(), "Framed Corner Pillar");
        add(FBContent.blockFramedStairs.get(), "Framed Stairs");
        add(FBContent.blockFramedWall.get(), "Framed Wall");
        add(FBContent.blockFramedFence.get(), "Framed Fence");
        add(FBContent.blockFramedGate.get(), "Framed Fence Gate");
        add(FBContent.blockFramedDoor.get(), "Framed Door");
        add(FBContent.blockFramedTrapDoor.get(), "Framed Trapdoor");
        add(FBContent.blockFramedPressurePlate.get(), "Framed Pressure Plate");
        add(FBContent.blockFramedLadder.get(), "Framed Ladder");
        add(FBContent.blockFramedButton.get(), "Framed Button");
        add(FBContent.blockFramedLever.get(), "Framed Lever");
        add(FBContent.blockFramedSign.get(), "Framed Sign");
        add(FBContent.blockFramedWallSign.get(), "Framed Sign");
        add(FBContent.blockFramedDoubleSlab.get(), "Framed Double Slab");
        add(FBContent.blockFramedDoublePanel.get(), "Framed Double Panel");
        add(FBContent.blockFramedDoubleSlope.get(), "Framed Double Slope");
        add(FBContent.blockFramedDoubleCorner.get(), "Framed Double Corner");
        add(FBContent.blockFramedDoublePrismCorner.get(), "Framed Double Prism Corner");
        add(FBContent.blockFramedDoubleThreewayCorner.get(), "Framed Double Threeway Corner");
        add(FBContent.blockFramedTorch.get(), "Framed Torch"); //Wall torch name is handled through WallTorchBlock
        add(FBContent.blockFramedSoulTorch.get(), "Framed Soul Torch"); //See above
        add(FBContent.blockFramedFloor.get(), "Framed Floor Board");
        add(FBContent.blockFramedLattice.get(), "Framed Lattice");
        add(FBContent.blockFramedVerticalStairs.get(), "Framed Vertical Stairs");
        add(FBContent.blockFramedChest.get(), "Framed Chest");
        add(FBContent.blockFramedBars.get(), "Framed Bars");
        add(FBContent.blockFramedPane.get(), "Framed Pane");
        add(FBContent.blockFramedRailSlope.get(), "Framed Rail Slope");
        add(FBContent.blockFramedFlowerPot.get(), "Framed Flower Pot");
        add(FBContent.blockFramedPillar.get(), "Framed Pillar");
        add(FBContent.blockFramedHalfPillar.get(), "Framed Half Pillar");
        add(FBContent.blockFramedPost.get(), "Framed Post");
        add(FBContent.blockFramedCollapsibleBlock.get(), "Framed Collapsible Block");
        add(FBContent.blockFramedHalfStairs.get(), "Framed Half Stairs");

        add(FBContent.itemFramedHammer.get(), "Framed Hammer");
        add(FBContent.itemFramedWrench.get(), "Framed Wrench");
        add(FBContent.itemFramedBlueprint.get(), "Framed Blueprint");

        add(FramedBlocks.FRAMED_TAB.getDisplayName().getString(), "FramedBlocks");
        add(FramedBlockEntity.MSG_BLACKLISTED.getKey(), "This block is blacklisted!");
        add(FramedBlockEntity.MSG_BLOCK_ENTITY.getKey(), "Blocks with BlockEntities cannot be inserted into framed blocks!");
        add(FramedChestBlockEntity.TITLE.getString(), "Framed Chest");
        add(FramedBlueprintItem.CONTAINED_BLOCK, "Contained Block: %s");
        add(FramedBlueprintItem.CAMO_BLOCK, "Camo Block: %s");
        add(FramedBlueprintItem.IS_ILLUMINATED, "Illuminated: %s");
        add(FramedBlueprintItem.BLOCK_NONE.getString(), "None");
        add(FramedBlueprintItem.BLOCK_INVALID.getString(), "Invalid");
        add(FramedBlueprintItem.ILLUMINATED_FALSE.getString(), "false");
        add(FramedBlueprintItem.ILLUMINATED_TRUE.getString(), "true");
        add(FramedBlueprintItem.CANT_COPY.getString(), "This block can currently not be copied!");
    }
}