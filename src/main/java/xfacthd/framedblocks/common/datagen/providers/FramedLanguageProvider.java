package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

public class FramedLanguageProvider extends LanguageProvider
{
    public FramedLanguageProvider(DataGenerator gen) { super(gen, FramedBlocks.MODID, "en_us"); }

    @Override
    protected void addTranslations()
    {
        add(FBContent.blockFramedCube, "Framed Cube");
        add(FBContent.blockFramedSlope, "Framed Slope");
        add(FBContent.blockFramedCornerSlope, "Framed Corner Slope");
        add(FBContent.blockFramedInnerCornerSlope, "Framed Inner Corner Slope");
        add(FBContent.blockFramedPrismCorner, "Framed Prism Corner");
        add(FBContent.blockFramedInnerPrismCorner, "Framed Inner Prism Corner");
        add(FBContent.blockFramedThreewayCorner, "Framed Threeway Corner");
        add(FBContent.blockFramedInnerThreewayCorner, "Framed Inner Threeway Corner");
        add(FBContent.blockFramedSlab, "Framed Slab");
        add(FBContent.blockFramedPanel, "Framed Panel");
        add(FBContent.blockFramedCornerPillar, "Framed Corner Pillar");
        add(FBContent.blockFramedStairs, "Framed Stairs");
        add(FBContent.blockFramedWall, "Framed Wall");
        add(FBContent.blockFramedFence, "Framed Fence");
        add(FBContent.blockFramedGate, "Framed Fence Gate");
        add(FBContent.blockFramedDoor, "Framed Door");
        add(FBContent.blockFramedTrapDoor, "Framed Trapdoor");
        add(FBContent.blockFramedPressurePlate, "Framed Pressure Plate");
        add(FBContent.blockFramedLadder, "Framed Ladder");
        add(FBContent.blockFramedButton, "Framed Button");
        add(FBContent.blockFramedLever, "Framed Lever");
        add(FBContent.blockFramedSign, "Framed Sign");
        add(FBContent.blockFramedWallSign, "Framed Sign");
        add(FBContent.blockFramedDoubleSlab, "Framed Double Slab");
        add(FBContent.blockFramedDoublePanel, "Framed Double Panel");
        add(FBContent.blockFramedDoubleSlope, "Framed Double Slope");
        //add(FBContent.blockFramedCollapsibleBlock, "Framed Collapsible Block");

        add(FBContent.itemFramedHammer, "Framed Hammer");

        add(FramedBlocks.FRAMED_GROUP.getGroupName().getString(), "FramedBlocks");
    }
}