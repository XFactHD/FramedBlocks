package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.common.data.LanguageProvider;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.client.screen.*;
import xfacthd.framedblocks.client.screen.overlay.*;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.client.util.KeyMappings;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramingSawBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import xfacthd.framedblocks.common.compat.jei.JeiCompat;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeMatchResult;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.util.*;

public final class FramedLanguageProvider extends LanguageProvider
{
    public FramedLanguageProvider(PackOutput output) { super(output, FramedConstants.MOD_ID, "en_us"); }

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
        add(FBContent.BLOCK_FRAMED_CUBE.get(), "Framed Cube");
        add(FBContent.BLOCK_FRAMED_SLOPE.get(), "Framed Slope");
        add(FBContent.BLOCK_FRAMED_CORNER_SLOPE.get(), "Framed Corner Slope");
        add(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE.get(), "Framed Inner Corner Slope");
        add(FBContent.BLOCK_FRAMED_PRISM_CORNER.get(), "Framed Prism Corner");
        add(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER.get(), "Framed Inner Prism Corner");
        add(FBContent.BLOCK_FRAMED_THREEWAY_CORNER.get(), "Framed Threeway Corner");
        add(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER.get(), "Framed Inner Threeway Corner");
        add(FBContent.BLOCK_FRAMED_SLAB.get(), "Framed Slab");
        add(FBContent.BLOCK_FRAMED_SLAB_EDGE.get(), "Framed Slab Edge");
        add(FBContent.BLOCK_FRAMED_SLAB_CORNER.get(), "Framed Slab Corner");
        add(FBContent.BLOCK_FRAMED_DIVIDED_SLAB.get(), "Framed Divided Slab");
        add(FBContent.BLOCK_FRAMED_PANEL.get(), "Framed Panel");
        add(FBContent.BLOCK_FRAMED_CORNER_PILLAR.get(), "Framed Corner Pillar");
        add(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR.get(), "Framed Divided Panel (Horizontal)");
        add(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.get(), "Framed Divided Panel (Vertical)");
        add(FBContent.BLOCK_FRAMED_STAIRS.get(), "Framed Stairs");
        add(FBContent.BLOCK_FRAMED_WALL.get(), "Framed Wall");
        add(FBContent.BLOCK_FRAMED_FENCE.get(), "Framed Fence");
        add(FBContent.BLOCK_FRAMED_FENCE_GATE.get(), "Framed Fence Gate");
        add(FBContent.BLOCK_FRAMED_DOOR.get(), "Framed Door");
        add(FBContent.BLOCK_FRAMED_IRON_DOOR.get(), "Framed Iron Door");
        add(FBContent.BLOCK_FRAMED_TRAP_DOOR.get(), "Framed Trapdoor");
        add(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get(), "Framed Iron Trapdoor");
        add(FBContent.BLOCK_FRAMED_PRESSURE_PLATE.get(), "Framed Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE.get(), "Framed Pressure Plate");
        add(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE.get(), "Framed Stone Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE.get(), "Framed Stone Pressure Plate");
        add(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE.get(), "Framed Obsidian Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE.get(), "Framed Obsidian Pressure Plate");
        add(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE.get(), "Framed Light Weighted Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE.get(), "Framed Light Weighted Pressure Plate");
        add(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE.get(), "Framed Heavy Weighted Pressure Plate");
        add(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE.get(), "Framed Heavy Weighted Pressure Plate");
        add(FBContent.BLOCK_FRAMED_LADDER.get(), "Framed Ladder");
        add(FBContent.BLOCK_FRAMED_BUTTON.get(), "Framed Button");
        add(FBContent.BLOCK_FRAMED_STONE_BUTTON.get(), "Framed Stone Button");
        add(FBContent.BLOCK_FRAMED_LEVER.get(), "Framed Lever");
        add(FBContent.BLOCK_FRAMED_SIGN.get(), "Framed Sign");
        add(FBContent.BLOCK_FRAMED_WALL_SIGN.get(), "Framed Sign");
        add(FBContent.BLOCK_FRAMED_DOUBLE_SLAB.get(), "Framed Double Slab");
        add(FBContent.BLOCK_FRAMED_DOUBLE_PANEL.get(), "Framed Double Panel");
        add(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE.get(), "Framed Double Slope");
        add(FBContent.BLOCK_FRAMED_DOUBLE_CORNER.get(), "Framed Double Corner");
        add(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER.get(), "Framed Double Prism Corner");
        add(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER.get(), "Framed Double Threeway Corner");
        add(FBContent.BLOCK_FRAMED_TORCH.get(), "Framed Torch"); //Wall torch name is handled through WallTorchBlock
        add(FBContent.BLOCK_FRAMED_SOUL_TORCH.get(), "Framed Soul Torch"); //See above
        add(FBContent.BLOCK_FRAMED_REDSTONE_TORCH.get(), "Framed Redstone Torch"); //See above
        add(FBContent.BLOCK_FRAMED_FLOOR.get(), "Framed Floor Board");
        add(FBContent.BLOCK_FRAMED_LATTICE.get(), "Framed Lattice");
        add(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.get(), "Framed Vertical Stairs");
        add(FBContent.BLOCK_FRAMED_CHEST.get(), "Framed Chest");
        add(FBContent.BLOCK_FRAMED_BARS.get(), "Framed Bars");
        add(FBContent.BLOCK_FRAMED_PANE.get(), "Framed Pane");
        add(FBContent.BLOCK_FRAMED_RAIL_SLOPE.get(), "Framed Rail Slope");
        add(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE.get(), "Framed Powered Rail Slope");
        add(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE.get(), "Framed Detector Rail Slope");
        add(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE.get(), "Framed Activator Rail Slope");
        add(FBContent.BLOCK_FRAMED_FLOWER_POT.get(), "Framed Flower Pot");
        add(FBContent.BLOCK_FRAMED_PILLAR.get(), "Framed Pillar");
        add(FBContent.BLOCK_FRAMED_HALF_PILLAR.get(), "Framed Half Pillar");
        add(FBContent.BLOCK_FRAMED_POST.get(), "Framed Post");
        add(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.get(), "Framed Collapsible Block");
        add(FBContent.BLOCK_FRAMED_HALF_STAIRS.get(), "Framed Half Stairs");
        add(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS.get(), "Framed Divided Stairs");
        add(FBContent.BLOCK_FRAMED_BOUNCY_CUBE.get(), "Framed Bouncy Cube");
        add(FBContent.BLOCK_FRAMED_SECRET_STORAGE.get(), "Framed Secret Storage");
        add(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK.get(), "Framed Redstone Block");
        add(FBContent.BLOCK_FRAMED_PRISM.get(), "Framed Prism");
        add(FBContent.BLOCK_FRAMED_INNER_PRISM.get(), "Framed Inner Prism");
        add(FBContent.BLOCK_FRAMED_DOUBLE_PRISM.get(), "Framed Double Prism");
        add(FBContent.BLOCK_FRAMED_SLOPED_PRISM.get(), "Framed Sloped Prism");
        add(FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM.get(), "Framed Inner Sloped Prism");
        add(FBContent.BLOCK_FRAMED_DOUBLE_SLOPED_PRISM.get(), "Framed Double Sloped Prism");
        add(FBContent.BLOCK_FRAMED_SLOPE_SLAB.get(), "Framed Slope Slab");
        add(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB.get(), "Framed Elevated Slope Slab");
        add(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB.get(), "Framed Double Slope Slab");
        add(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB.get(), "Framed Inverted Double Slope Slab");
        add(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB.get(), "Framed Elevated Double Slope Slab");
        add(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB.get(), "Framed Stacked Slope Slab");
        add(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.get(), "Framed Flat Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.get(), "Framed Flat Inner Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER.get(), "Framed Flat Elevated Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER.get(), "Framed Flat Elevated Inner Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER.get(), "Framed Flat Double Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER.get(), "Framed Flat Inverse Double Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER.get(), "Framed Flat Elevated Double Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER.get(), "Framed Flat Elevated Inner Double Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER.get(), "Framed Flat Stacked Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER.get(), "Framed Flat Stacked Inner Slope Slab Corner");
        add(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.get(), "Framed Vertical Half Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS.get(), "Framed Vertical Divided Stairs");
        add(FBContent.BLOCK_FRAMED_SLOPE_PANEL.get(), "Framed Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL.get(), "Framed Extended Slope Panel");
        add(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL.get(), "Framed Double Slope Panel");
        add(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL.get(), "Framed Inverted Double Slope Panel");
        add(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL.get(), "Framed Extended Double Slope Panel");
        add(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL.get(), "Framed Stacked Slope Panel");
        add(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.get(), "Framed Flat Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.get(), "Framed Flat Inner Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER.get(), "Framed Flat Extended Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER.get(), "Framed Flat Extended Inner Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER.get(), "Framed Flat Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER.get(), "Framed Flat Inverse Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER.get(), "Framed Flat Extended Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER.get(), "Framed Flat Extended Inner Double Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER.get(), "Framed Flat Stacked Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER.get(), "Framed Flat Stacked Inner Slope Panel Corner");
        add(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS.get(), "Framed Double Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS.get(), "Framed Vertical Double Stairs");
        add(FBContent.BLOCK_FRAMED_WALL_BOARD.get(), "Framed Wall Board");
        add(FBContent.BLOCK_FRAMED_GLOWING_CUBE.get(), "Framed Glowing Cube");
        add(FBContent.BLOCK_FRAMED_PYRAMID.get(), "Framed Pyramid");
        add(FBContent.BLOCK_FRAMED_PYRAMID_SLAB.get(), "Framed Pyramid Slab");
        add(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE.get(), "Framed Horizontal Pane");
        add(FBContent.BLOCK_FRAMED_LARGE_BUTTON.get(), "Large Framed Button");
        add(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON.get(), "Large Framed Stone Button");
        add(FBContent.BLOCK_FRAMED_TARGET.get(), "Framed Target");
        add(FBContent.BLOCK_FRAMED_GATE.get(), "Framed Gate");
        add(FBContent.BLOCK_FRAMED_IRON_GATE.get(), "Framed Iron Gate");
        add(FBContent.BLOCK_FRAMED_ITEM_FRAME.get(), "Framed Item Frame");
        add(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME.get(), "Framed Glow Item Frame");
        add(FBContent.BLOCK_FRAMED_FANCY_RAIL.get(), "Framed Fancy Rail");
        add(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.get(), "Framed Fancy Powered Rail");
        add(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.get(), "Framed Fancy Detector Rail");
        add(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.get(), "Framed Fancy Activator Rail");
        add(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE.get(), "Framed Fancy Rail Slope");
        add(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE.get(), "Framed Fancy Powered Rail Slope");
        add(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE.get(), "Framed Fancy Detector Rail Slope");
        add(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE.get(), "Framed Fancy Activator Rail Slope");
        add(FBContent.BLOCK_FRAMED_HALF_SLOPE.get(), "Framed Half Slope");
        add(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.get(), "Framed Half Slope");
        add(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE.get(), "Framed Divided Slope");
        add(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE.get(), "Framed Double Half Slope");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.get(), "Framed Double Half Slope");
        add(FBContent.BLOCK_FRAMED_SLOPED_STAIRS.get(), "Framed Sloped Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS.get(), "Framed Vertical Sloped Stairs");
        add(FBContent.BLOCK_FRAMED_MINI_CUBE.get(), "Framed Mini Cube");
        add(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW.get(), "Framed One-Way Window");
    }

    private void addSpecialBlockTranslations()
    {
        add(FBContent.BLOCK_FRAMING_SAW.get(), "Framing Saw");
    }

    private void addItemTranslations()
    {
        add(FBContent.ITEM_FRAMED_HAMMER.get(), "Framed Hammer");
        add(FBContent.ITEM_FRAMED_WRENCH.get(), "Framed Wrench");
        add(FBContent.ITEM_FRAMED_BLUEPRINT.get(), "Framed Blueprint");
        add(FBContent.ITEM_FRAMED_KEY.get(), "Framed Key");
        add(FBContent.ITEM_FRAMED_SCREWDRIVER.get(), "Framed Screwdriver");
        add(FBContent.ITEM_FRAMED_REINFORCEMENT.get(), "Framed Reinforcement");
    }

    private void addSpecialTranslations()
    {
        add(KeyMappings.KEY_CATEGORY, "FramedBlocks");
        add(KeyMappings.KEYMAPPING_UPDATE_CULLING.get().getName(), "Update culling cache");

        add(FBContent.MAIN_TAB.get().getDisplayName(), "FramedBlocks");

        add(JeiCompat.MSG_INVALID_RECIPE, "Invalid recipe");
        add(JeiCompat.MSG_TRANSFER_NOT_IMPLEMENTED, "No items will be transferred");
    }

    private void addStatusMessageTranslations()
    {
        add(FramedBlockEntity.MSG_BLACKLISTED, "This block is blacklisted!");
        add(FramedBlockEntity.MSG_BLOCK_ENTITY, "Blocks with BlockEntities cannot be inserted into framed blocks!");

        add(IFramedBlock.LOCK_MESSAGE, "The state of this block is now %s");
    }

    private void addScreenTranslations()
    {
        add(FramedChestBlockEntity.TITLE, "Framed Chest");
        add(FramedStorageBlockEntity.TITLE, "Framed Secret Storage");

        add(FramedSignScreen.TITLE, "Edit sign");
        add(FramedSignScreen.DONE, "Done");

        add(FramingSawBlock.MENU_TITLE, "Framing Saw");
        add(FramingSawScreen.TOOLTIP_MATERIAL, "Material value: %s");
        add(FramingSawScreen.TOOLTIP_LOOSE_ADDITIVE, "Item was crafted with additive ingredients, these will be lost");
        add(FramingSawScreen.TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM, "Have %s, but need %s");
        add(FramingSawScreen.TOOLTIP_HAVE_X_BUT_NEED_Y_TAG, "Have %s, but need any %s");
        add(FramingSawScreen.TOOLTIP_HAVE_X_BUT_NEED_Y_ITEM_COUNT, "Have %s item(s), but need at least %s item(s)");
        add(FramingSawScreen.TOOLTIP_HAVE_X_BUT_NEED_Y_MATERIAL_COUNT, "Have %s material, but need at least %s material");
        add(FramingSawScreen.TOOLTIP_HAVE_ITEM_NONE, "none");
        add(FramingSawScreen.TOOLTIP_PRESS_TO_SHOW, "Press [%s] to show all possible items");
        add(FramingSawRecipeMatchResult.SUCCESS.translation(), "Craftable");
        add(FramingSawRecipeMatchResult.MATERIAL_VALUE.translation(), "Insufficient input material available");
        add(FramingSawRecipeMatchResult.MATERIAL_LCM.translation(), "Too few input items to evenly convert to this output");
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
        add(FramedBlueprintItem.BLOCK_NONE, "None");
        add(FramedBlueprintItem.BLOCK_INVALID, "Invalid");
        add(FramedBlueprintItem.FALSE, "false");
        add(FramedBlueprintItem.TRUE, "true");
        add(FramedBlueprintItem.CANT_COPY, "This block can currently not be copied!");
        add(FramedBlueprintItem.CANT_PLACE_FLUID_CAMO, "Copying blocks with fluid camos is currently not possible!");
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
    }

    private void addConfigTranslations()
    {
        add(CommonConfig.TRANSLATION_FIREPROOF_BLOCKS, "Fireproof blocks");

        add(ServerConfig.TRANSLATION_ALLOW_BLOCK_ENTITIES, "Allow BlockEntities");
        add(ServerConfig.TRANSLATION_ENABLE_INTANGIBILITY, "Enable intangibility feature");
        add(ServerConfig.TRANSLATION_INTANGIBLE_MARKER, "Intangibility marker item");
        add(ServerConfig.TRANSLATION_ONE_WAY_WINDOW_OWNABLE, "One-Way Window ownability");

        add(ClientConfig.TRANSLATION_SHOW_GHOST_BLOCKS, "Show ghost blocks");
        add(ClientConfig.TRANSLATION_FANCY_HITBOXES, "Fancy hitboxes");
        add(ClientConfig.TRANSLATION_DETAILED_CULLING, "Detailed culling");
        add(ClientConfig.TRANSLATION_USE_DISCRETE_UV_STEPS, "Use discrete UV steps");
        add(ClientConfig.TRANSLATION_CON_TEX_MODE, "Connected textures mode");
        add(ClientConfig.TRANSLATION_STATE_LOCK_MODE, "State lock overlay: Display mode");
        add(ClientConfig.TRANSLATION_TOGGLE_WATERLOG_MODE, "Toggle waterloggable overlay: Display mode");
        add(ClientConfig.TRANSLATION_TOGGLE_Y_SLOPE_MODE, "Toggle Y slope overlay: Display mode");
        add(ClientConfig.TRANSLATION_REINFORCEMENT_MODE, "Reinforcement overlay: Display mode");
        add(ClientConfig.TRANSLATION_PRISM_OFFSET_MODE, "Prism offset overlay: Display mode");
        add(ClientConfig.TRANSLATION_SPLIT_LINES_MODE, "Collapsible block split lines overlay: Display mode");
        add(ClientConfig.TRANSLATION_ONE_WAY_WINDOW_MODE, "One-Way Window overlay: Display mode");
        add(ClientConfig.TRANSLATION_FRAME_BACKGROUND_MODE, "Item Frame Background overlay: Display mode");
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