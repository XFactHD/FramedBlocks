package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.client.loader.overlay.OverlayLoaderBuilder;
import xfacthd.framedblocks.client.model.cube.FramedMarkedCubeModel;
import xfacthd.framedblocks.client.model.cube.FramedTargetModel;
import xfacthd.framedblocks.client.model.interactive.FramedMarkedPressurePlateModel;
import xfacthd.framedblocks.client.model.rail.FramedFancyRailModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class FramedBlockStateProvider extends BlockStateProvider
{
    private final ResourceLocation TEXTURE;

    public FramedBlockStateProvider(PackOutput output, ExistingFileHelper fileHelper)
    {
        super(output, FramedConstants.MOD_ID, fileHelper);
        TEXTURE = modLoc("block/framed_block");
    }

    @Override
    protected void registerStatesAndModels()
    {
        ModelFile cube = models().cubeAll("framed_cube", TEXTURE).renderType("cutout");
        ModelFile stoneCube = makeUnderlayedCube("framed_stone_cube", mcLoc("block/stone"));
        ModelFile obsidianCube = makeUnderlayedCube("framed_obsidian_cube", mcLoc("block/obsidian"));
        ModelFile ironCube = makeUnderlayedCube("framed_iron_cube", mcLoc("block/iron_block"));
        ModelFile goldCube = makeUnderlayedCube("framed_gold_cube", mcLoc("block/gold_block"));

        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_CORNER_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_INNER_CORNER_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_PRISM_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_INNER_PRISM_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_THREEWAY_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_INNER_THREEWAY_CORNER, cube, "cutout");
        simpleBlock(FBContent.BLOCK_FRAMED_SLAB_EDGE.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_SLAB_CORNER.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_DIVIDED_SLAB.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_PANEL.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_CORNER_PILLAR.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_HOR.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_IRON_TRAP_DOOR.get(), ironCube);
        simpleBlock(FBContent.BLOCK_FRAMED_STONE_BUTTON.get(), stoneCube);
        simpleBlock(FBContent.BLOCK_FRAMED_WALL_SIGN.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_LATTICE.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.get(), cube);
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_SLAB, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE.get(), cube);
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_PRISM_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_THREEWAY_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_RAIL_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_POWERED_RAIL_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DETECTOR_RAIL_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_ACTIVATOR_RAIL_SLOPE, cube, "cutout");
        simpleBlock(FBContent.BLOCK_FRAMED_PILLAR.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_HALF_PILLAR.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_POST.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_HALF_STAIRS.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_DIVIDED_STAIRS.get(), cube);
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_PRISM, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_INNER_PRISM, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_PRISM, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SLOPED_PRISM, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_INNER_SLOPED_PRISM, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_SLOPED_PRISM, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SLOPE_SLAB, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_SLAB, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_STACKED_SLOPE_SLAB, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_DOUBLE_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_ELEVATED_INNER_DOUBLE_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER, cube, "cutout");
        simpleBlock(FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_VERTICAL_DIVIDED_STAIRS.get(), cube);
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_STACKED_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_DOUBLE_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_LARGE_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_LARGE_DOUBLE_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_INVERSE_DOUBLE_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_WALL, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_STAIRS, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS, cube, "cutout");
        simpleBlock(FBContent.BLOCK_FRAMED_WALL_BOARD.get(), cube);
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_PYRAMID, cube);
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_PYRAMID_SLAB, cube);
        simpleBlock(FBContent.BLOCK_FRAMED_HORIZONTAL_PANE.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_LARGE_BUTTON.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_LARGE_STONE_BUTTON.get(), stoneCube);
        simpleBlock(FBContent.BLOCK_FRAMED_GATE.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_IRON_GATE.get(), ironCube);
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FANCY_RAIL_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_HALF_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DIVIDED_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_DOUBLE_HALF_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SLOPED_STAIRS, cube, "cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_VERTICAL_SLOPED_STAIRS, cube, "cutout");

        registerFramedCube(cube);
        registerFramedSlab(cube);
        registerFramedStairs(cube);
        registerFramedWall(cube);
        registerFramedFence(cube);
        registerFramedFenceGate(cube);
        registerFramedDoor(cube);
        registerFramedIronDoor(ironCube);
        registerFramedTrapDoor(cube);
        registerFramedPressurePlate(cube);
        registerFramedStonePressurePlate(stoneCube);
        registerFramedObsidianPressurePlate(obsidianCube);
        registerFramedGoldPressurePlate(goldCube);
        registerFramedIronPressurePlate(ironCube);
        registerFramedLadder();
        registerFramedButton(cube);
        registerFramedLever();
        registerFramedSign(cube);
        registerFramedHangingSign();
        registerFramedWallHangingSign();
        registerFramedTorch();
        registerFramedWallTorch();
        registerFramedSoulTorch();
        registerFramedSoulWallTorch();
        registerFramedRedstoneTorch();
        registerFramedRedstoneWallTorch();
        registerFramedFloorBoard(cube);
        registerFramedChest();
        registerFramedBarsBlock(cube);
        registerFramedPaneBlock(cube);
        registerFramedFlowerPotBlock(cube);
        registerFramedCollapsibleBlock();
        registerFramedBouncyBlock();
        registerFramedSecretStorage();
        registerFramedRedstoneBlock();
        registerFramedGlowingCube();
        registerFramedTarget(cube);
        registerFramedItemFrame();
        registerFramedFancyRail();
        registerFramedFancyPoweredRail();
        registerFramedFancyDetectorRail();
        registerFramedFancyActivatorRail();
        registerFramedMiniCube(cube);
        registerFramedOneWayWindow();

        registerFramingSaw();
    }

    private void registerFramedCube(ModelFile cube)
    {
        ModelFile altCube = models().cubeAll("framed_cube_alt", modLoc("block/framed_block_alt"))
                .renderType("cutout");
        ModelFile reinforcement = models().cubeAll("framed_reinforcement", modLoc("block/framed_reinforcement"))
                .renderType("cutout");

        getMultipartBuilder(FBContent.BLOCK_FRAMED_CUBE.get())
                .part()
                    .modelFile(cube)
                    .addModel()
                    .condition(FramedProperties.ALT, false)
                    .end()
                .part()
                    .modelFile(altCube)
                    .addModel()
                    .condition(FramedProperties.ALT, true)
                    .end()
                .part()
                    .modelFile(reinforcement)
                    .addModel()
                    .condition(FramedProperties.REINFORCED, true)
                    .end();

        simpleBlockItem(FBContent.BLOCK_FRAMED_CUBE, cube);
    }

    private void registerFramedSlab(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_SLAB.get(), cube);
        itemModels().slab("framed_slab", TEXTURE, TEXTURE, TEXTURE).renderType("cutout");
    }

    private void registerFramedStairs(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_STAIRS.get(), cube);
        itemModels().stairs("framed_stairs", TEXTURE, TEXTURE, TEXTURE).renderType("cutout");
    }

    private void registerFramedWall(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_WALL.get(), cube);
        itemModels().getBuilder("framed_wall")
                .parent(models().getExistingFile(mcLoc("block/wall_inventory")))
                .texture("wall", TEXTURE)
                .renderType("cutout");
    }

    private void registerFramedFence(ModelFile cube)
    {
        getMultipartBuilder(FBContent.BLOCK_FRAMED_FENCE.get())
                .part()
                .modelFile(cube)
                .addModel();

        itemModels().getBuilder("framed_fence")
                .parent(models().getExistingFile(mcLoc("block/fence_inventory")))
                .texture("texture", TEXTURE)
                .renderType("cutout");
    }

    private void registerFramedFenceGate(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_FENCE_GATE.get(), cube);
        itemModels().fenceGate("framed_fence_gate", TEXTURE).renderType("cutout");
    }

    private void registerFramedDoor(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_DOOR.get(), cube);
        simpleItem(FBContent.BLOCK_FRAMED_DOOR, "cutout");
    }

    private void registerFramedIronDoor(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_IRON_DOOR.get(), cube);
        simpleItem(FBContent.BLOCK_FRAMED_IRON_DOOR, "cutout");
    }

    private void registerFramedTrapDoor(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_TRAP_DOOR.get(), cube);
        itemModels().withExistingParent("framed_trapdoor", mcLoc("block/template_orientable_trapdoor_bottom"))
                .texture("texture", TEXTURE)
                .renderType("cutout");
    }

    private void registerFramedPressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_PRESSURE_PLATE.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE.get(), cube);

        itemModels().withExistingParent("framed_pressure_plate", mcLoc("block/pressure_plate_up"))
                .texture("texture", TEXTURE).renderType("cutout");
    }

    private void registerFramedStonePressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_STONE_PRESSURE_PLATE.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE.get(), cube);

        makeOverlayModel(
                FramedMarkedPressurePlateModel.STONE_FRAME_LOCATION,
                modLoc("block/framed_pressure_plate_frame_up"),
                "texture",
                modLoc("block/stone_plate_frame"),
                new Vector3f(8F, 0.5F, 8F)
        );
        makeOverlayModel(
                FramedMarkedPressurePlateModel.STONE_FRAME_DOWN_LOCATION,
                modLoc("block/framed_pressure_plate_frame_down"),
                "texture",
                modLoc("block/stone_plate_frame"),
                new Vector3f(8F, 0.25F, 8F)
        );

        itemModels().withExistingParent("framed_stone_pressure_plate", modLoc("block/framed_pressure_plate_up"))
                .texture("background", mcLoc("block/stone"))
                .renderType("cutout");
    }

    private void registerFramedObsidianPressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_OBSIDIAN_PRESSURE_PLATE.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE.get(), cube);

        makeOverlayModel(
                FramedMarkedPressurePlateModel.OBSIDIAN_FRAME_LOCATION,
                modLoc("block/framed_pressure_plate_frame_up"),
                "texture",
                modLoc("block/obsidian_plate_frame"),
                new Vector3f(8F, 0.5F, 8F)
        );
        makeOverlayModel(
                FramedMarkedPressurePlateModel.OBSIDIAN_FRAME_DOWN_LOCATION,
                modLoc("block/framed_pressure_plate_frame_down"),
                "texture",
                modLoc("block/obsidian_plate_frame"),
                new Vector3f(8F, 0.25F, 8F)
        );

        itemModels().withExistingParent("framed_obsidian_pressure_plate", modLoc("block/framed_pressure_plate_up"))
                .texture("background", mcLoc("block/obsidian"))
                .renderType("cutout");
    }

    private void registerFramedGoldPressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_GOLD_PRESSURE_PLATE.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE.get(), cube);

        makeOverlayModel(
                FramedMarkedPressurePlateModel.GOLD_FRAME_LOCATION,
                modLoc("block/framed_pressure_plate_frame_up"),
                "texture",
                modLoc("block/gold_plate_frame"),
                new Vector3f(8F, 0.5F, 8F)
        );
        makeOverlayModel(
                FramedMarkedPressurePlateModel.GOLD_FRAME_DOWN_LOCATION,
                modLoc("block/framed_pressure_plate_frame_down"),
                "texture",
                modLoc("block/gold_plate_frame"),
                new Vector3f(8F, 0.25F, 8F)
        );

        itemModels().withExistingParent("framed_gold_pressure_plate", modLoc("block/framed_pressure_plate_up"))
                .texture("background", mcLoc("block/gold_block"))
                .renderType("cutout");
    }

    private void registerFramedIronPressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_IRON_PRESSURE_PLATE.get(), cube);
        simpleBlock(FBContent.BLOCK_FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE.get(), cube);

        makeOverlayModel(
                FramedMarkedPressurePlateModel.IRON_FRAME_LOCATION,
                modLoc("block/framed_pressure_plate_frame_up"),
                "texture",
                modLoc("block/iron_plate_frame"),
                new Vector3f(8F, 0.5F, 8F)
        );
        makeOverlayModel(
                FramedMarkedPressurePlateModel.IRON_FRAME_DOWN_LOCATION,
                modLoc("block/framed_pressure_plate_frame_down"),
                "texture",
                modLoc("block/iron_plate_frame"),
                new Vector3f(8F, 0.25F, 8F)
        );

        itemModels().withExistingParent("framed_iron_pressure_plate", modLoc("block/framed_pressure_plate_up"))
                .texture("background", mcLoc("block/iron_block"))
                .renderType("cutout");
    }

    private void registerFramedLadder()
    {
        ModelFile ladder = models()
                .withExistingParent("framed_ladder", mcLoc("block/cube"))
                .texture("0", TEXTURE)
                .texture("particle", TEXTURE)
                .element()
                    .face(Direction.NORTH).texture("#0").end()
                    .face(Direction.SOUTH).texture("#0").end()
                .end();
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_LADDER, ladder, "cutout");
    }

    private void registerFramedButton(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_BUTTON.get(), cube);

        itemModels().getBuilder("framed_button")
                .parent(models().getExistingFile(mcLoc("block/button_inventory")))
                .texture("texture", TEXTURE)
                .renderType("cutout");
    }

    private void registerFramedLever()
    {
        ModelFile lever = models()
                .withExistingParent("framed_lever", mcLoc("block/lever"))
                .texture("base", ClientUtils.DUMMY_TEXTURE)
                .texture("particle", TEXTURE);
        ModelFile leverOn = models()
                .withExistingParent("framed_lever_on", mcLoc("block/lever_on"))
                .texture("base", ClientUtils.DUMMY_TEXTURE)
                .texture("particle", TEXTURE);

        getVariantBuilder(FBContent.BLOCK_FRAMED_LEVER.get()).forAllStatesExcept(state ->
        {
            Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
            AttachFace face = state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE);
            boolean powered = state.getValue(LeverBlock.POWERED);

            int rotY = (int)(facing.toYRot() + 180F) % 360;
            int rotX = face.ordinal() * 90;
            ModelFile model = powered ? leverOn : lever;
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .build();
        }, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);

        simpleItem(FBContent.BLOCK_FRAMED_LEVER, "cutout");
    }

    private void registerFramedSign(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_SIGN.get(), cube);
        simpleItem(FBContent.BLOCK_FRAMED_SIGN, "cutout");
    }

    private void registerFramedHangingSign()
    {
        ModelFile model = models().getExistingFile(modLoc("block/framed_hanging_sign"));
        ModelFile modelAttached = models().getExistingFile(modLoc("block/framed_hanging_sign_attached"));

        getVariantBuilder(FBContent.BLOCK_FRAMED_HANGING_SIGN.get()).forAllStatesExcept(state ->
        {
            int rotation = state.getValue(BlockStateProperties.ROTATION_16);
            Direction facing = Direction.from2DDataValue(rotation / 4);
            boolean attached = state.getValue(BlockStateProperties.ATTACHED);

            return ConfiguredModel.builder()
                    .modelFile(attached ? modelAttached : model)
                    .rotationY((int) facing.toYRot())
                    .build();
        }, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT, BlockStateProperties.WATERLOGGED);

        simpleItem(FBContent.BLOCK_FRAMED_HANGING_SIGN, "cutout");
    }

    private void registerFramedWallHangingSign()
    {
        ModelFile model = models().getExistingFile(modLoc("block/framed_wall_hanging_sign"));

        getVariantBuilder(FBContent.BLOCK_FRAMED_WALL_HANGING_SIGN.get()).forAllStatesExcept(state ->
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int) facing.toYRot())
                    .build();
        }, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT, BlockStateProperties.WATERLOGGED);
    }

    private void registerFramedTorch()
    {
        ModelFile torch = models().getExistingFile(modLoc("framed_torch"));
        simpleBlock(FBContent.BLOCK_FRAMED_TORCH.get(), torch);
        simpleItem(FBContent.BLOCK_FRAMED_TORCH, "block/framed_torch", "cutout");
    }

    private void registerFramedWallTorch()
    {
        ModelFile wallTorch = models().getExistingFile(modLoc("framed_wall_torch"));
        getVariantBuilder(FBContent.BLOCK_FRAMED_WALL_TORCH.get()).forAllStatesExcept(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.toYRot() + 90) % 360;
            return ConfiguredModel.builder().modelFile(wallTorch).rotationY(rotY).build();
        }, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    private void registerFramedSoulTorch()
    {
        ModelFile torch = models().withExistingParent("framed_soul_torch", modLoc("framed_torch"))
                .texture("particle", modLoc("block/framed_soul_torch"))
                .texture("top", mcLoc("block/soul_torch"));
        simpleBlock(FBContent.BLOCK_FRAMED_SOUL_TORCH.get(), torch);
        simpleItem(FBContent.BLOCK_FRAMED_SOUL_TORCH, "block/framed_soul_torch", "cutout");
    }

    private void registerFramedSoulWallTorch()
    {
        ModelFile wallTorch = models().withExistingParent("framed_soul_wall_torch", modLoc("framed_wall_torch"))
                .texture("particle", modLoc("block/framed_soul_torch"))
                .texture("top", mcLoc("block/soul_torch"));
        getVariantBuilder(FBContent.BLOCK_FRAMED_SOUL_WALL_TORCH.get()).forAllStatesExcept(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.toYRot() + 90) % 360;
            return ConfiguredModel.builder().modelFile(wallTorch).rotationY(rotY).build();
        }, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    private void registerFramedRedstoneTorch()
    {
        ModelFile torch = models().getExistingFile(modLoc("framed_redstone_torch"));
        ModelFile torchOff = models().withExistingParent("framed_redstone_torch_off", modLoc("framed_torch"))
                .texture("particle", modLoc("block/framed_redstone_torch_off"))
                .texture("top", mcLoc("block/redstone_torch_off"));

        getVariantBuilder(FBContent.BLOCK_FRAMED_REDSTONE_TORCH.get()).forAllStatesExcept(state ->
        {
            ModelFile model = state.getValue(BlockStateProperties.LIT) ? torch : torchOff;
            return ConfiguredModel.builder().modelFile(model).build();
        }, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);

        simpleItem(FBContent.BLOCK_FRAMED_REDSTONE_TORCH, "block/framed_redstone_torch", "cutout");
    }

    private void registerFramedRedstoneWallTorch()
    {
        ModelFile wallTorch = models().getExistingFile(modLoc("framed_redstone_wall_torch"));
        ModelFile wallTorchOff = models().withExistingParent("framed_redstone_wall_torch_off", modLoc("framed_wall_torch"))
                .texture("particle", modLoc("block/framed_redstone_torch_off"))
                .texture("top", mcLoc("block/redstone_torch_off"));

        getVariantBuilder(FBContent.BLOCK_FRAMED_REDSTONE_WALL_TORCH.get()).forAllStatesExcept(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.toYRot() + 90) % 360;
            ModelFile model = state.getValue(BlockStateProperties.LIT) ? wallTorch : wallTorchOff;
            return ConfiguredModel.builder().modelFile(model).rotationY(rotY).build();
        }, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    private void registerFramedFloorBoard(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_FLOOR.get(), cube);
        itemModels().carpet("framed_floor_board", TEXTURE).renderType("cutout");
    }

    private void registerFramedChest()
    {
        ModelFile chest = models().getExistingFile(modLoc("block/framed_chest"));

        getVariantBuilder(FBContent.BLOCK_FRAMED_CHEST.get()).forAllStatesExcept(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.toYRot() + 180) % 360;
            return ConfiguredModel.builder().modelFile(chest).rotationY(rotY).build();
        }, BlockStateProperties.WATERLOGGED, PropertyHolder.LATCH_TYPE, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);

        simpleBlockItem(FBContent.BLOCK_FRAMED_CHEST, chest, "cutout");
    }

    private void registerFramedBarsBlock(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_BARS.get(), cube);
        simpleItem(FBContent.BLOCK_FRAMED_BARS, "cutout");
    }

    private void registerFramedPaneBlock(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_PANE.get(), cube);
        simpleItem(FBContent.BLOCK_FRAMED_PANE, TEXTURE.getPath(), "cutout");
    }

    private void registerFramedFlowerPotBlock(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_FLOWER_POT.get(), cube);
        simpleItem(FBContent.BLOCK_FRAMED_FLOWER_POT, "cutout");
    }

    private void registerFramedCollapsibleBlock()
    {
        ModelFile block = makeUnderlayedCube("framed_collapsible_block", mcLoc("block/oak_planks")).renderType("cutout");
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK, block, "cutout");
    }

    private void registerFramedBouncyBlock()
    {
        ModelFile block = makeUnderlayedCube("framed_bouncy_cube", mcLoc("block/slime_block"));

        simpleBlockWithItem(FBContent.BLOCK_FRAMED_BOUNCY_CUBE, block, "cutout");

        makeOverlayModel(
                FramedMarkedCubeModel.SLIME_FRAME_LOCATION,
                mcLoc("block/cube_all"),
                "all",
                modLoc("block/slime_frame")
        );
    }

    private void registerFramedSecretStorage()
    {
        ModelFile block = models().withExistingParent("framed_secret_storage", "block/block")
                .element()
                    .cube("#barrel")
                    .face(Direction.UP)
                        .texture("#barrel_top")
                        .end()
                    .face(Direction.DOWN)
                        .texture("#barrel_bottom")
                        .end()
                    .end()
                .element()
                    .cube("#frame")
                    .end()
                .texture("barrel", mcLoc("block/barrel_side"))
                .texture("barrel_top", mcLoc("block/barrel_top"))
                .texture("barrel_bottom", mcLoc("block/barrel_bottom"))
                .texture("frame", TEXTURE)
                .texture("particle", TEXTURE);

        simpleBlockWithItem(FBContent.BLOCK_FRAMED_SECRET_STORAGE, block, "cutout");
    }

    private void registerFramedRedstoneBlock()
    {
        ModelFile block = makeUnderlayedCube("framed_redstone_block", mcLoc("block/redstone_block"));

        simpleBlockWithItem(FBContent.BLOCK_FRAMED_REDSTONE_BLOCK, block, "cutout");

        makeOverlayModel(
                FramedMarkedCubeModel.REDSTONE_FRAME_LOCATION,
                mcLoc("block/cube_all"),
                "all",
                modLoc("block/redstone_frame")
        );
    }

    private void registerFramedGlowingCube()
    {
        ModelFile block = makeUnderlayedCube("framed_glowing_cube", new ResourceLocation("forge", "white"));
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_GLOWING_CUBE, block);
    }

    private void registerFramedTarget(ModelFile cube)
    {
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_TARGET, cube, "cutout");

        models().getBuilder("target_overlay")
                .customLoader(OverlayLoaderBuilder::new)
                .model(models().nested().parent(models().getExistingFile(mcLoc("block/block")))
                        .element()
                            .cube("#overlay")
                            .faces((dir, face) -> face.tintindex(FramedTargetModel.OVERLAY_TINT_IDX))
                            .end()
                        .texture("overlay", modLoc("block/target_overlay"))
                        .renderType("cutout")
                );
    }

    private void registerFramedItemFrame()
    {
        ModelFile normalFrame = models().withExistingParent("framed_item_frame", modLoc("block/template_framed_item_frame"))
                .texture("front", mcLoc("block/item_frame"))
                .texture("back", ClientUtils.DUMMY_TEXTURE)
                .texture("wood", ClientUtils.DUMMY_TEXTURE)
                .texture("particle", TEXTURE);

        ModelFile normalMapFrame = models().withExistingParent("framed_item_frame_map", modLoc("block/template_framed_item_frame_map"))
                .texture("front", mcLoc("block/item_frame"))
                .texture("back", ClientUtils.DUMMY_TEXTURE)
                .texture("wood", ClientUtils.DUMMY_TEXTURE)
                .texture("particle", TEXTURE);

        ModelFile glowFrame = models().withExistingParent("framed_glow_item_frame", modLoc("block/template_framed_item_frame"))
                .texture("front", mcLoc("block/glow_item_frame"))
                .texture("back", ClientUtils.DUMMY_TEXTURE)
                .texture("wood", ClientUtils.DUMMY_TEXTURE)
                .texture("particle", TEXTURE);

        ModelFile glowMapFrame = models().withExistingParent("framed_glow_item_frame_map", modLoc("block/template_framed_item_frame_map"))
                .texture("front", mcLoc("block/glow_item_frame"))
                .texture("back", ClientUtils.DUMMY_TEXTURE)
                .texture("wood", ClientUtils.DUMMY_TEXTURE)
                .texture("particle", TEXTURE);

        BiFunction<ModelFile, ModelFile, Function<BlockState, ConfiguredModel[]>> mapper = (frame, mapFrame) -> state ->
        {
            int xRot = 0;
            int yRot = 0;

            Direction dir = state.getValue(BlockStateProperties.FACING);
            if (Utils.isY(dir))
            {
                xRot = dir == Direction.UP ? 90 : -90;
            }
            else
            {
                yRot = (int)dir.toYRot();
            }

            boolean map = state.getValue(PropertyHolder.MAP_FRAME);
            return ConfiguredModel.builder()
                    .modelFile(map ? mapFrame : frame)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        };

        getVariantBuilder(FBContent.BLOCK_FRAMED_ITEM_FRAME.get()).forAllStatesExcept(
                mapper.apply(normalFrame, normalMapFrame),
                PropertyHolder.LEATHER,
                FramedProperties.GLOWING,
                FramedProperties.PROPAGATES_SKYLIGHT
        );
        getVariantBuilder(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME.get()).forAllStatesExcept(
                mapper.apply(glowFrame, glowMapFrame),
                PropertyHolder.LEATHER,
                FramedProperties.GLOWING,
                FramedProperties.PROPAGATES_SKYLIGHT
        );

        simpleItem(FBContent.BLOCK_FRAMED_ITEM_FRAME, "cutout");
        simpleItem(FBContent.BLOCK_FRAMED_GLOWING_ITEM_FRAME, "cutout");
    }

    private static Function<BlockState, ConfiguredModel[]> railStates(
            EnumProperty<RailShape> shapeProp,
            Function<BlockState, ModelFile> normalRail,
            Function<BlockState, ModelFile> ascendingRail,
            Function<BlockState, ModelFile> curvedRail
    ) {
        return state ->
        {
            RailShape shape = state.getValue(shapeProp);

            ModelFile model;
            int rotY = (int) FramedFancyRailModel.getDirectionFromRailShape(shape).toYRot();
            if (shape.isAscending())
            {
                model = ascendingRail.apply(state);
                rotY = (rotY + 180) % 360;
            }
            else if (shape == RailShape.NORTH_SOUTH || shape == RailShape.EAST_WEST)
            {
                model = normalRail.apply(state);
            }
            else
            {
                model = curvedRail.apply(state);
                if (shape == RailShape.NORTH_EAST || shape == RailShape.SOUTH_WEST)
                {
                    rotY = (rotY + 180) % 360;
                }
                else
                {
                    rotY = (rotY + 90) % 360;
                }
            }

            return ConfiguredModel.builder()
                    .rotationY(rotY)
                    .modelFile(model)
                    .build();
        };
    }

    private void registerFramedFancyRail()
    {
        ModelFile normalRail = existingBlock(FBContent.BLOCK_FRAMED_FANCY_RAIL);
        ModelFile ascendingRail = existingBlock(FBContent.BLOCK_FRAMED_FANCY_RAIL, "ascending");
        ModelFile curvedRail = existingBlock(FBContent.BLOCK_FRAMED_FANCY_RAIL, "curved");

        getVariantBuilder(FBContent.BLOCK_FRAMED_FANCY_RAIL.get()).forAllStatesExcept(
                railStates(BlockStateProperties.RAIL_SHAPE, state -> normalRail, state -> ascendingRail, state -> curvedRail),
                BlockStateProperties.WATERLOGGED, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT
        );
    }

    private void registerFramedFancyPoweredRail()
    {
        ModelFile normalRail = existingBlock(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL);
        ModelFile normalRailOn = block(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, "on")
                .parent(normalRail)
                .texture("texture", mcLoc("block/powered_rail_on"))
                .texture("particle", mcLoc("block/powered_rail_on"));
        ModelFile ascendingRail = existingBlock(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, "ascending");
        ModelFile ascendingRailOn = block(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL, "ascending_on")
                .parent(ascendingRail)
                .texture("texture", mcLoc("block/powered_rail_on"))
                .texture("particle", mcLoc("block/powered_rail_on"));

        getVariantBuilder(FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL.get()).forAllStatesExcept(
                railStates(
                        BlockStateProperties.RAIL_SHAPE_STRAIGHT,
                        state -> state.getValue(BlockStateProperties.POWERED) ? normalRailOn : normalRail,
                        state -> state.getValue(BlockStateProperties.POWERED) ? ascendingRailOn : ascendingRail,
                        state -> null
                ),
                BlockStateProperties.WATERLOGGED, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT
        );
    }

    private void registerFramedFancyDetectorRail()
    {
        ModelFile normalRail = existingBlock(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL);
        ModelFile normalRailOn = block(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, "on")
                .parent(normalRail)
                .texture("texture", mcLoc("block/detector_rail_on"))
                .texture("particle", mcLoc("block/detector_rail_on"));
        ModelFile ascendingRail = existingBlock(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, "ascending");
        ModelFile ascendingRailOn = block(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL, "ascending_on")
                .parent(ascendingRail)
                .texture("texture", mcLoc("block/detector_rail_on"))
                .texture("particle", mcLoc("block/detector_rail_on"));

        getVariantBuilder(FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.get()).forAllStatesExcept(
                railStates(
                        BlockStateProperties.RAIL_SHAPE_STRAIGHT,
                        state -> state.getValue(BlockStateProperties.POWERED) ? normalRailOn : normalRail,
                        state -> state.getValue(BlockStateProperties.POWERED) ? ascendingRailOn : ascendingRail,
                        state -> null
                ),
                BlockStateProperties.WATERLOGGED, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT
        );
    }

    private void registerFramedFancyActivatorRail()
    {
        ModelFile normalRail = existingBlock(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL);
        ModelFile normalRailOn = block(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, "on")
                .parent(normalRail)
                .texture("texture", mcLoc("block/activator_rail_on"))
                .texture("particle", mcLoc("block/activator_rail_on"));
        ModelFile ascendingRail = existingBlock(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, "ascending");
        ModelFile ascendingRailOn = block(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL, "ascending_on")
                .parent(ascendingRail)
                .texture("texture", mcLoc("block/activator_rail_on"))
                .texture("particle", mcLoc("block/activator_rail_on"));

        getVariantBuilder(FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL.get()).forAllStatesExcept(
                railStates(
                        BlockStateProperties.RAIL_SHAPE_STRAIGHT,
                        state -> state.getValue(BlockStateProperties.POWERED) ? normalRailOn : normalRail,
                        state -> state.getValue(BlockStateProperties.POWERED) ? ascendingRailOn : ascendingRail,
                        state -> null
                ),
                BlockStateProperties.WATERLOGGED, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT
        );
    }

    private void registerFramedMiniCube(ModelFile cube)
    {
        simpleBlock(FBContent.BLOCK_FRAMED_MINI_CUBE.get(), cube);

        itemModels().withExistingParent("framed_mini_cube", mcLoc("block/block"))
                .element()
                .from(4, 0, 4)
                .to(12, 8, 12)
                .allFaces((d, f) -> f.uvs(0, 0, 16, 16))
                .textureAll("#all")
                .end()
                .texture("all", TEXTURE)
                .renderType("cutout");
    }

    private void registerFramedOneWayWindow()
    {
        ModelFile model = makeUnderlayedCube("framed_one_way_window", mcLoc("block/moss_block"));
        simpleBlockWithItem(FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW, model);
    }



    private void registerFramingSaw()
    {
        ModelFile model = models().getExistingFile(modLoc("block/framing_saw"));
        getVariantBuilder(FBContent.BLOCK_FRAMING_SAW.get()).forAllStates(state ->
        {
            int rotY = (int) state.getValue(FramedProperties.FACING_HOR).toYRot();
            return ConfiguredModel.builder()
                    .rotationY(rotY)
                    .modelFile(model)
                    .build();
        });
        simpleBlockItem(FBContent.BLOCK_FRAMING_SAW, model);
    }



    @SuppressWarnings("unused")
    private BlockModelBuilder block(RegistryObject<Block> block)
    {
        return block(block, "");
    }

    private BlockModelBuilder block(RegistryObject<Block> block, String suffix)
    {
        String name = block.getId().getPath();
        String path = "block/" + name;
        if (!suffix.isBlank())
        {
            path += "_" + suffix;
        }
        return models().getBuilder(path);
    }

    private ModelFile existingBlock(RegistryObject<Block> block)
    {
        return existingBlock(block, "");
    }

    private ModelFile existingBlock(RegistryObject<Block> block, String suffix)
    {
        ResourceLocation name = block.getId();
        String path = "block/" + name.getPath();
        if (!suffix.isBlank())
        {
            path += "_" + suffix;
        }
        return models().getExistingFile(new ResourceLocation(name.getNamespace(), path));
    }

    @SuppressWarnings({ "UnusedReturnValue", "SameParameterValue" })
    private ItemModelBuilder simpleBlockWithItem(RegistryObject<Block> block, ModelFile model, String itemRenderType)
    {
        return simpleBlockWithItem(block, model).renderType(itemRenderType);
    }

    private ItemModelBuilder simpleBlockWithItem(RegistryObject<Block> block, ModelFile model)
    {
        simpleBlock(block.get(), model);
        return simpleBlockItem(block, model);
    }

    @SuppressWarnings({ "UnusedReturnValue", "SameParameterValue" })
    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> block, ModelFile model, String renderType)
    {
        return simpleBlockItem(block, model).renderType(renderType);
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> block, ModelFile model)
    {
        return itemModels().getBuilder(block.getId().getPath()).parent(model);
    }

    @SuppressWarnings({ "UnusedReturnValue", "SameParameterValue" })
    private ItemModelBuilder simpleItem(RegistryObject<Block> block, String renderType)
    {
        return simpleItem(block.getId().getPath(), renderType);
    }

    @SuppressWarnings({ "UnusedReturnValue", "SameParameterValue" })
    private ItemModelBuilder simpleItem(RegistryObject<Block> block, String texture, String renderType)
    {
        return simpleItem(block.getId().getPath(), texture, renderType);
    }

    private ItemModelBuilder simpleItem(String name, String renderType)
    {
        return simpleItem(name, "item/" + name, renderType);
    }

    private ItemModelBuilder simpleItem(String name, String texture, String renderType)
    {
        return itemModels().singleTexture(name, mcLoc("item/generated"), "layer0", modLoc(texture)).renderType(renderType);
    }

    private BlockModelBuilder makeUnderlayedCube(String name, ResourceLocation underlayTex)
    {
        return models().withExistingParent(name, "block/block")
                .element()
                    .cube("#underlay")
                    .end()
                .element()
                    .cube("#frame")
                    .end()
                .texture("frame", TEXTURE)
                .texture("underlay", underlayTex)
                .texture("particle", TEXTURE)
                .renderType("cutout");
    }

    @SuppressWarnings("SameParameterValue")
    private void makeOverlayModel(ResourceLocation name, ResourceLocation parent, String textureKey, ResourceLocation texture)
    {
        makeOverlayModel(name, parent, textureKey, texture, null);
    }

    private void makeOverlayModel(ResourceLocation name, ResourceLocation parent, String textureKey, ResourceLocation texture, Vector3f center)
    {
        OverlayLoaderBuilder builder = models().getBuilder(name.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(parent))
                        .texture(textureKey, texture)
                );

        if (center != null)
        {
            builder.center(center);
        }
    }
}
