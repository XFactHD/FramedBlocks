package io.github.xfacthd.framedblocks.common.datagen.providers;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.ShapeLockableBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.CamoPrinter;
import io.github.xfacthd.framedblocks.api.camo.block.SimpleBlockCamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.screen.FramingSawScreen;
import io.github.xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;
import io.github.xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import io.github.xfacthd.framedblocks.client.screen.overlay.impl.*;
import io.github.xfacthd.framedblocks.client.util.KeyMappings;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.special.FramingSawBlock;
import io.github.xfacthd.framedblocks.common.block.special.PoweredFramingSawBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedHopperBlockEntity;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import io.github.xfacthd.framedblocks.common.compat.atlasviewer.AtlasViewerCompat;
import io.github.xfacthd.framedblocks.common.compat.jade.JadeCompat;
import io.github.xfacthd.framedblocks.common.compat.jei.JeiCompat;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import io.github.xfacthd.framedblocks.common.config.DevToolsConfig;
import io.github.xfacthd.framedblocks.common.config.ServerConfig;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeMatchResult;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import io.github.xfacthd.framedblocks.common.datagen.GeneratorHandler;
import io.github.xfacthd.framedblocks.common.item.FramedBlueprintItem;
import io.github.xfacthd.framedblocks.common.item.FramedWrenchItem;
import io.github.xfacthd.framedblocks.common.item.PhantomPasteItem;
import io.github.xfacthd.framedblocks.common.item.block.FramedMirroringBlockItem;
import io.github.xfacthd.framedblocks.common.item.block.FramedTankBlockItem;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class FramedLanguageProvider extends LanguageProvider {
    private final CompletableFuture<HolderLookup.Provider> registries;
    private HolderLookup.@Nullable Provider lookup;

    public FramedLanguageProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, FramedConstants.MOD_ID, "en_us");
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return registries.thenCompose(lookup -> {
            this.lookup = lookup;
            return super.run(cache);
        });
    }

    @Override
    protected void addTranslations() {
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

    private void addFramedBlockTranslations() {
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
        add(FBContent.BLOCK_FRAMED_CORNER_SLOPE_EDGE.value(), "Framed Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE_EDGE.value(), "Framed Inner Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_ELEVATED_CORNER_SLOPE_EDGE.value(), "Framed Elevated Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_ELEVATED_INNER_CORNER_SLOPE_EDGE.value(), "Framed Elevated Inner Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_CORNER_SLOPE_EDGE.value(), "Framed Elevated Double Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_INNER_CORNER_SLOPE_EDGE.value(), "Framed Elevated Double Inner Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_EDGE.value(), "Framed Stacked Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_EDGE.value(), "Framed Stacked Inner Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_THREEWAY_CORNER_SLOPE_EDGE.value(), "Framed Threeway Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER_SLOPE_EDGE.value(), "Framed Inner Threeway Corner Slope Edge");
        add(FBContent.BLOCK_FRAMED_SLOPE_EDGE_SLAB.value(), "Framed Slope Edge Slab");
        add(FBContent.BLOCK_FRAMED_SLOPE_EDGE_PANEL.value(), "Framed Slope Edge Panel");
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
        add(FBContent.BLOCK_FRAMED_SLOPED_DOUBLE_STAIRS.value(), "Framed Sloped Double Stairs");
        add(FBContent.BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLAB.value(), "Framed Sliced Sloped Stairs (Slab)");
        add(FBContent.BLOCK_FRAMED_SLICED_SLOPED_STAIRS_SLOPE.value(), "Framed Sliced Sloped Stairs (Slope)");
        add(FBContent.BLOCK_FRAMED_SLICED_STAIRS_SLAB.value(), "Framed Sliced Stairs (Slab)");
        add(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL.value(), "Framed Sliced Stairs (Panel)");
        add(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value(), "Framed Vertical Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS.value(), "Framed Vertical Double Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value(), "Framed Vertical Half Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS.value(), "Framed Vertical Divided Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS.value(), "Framed Vertical Double Half Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS.value(), "Framed Vertical Sliced Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS.value(), "Framed Vertical Sloped Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS.value(), "Framed Vertical Sloped Double Stairs");
        add(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_PANEL.value(), "Framed Vertical Sliced Sloped Stairs (Panel)");
        add(FBContent.BLOCK_FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE.value(), "Framed Vertical Sliced Sloped Stairs (Slope)");
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
        add(FBContent.BLOCK_FRAMED_COPPER_TORCH.value(), "Framed Copper Torch"); //See above
        add(FBContent.BLOCK_FRAMED_REDSTONE_TORCH.value(), "Framed Redstone Torch"); //See above
        add(FBContent.BLOCK_FRAMED_BOARD.value(), "Framed Board");
        add(FBContent.BLOCK_FRAMED_HALF_BOARD.value(), "Framed Half Board");
        add(FBContent.BLOCK_FRAMED_DIVIDED_BOARD.value(), "Framed Divided Board");
        add(FBContent.BLOCK_FRAMED_CORNER_BOARD.value(), "Framed Corner Board");
        add(FBContent.BLOCK_FRAMED_INNER_CORNER_BOARD.value(), "Framed Inner Corner Board");
        add(FBContent.BLOCK_FRAMED_DOUBLE_CORNER_BOARD.value(), "Framed Double Corner Board");
        add(FBContent.BLOCK_FRAMED_CORNER_STRIP.value(), "Framed Corner Strip");
        add(FBContent.BLOCK_FRAMED_LATTICE.value(), "Framed Lattice");
        add(FBContent.BLOCK_FRAMED_THICK_LATTICE.value(), "Framed Thick Lattice");
        add(FBContent.BLOCK_FRAMED_CHEST.value(), "Framed Chest");
        add(FBContent.BLOCK_FRAMED_SECRET_STORAGE.value(), "Framed Secret Storage");
        add(FBContent.BLOCK_FRAMED_TANK.value(), "Framed Tank");
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
        add(FBContent.BLOCK_FRAMED_PILLAR_SOCKET.value(), "Framed Pillar Socket");
        add(FBContent.BLOCK_FRAMED_SPLIT_PILLAR_SOCKET.value(), "Framed Split Pillar Socket");
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
        add(FBContent.BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER.value(), "Framed Small Prism Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_SMALL_PRISM_SLOPE_PANEL_CORNER_WALL.value(), "Framed Small Prism Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER.value(), "Framed Large Prism Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_PRISM_SLOPE_PANEL_CORNER_WALL.value(), "Framed Large Prism Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER.value(), "Framed Small Inner Prism Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER_WALL.value(), "Framed Small Inner Prism Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER.value(), "Framed Large Inner Prism Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_LARGE_INNER_PRISM_SLOPE_PANEL_CORNER_WALL.value(), "Framed Large Inner Prism Corner Slope Panel");
        add(FBContent.BLOCK_FRAMED_PYRAMID.value(), "Framed Pyramid");
        add(FBContent.BLOCK_FRAMED_PYRAMID_SLAB.value(), "Framed Pyramid Slab");
        add(FBContent.BLOCK_FRAMED_ELEVATED_PYRAMID_SLAB.value(), "Framed Elevated Pyramid Slab");
        add(FBContent.BLOCK_FRAMED_UPPER_PYRAMID_SLAB.value(), "Framed Upper Pyramid Slab");
        add(FBContent.BLOCK_FRAMED_STACKED_PYRAMID_SLAB.value(), "Framed Stacked Pyramid Slab");
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
        add(FBContent.BLOCK_FRAMED_CORNER_TUBE.value(), "Framed Corner Tube");
        add(FBContent.BLOCK_FRAMED_CHAIN.value(), "Framed Chain");
        add(FBContent.BLOCK_FRAMED_LANTERN.value(), "Framed Lantern");
        add(FBContent.BLOCK_FRAMED_SOUL_LANTERN.value(), "Framed Soul Lantern");
        add(FBContent.BLOCK_FRAMED_COPPER_LANTERN.value(), "Framed Copper Lantern");
        add(FBContent.BLOCK_FRAMED_HOPPER.value(), "Framed Hopper");
        add(FBContent.BLOCK_FRAMED_LAYERED_CUBE.value(), "Framed Layered Cube");
        add(FBContent.BLOCK_FRAMED_LIGHTNING_ROD.value(), "Framed Lightning Rod");
        add(FBContent.BLOCK_FRAMED_PATH.value(), "Framed Path");
        add(FBContent.BLOCK_FRAMED_SHELF.value(), "Framed Shelf");
    }

    private void addSpecialBlockTranslations() {
        add(FBContent.BLOCK_FRAMING_SAW.value(), "Framing Saw");
        add(FBContent.BLOCK_POWERED_FRAMING_SAW.value(), "Powered Framing Saw");
    }

    private void addItemTranslations() {
        add(FBContent.ITEM_FRAMED_HAMMER.value(), "Framed Hammer");
        add(FBContent.ITEM_FRAMED_WRENCH.value(), "Framed Wrench");
        add(FBContent.ITEM_FRAMED_BLUEPRINT.value(), "Framed Blueprint");
        add(FBContent.ITEM_FRAMED_KEY.value(), "Framed Key");
        add(FBContent.ITEM_FRAMED_SCREWDRIVER.value(), "Framed Screwdriver");
        add(FBContent.ITEM_FRAMED_REINFORCEMENT.value(), "Framed Reinforcement");
        add(FBContent.ITEM_PHANTOM_PASTE.value(), "Phantom Paste");
        add(FBContent.ITEM_GLOW_PASTE.value(), "Glow Paste");
        add(Objects.requireNonNull(GeneratorHandler.framingSawPattern).value(), "Framing Saw Pattern");
    }

    private void addSpecialTranslations() {
        add(KeyMappings.KEY_CATEGORY.label(), "FramedBlocks");
        add(KeyMappings.KEYMAPPING_UPDATE_CULLING.get().getName(), "Update culling cache");
        add(KeyMappings.KEYMAPPING_WIPE_CACHE.get().getName(), "Clear model cache");

        add(FBContent.MAIN_TAB.value().getDisplayName(), "FramedBlocks");

        add(EmptyCamoContainer.CAMO_NAME, "Empty");

        add(JeiCompat.MSG_INVALID_RECIPE, "Invalid recipe");
        add(JeiCompat.MSG_TRANSFER_NOT_IMPLEMENTED, "Transfer not implemented, no items will be transferred");
        add(JeiCompat.MSG_SUPPORTS_MOST_CAMOS, "Supports most items that can be used to apply camos by block interaction");

        add(AtlasViewerCompat.LABEL_TEXTURE, "Texture");
        add(AtlasViewerCompat.LABEL_FRAMES, "Frames");
        add(AtlasViewerCompat.LABEL_MASK_TEXTURE, "Texture");
        add(AtlasViewerCompat.LABEL_MASK_SPRITE, "Sprite");
        add(AtlasViewerCompat.LABEL_MASK_AREA, "Area");
        add(AtlasViewerCompat.VALUE_MASK_AREA, "X: %s Y: %s Width: %s Height: %s");
        add(AtlasViewerCompat.LABEL_MASK_OFFSET, "Offset");
        add(AtlasViewerCompat.VALUE_MASK_OFFSET, "X: %s Y: %s");

        add(JadeCompat.configTranslation(JadeCompat.ID_FRAMED_BLOCK), "FramedBlocks camo");
        add(JadeCompat.configTranslation(JadeCompat.ID_ITEM_FRAME), "Framed Item Frame");
        add(JadeCompat.LABEL_CAMO, "Camo: %s");
        add(JadeCompat.LABEL_CAMO_ONE, "Camo one: %s");
        add(JadeCompat.LABEL_CAMO_TWO, "Camo two: %s");
        add(JadeCompat.DETAIL_PREFIX, "    %s");

        add(Utils.TOOL_WRENCH, "Wrenches");
        add(Utils.DISABLE_INTANGIBLE, "Disable Intangibility");
        add(Utils.GROUP_FULL_CUBE, "Full Framed Blocks");
        add(Utils.CRAFTING_BLOCKED_FLUID_CONTAINERS, "Camo-crafting-blocked Fluid Containers");

        addBlockOverlay("grass", "Grass");
        addBlockOverlay("podzol", "Podzol");
        addBlockOverlay("mycelium", "Mycelium");
        addBlockOverlay("path", "Dirt Path");
        addBlockOverlay("crimson_nylium", "Crimson Nylium");
        addBlockOverlay("warped_nylium", "Warped Nylium");
        addBlockOverlay("snow", "Snow");
        addBlockOverlay("moss", "Moss");
        for (DyeColor color : DyeColor.values()) {
            StringBuilder name = new StringBuilder();
            for (String part : color.getName().split("_")) {
                name.append(StringUtils.capitalize(part)).append(" ");
            }
            addBlockOverlay(color.getName() + "_carpet", name.append("Carpet").toString());
        }
    }

    private void addStatusMessageTranslations() {
        add(CamoContainerFactory.MSG_BLACKLISTED, "This block is disallowed as a camo!");
        add(SimpleBlockCamoContainerFactory.MSG_BLOCK_ENTITY, "Blocks with BlockEntities cannot be inserted into framed blocks!");
        add(SimpleBlockCamoContainerFactory.MSG_NON_SOLID, "Untagged non-solid blocks cannot be inserted into framed blocks!");

        add(ShapeLockableBlock.LOCK_MESSAGE, "The state of this block is now %s");
    }

    private void addScreenTranslations() {
        add(FramedChestBlockEntity.TITLE, "Framed Chest");
        add(FramedStorageBlockEntity.TITLE, "Framed Secret Storage");
        add(FramedHopperBlockEntity.TITLE, "Framed Item Hopper");

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
        add(FramingSawScreen.TOOLTIP_USE_INTERMEDIATE, "Use a smaller block as an intermediate step");
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
        add(FramingSawRecipeMatchResult.CAMO_PRESENT.translation(), "Input item must not have any camo");
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

    private void addTooltipTranslations() {
        add(BlueprintData.CONTAINED_BLOCK, "Contained Block: %s");
        add(BlueprintData.STORED_OVERLAY, "Overlay: %s");
        add(BlueprintData.IS_ILLUMINATED, "Illuminated: %s");
        add(BlueprintData.IS_INTANGIBLE, "Intangible: %s");
        add(BlueprintData.IS_REINFORCED, "Reinforced: %s");
        add(BlueprintData.IS_EMISSIVE, "Emissive: %s");
        add(BlueprintData.MISSING_MATERIALS, "[Framed Blueprint] Missing required materials:");
        add(CamoPrinter.BLOCK_NONE, "None");
        add(CamoPrinter.DOUBLE_CAMO_SEPARATOR_KEY, "%s | %s");
        add(CamoPrinter.MULTI_CAMO_ENTRY_PREFIX_KEY, "  - %s");
        add(BlueprintData.BLOCK_INVALID, "Invalid");
        add(BlueprintData.OVERLAY_NONE, "None");
        add(BlueprintData.FALSE, "false");
        add(BlueprintData.TRUE, "true");
        add(BlueprintData.CANT_COPY, "[Framed Blueprint] This block can currently not be copied!");
        add(FramedBlueprintItem.CANT_PLACE_FLUID_CAMO, "[Framed Blueprint] Copying blocks with fluid camos is currently not possible!");
        add(IFramedBlock.CAMO_LABEL, "Camo: %s");
        add(IFramedBlock.CAMO_LABEL_MULTI, "Camos: %s");
        add(PhantomPasteItem.FEATURE_DISABLED, "The intangibility feature is disabled, this item therefor has no function!");
        add(FramedTankBlockItem.TANK_CONTENTS, "Stored Fluid: %s");
        add(FramedTankBlockItem.EMPTY_FLUID, "Empty");
        add(FramedMirroringBlockItem.PLACE_UPSIDE_DOWN, "Hold sneak key to place upside down");
        add(FramedWrenchItem.LABEL_MODE, "Rotation Mode: %s");
        add(FramedWrenchItem.LABEL_TOGGLE, "Right-click while crouching to toggle mode");
        add(WrenchRotationMode.PRIMARY.getTranslatedName(), "Primary Axis");
        add(WrenchRotationMode.SECONDARY.getTranslatedName(), "Secondary Axis");
    }

    private void addOverlayTranslations() {
        add(StateLockOverlay.LOCK_MESSAGE, "State %s");
        add(ShapeLockableBlock.STATE_LOCKED, "locked");
        add(ShapeLockableBlock.STATE_UNLOCKED, "unlocked");

        add(ToggleWaterloggableOverlay.MSG_IS_WATERLOGGABLE, "Block is waterloggable.");
        add(ToggleWaterloggableOverlay.MSG_IS_NOT_WATERLOGGABLE, "Block is not waterloggable.");
        add(ToggleWaterloggableOverlay.MSG_MAKE_WATERLOGGABLE, "Hit with a Framed Hammer to make waterloggable");
        add(ToggleWaterloggableOverlay.MSG_MAKE_NOT_WATERLOGGABLE, "Hit with a Framed Hammer to make not waterloggable");

        add(ToggleAltSlopeOverlay.SLOPE_MESSAGE_VERT, "Block uses %s faces for vertical sloped faces.");
        add(ToggleAltSlopeOverlay.TOGGLE_MESSAGE_VERT, "Hit with a Framed Wrench to switch to %s faces");
        add(ToggleAltSlopeOverlay.SLOPE_VERT_HOR, "horizontal");
        add(ToggleAltSlopeOverlay.SLOPE_VERT_VERT, "vertical");
        add(ToggleAltSlopeOverlay.SLOPE_MESSAGE_HOR, "Block uses the %s face for horizontal sloped faces.");
        add(ToggleAltSlopeOverlay.TOGGLE_MESSAGE_HOR, "Hit with a Framed Wrench to switch to the %s face");
        add(ToggleAltSlopeOverlay.SLOPE_HOR_FRONT, "front");
        add(ToggleAltSlopeOverlay.SLOPE_HOR_SIDE, "right");

        add(ReinforcementOverlay.REINFORCE_MESSAGE, "Block is %s.");
        add(ReinforcementOverlay.STATE_NOT_REINFORCED, "not reinforced");
        add(ReinforcementOverlay.STATE_REINFORCED, "reinforced");

        add(PrismOffsetOverlay.PRISM_OFFSET_FALSE, "Triangle texture is not offset.");
        add(PrismOffsetOverlay.PRISM_OFFSET_TRUE, "Triangle texture is offset by half a block.");
        add(PrismOffsetOverlay.MSG_SWITCH_OFFSET, "Hit with a Framed Hammer to toggle the offset");

        add(SplitLineOverlay.SPLIT_LINE_FALSE, "Split-line of the deformed face runs along the steep diagonal.");
        add(SplitLineOverlay.SPLIT_LINE_TRUE, "Split-line of the deformed face runs along the shallow diagonal.");
        add(SplitLineOverlay.MSG_SWITCH_SPLIT_LINE, "Hit with a Framed Wrench to switch the orientation of the split-line");

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

        add(TrapdoorTextureRotationOverlay.ROTATING_FALSE, "Camo texture will not rotate when opening the trapdoor");
        add(TrapdoorTextureRotationOverlay.ROTATING_TRUE, "Camo texture will rotate when opening the trapdoor");
        add(TrapdoorTextureRotationOverlay.ROTATING_TOGGLE, "Hit with a Framed Hammer to toggle texture rotation");

        add(CopycatStyleOverlay.LINE_USE_STANDARD, "The targetted block uses standard appearance");
        add(CopycatStyleOverlay.LINE_USE_COPYCAT, "The targetted block uses copycat-style appearance");
        add(CopycatStyleOverlay.LINE_SET_STANDARD, "Hit with a Framed Hammer to use standard appearance");
        add(CopycatStyleOverlay.LINE_SET_COPYCAT, "Hit with a Framed Hammer to use copycat-style appearance");
    }

    private void addConfigTranslations() {
        add("framedblocks.configuration.title", "FramedBlocks Configuration");

        add("framedblocks.configuration.section.framedblocks.server.toml", "Server Settings");
        add("framedblocks.configuration.section.framedblocks.server.toml.title", "FramedBlocks Server Configuration");
        addConfigCategory(ServerConfig.TRANSLATION_CATEGORY_GENERAL, "General", "General Settings", "Configure general server settings");
        addConfigCategory(ServerConfig.TRANSLATION_CATEGORY_POWERED_FRAMING_SAW, "Powered Framing Saw", "Powered Framing Saw Settings", "Configure Powered Framing Saw settings");
        addConfigValue(ServerConfig.ALLOW_BLOCK_ENTITIES_VALUE, "Allow BlockEntities");
        addConfigValue(ServerConfig.ENABLE_INTANGIBILITY_VALUE, "Enable intangibility feature");
        addConfigValue(ServerConfig.ONE_WAY_WINDOW_OWNABLE_VALUE, "One-Way Window ownability");
        addConfigValue(ServerConfig.CONSUME_CAMO_ITEM_VALUE, "Consume camo item");
        addConfigValue(ServerConfig.GLOWSTONE_LIGHT_LEVEL_VALUE, "Glowstone Light Level");
        addConfigValue(ServerConfig.FIREPROOF_BLOCKS_VALUE, "Fireproof blocks");
        addConfigValue(ServerConfig.POWERED_SAW_ENERGY_CAPACITY_VALUE, "Energy Capacity");
        addConfigValue(ServerConfig.POWERED_SAW_MAX_RECEIVE_VALUE, "Max input");
        addConfigValue(ServerConfig.POWERED_SAW_CONSUMPTION_VALUE, "Consumption");
        addConfigValue(ServerConfig.POWERED_SAW_RECIPE_DURATION_VALUE, "Crafting Duration");

        add("framedblocks.configuration.section.framedblocks.client.toml", "Client Settings");
        add("framedblocks.configuration.section.framedblocks.client.toml.title", "FramedBlocks Client Configuration");
        addConfigCategory(ClientConfig.TRANSLATION_CATEGORY_GENERAL, "General", "General Settings", "Configure general client settings");
        addConfigCategory(ClientConfig.TRANSLATION_CATEGORY_OVERLAY, "Overlays", "Overlay Settings", "Configure overlay settings");
        addConfigValue(ClientConfig.SHOW_GHOST_BLOCKS_VALUE, "Show ghost blocks");
        addConfigValue(ClientConfig.ALT_GHOST_RENDERER_VALUE, "Use alternative placement preview renderer");
        addConfigValue(ClientConfig.GHOST_RENDER_OPACITY_VALUE, "Placement preview opacity");
        addConfigValue(ClientConfig.FANCY_HITBOXES_VALUE, "Fancy hitboxes");
        addConfigValue(ClientConfig.DETAILED_CULLING_VALUE, "Detailed culling");
        addConfigValue(ClientConfig.CON_TEX_MODE_VALUE, "Connected textures mode");
        addConfigValue(ClientConfig.CAMO_MESSAGE_VERBOSITY_VALUE, "Disallowed camo message verbosity");
        addConfigValue(ClientConfig.FORCE_AO_ON_GLOWING_BLOCKS_VALUE, "Force ambient occlusion on glowing framed blocks");
        addConfigValue(ClientConfig.RENDER_ITEM_MODELS_WITH_CAMO_VALUE, "Render item models with camo");
        addConfigValue(ClientConfig.SHOW_ALL_RECIPE_PERMUTATIONS_IN_EMI_VALUE, "Show all Framing Saw recipe permutations in EMI");
        addConfigValue(ClientConfig.SOLID_FRAME_MODE_VALUE, "Solid frame mode");
        addConfigValue(ClientConfig.SHOW_BUTTON_PLATE_OVERLAY_VALUE, "Show button and pressure plate type overlay");
        addConfigValue(ClientConfig.SHOW_SPECIAL_CUBE_OVERLAY_VALUE, "Show special cube type overlay");
        addConfigValue(ClientConfig.RENDER_CAMO_IN_JADE_VALUE, "Render camo in Jade overlay");
        addConfigValue(ClientConfig.SHOW_CAMO_CRAFTING_IN_JEI_VALUE, "Show camo application recipes in JEI");
        addConfigValue(ClientConfig.MAX_OVERLAY_MODE_VALUE, "Max overlay display mode");
        addConfigValue(ClientConfig.STATE_LOCK_MODE_VALUE, "State lock overlay: Display mode");
        addConfigValue(ClientConfig.TOGGLE_WATERLOG_MODE_VALUE, "Toggle waterloggable overlay: Display mode");
        addConfigValue(ClientConfig.TOGGLE_ALT_SLOPE_MODE_VALUE, "Toggle Y slope overlay: Display mode");
        addConfigValue(ClientConfig.REINFORCEMENT_MODE_VALUE, "Reinforcement overlay: Display mode");
        addConfigValue(ClientConfig.PRISM_OFFSET_MODE_VALUE, "Prism offset overlay: Display mode");
        addConfigValue(ClientConfig.SPLIT_LINE_MODE_VALUE, "Collapsible block split lines overlay: Display mode");
        addConfigValue(ClientConfig.ONE_WAY_WINDOW_MODE_VALUE, "One-Way Window overlay: Display mode");
        addConfigValue(ClientConfig.FRAME_BACKGROUND_MODE_VALUE, "Item Frame Background overlay: Display mode");
        addConfigValue(ClientConfig.CAMO_ROTATION_MODE_VALUE, "Camo Rotation overlay: Display mode");
        addConfigValue(ClientConfig.TRAPDOOR_TEXTURE_ROTATION_MODE_VALUE, "Trapdoor Texture Rotation overlay: Display mode");
        addConfigValue(ClientConfig.COPYCAT_STYLE_MODE_VALUE, "Copycat Style overlay: Display mode");

        add("framedblocks.configuration.section.framedblocks.devtools.toml", "Dev Tools Settings");
        add("framedblocks.configuration.section.framedblocks.devtools.toml.title", "FramedBlocks Dev Tools Configuration");
        addConfigValue(DevToolsConfig.DOUBLE_BLOCK_PART_DEBUG_VALUE, "Double-block part debug");
        addConfigValue(DevToolsConfig.CONNECTION_DEBUG_VALUE, "ConnectionPredicate debug");
        addConfigValue(DevToolsConfig.QUAD_WINDING_DEBUG_VALUE, "Quad-winding debug");
        addConfigValue(DevToolsConfig.STATE_MERGER_DEBUG_VALUE, "StateMerger debug");
        addConfigValue(DevToolsConfig.STATE_MERGER_DEBUG_FILTER_VALUE, "StateMerger debug filter");
        addConfigValue(DevToolsConfig.OCCLUSION_SHAPE_DEBUG_VALUE, "Occlusion shape debug");
        addConfigValue(DevToolsConfig.COLLAPSIBLE_BLOCK_DEBUG_VALUE, "Collapsible block debug");
    }

    private void add(Component key, String value) {
        ComponentContents contents = key.getContents();
        if (contents instanceof TranslatableContents translatable) {
            add(translatable.getKey(), value);
        } else {
            add(key.getString(), value);
        }
    }

    private void addConfigCategory(String key, String catValue, String catButtonValue, String catTooltipValue) {
        add(key, catValue);
        add(key + ".button", catButtonValue);
        add(key + ".tooltip", catTooltipValue);
    }

    private void addConfigValue(ModConfigSpec.@UnknownNullability ConfigValue<?> configValue, String value) {
        Objects.requireNonNull(configValue);
        String translationKey = Objects.requireNonNull(configValue.getSpec().getTranslationKey());
        add(translationKey, value);
        add(translationKey + ".tooltip", Objects.requireNonNull(configValue.getSpec().getComment()));
    }

    private void addBlockOverlay(String id, String name) {
        Holder<BlockOverlay> overlay = Objects.requireNonNull(lookup).getOrThrow(
                ResourceKey.create(FramedConstants.BLOCK_OVERLAY_REGISTRY_KEY, Utils.id(id))
        );
        add(BlockOverlay.getDescriptionId(overlay), name);
    }
}
