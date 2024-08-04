package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.neoforge.common.data.LanguageProvider;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.screen.*;
import xfacthd.framedblocks.client.screen.overlay.*;
import xfacthd.framedblocks.common.compat.atlasviewer.AtlasViewerCompat;
import xfacthd.framedblocks.common.compat.jade.JadeCompat;
import xfacthd.framedblocks.common.compat.jei.JeiMessages;
import xfacthd.framedblocks.common.config.*;
import xfacthd.framedblocks.client.util.KeyMappings;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.special.FramingSawBlock;
import xfacthd.framedblocks.common.block.special.PoweredFramingSawBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeMatchResult;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;

public final class FramedLanguageProvider extends LanguageProvider
{
    public FramedLanguageProvider(PackOutput output)
    {
        super(output, FramedConstants.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations()
    {
        addFramedBlockTranslations();
        addSpecialBlockTranslations();
        addItemTranslations();
        addSpecialTranslations();
        addStatusMessageTranslations();
        addScreenTranslations();
        addTooltipTranslations();
        addOverlayTranslations();
        addConfigTranslations();
    }

    private void addFramedBlockTranslations()
    {
        add(FBContent.BLOCK_FRAMED_CUBE.value(), "Framed Cube");
        add(FBContent.BLOCK_FRAMED_SLOPE.value(), "Framed Slope");
        add(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE.value(), "Framed Double Slope");
        add(FBContent.BLOCK_FRAMED_HALF_SLOPE.value(), "Framed Half Slope");
        add(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.value(), "Framed Half Slope");
        add(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.value(), "Framed Divided Slope");
        add(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.value(), "Framed Double Half Slope");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.value(), "Framed Double Half Slope");
        add(FBContent.BLOCK_FRAMED_CORNER_SLOPE.value(), "Framed Corner Slope");
        add(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.value(), "Framed Inner Corner Slope");
        add(FBContent.BLOCK_FRAMED_DOUBLE_CORNER.value(), "Framed Double Corner");
        add(FBContent.BLOCK_FRAMED_PRISM_CORNER.value(), "Framed Prism Corner");
        add(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.value(), "Framed Inner Prism Corner");
        add(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER.value(), "Framed Double Prism Corner");
        add(FBContent.BLOCK_FRAMED_THREEWAY_CORNER.value(), "Framed Threeway Corner");
        add(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER.value(), "Framed Inner Threeway Corner");
        add(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER.value(), "Framed Double Threeway Corner");
        add(FBContent.BLOCK_FRAMED_SLOPE_EDGE.value(), "Framed Slope Edge");
        add(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_EDGE.value(), "Framed Elevated Slope Edge");
        add(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_EDGE.value(), "Framed Elevated Double Slope Edge");
        add(FBContent.BLOCK_FRAMED_STACKED_SLOPE_EDGE.value(), "Framed Stacked Slope Edge");
        add(FBContent.BLOCK_FRAMED_SLAB.value(), "Framed Slab");
        add(FBContent.BLOCK_FRAMED_DOUBLE_SLAB.value(), "Framed Double Slab");
        add(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_SLAB.value(), "Framed Adjustable Double Slab");
        add(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_SLAB.value(), "Framed Adjustable Double Copycat Slab");
        add(FBContent.BLOCK_FRAMED_DIVIDED_SLAB.value(), "Framed Divided Slab");
        add(FBContent.BLOCK_FRAMED_SLAB_EDGE.value(), "Framed Slab Edge");
        add(FBContent.BLOCK_FRAMED_SLAB_CORNER.value(), "Framed Slab Corner");
        add(FBContent.BLOCK_FRAMED_PANEL.value(), "Framed Panel");
        add(FBContent.BLOCK_FRAMED_DOUBLE_PANEL.value(), "Framed Double Panel");
        add(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_PANEL.value(), "Framed Adjustable Double Panel");
        add(FBContent.BLOCK_FRAMED_ADJ_DOUBLE_COPYCAT_PANEL.value(), "Framed Adjustable Double Copycat Panel");
        add(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR.value(), "Framed Divided Panel (Horizontal)");
        add(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.value(), "Framed Divided Panel (Vertical)");
        add(FBContent.BLOCK_FRAMED_CORNER_PILLAR.value(), "Framed Corner Pillar");
        add(FBContent.BLOCK_FRAMED_STAIRS.value(), "Framed Stairs");
        add(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS.value(), "Framed Double Stairs");
        add(FBContent.BLOCK_FRAMED_HALF_STAIRS.value(), "Framed Half Stairs");
        add(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS.value(), "Framed Divided Stairs");
        add(FBContent.BLOCK_FRAMED_DOUBLE_HALF_STAIRS.value(), "Framed Double Half Stairs");
        add(FBContent.BLOCK_FRAMED_SLOPED_STAIRS.value(), "Framed Sloped Stairs");
        add(FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB.value(), "Framed Sliced Stairs (Slab)");
        add(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL.value(), "Framed Sliced Stairs (Panel)");
        add(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value(), "Framed Vertical Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS.value(), "Framed Vertical Double Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value(), "Framed Vertical Half Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS.value(), "Framed Vertical Divided Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS.value(), "Framed Vertical Double Half Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS.value(), "Framed Vertical Sliced Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS.value(), "Framed Vertical Sloped Stairs");
        add(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR.value(), "Framed Threeway Corner Pillar");
        add(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR.value(), "Framed Double Threeway Corner Pillar");
        add(FBContent.BLOCK_FRAMED_WALL.value(), "Framed Wall");
        add(FBContent.BLOCK_FRAMED_FENCE.value(), "Framed Fence");
        add(FBContent.BLOCK_FRAMED_FENCE_GATE.value(), "Framed Fence Gate");
        add(FBContent.BLOCK_FRAMED_DOOR.value(), "Framed Door");
        add(FBContent.BLOCK_FRAMED_IRON_DOOR.value(), "Framed Iron Door");
        add(FBContent.BLOCK_FRAMED_TRAP_DOOR.value(), "Framed Trapdoor");
        add(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.value(), "Framed Iron Trapdoor");
        add(FBContent.BLOCK_FRAMED_PRESSURE_PLATE.value(), "Framed Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE.value(), "Framed Pressure Plate");
        add(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE.value(), "Framed Stone Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE.value(), "Framed Stone Pressure Plate");
        add(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE.value(), "Framed Obsidian Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE.value(), "Framed Obsidian Pressure Plate");
        add(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE.value(), "Framed Light Weighted Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE.value(), "Framed Light Weighted Pressure Plate");
        add(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE.value(), "Framed Heavy Weighted Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE.value(), "Framed Heavy Weighted Pressure Plate");
        add(FBContent.BLOCK_FRAMED_LADDER.value(), "Framed Ladder");
        add(FBContent.BLOCK_FRAMED_BUTTON.value(), "Framed Button");
        add(FBContent.BLOCK_FRAMED_STONE_BUTTON.value(), "Framed Stone Button");
        add(FBContent.BLOCK_FRAMED_LARGE_BUTTON.value(), "Large Framed Button");
        add(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON.value(), "Large Framed Stone Button");
        add(FBContent.BLOCK_FRAMED_LEVER.value(), "Framed Lever");
        add(FBContent.BLOCK_FRAMED_SIGN.value(), "Framed Sign");
        add(FBContent.BLOCK_FRAMED_WALL_SIGN.value(), "Framed Sign");
        add(FBContent.BLOCK_FRAMED_HANGING_SIGN.value(), "Framed Hanging Sign");
        add(FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN.value(), "Framed Hanging Sign");
        add(FBContent.BLOCK_FRAMED_TORCH.value(), "Framed Torch"); //Wall torch name is handled through WallTorchBlock
        add(FBContent.BLOCK_FRAMED_SOUL_TORCH.value(), "Framed Soul Torch"); //See above
        add(FBContent.BLOCK_FRAMED_REDSTONE_TORCH.value(), "Framed Redstone Torch"); //See above
        add(FBContent.BLOCK_FRAMED_FLOOR.value(), "Framed Floor Board");
        add(FBContent.BLOCK_FRAMED_WALL_BOARD.value(), "Framed Wall Board");
        add(FBContent.BLOCK_FRAMED_CORNER_STRIP.value(), "Framed Corner Strip");
        add(FBContent.BLOCK_FRAMED_LATTICE.value(), "Framed Lattice");
        add(FBContent.BLOCK_FRAMED_THICK_LATTICE.value(), "Framed Thick Lattice");
        add(FBContent.BLOCK_FRAMED_CHEST.value(), "Framed Chest");
        add(FBContent.BLOCK_FRAMED_SECRET_STORAGE.value(), "Framed Secret Storage");
        add(FBContent.BLOCK_FRAMED_BARS.value(), "Framed Bars");
        add(FBContent.BLOCK_FRAMED_PANE.value(), "Framed Pane");
        add(FBContent.BLOCK_FRAMED_RAIL_SLOPE.value(), "Framed Rail Slope");
        add(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE.value(), "Framed Powered Rail Slope");
        add(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE.value(), "Framed Detector Rail Slope");
        add(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE.value(), "Framed Activator Rail Slope");
        add(FBContent.BLOCK_FRAMED_FANCY_RAIL.value(), "Framed Fancy Rail");
        add(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.value(), "Framed Fancy Powered Rail");
        add(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.value(), "Framed Fancy Detector Rail");
        add(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.value(), "Framed Fancy Activator Rail");
        add(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE.value(), "Framed Fancy Rail Slope");
        add(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE.value(), "Framed Fancy Powered Rail Slope");
        add(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE.value(), "Framed Fancy Detector Rail Slope");
        add(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE.value(), "Framed Fancy Activator Rail Slope");
        add(FBContent.BLOCK_FRAMED_FLOWER_POT.value(), "Framed Flower Pot");
        add(FBContent.BLOCK_FRAMED_PILLAR.value(), "Framed Pillar");
        add(FBContent.BLOCK_FRAMED_HALF_PILLAR.value(), "Framed Half Pillar");
        add(FBContent.BLOCK_FRAMED_POST.value(), "Framed Post");
        add(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.value(), "Framed Collapsible Block");
        add(FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK.value(), "Framed Collapsible Copycat Block");
        add(FBContent.BLOCK_FRAMED_BOUNCY_CUBE.value(), "Framed Bouncy Cube");
        add(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK.value(), "Framed Redstone Block");
        add(FBContent.BLOCK_FRAMED_PRISM.value(), "Framed Prism");
        add(FBContent.BLOCK_FRAMED_ELEVATED_INNER_PRISM.value(), "Framed Inner Prism");
        add(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_PRISM.value(), "Framed Double Prism");
        add(FBContent.BLOCK_FRAMED_SLOPED_PRISM.value(), "Framed Sloped Prism");
        add(FBContent.BLOCK_FRAMED_ELEVATED_INNER_SLOPED_PRISM.value(), "Framed Inner Sloped Prism");
        add(FBContent.BLOCK_FRAMED_ELEVATED_INNER_DOUBLE_SLOPED_PRISM.value(), "Framed Double Sloped Prism");
        add(FBContent.BLOCK_FRAMED_SLOPE_SLAB.value(), "Framed Slope Slab");
        add(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB.value(), "Framed Elevated Slope Slab");
        add(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_SLAB.value(), "Framed Compound Slope Slab");
        add(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB.value(), "Framed Double Slope Slab");
        add(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB.value(), "Framed Inverted Double Slope Slab");
        add(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB.value(), "Framed Elevated Double Slope Slab");
        add(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB.value(), "Framed Stacked Slope Slab");
        add(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value(), "Framed Flat Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value(), "Framed Flat Inner Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER.value(), "Framed Flat Elevated Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER.value(), "Framed Flat Elevated Inner Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER.value(), "Framed Flat Double Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER.value(), "Framed Flat Inverse Double Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER.value(), "Framed Flat Elevated Double Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER.value(), "Framed Flat Elevated Inner Double Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER.value(), "Framed Flat Stacked Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER.value(), "Framed Flat Stacked Inner Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_SLOPE_PANEL.value(), "Framed Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL.value(), "Framed Extended Slope Panel");
        add(FBContent.BLOCK_FRAMED_COMPOUND_SLOPE_PANEL.value(), "Framed Compound Slope Panel");
        add(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL.value(), "Framed Double Slope Panel");
        add(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL.value(), "Framed Inverted Double Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL.value(), "Framed Extended Double Slope Panel");
        add(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL.value(), "Framed Stacked Slope Panel");
        add(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.value(), "Framed Flat Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.value(), "Framed Flat Inner Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER.value(), "Framed Flat Extended Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER.value(), "Framed Flat Extended Inner Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER.value(), "Framed Flat Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER.value(), "Framed Flat Inverse Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER.value(), "Framed Flat Extended Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER.value(), "Framed Flat Extended Inner Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER.value(), "Framed Flat Stacked Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER.value(), "Framed Flat Stacked Inner Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL.value(), "Framed Small Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL.value(), "Framed Small Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL.value(), "Framed Large Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL.value(), "Framed Large Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL.value(), "Framed Small Inner Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL.value(), "Framed Small Inner Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL.value(), "Framed Large Inner Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL.value(), "Framed Large Inner Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL.value(), "Framed Small Double Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_WALL.value(), "Framed Small Double Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL.value(), "Framed Large Double Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL.value(), "Framed Large Double Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL.value(), "Framed Inverse Double Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL_WALL.value(), "Framed Inverse Double Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL.value(), "Framed Extended Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL.value(), "Framed Extended Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL.value(), "Framed Extended Inner Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL.value(), "Framed Extended Inner Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL.value(), "Framed Extended Double Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL.value(), "Framed Extended Double Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL.value(), "Framed Extended Inner Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL.value(), "Framed Extended Inner Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL.value(), "Framed Stacked Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL.value(), "Framed Stacked Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL.value(), "Framed Stacked Inner Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_WALL.value(), "Framed Stacked Inner Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_GLOWING_CUBE.value(), "Framed Glowing Cube");
        add(FBContent.BLOCK_FRAMED_PYRAMID.value(), "Framed Pyramid");
        add(FBContent.BLOCK_FRAMED_PYRAMID_SLAB.value(), "Framed Pyramid Slab");
        add(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE.value(), "Framed Horizontal Pane");
        add(FBContent.BLOCK_FRAMED_TARGET.value(), "Framed Target");
        add(FBContent.BLOCK_FRAMED_GATE.value(), "Framed Gate");
        add(FBContent.BLOCK_FRAMED_IRON_GATE.value(), "Framed Iron Gate");
        add(FBContent.BLOCK_FRAMED_ITEM_FRAME.value(), "Framed Item Frame");
        add(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME.value(), "Framed Glow Item Frame");
        add(FBContent.BLOCK_FRAMED_MINI_CUBE.value(), "Framed Mini Cube");
        add(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW.value(), "Framed One-Way Window");
        add(FBContent.BLOCK_FRAMED_BOOKSHELF.value(), "Framed Bookshelf");
        add(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF.value(), "Framed Chiseled Bookshelf");
        add(FBContent.BLOCK_FRAMED_CENTERED_SLAB.value(), "Framed Centered Slab");
        add(FBContent.BLOCK_FRAMED_CENTERED_PANEL.value(), "Framed Centered Panel");
        add(FBContent.BLOCK_FRAMED_MASONRY_CORNER_SEGMENT.value(), "Framed Masonry Corner Segment");
        add(FBContent.BLOCK_FRAMED_MASONRY_CORNER.value(), "Framed Masonry Corner");
        add(FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT.value(), "Framed Checkered Cube Segment");
        add(FBContent.BLOCK_FRAMED_CHECKERED_CUBE.value(), "Framed Checkered Cube");
        add(FBContent.BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT.value(), "Framed Checkered Slab Segment");
        add(FBContent.BLOCK_FRAMED_CHECKERED_SLAB.value(), "Framed Checkered Slab");
        add(FBContent.BLOCK_FRAMED_CHECKERED_PANEL_SEGMENT.value(), "Framed Checkered Panel Segment");
        add(FBContent.BLOCK_FRAMED_CHECKERED_PANEL.value(), "Framed Checkered Panel");
        add(FBContent.BLOCK_FRAMED_TUBE.value(), "Framed Tube");
    }

    private void addSpecialBlockTranslations()
    {
        add(FBContent.BLOCK_FRAMING_SAW.value(), "Framing Saw");
        add(FBContent.BLOCK_POWERED_FRAMING_SAW.value(), "Powered Framing Saw");
    }

    private void addItemTranslations()
    {
        add(FBContent.ITEM_FRAMED_HAMMER.value(), "Framed Hammer");
        add(FBContent.ITEM_FRAMED_WRENCH.value(), "Framed Wrench");
        add(FBContent.ITEM_FRAMED_BLUEPRINT.value(), "Framed Blueprint");
        add(FBContent.ITEM_FRAMED_KEY.value(), "Framed Key");
        add(FBContent.ITEM_FRAMED_SCREWDRIVER.value(), "Framed Screwdriver");
        add(FBContent.ITEM_FRAMED_REINFORCEMENT.value(), "Framed Reinforcement");
        add("item.framedblocks.framing_saw_pattern", "Framing Saw Pattern");
    }

    private void addSpecialTranslations()
    {
        add(KeyMappings.KEY_CATEGORY, "FramedBlocks");
        add(KeyMappings.KEYMAPPING_UPDATE_CULLING.get().getName(), "Update culling cache");
        add(KeyMappings.KEYMAPPING_WIPE_CACHE.get().getName(), "Clear model cache");

        add(FBContent.MAIN_TAB.value().getDisplayName(), "FramedBlocks");

        add(EmptyCamoContainer.CAMO_NAME, "Empty");

        add(JeiMessages.MSG_INVALID_RECIPE, "Invalid recipe");
        add(JeiMessages.MSG_TRANSFER_NOT_IMPLEMENTED, "Transfer not implemented, no items will be transferred");

        add(AtlasViewerCompat.LABEL_TEXTURE, "Texture");
        add(AtlasViewerCompat.LABEL_FRAMES, "Frames");

        add(JadeCompat.configTranslation(JadeCompat.ID_FRAMED_BLOCK), "FramedBlocks camo");
        add(JadeCompat.configTranslation(JadeCompat.ID_ITEM_FRAME), "Framed Item Frame");
        add(JadeCompat.LABEL_CAMO, "Camo: %s");
        add(JadeCompat.LABEL_CAMO_ONE, "Camo one: %s");
        add(JadeCompat.LABEL_CAMO_TWO, "Camo two: %s");

        add(Utils.TOOL_WRENCH, "Wrenches");
        add(Utils.DISABLE_INTANGIBLE, "Disable Intangibility");
    }

    private void addStatusMessageTranslations()
    {
        add(FramedBlockEntity.MSG_BLACKLISTED, "This block is disallowed as a camo!");
        add(FramedBlockEntity.MSG_BLOCK_ENTITY, "Blocks with BlockEntities cannot be inserted into framed blocks!");
        add(FramedBlockEntity.MSG_NON_SOLID, "Untagged non-solid blocks cannot be inserted into framed blocks!");

        add(IFramedBlock.LOCK_MESSAGE, "The state of this block is now %s");
    }

    private void addScreenTranslations()
    {
        add(FramedChestBlockEntity.TITLE, "Framed Chest");
        add(FramedStorageBlockEntity.TITLE, "Framed Secret Storage");

        add(FramedSignScreen.TITLE, "Edit sign");

        add(FramingSawBlock.SAW_MENU_TITLE, "Framing Saw");
        add(PoweredFramingSawBlock.POWERED_SAW_MENU_TITLE, "Powered Framing Saw");
        add(FramingSawScreen.TOOLTIP_MATERIAL, "Material value: %s");
        add(FramingSawScreen.TOOLTIP_LOOSE_ADDITIVE, "Item was crafted with additive ingredients, these will be lost");
        add(FramingSawScreen.TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM, "Have %s, but need %s");
        add(FramingSawScreen.TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_MULTI, "Have %s, but need %s or listed alternatives");
        add(FramingSawScreen.TOOLTIP_HAVE_X_BUT_NEED_Y_TAG, "Have %s, but need any %s");
        add(FramingSawScreen.TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_COUNT, "Have %s item(s), but need at least %s item(s)");
        add(FramingSawScreen.TOOLTIP_HAVE_X_BUT_NEED_Y_MATERIAL_COUNT, "Have %s material, but need at least %s material");
        add(FramingSawScreen.TOOLTIP_OUTPUT_COUNT, "Result size: %s, max size: %s");
        add(FramingSawScreen.TOOLTIP_HAVE_ITEM_NONE, "none");
        add(FramingSawScreen.TOOLTIP_PRESS_TO_SHOW, "Press [%s] to show all possible items");
        add(FramingSawScreen.MSG_HINT_SEARCH, "Search...");
        add(FramingSawWithEncoderScreen.TOOLTIP_TAB_CRAFTING, "Crafting");
        add(FramingSawWithEncoderScreen.TOOLTIP_TAB_PATTERN, "AE2 Pattern Encoding");
        add(PoweredFramingSawScreen.TITLE_TARGETBLOCK, "Target:");
        add(PoweredFramingSawScreen.MSG_STATUS, "Status: ");
        add(PoweredFramingSawScreen.MSG_STATUS_NO_RECIPE, "No recipe");
        add(PoweredFramingSawScreen.MSG_STATUS_NO_MATCH, "Recipe doesn't match");
        add(PoweredFramingSawScreen.MSG_STATUS_READY, "Ready");
        add(PoweredFramingSawScreen.TOOLTIP_STATUS_NO_RECIPE, "No recipe selected, click the target slot with any framed block to select a recipe");
        add(PoweredFramingSawScreen.TOOLTIP_ENERGY, "%s / %s FE");
        add(FramingSawRecipeMatchResult.SUCCESS.translation(), "Craftable");
        add(FramingSawRecipeMatchResult.MATERIAL_VALUE.translation(), "Insufficient input material available");
        add(FramingSawRecipeMatchResult.MATERIAL_LCM.translation(), "Too few input items to evenly convert to this output");
        add(FramingSawRecipeMatchResult.OUTPUT_SIZE.translation(), "Result count exceeds maximum result stack size");
        add(FramingSawRecipeMatchResult.MISSING_ADDITIVE_0.translation(), "Missing additive ingredient in the first slot");
        add(FramingSawRecipeMatchResult.MISSING_ADDITIVE_1.translation(), "Missing additive ingredient in the second slot");
        add(FramingSawRecipeMatchResult.MISSING_ADDITIVE_2.translation(), "Missing additive ingredient in the third slot");
        add(FramingSawRecipeMatchResult.UNEXPECTED_ADDITIVE_0.translation(), "Unexpected additive ingredient present in the first slot");
        add(FramingSawRecipeMatchResult.UNEXPECTED_ADDITIVE_1.translation(), "Unexpected additive ingredient present in the second slot");
        add(FramingSawRecipeMatchResult.UNEXPECTED_ADDITIVE_2.translation(), "Unexpected additive ingredient present in the third slot");
        add(FramingSawRecipeMatchResult.INCORRECT_ADDITIVE_0.translation(), "Incorrect additive ingredient present in the first slot");
        add(FramingSawRecipeMatchResult.INCORRECT_ADDITIVE_1.translation(), "Incorrect additive ingredient present in the second slot");
        add(FramingSawRecipeMatchResult.INCORRECT_ADDITIVE_2.translation(), "Incorrect additive ingredient present in the third slot");
        add(FramingSawRecipeMatchResult.INSUFFICIENT_ADDITIVE_0.translation(), "Insufficient amount of additive ingredient present in the first slot");
        add(FramingSawRecipeMatchResult.INSUFFICIENT_ADDITIVE_1.translation(), "Insufficient amount of additive ingredient present in the second slot");
        add(FramingSawRecipeMatchResult.INSUFFICIENT_ADDITIVE_2.translation(), "Insufficient amount of additive ingredient present in the third slot");
    }

    private void addTooltipTranslations()
    {
        add(FramedBlueprintItem.CONTAINED_BLOCK, "Contained Block: %s");
        add(FramedBlueprintItem.CAMO_BLOCK, "Camo Block: %s");
        add(FramedBlueprintItem.IS_ILLUMINATED, "Illuminated: %s");
        add(FramedBlueprintItem.IS_INTANGIBLE, "Intangible: %s");
        add(FramedBlueprintItem.IS_REINFORCED, "Reinforced: %s");
        add(FramedBlueprintItem.MISSING_MATERIALS, "[Framed Blueprint] Missing required materials:");
        add(FramedBlueprintItem.BLOCK_NONE, "None");
        add(FramedBlueprintItem.BLOCK_INVALID, "Invalid");
        add(FramedBlueprintItem.FALSE, "false");
        add(FramedBlueprintItem.TRUE, "true");
        add(FramedBlueprintItem.CANT_COPY, "[Framed Blueprint] This block can currently not be copied!");
        add(FramedBlueprintItem.CANT_PLACE_FLUID_CAMO, "[Framed Blueprint] Copying blocks with fluid camos is currently not possible!");
        add(IFramedBlock.CAMO_LABEL, "Camo: %s");
    }

    private void addOverlayTranslations()
    {
        add(StateLockOverlay.LOCK_MESSAGE, "State %s");
        add(IFramedBlock.STATE_LOCKED, "locked");
        add(IFramedBlock.STATE_UNLOCKED, "unlocked");

        add(ToggleWaterloggableOverlay.MSG_IS_WATERLOGGABLE, "Block is waterloggable.");
        add(ToggleWaterloggableOverlay.MSG_IS_NOT_WATERLOGGABLE, "Block is not waterloggable.");
        add(ToggleWaterloggableOverlay.MSG_MAKE_WATERLOGGABLE, "Hit with a Framed Hammer to make waterloggable");
        add(ToggleWaterloggableOverlay.MSG_MAKE_NOT_WATERLOGGABLE, "Hit with a Framed Hammer to make not waterloggable");

        add(ToggleYSlopeOverlay.SLOPE_MESSAGE, "Block uses %s faces for vertical sloped faces.");
        add(ToggleYSlopeOverlay.TOGGLE_MESSAGE, "Hit with a Framed Wrench to switch to %s faces");
        add(ToggleYSlopeOverlay.SLOPE_HOR, "horizontal");
        add(ToggleYSlopeOverlay.SLOPE_VERT, "vertical");
        add(ToggleYSlopeOverlay.SLOPE_MESSAGE_ALT, "Block uses the %s face for horizontal sloped faces.");
        add(ToggleYSlopeOverlay.TOGGLE_MESSAGE_ALT, "Hit with a Framed Wrench to switch to the %s face");
        add(ToggleYSlopeOverlay.SLOPE_FRONT, "front");
        add(ToggleYSlopeOverlay.SLOPE_SIDE, "right");

        add(ReinforcementOverlay.REINFORCE_MESSAGE, "Block is %s.");
        add(ReinforcementOverlay.STATE_NOT_REINFORCED, "not reinforced");
        add(ReinforcementOverlay.STATE_REINFORCED, "reinforced");

        add(PrismOffsetOverlay.PRISM_OFFSET_FALSE, "Triangle texture is not offset.");
        add(PrismOffsetOverlay.PRISM_OFFSET_TRUE, "Triangle texture is offset by half a block.");
        add(PrismOffsetOverlay.MSG_SWITCH_OFFSET, "Hit with a Framed Hammer to toggle the offset");

        add(SplitLineOverlay.SPLIT_LINE_FALSE, "Split-line of the deformed face runs along the steep diagonal.");
        add(SplitLineOverlay.SPLIT_LINE_TRUE, "Split-line of the deformed face runs along the shallow diagonal.");
        add(SplitLineOverlay.MSG_SWITCH_SPLIT_LINE, "Hit with a Framed Hammer to switch the orientation of the split-line");

        add(OneWayWindowOverlay.LINE_CURR_FACE, "Current see-through side: %s");
        add(OneWayWindowOverlay.LINE_SET_FACE, "Hit with a Framed Wrench to set the see-through side to %s");
        add(OneWayWindowOverlay.LINE_CLEAR_FACE, "Hit with a Framed Wrench while crouching to clear see-through side");
        add(OneWayWindowOverlay.FACE_VALUE_LINES[NullableDirection.NONE.ordinal()], "None");
        add(OneWayWindowOverlay.FACE_VALUE_LINES[NullableDirection.DOWN.ordinal()], "Down");
        add(OneWayWindowOverlay.FACE_VALUE_LINES[NullableDirection.UP.ordinal()], "Up");
        add(OneWayWindowOverlay.FACE_VALUE_LINES[NullableDirection.NORTH.ordinal()], "North");
        add(OneWayWindowOverlay.FACE_VALUE_LINES[NullableDirection.SOUTH.ordinal()], "South");
        add(OneWayWindowOverlay.FACE_VALUE_LINES[NullableDirection.WEST.ordinal()], "West");
        add(OneWayWindowOverlay.FACE_VALUE_LINES[NullableDirection.EAST.ordinal()], "East");
        add(OneWayWindowOverlay.DIR_VALUE_LINES[Direction.DOWN.ordinal()], "Down");
        add(OneWayWindowOverlay.DIR_VALUE_LINES[Direction.UP.ordinal()], "Up");
        add(OneWayWindowOverlay.DIR_VALUE_LINES[Direction.NORTH.ordinal()], "North");
        add(OneWayWindowOverlay.DIR_VALUE_LINES[Direction.SOUTH.ordinal()], "South");
        add(OneWayWindowOverlay.DIR_VALUE_LINES[Direction.WEST.ordinal()], "West");
        add(OneWayWindowOverlay.DIR_VALUE_LINES[Direction.EAST.ordinal()], "East");
        add(OneWayWindowOverlay.FACE_VALUE_ABBRS[NullableDirection.NONE.ordinal()], "-");
        add(OneWayWindowOverlay.FACE_VALUE_ABBRS[NullableDirection.DOWN.ordinal()], "D");
        add(OneWayWindowOverlay.FACE_VALUE_ABBRS[NullableDirection.UP.ordinal()], "U");
        add(OneWayWindowOverlay.FACE_VALUE_ABBRS[NullableDirection.NORTH.ordinal()], "N");
        add(OneWayWindowOverlay.FACE_VALUE_ABBRS[NullableDirection.SOUTH.ordinal()], "S");
        add(OneWayWindowOverlay.FACE_VALUE_ABBRS[NullableDirection.WEST.ordinal()], "W");
        add(OneWayWindowOverlay.FACE_VALUE_ABBRS[NullableDirection.EAST.ordinal()], "E");

        add(FrameBackgroundOverlay.LINE_USE_CAMO_BG, "Framed Item Frame uses the camo as background");
        add(FrameBackgroundOverlay.LINE_USE_LEATHER_BG, "Framed Item Frame uses leather as background");
        add(FrameBackgroundOverlay.LINE_SET_CAMO_BG, "Hit with a Framed Hammer to use the camo as background");
        add(FrameBackgroundOverlay.LINE_SET_LEATHER_BG, "Hit with a Framed Hammer to use leather as background");

        add(CamoRotationOverlay.ROTATEABLE_FALSE, "The targetted camo cannot be rotated");
        add(CamoRotationOverlay.ROTATEABLE_TRUE, "The targetted camo can be rotated");
    }

    private void addConfigTranslations()
    {
        add("framedblocks.configuration.general", "General");
        add("framedblocks.configuration.powered_framing_saw", "Powered Framing Saw");
        add(ServerConfig.TRANSLATION_ALLOW_BLOCK_ENTITIES, "Allow BlockEntities");
        add(ServerConfig.TRANSLATION_ENABLE_INTANGIBILITY, "Enable intangibility feature");
        add(ServerConfig.TRANSLATION_INTANGIBLE_MARKER, "Intangibility marker item");
        add(ServerConfig.TRANSLATION_ONE_WAY_WINDOW_OWNABLE, "One-Way Window ownability");
        add(ServerConfig.TRANSLATION_CONSUME_CAMO_ITEM, "Consume camo item");
        add(ServerConfig.TRANSLATION_GLOWSTONE_LIGHT_LEVEL, "Glowstone Light Level");
        add(ServerConfig.TRANSLATION_FIREPROOF_BLOCKS, "Fireproof blocks");
        add(ServerConfig.TRANSLATION_POWERED_SAW_ENERGY_CAPACITY, "Energy Capacity");
        add(ServerConfig.TRANSLATION_POWERED_SAW_MAX_RECEIVE, "Max input");
        add(ServerConfig.TRANSLATION_POWERED_SAW_CONSUMPTION, "Consumption");
        add(ServerConfig.TRANSLATION_POWERED_SAW_RECIPE_DURATION, "Crafting Duration");

        //add("framedblocks.configuration.general", "General");
        add("framedblocks.configuration.overlay", "Overlays");
        add(ClientConfig.TRANSLATION_SHOW_GHOST_BLOCKS, "Show ghost blocks");
        add(ClientConfig.TRANSLATION_ALT_GHOST_RENDERER, "Use alternative placement preview renderer");
        add(ClientConfig.TRANSLATION_GHOST_RENDER_OPACITY, "Placement preview opacity");
        add(ClientConfig.TRANSLATION_FANCY_HITBOXES, "Fancy hitboxes");
        add(ClientConfig.TRANSLATION_DETAILED_CULLING, "Detailed culling");
        add(ClientConfig.TRANSLATION_USE_DISCRETE_UV_STEPS, "Use discrete UV steps");
        add(ClientConfig.TRANSLATION_CON_TEX_MODE, "Connected textures mode");
        add(ClientConfig.TRANSLATION_CAMO_MESSAGE_VERBOSITY, "Disallowed camo message verbosity");
        add(ClientConfig.TRANSLATION_FORCE_AO_ON_GLOWING_BLOCKS, "Force ambient occlusion on glowing framed blocks");
        add(ClientConfig.TRANSLATION_RENDER_ITEM_MODELS_WITH_CAMO, "Render item models with camo");
        add(ClientConfig.TRANSLATION_SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI, "Show all Framing Saw recipe permutations in EMI");
        add(ClientConfig.TRANSLATION_SOLID_FRAME_MODE, "Solid frame mode");
        add(ClientConfig.TRANSLATION_SHOW_BUTTON_PLATE_OVERLAY, "Show button and pressure plate type overlay");
        add(ClientConfig.TRANSLATION_SHOW_SPECIAL_CUBE_OVERLAY, "Show special cube type overlay");
        add(ClientConfig.TRANSLATION_RENDER_CAMO_IN_JADE, "Render camo in Jade overlay");
        add(ClientConfig.TRANSLATION_STATE_LOCK_MODE, "State lock overlay: Display mode");
        add(ClientConfig.TRANSLATION_TOGGLE_WATERLOG_MODE, "Toggle waterloggable overlay: Display mode");
        add(ClientConfig.TRANSLATION_TOGGLE_Y_SLOPE_MODE, "Toggle Y slope overlay: Display mode");
        add(ClientConfig.TRANSLATION_REINFORCEMENT_MODE, "Reinforcement overlay: Display mode");
        add(ClientConfig.TRANSLATION_PRISM_OFFSET_MODE, "Prism offset overlay: Display mode");
        add(ClientConfig.TRANSLATION_SPLIT_LINES_MODE, "Collapsible block split lines overlay: Display mode");
        add(ClientConfig.TRANSLATION_ONE_WAY_WINDOW_MODE, "One-Way Window overlay: Display mode");
        add(ClientConfig.TRANSLATION_FRAME_BACKGROUND_MODE, "Item Frame Background overlay: Display mode");
        add(ClientConfig.TRANSLATION_CAMO_ROTATION_MODE, "Camo Rotation overlay: Display mode");

        add("framedblocks.configuration.section.framedblocks.devtools.toml", "Dev Tools Settings");
        add("framedblocks.configuration.section.framedblocks.devtools.toml.title", "FramedBlocks Dev Tools Configuration");
        add(DevToolsConfig.TRANSLATION_DOUBLE_BLOCK_PART_DEBUG, "Double-block part debug");
        add(DevToolsConfig.TRANSLATION_CONNECTION_DEBUG, "ConnectionPredicate debug");
        add(DevToolsConfig.TRANSLATION_QUAD_WINDING_DEBUG, "Quad-winding debug");
        add(DevToolsConfig.TRANSLATION_STATE_MERGER_DEBUG, "StateMerger debug");
        add(DevToolsConfig.TRANSLATION_STATE_MERGER_DEBUG_FILTER, "StateMerger debug filter");
        add(DevToolsConfig.TRANSLATION_OCCLUSION_SHAPE_DEBUG, "Occlusion shape debug");
    }

    private void add(Component key, String value)
    {
        ComponentContents contents = key.getContents();
        if (contents instanceof TranslatableContents translatable)
        {
            add(translatable.getKey(), value);
        }
        else
        {
            add(key.getString(), value);
        }
    }
}