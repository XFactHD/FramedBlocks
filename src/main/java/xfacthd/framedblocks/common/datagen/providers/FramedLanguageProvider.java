package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.client.screen.StateLockOverlay;
import xfacthd.framedblocks.client.util.KeyMappings;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedStorageBlockEntity;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.FramedChestBlockEntity;
import xfacthd.framedblocks.api.block.FramedBlockEntity;

public class FramedLanguageProvider extends LanguageProvider
{
    public FramedLanguageProvider(DataGenerator gen) { super(gen, FramedConstants.MOD_ID, "en_us"); }

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
        add(FBContent.blockFramedIronDoor.get(), "Framed Iron Door");
        add(FBContent.blockFramedTrapDoor.get(), "Framed Trapdoor");
        add(FBContent.blockFramedIronTrapDoor.get(), "Framed Iron Trapdoor");
        add(FBContent.blockFramedPressurePlate.get(), "Framed Pressure Plate");
        add(FBContent.blockFramedStonePressurePlate.get(), "Framed Stone Pressure Plate");
        add(FBContent.blockFramedObsidianPressurePlate.get(), "Framed Obsidian Pressure Plate");
        add(FBContent.blockFramedGoldPressurePlate.get(), "Framed Light Weighted Pressure Plate");
        add(FBContent.blockFramedIronPressurePlate.get(), "Framed Heavy Weighted Pressure Plate");
        add(FBContent.blockFramedLadder.get(), "Framed Ladder");
        add(FBContent.blockFramedButton.get(), "Framed Button");
        add(FBContent.blockFramedStoneButton.get(), "Framed Stone Button");
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
        add(FBContent.blockFramedBouncyCube.get(), "Framed Bouncy Cube");
        add(FBContent.blockFramedSecretStorage.get(), "Framed Secret Storage");
        add(FBContent.blockFramedRedstoneBlock.get(), "Framed Redstone Block");
        add(FBContent.blockFramedPrism.get(), "Framed Prism");
        add(FBContent.blockFramedSlopedPrism.get(), "Framed Sloped Prism");
        add(FBContent.blockFramedSlopeSlab.get(), "Framed Slope Slab");
        add(FBContent.blockFramedElevatedSlopeSlab.get(), "Framed Elevated Slope Slab");
        add(FBContent.blockFramedDoubleSlopeSlab.get(), "Framed Double Slope Slab");
        add(FBContent.blockFramedInverseDoubleSlopeSlab.get(), "Framed Inverted Double Slope Slab");
        add(FBContent.blockFramedVerticalHalfStairs.get(), "Framed Vertical Half Stairs");
        add(FBContent.blockFramedSlopePanel.get(), "Framed Slope Panel");
        add(FBContent.blockFramedExtendedSlopePanel.get(), "Framed Elevated Slope Panel");
        add(FBContent.blockFramedDoubleSlopePanel.get(), "Framed Double Slope Panel");
        add(FBContent.blockFramedInverseDoubleSlopePanel.get(), "Framed Inverted Double Slope Panel");
        add(FBContent.blockFramedDoubleStairs.get(), "Framed Double Stairs");
        add(FBContent.blockFramedVerticalDoubleStairs.get(), "Framed Vertical Double Stairs");
        add(FBContent.blockFramedWallBoard.get(), "Framed Wall Board");
        add(FBContent.blockFramedLargeButton.get(), "Large Framed Button");
        add(FBContent.blockFramedLargeStoneButton.get(), "Large Framed Stone Button");
        add(FBContent.blockFramedHorizontalPane.get(), "Framed Horizontal Pane");
        add(FBContent.blockFramedTarget.get(), "Framed Target");

        add(FBContent.itemFramedHammer.get(), "Framed Hammer");
        add(FBContent.itemFramedWrench.get(), "Framed Wrench");
        add(FBContent.itemFramedBlueprint.get(), "Framed Blueprint");
        add(FBContent.itemFramedKey.get(), "Framed Key");
        add(FBContent.itemFramedScrewdriver.get(), "Framed Screwdriver");

        add(KeyMappings.KEY_CATEGORY, "FramedBlocks");
        add(KeyMappings.KEYMAPPING_UPDATE_CULLING.get().getName(), "Update culling cache");

        add(FramedBlocks.FRAMED_TAB.getDisplayName().getString(), "FramedBlocks");
        add(FramedBlockEntity.MSG_BLACKLISTED.getKey(), "This block is blacklisted!");
        add(FramedBlockEntity.MSG_BLOCK_ENTITY.getKey(), "Blocks with BlockEntities cannot be inserted into framed blocks!");
        add(FramedChestBlockEntity.TITLE.getString(), "Framed Chest");
        add(FramedStorageBlockEntity.TITLE.getString(), "Framed Secret Storage");
        add(FramedBlueprintItem.CONTAINED_BLOCK, "Contained Block: %s");
        add(FramedBlueprintItem.CAMO_BLOCK, "Camo Block: %s");
        add(FramedBlueprintItem.IS_ILLUMINATED, "Illuminated: %s");
        add(FramedBlueprintItem.BLOCK_NONE.getString(), "None");
        add(FramedBlueprintItem.BLOCK_INVALID.getString(), "Invalid");
        add(FramedBlueprintItem.ILLUMINATED_FALSE.getString(), "false");
        add(FramedBlueprintItem.ILLUMINATED_TRUE.getString(), "true");
        add(FramedBlueprintItem.CANT_COPY.getString(), "This block can currently not be copied!");
        add(FramedBlueprintItem.CANT_PLACE_FLUID_CAMO.getString(), "Copying blocks with fluid camos is currently not possible!");
        add(IFramedBlock.LOCK_MESSAGE, "The state of this block is now %s");
        add(StateLockOverlay.LOCK_MESSAGE, "State %s");
        add(IFramedBlock.STATE_LOCKED.getString(), "locked");
        add(IFramedBlock.STATE_UNLOCKED.getString(), "unlocked");
    }
}