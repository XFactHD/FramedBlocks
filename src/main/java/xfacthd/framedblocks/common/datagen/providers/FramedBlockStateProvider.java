package xfacthd.framedblocks.common.datagen.providers;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.ClientUtils;
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

    public FramedBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, FramedConstants.MOD_ID, fileHelper);
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

        simpleBlockWithItem(FBContent.blockFramedSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedCornerSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInnerCornerSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedPrismCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInnerPrismCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedThreewayCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInnerThreewayCorner, cube, "cutout");
        simpleBlock(FBContent.blockFramedSlabEdge.get(), cube);
        simpleBlock(FBContent.blockFramedSlabCorner.get(), cube);
        simpleBlock(FBContent.blockFramedDividedSlab.get(), cube);
        simpleBlock(FBContent.blockFramedPanel.get(), cube);
        simpleBlock(FBContent.blockFramedCornerPillar.get(), cube);
        simpleBlock(FBContent.blockFramedDividedPanelHor.get(), cube);
        simpleBlock(FBContent.blockFramedDividedPanelVert.get(), cube);
        simpleBlock(FBContent.blockFramedIronTrapDoor.get(), ironCube);
        simpleBlock(FBContent.blockFramedStoneButton.get(), stoneCube);
        simpleBlock(FBContent.blockFramedWallSign.get(), cube);
        simpleBlock(FBContent.blockFramedLattice.get(), cube);
        simpleBlock(FBContent.blockFramedVerticalStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoublePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoublePrismCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleThreewayCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedRailSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedPoweredRailSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDetectorRailSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedActivatorRailSlope, cube, "cutout");
        simpleBlock(FBContent.blockFramedPillar.get(), cube);
        simpleBlock(FBContent.blockFramedHalfPillar.get(), cube);
        simpleBlock(FBContent.blockFramedPost.get(), cube);
        simpleBlock(FBContent.blockFramedHalfStairs.get(), cube);
        simpleBlock(FBContent.blockFramedDividedStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedPrism, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInnerPrism, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoublePrism, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedSlopedPrism, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInnerSlopedPrism, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopedPrism, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedSlopeSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedElevatedSlopeSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopeSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInverseDoubleSlopeSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedElevatedDoubleSlopeSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedStackedSlopeSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatSlopeSlabCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatInnerSlopeSlabCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatElevatedSlopeSlabCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatDoubleSlopeSlabCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatElevatedDoubleSlopeSlabCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatStackedSlopeSlabCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatStackedInnerSlopeSlabCorner, cube, "cutout");
        simpleBlock(FBContent.blockFramedVerticalHalfStairs.get(), cube);
        simpleBlock(FBContent.blockFramedVerticalDividedStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedExtendedSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInverseDoubleSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedExtendedDoubleSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedStackedSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatInnerSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatExtendedSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatExtendedInnerSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatDoubleSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatExtendedDoubleSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatExtendedInnerDoubleSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatStackedSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFlatStackedInnerSlopePanelCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleStairs, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedVerticalDoubleStairs, cube, "cutout");
        simpleBlock(FBContent.blockFramedWallBoard.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedPyramid, cube);
        simpleBlockWithItem(FBContent.blockFramedPyramidSlab, cube);
        simpleBlock(FBContent.blockFramedHorizontalPane.get(), cube);
        simpleBlock(FBContent.blockFramedLargeButton.get(), cube);
        simpleBlock(FBContent.blockFramedLargeStoneButton.get(), stoneCube);
        simpleBlock(FBContent.blockFramedGate.get(), cube);
        simpleBlock(FBContent.blockFramedIronGate.get(), ironCube);
        simpleBlockWithItem(FBContent.blockFramedFancyRailSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFancyPoweredRailSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFancyDetectorRailSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedFancyActivatorRailSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedHalfSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedVerticalHalfSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDividedSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleHalfSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedVerticalDoubleHalfSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedSlopedStairs, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedVerticalSlopedStairs, cube, "cutout");

        registerFramedCube(cube);
        registerFramedSlab(cube);
        registerFramedStairs(cube);
        registerFramedWall(cube);
        registerFramedFence(cube);
        registerFramedGate(cube);
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

        getMultipartBuilder(FBContent.blockFramedCube.get())
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

        simpleBlockItem(FBContent.blockFramedCube, cube);
    }

    private void registerFramedSlab(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedSlab.get(), cube);
        itemModels().slab("framed_slab", TEXTURE, TEXTURE, TEXTURE).renderType("cutout");
    }

    private void registerFramedStairs(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedStairs.get(), cube);
        itemModels().stairs("framed_stairs", TEXTURE, TEXTURE, TEXTURE).renderType("cutout");
    }

    private void registerFramedWall(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedWall.get(), cube);
        itemModels().getBuilder("framed_wall")
                .parent(models().getExistingFile(mcLoc("block/wall_inventory")))
                .texture("wall", TEXTURE)
                .renderType("cutout");
    }

    private void registerFramedFence(ModelFile cube)
    {
        getMultipartBuilder(FBContent.blockFramedFence.get())
                .part()
                .modelFile(cube)
                .addModel();

        itemModels().getBuilder("framed_fence")
                .parent(models().getExistingFile(mcLoc("block/fence_inventory")))
                .texture("texture", TEXTURE)
                .renderType("cutout");
    }

    private void registerFramedGate(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedFenceGate.get(), cube);
        itemModels().fenceGate("framed_gate", TEXTURE).renderType("cutout");
    }

    private void registerFramedDoor(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedDoor.get(), cube);
        simpleItem(FBContent.blockFramedDoor, "cutout");
    }

    private void registerFramedIronDoor(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedIronDoor.get(), cube);
        simpleItem(FBContent.blockFramedIronDoor, "cutout");
    }

    private void registerFramedTrapDoor(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedTrapDoor.get(), cube);
        itemModels().withExistingParent("framed_trapdoor", mcLoc("block/template_orientable_trapdoor_bottom"))
                .texture("texture", TEXTURE)
                .renderType("cutout");
    }

    private void registerFramedPressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedPressurePlate.get(), cube);
        simpleBlock(FBContent.blockFramedWaterloggablePressurePlate.get(), cube);

        itemModels().withExistingParent("framed_pressure_plate", mcLoc("block/pressure_plate_up"))
                .texture("texture", TEXTURE).renderType("cutout");
    }

    private void registerFramedStonePressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedStonePressurePlate.get(), cube);
        simpleBlock(FBContent.blockFramedWaterloggableStonePressurePlate.get(), cube);

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
        simpleBlock(FBContent.blockFramedObsidianPressurePlate.get(), cube);
        simpleBlock(FBContent.blockFramedWaterloggableObsidianPressurePlate.get(), cube);

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
        simpleBlock(FBContent.blockFramedGoldPressurePlate.get(), cube);
        simpleBlock(FBContent.blockFramedWaterloggableGoldPressurePlate.get(), cube);

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
        simpleBlock(FBContent.blockFramedIronPressurePlate.get(), cube);
        simpleBlock(FBContent.blockFramedWaterloggableIronPressurePlate.get(), cube);

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
        simpleBlockWithItem(FBContent.blockFramedLadder, ladder, "cutout");
    }

    private void registerFramedButton(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedButton.get(), cube);

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

        getVariantBuilder(FBContent.blockFramedLever.get()).forAllStates(state ->
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
        });

        simpleItem(FBContent.blockFramedLever, "cutout");
    }

    private void registerFramedSign(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedSign.get(), cube);
        simpleItem(FBContent.blockFramedSign, "cutout");
    }

    private void registerFramedTorch()
    {
        ModelFile torch = models().getExistingFile(modLoc("framed_torch"));
        simpleBlock(FBContent.blockFramedTorch.get(), torch);
        simpleItem(FBContent.blockFramedTorch, "block/framed_torch", "cutout");
    }

    private void registerFramedWallTorch()
    {
        ModelFile wallTorch = models().getExistingFile(modLoc("framed_wall_torch"));
        getVariantBuilder(FBContent.blockFramedWallTorch.get()).forAllStates(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.toYRot() + 90) % 360;
            return ConfiguredModel.builder().modelFile(wallTorch).rotationY(rotY).build();
        });
    }

    private void registerFramedSoulTorch()
    {
        ModelFile torch = models().withExistingParent("framed_soul_torch", modLoc("framed_torch"))
                .texture("particle", modLoc("block/framed_soul_torch"))
                .texture("top", mcLoc("block/soul_torch"));
        simpleBlock(FBContent.blockFramedSoulTorch.get(), torch);
        simpleItem(FBContent.blockFramedSoulTorch, "block/framed_soul_torch", "cutout");
    }

    private void registerFramedSoulWallTorch()
    {
        ModelFile wallTorch = models().withExistingParent("framed_soul_wall_torch", modLoc("framed_wall_torch"))
                .texture("particle", modLoc("block/framed_soul_torch"))
                .texture("top", mcLoc("block/soul_torch"));
        getVariantBuilder(FBContent.blockFramedSoulWallTorch.get()).forAllStates(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.toYRot() + 90) % 360;
            return ConfiguredModel.builder().modelFile(wallTorch).rotationY(rotY).build();
        });
    }

    private void registerFramedRedstoneTorch()
    {
        ModelFile torch = models().getExistingFile(modLoc("framed_redstone_torch"));
        ModelFile torchOff = models().withExistingParent("framed_redstone_torch_off", modLoc("framed_torch"))
                .texture("particle", modLoc("block/framed_redstone_torch_off"))
                .texture("top", mcLoc("block/redstone_torch_off"));

        getVariantBuilder(FBContent.blockFramedRedstoneTorch.get()).forAllStates(state ->
        {
            ModelFile model = state.getValue(BlockStateProperties.LIT) ? torch : torchOff;
            return ConfiguredModel.builder().modelFile(model).build();
        });

        simpleItem(FBContent.blockFramedRedstoneTorch, "block/framed_redstone_torch", "cutout");
    }

    private void registerFramedRedstoneWallTorch()
    {
        ModelFile wallTorch = models().getExistingFile(modLoc("framed_redstone_wall_torch"));
        ModelFile wallTorchOff = models().withExistingParent("framed_redstone_wall_torch_off", modLoc("framed_wall_torch"))
                .texture("particle", modLoc("block/framed_redstone_torch_off"))
                .texture("top", mcLoc("block/redstone_torch_off"));

        getVariantBuilder(FBContent.blockFramedRedstoneWallTorch.get()).forAllStates(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.toYRot() + 90) % 360;
            ModelFile model = state.getValue(BlockStateProperties.LIT) ? wallTorch : wallTorchOff;
            return ConfiguredModel.builder().modelFile(model).rotationY(rotY).build();
        });
    }

    private void registerFramedFloorBoard(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedFloor.get(), cube);
        itemModels().carpet("framed_floor_board", TEXTURE).renderType("cutout");
    }

    private void registerFramedChest()
    {
        ModelFile chest = models().getExistingFile(modLoc("block/framed_chest"));

        getVariantBuilder(FBContent.blockFramedChest.get()).forAllStatesExcept(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.toYRot() + 180) % 360;
            return ConfiguredModel.builder().modelFile(chest).rotationY(rotY).build();
        }, BlockStateProperties.WATERLOGGED, PropertyHolder.LATCH_TYPE);

        simpleBlockItem(FBContent.blockFramedChest, chest, "cutout");
    }

    private void registerFramedBarsBlock(ModelFile cube)
    {
        getMultipartBuilder(FBContent.blockFramedBars.get())
                .part()
                .modelFile(cube)
                .addModel();

        simpleItem(FBContent.blockFramedBars, "cutout");
    }

    private void registerFramedPaneBlock(ModelFile cube)
    {
        getMultipartBuilder(FBContent.blockFramedPane.get())
                .part()
                .modelFile(cube)
                .addModel();

        simpleItem(FBContent.blockFramedPane, TEXTURE.getPath(), "cutout");
    }

    private void registerFramedFlowerPotBlock(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedFlowerPot.get(), cube);
        simpleItem(FBContent.blockFramedFlowerPot, "cutout");
    }

    private void registerFramedCollapsibleBlock()
    {
        ModelFile block = makeUnderlayedCube("framed_collapsible_block", mcLoc("block/oak_planks")).renderType("cutout");
        simpleBlockWithItem(FBContent.blockFramedCollapsibleBlock, block, "cutout");
    }

    private void registerFramedBouncyBlock()
    {
        ModelFile block = makeUnderlayedCube("framed_bouncy_cube", mcLoc("block/slime_block"));

        simpleBlockWithItem(FBContent.blockFramedBouncyCube, block, "cutout");

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

        simpleBlockWithItem(FBContent.blockFramedSecretStorage, block, "cutout");
    }

    private void registerFramedRedstoneBlock()
    {
        ModelFile block = makeUnderlayedCube("framed_redstone_block", mcLoc("block/redstone_block"));

        simpleBlockWithItem(FBContent.blockFramedRedstoneBlock, block, "cutout");

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
        simpleBlockWithItem(FBContent.blockFramedGlowingCube, block);
    }

    private void registerFramedTarget(ModelFile cube)
    {
        simpleBlockWithItem(FBContent.blockFramedTarget, cube, "cutout");

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

        getVariantBuilder(FBContent.blockFramedItemFrame.get())
                .forAllStatesExcept(mapper.apply(normalFrame, normalMapFrame), PropertyHolder.LEATHER);
        getVariantBuilder(FBContent.blockFramedGlowingItemFrame.get())
                .forAllStatesExcept(mapper.apply(glowFrame, glowMapFrame), PropertyHolder.LEATHER);

        simpleItem(FBContent.blockFramedItemFrame, "cutout");
        simpleItem(FBContent.blockFramedGlowingItemFrame, "cutout");
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
        ModelFile normalRail = existingBlock(FBContent.blockFramedFancyRail);
        ModelFile ascendingRail = existingBlock(FBContent.blockFramedFancyRail, "ascending");
        ModelFile curvedRail = existingBlock(FBContent.blockFramedFancyRail, "curved");

        getVariantBuilder(FBContent.blockFramedFancyRail.get()).forAllStatesExcept(
                railStates(BlockStateProperties.RAIL_SHAPE, state -> normalRail, state -> ascendingRail, state -> curvedRail),
                BlockStateProperties.WATERLOGGED
        );
    }

    private void registerFramedFancyPoweredRail()
    {
        ModelFile normalRail = existingBlock(FBContent.blockFramedFancyPoweredRail);
        ModelFile normalRailOn = block(FBContent.blockFramedFancyPoweredRail, "on")
                .parent(normalRail)
                .texture("texture", mcLoc("block/powered_rail_on"))
                .texture("particle", mcLoc("block/powered_rail_on"));
        ModelFile ascendingRail = existingBlock(FBContent.blockFramedFancyPoweredRail, "ascending");
        ModelFile ascendingRailOn = block(FBContent.blockFramedFancyPoweredRail, "ascending_on")
                .parent(ascendingRail)
                .texture("texture", mcLoc("block/powered_rail_on"))
                .texture("particle", mcLoc("block/powered_rail_on"));

        getVariantBuilder(FBContent.blockFramedFancyPoweredRail.get()).forAllStatesExcept(
                railStates(
                        BlockStateProperties.RAIL_SHAPE_STRAIGHT,
                        state -> state.getValue(BlockStateProperties.POWERED) ? normalRailOn : normalRail,
                        state -> state.getValue(BlockStateProperties.POWERED) ? ascendingRailOn : ascendingRail,
                        state -> null
                ),
                BlockStateProperties.WATERLOGGED
        );
    }

    private void registerFramedFancyDetectorRail()
    {
        ModelFile normalRail = existingBlock(FBContent.blockFramedFancyDetectorRail);
        ModelFile normalRailOn = block(FBContent.blockFramedFancyDetectorRail, "on")
                .parent(normalRail)
                .texture("texture", mcLoc("block/detector_rail_on"))
                .texture("particle", mcLoc("block/detector_rail_on"));
        ModelFile ascendingRail = existingBlock(FBContent.blockFramedFancyDetectorRail, "ascending");
        ModelFile ascendingRailOn = block(FBContent.blockFramedFancyDetectorRail, "ascending_on")
                .parent(ascendingRail)
                .texture("texture", mcLoc("block/detector_rail_on"))
                .texture("particle", mcLoc("block/detector_rail_on"));

        getVariantBuilder(FBContent.blockFramedFancyDetectorRail.get()).forAllStatesExcept(
                railStates(
                        BlockStateProperties.RAIL_SHAPE_STRAIGHT,
                        state -> state.getValue(BlockStateProperties.POWERED) ? normalRailOn : normalRail,
                        state -> state.getValue(BlockStateProperties.POWERED) ? ascendingRailOn : ascendingRail,
                        state -> null
                ),
                BlockStateProperties.WATERLOGGED
        );
    }

    private void registerFramedFancyActivatorRail()
    {
        ModelFile normalRail = existingBlock(FBContent.blockFramedFancyActivatorRail);
        ModelFile normalRailOn = block(FBContent.blockFramedFancyActivatorRail, "on")
                .parent(normalRail)
                .texture("texture", mcLoc("block/activator_rail_on"))
                .texture("particle", mcLoc("block/activator_rail_on"));
        ModelFile ascendingRail = existingBlock(FBContent.blockFramedFancyActivatorRail, "ascending");
        ModelFile ascendingRailOn = block(FBContent.blockFramedFancyActivatorRail, "ascending_on")
                .parent(ascendingRail)
                .texture("texture", mcLoc("block/activator_rail_on"))
                .texture("particle", mcLoc("block/activator_rail_on"));

        getVariantBuilder(FBContent.blockFramedFancyActivatorRail.get()).forAllStatesExcept(
                railStates(
                        BlockStateProperties.RAIL_SHAPE_STRAIGHT,
                        state -> state.getValue(BlockStateProperties.POWERED) ? normalRailOn : normalRail,
                        state -> state.getValue(BlockStateProperties.POWERED) ? ascendingRailOn : ascendingRail,
                        state -> null
                ),
                BlockStateProperties.WATERLOGGED
        );
    }

    private void registerFramedMiniCube(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedMiniCube.get(), cube);

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
        simpleBlockWithItem(FBContent.blockFramedOneWayWindow, model);
    }



    private void registerFramingSaw()
    {
        ModelFile model = models().getExistingFile(modLoc("block/framing_saw"));
        getVariantBuilder(FBContent.blockFramingSaw.get()).forAllStates(state ->
        {
            int rotY = (int) state.getValue(FramedProperties.FACING_HOR).toYRot();
            return ConfiguredModel.builder()
                    .rotationY(rotY)
                    .modelFile(model)
                    .build();
        });
        simpleBlockItem(FBContent.blockFramingSaw, model);
    }



    @SuppressWarnings("unused")
    private BlockModelBuilder block(RegistryObject<Block> block) { return block(block, ""); }

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

    private ModelFile existingBlock(RegistryObject<Block> block) { return existingBlock(block, ""); }

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