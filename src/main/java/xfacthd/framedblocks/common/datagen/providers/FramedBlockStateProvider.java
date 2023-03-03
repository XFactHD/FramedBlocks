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
        ModelFile cube = models().cubeAll("framed_cube", TEXTURE);

        simpleBlockWithItem(FBContent.blockFramedCube, cube);
        simpleBlockWithItem(FBContent.blockFramedSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedCornerSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedInnerCornerSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedPrismCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedInnerPrismCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedThreewayCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedInnerThreewayCorner, cube);
        simpleBlock(FBContent.blockFramedSlabEdge.get(), cube);
        simpleBlock(FBContent.blockFramedSlabCorner.get(), cube);
        simpleBlock(FBContent.blockFramedDividedSlab.get(), cube);
        simpleBlock(FBContent.blockFramedPanel.get(), cube);
        simpleBlock(FBContent.blockFramedCornerPillar.get(), cube);
        simpleBlock(FBContent.blockFramedDividedPanelHor.get(), cube);
        simpleBlock(FBContent.blockFramedDividedPanelVert.get(), cube);
        simpleBlock(FBContent.blockFramedWallSign.get(), cube);
        simpleBlock(FBContent.blockFramedLattice.get(), cube);
        simpleBlock(FBContent.blockFramedVerticalStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedDoublePanel, cube);
        simpleBlock(FBContent.blockFramedDoubleSlope.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedDoublePrismCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleThreewayCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedRailSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedPoweredRailSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedDetectorRailSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedActivatorRailSlope, cube);
        simpleBlock(FBContent.blockFramedPillar.get(), cube);
        simpleBlock(FBContent.blockFramedHalfPillar.get(), cube);
        simpleBlock(FBContent.blockFramedPost.get(), cube);
        simpleBlock(FBContent.blockFramedHalfStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedPrism, cube);
        simpleBlockWithItem(FBContent.blockFramedInnerPrism, cube);
        simpleBlockWithItem(FBContent.blockFramedDoublePrism, cube);
        simpleBlockWithItem(FBContent.blockFramedSlopedPrism, cube);
        simpleBlockWithItem(FBContent.blockFramedInnerSlopedPrism, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopedPrism, cube);
        simpleBlockWithItem(FBContent.blockFramedSlopeSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedElevatedSlopeSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopeSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedInverseDoubleSlopeSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedElevatedDoubleSlopeSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedStackedSlopeSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatSlopeSlabCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatInnerSlopeSlabCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatElevatedSlopeSlabCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatDoubleSlopeSlabCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatInverseDoubleSlopeSlabCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatElevatedDoubleSlopeSlabCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatElevatedInnerDoubleSlopeSlabCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatStackedSlopeSlabCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatStackedInnerSlopeSlabCorner, cube);
        simpleBlock(FBContent.blockFramedVerticalHalfStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedExtendedSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedInverseDoubleSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedExtendedDoubleSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedStackedSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatInnerSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatExtendedSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatExtendedInnerSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatDoubleSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatInverseDoubleSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatExtendedDoubleSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatExtendedInnerDoubleSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatStackedSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedFlatStackedInnerSlopePanelCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleStairs, cube);
        simpleBlockWithItem(FBContent.blockFramedVerticalDoubleStairs, cube);
        simpleBlock(FBContent.blockFramedWallBoard.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedPyramid, cube);
        simpleBlockWithItem(FBContent.blockFramedPyramidSlab, cube);
        simpleBlock(FBContent.blockFramedLargeButton.get(), cube);
        simpleBlock(FBContent.blockFramedHorizontalPane.get(), cube);
        simpleBlock(FBContent.blockFramedGate.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedFancyRailSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedFancyPoweredRailSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedFancyDetectorRailSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedFancyActivatorRailSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedHalfSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedVerticalHalfSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedDividedSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleHalfSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedVerticalDoubleHalfSlope, cube);
        simpleBlockWithItem(FBContent.blockFramedSlopedStairs, cube);
        simpleBlockWithItem(FBContent.blockFramedVerticalSlopedStairs, cube);

        registerFramedSlab(cube);
        registerFramedStairs(cube);
        registerFramedWall(cube);
        registerFramedFence(cube);
        registerFramedGate(cube);
        registerFramedDoor(cube);
        registerFramedTrapDoor(cube);
        registerFramedIronDoor();
        registerFramedIronTrapDoor();
        registerFramedPressurePlate(cube);
        registerFramedStonePressurePlate();
        registerFramedObsidianPressurePlate();
        registerFramedGoldPressurePlate();
        registerFramedIronPressurePlate();
        registerFramedLadder();
        registerFramedButton(cube);
        registerFramedStoneButton();
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
        registerFramedLargeStoneButton();
        registerFramedTarget(cube);
        registerFramedIronGate();
        registerFramedItemFrame();
        registerFramedFancyRail();
        registerFramedFancyPoweredRail();
        registerFramedFancyDetectorRail();
        registerFramedFancyActivatorRail();
        registerFramedMiniCube(cube);

        registerFramingSaw();
    }

    private void registerFramedSlab(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedSlab.get(), cube);
        itemModels().slab("framed_slab", TEXTURE, TEXTURE, TEXTURE);
    }

    private void registerFramedStairs(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedStairs.get(), cube);
        itemModels().stairs("framed_stairs", TEXTURE, TEXTURE, TEXTURE);
    }

    private void registerFramedWall(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedWall.get(), cube);
        itemModels().getBuilder("framed_wall")
                .parent(models().getExistingFile(mcLoc("block/wall_inventory")))
                .texture("wall", TEXTURE);
    }

    private void registerFramedFence(ModelFile cube)
    {
        getMultipartBuilder(FBContent.blockFramedFence.get())
                .part()
                .modelFile(cube)
                .addModel();

        itemModels().getBuilder("framed_fence")
                .parent(models().getExistingFile(mcLoc("block/fence_inventory")))
                .texture("texture", TEXTURE);
    }

    private void registerFramedGate(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedFenceGate.get(), cube);
        itemModels().fenceGate("framed_gate", TEXTURE);
    }

    private void registerFramedDoor(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedDoor.get(), cube);
        itemModels().singleTexture("framed_door", mcLoc("item/generated"), "layer0", modLoc("item/framed_door"));
    }

    private void registerFramedTrapDoor(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedTrapDoor.get(), cube);
        itemModels().withExistingParent("framed_trapdoor", mcLoc("block/template_orientable_trapdoor_bottom"))
                .texture("texture", TEXTURE);
    }

    private void registerFramedIronDoor()
    {
        ModelFile door = models().getExistingFile(modLoc("block/framed_iron_door"));
        doorBlock(
                (DoorBlock) FBContent.blockFramedIronDoor.get(),
                door, door, door, door
        );
        itemModels().singleTexture("framed_iron_door", mcLoc("item/generated"), "layer0", modLoc("item/framed_iron_door"));
    }

    private void registerFramedIronTrapDoor()
    {
        ModelFile trapdoorBot = models().getExistingFile(modLoc("block/framed_iron_trapdoor_bot"));
        ModelFile trapdoorTop = models().getExistingFile(modLoc("block/framed_iron_trapdoor_top"));
        ModelFile trapdoorOpen = models().getExistingFile(modLoc("block/framed_iron_trapdoor_open"));
        trapdoorBlock(
                (TrapDoorBlock) FBContent.blockFramedIronTrapDoor.get(),
                trapdoorBot,
                trapdoorTop,
                trapdoorOpen,
                true
        );
        simpleBlockItem(FBContent.blockFramedIronTrapDoor.get(), trapdoorBot);
    }

    private void registerFramedPressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedPressurePlate.get(), cube);
        simpleBlock(FBContent.blockFramedWaterloggablePressurePlate.get(), cube);

        itemModels().withExistingParent("framed_pressure_plate", mcLoc("block/pressure_plate_up"))
                .texture("texture", TEXTURE);
    }

    private void registerFramedStonePressurePlate()
    {
        ModelFile modelUp = models().withExistingParent(
                "framed_stone_pressure_plate_up",
                modLoc("block/framed_pressure_plate_up")
        ).texture("background", mcLoc("block/stone"));
        ModelFile modelDown = models().withExistingParent(
                "framed_stone_pressure_plate_down",
                modLoc("block/framed_pressure_plate_down")
        ).texture("background", mcLoc("block/stone"));

        Function<BlockState, ConfiguredModel[]> mapper = state ->
        {
            boolean pressed = state.getValue(PressurePlateBlock.POWERED);
            return ConfiguredModel.builder()
                    .modelFile(pressed ? modelDown : modelUp)
                    .build();
        };

        getVariantBuilder(FBContent.blockFramedStonePressurePlate.get()).forAllStates(mapper);
        getVariantBuilder(FBContent.blockFramedWaterloggableStonePressurePlate.get()).forAllStates(mapper);

        models().getBuilder(FramedMarkedPressurePlateModel.STONE_FRAME_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(modLoc("block/framed_pressure_plate_frame_up")))
                        .texture("texture", modLoc("block/stone_plate_frame"))
                )
                .center(new Vector3f(8F, 0.5F, 8F));

        models().getBuilder(FramedMarkedPressurePlateModel.STONE_FRAME_DOWN_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(modLoc("block/framed_pressure_plate_frame_down")))
                        .texture("texture", modLoc("block/stone_plate_frame"))
                )
                .center(new Vector3f(8F, 0.25F, 8F));

        itemModels().withExistingParent("framed_stone_pressure_plate", modLoc("block/framed_stone_pressure_plate_up"));
    }

    private void registerFramedObsidianPressurePlate()
    {
        ModelFile modelUp = models().withExistingParent(
                "framed_obsidian_pressure_plate_up",
                modLoc("block/framed_pressure_plate_up")
        ).texture("background", mcLoc("block/obsidian"));
        ModelFile modelDown = models().withExistingParent(
                "framed_obsidian_pressure_plate_down",
                modLoc("block/framed_pressure_plate_down")
        ).texture("background", mcLoc("block/obsidian"));

        Function<BlockState, ConfiguredModel[]> mapper = state ->
        {
            boolean pressed = state.getValue(PressurePlateBlock.POWERED);
            return ConfiguredModel.builder()
                    .modelFile(pressed ? modelDown : modelUp)
                    .build();
        };

        getVariantBuilder(FBContent.blockFramedObsidianPressurePlate.get()).forAllStates(mapper);
        getVariantBuilder(FBContent.blockFramedWaterloggableObsidianPressurePlate.get()).forAllStates(mapper);

        models().getBuilder(FramedMarkedPressurePlateModel.OBSIDIAN_FRAME_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(modLoc("block/framed_pressure_plate_frame_up")))
                        .texture("texture", modLoc("block/obsidian_plate_frame"))
                )
                .center(new Vector3f(8F, 0.5F, 8F));

        models().getBuilder(FramedMarkedPressurePlateModel.OBSIDIAN_FRAME_DOWN_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(modLoc("block/framed_pressure_plate_frame_down")))
                        .texture("texture", modLoc("block/obsidian_plate_frame"))
                )
                .center(new Vector3f(8F, 0.25F, 8F));

        itemModels().withExistingParent("framed_obsidian_pressure_plate", modLoc("block/framed_obsidian_pressure_plate_up"));
    }

    private void registerFramedGoldPressurePlate()
    {
        ModelFile modelUp = models().withExistingParent(
                "framed_gold_pressure_plate_up",
                modLoc("block/framed_pressure_plate_up")
        ).texture("background", mcLoc("block/gold_block"));
        ModelFile modelDown = models().withExistingParent(
                "framed_gold_pressure_plate_down",
                modLoc("block/framed_pressure_plate_down")
        ).texture("background", mcLoc("block/gold_block"));

        Function<BlockState, ConfiguredModel[]> mapper = state ->
        {
            boolean pressed = state.getValue(WeightedPressurePlateBlock.POWER) > 0;
            return ConfiguredModel.builder()
                    .modelFile(pressed ? modelDown : modelUp)
                    .build();
        };

        getVariantBuilder(FBContent.blockFramedGoldPressurePlate.get()).forAllStates(mapper);
        getVariantBuilder(FBContent.blockFramedWaterloggableGoldPressurePlate.get()).forAllStates(mapper);

        models().getBuilder(FramedMarkedPressurePlateModel.GOLD_FRAME_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(modLoc("block/framed_pressure_plate_frame_up")))
                        .texture("texture", modLoc("block/gold_plate_frame"))
                )
                .center(new Vector3f(8F, 0.5F, 8F));

        models().getBuilder(FramedMarkedPressurePlateModel.GOLD_FRAME_DOWN_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(modLoc("block/framed_pressure_plate_frame_down")))
                        .texture("texture", modLoc("block/gold_plate_frame"))
                )
                .center(new Vector3f(8F, 0.25F, 8F));

        itemModels().withExistingParent("framed_gold_pressure_plate", modLoc("block/framed_gold_pressure_plate_up"));
    }

    private void registerFramedIronPressurePlate()
    {
        ModelFile modelUp = models().withExistingParent(
                "framed_iron_pressure_plate_up",
                modLoc("block/framed_pressure_plate_up")
        ).texture("background", mcLoc("block/iron_block"));
        ModelFile modelDown = models().withExistingParent(
                "framed_iron_pressure_plate_down",
                modLoc("block/framed_pressure_plate_down")
        ).texture("background", mcLoc("block/iron_block"));

        Function<BlockState, ConfiguredModel[]> mapper = state ->
        {
            boolean pressed = state.getValue(WeightedPressurePlateBlock.POWER) > 0;
            return ConfiguredModel.builder()
                    .modelFile(pressed ? modelDown : modelUp)
                    .build();
        };

        getVariantBuilder(FBContent.blockFramedIronPressurePlate.get()).forAllStates(mapper);
        getVariantBuilder(FBContent.blockFramedWaterloggableIronPressurePlate.get()).forAllStates(mapper);

        models().getBuilder(FramedMarkedPressurePlateModel.IRON_FRAME_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(modLoc("block/framed_pressure_plate_frame_up")))
                        .texture("texture", modLoc("block/iron_plate_frame"))
                )
                .center(new Vector3f(8F, 0.5F, 8F));

        models().getBuilder(FramedMarkedPressurePlateModel.IRON_FRAME_DOWN_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(modLoc("block/framed_pressure_plate_frame_down")))
                        .texture("texture", modLoc("block/iron_plate_frame"))
                )
                .center(new Vector3f(8F, 0.25F, 8F));

        itemModels().withExistingParent("framed_iron_pressure_plate", modLoc("block/framed_iron_pressure_plate_up"));
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
        simpleBlockWithItem(FBContent.blockFramedLadder, ladder);
    }

    private void registerFramedButton(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedButton.get(), cube);

        itemModels().getBuilder("framed_button")
                .parent(models().getExistingFile(mcLoc("block/button_inventory")))
                .texture("texture", TEXTURE);
    }

    private void registerFramedStoneButton()
    {
        ModelFile button = models().getExistingFile(modLoc("framed_stone_button"));
        ModelFile buttonPressed = models().getExistingFile(modLoc("framed_stone_button_pressed"));

        getVariantBuilder(FBContent.blockFramedStoneButton.get()).forAllStates(state ->
        {
            Direction facing = state.getValue(ButtonBlock.FACING);
            AttachFace face = state.getValue(ButtonBlock.FACE);
            boolean pressed = state.getValue(ButtonBlock.POWERED);

            int rotX;
            int rotY;

            if (face == AttachFace.WALL)
            {
                rotX = 90;
                rotY = (int)(facing.toYRot() + 180) % 360;
            }
            else
            {
                rotX = face == AttachFace.CEILING ? 180 : 0;
                rotY = (int)facing.toYRot();
            }

            return ConfiguredModel.builder()
                    .modelFile(pressed ? buttonPressed : button)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .build();
        });
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

        itemModels().singleTexture("framed_lever", mcLoc("item/generated"), "layer0", modLoc("item/framed_lever"));
    }

    private void registerFramedSign(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedSign.get(), cube);
        itemModels().singleTexture("framed_sign", mcLoc("item/generated"), "layer0", modLoc("item/framed_sign"));
    }

    private void registerFramedTorch()
    {
        ModelFile torch = models().getExistingFile(modLoc("framed_torch"));
        simpleBlock(FBContent.blockFramedTorch.get(), torch);
        itemModels().withExistingParent("framed_torch", "item/generated").texture("layer0", modLoc("block/framed_torch"));
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
        itemModels().withExistingParent("framed_soul_torch", "item/generated").texture("layer0", modLoc("block/framed_soul_torch"));
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

        itemModels().withExistingParent("framed_redstone_torch", "item/generated").texture("layer0", modLoc("block/framed_redstone_torch"));
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
        itemModels().carpet("framed_floor_board", TEXTURE);
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

        simpleBlockItem(FBContent.blockFramedChest.get(), chest);
    }

    private void registerFramedBarsBlock(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedBars.get(), cube);
        itemModels().withExistingParent("framed_bars", "item/generated").texture("layer0", modLoc("item/framed_bars"));
    }

    private void registerFramedPaneBlock(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedPane.get(), cube);
        itemModels().withExistingParent("framed_pane", "item/generated").texture("layer0", TEXTURE);
    }

    private void registerFramedFlowerPotBlock(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedFlowerPot.get(), cube);
        itemModels().withExistingParent("framed_flower_pot", "item/generated").texture("layer0", modLoc("item/framed_flower_pot"));
    }

    private void registerFramedCollapsibleBlock()
    {
        ModelFile block = makeUnderlayedCube("framed_collapsible_block", mcLoc("block/oak_planks"));
        simpleBlockWithItem(FBContent.blockFramedCollapsibleBlock, block);
    }

    private void registerFramedBouncyBlock()
    {
        ModelFile block = models().withExistingParent("framed_bouncy_cube", "block/block")
                .element()
                    .cube("#slime")
                    .end()
                .element()
                    .cube("#frame")
                    .end()
                .texture("frame", TEXTURE)
                .texture("slime", mcLoc("block/slime_block"))
                .texture("particle", TEXTURE);

        simpleBlockWithItem(FBContent.blockFramedBouncyCube, block);

        models().getBuilder(FramedMarkedCubeModel.SLIME_FRAME_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(mcLoc("block/cube_all")))
                        .texture("all", modLoc("block/slime_frame"))
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

        simpleBlockWithItem(FBContent.blockFramedSecretStorage, block);
    }

    private void registerFramedRedstoneBlock()
    {
        ModelFile block = models().withExistingParent("framed_redstone_block", "block/block")
                .element()
                .cube("#redstone")
                .end()
                .element()
                .cube("#frame")
                .end()
                .texture("frame", TEXTURE)
                .texture("redstone", mcLoc("block/redstone_block"))
                .texture("particle", TEXTURE);

        simpleBlockWithItem(FBContent.blockFramedRedstoneBlock, block);

        models().getBuilder(FramedMarkedCubeModel.REDSTONE_FRAME_LOCATION.getPath())
                .customLoader(OverlayLoaderBuilder::new)
                .model(models()
                        .nested()
                        .parent(models().getExistingFile(mcLoc("block/cube_all")))
                        .texture("all", modLoc("block/redstone_frame"))
                );
    }

    private void registerFramedGlowingCube()
    {
        ModelFile block = models().withExistingParent("framed_glowing_cube", "block/block")
                .element()
                .cube("#white")
                .end()
                .element()
                .cube("#frame")
                .end()
                .texture("frame", TEXTURE)
                .texture("white", new ResourceLocation("forge", "white"))
                .texture("particle", TEXTURE);

        simpleBlockWithItem(FBContent.blockFramedGlowingCube, block);
    }

    private void registerFramedLargeStoneButton()
    {
        ModelFile button = models().getExistingFile(modLoc("framed_large_stone_button"));
        ModelFile buttonPressed = models().getExistingFile(modLoc("framed_large_stone_button_pressed"));

        getVariantBuilder(FBContent.blockFramedLargeStoneButton.get()).forAllStates(state ->
        {
            Direction facing = state.getValue(ButtonBlock.FACING);
            AttachFace face = state.getValue(ButtonBlock.FACE);
            boolean pressed = state.getValue(ButtonBlock.POWERED);

            int rotX;
            int rotY;

            if (face == AttachFace.WALL)
            {
                rotX = 90;
                rotY = (int)(facing.toYRot() + 180) % 360;
            }
            else
            {
                rotX = face == AttachFace.CEILING ? 180 : 0;
                rotY = 0;
            }

            return ConfiguredModel.builder()
                    .modelFile(pressed ? buttonPressed : button)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .build();
        });
    }

    private void registerFramedTarget(ModelFile cube)
    {
        simpleBlockWithItem(FBContent.blockFramedTarget, cube);

        models().getBuilder("target_overlay")
                .customLoader(OverlayLoaderBuilder::new)
                .model(models().nested().parent(models().getExistingFile(mcLoc("block/block")))
                        .element()
                        .cube("#overlay")
                        .faces((dir, face) -> face.tintindex(FramedTargetModel.OVERLAY_TINT_IDX))
                        .end()
                        .texture("overlay", modLoc("block/target_overlay"))
                );
    }

    private void registerFramedIronGate()
    {
        ModelFile door = models().getExistingFile(modLoc("block/framed_iron_door"));
        getVariantBuilder(FBContent.blockFramedIronGate.get()).forAllStatesExcept(state ->
        {
            int yRot = ((int) state.getValue(DoorBlock.FACING).toYRot()) + 90;
            boolean right = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
            boolean open = state.getValue(DoorBlock.OPEN);
            if (open)
            {
                yRot += 90;
            }
            if (right && open)
            {
                yRot += 180;
            }
            yRot %= 360;

            return ConfiguredModel.builder().modelFile(door)
                    .rotationY(yRot)
                    .build();
        }, DoorBlock.POWERED, FramedProperties.SOLID, FramedProperties.GLOWING);
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

        itemModels().withExistingParent("framed_item_frame", "item/generated").texture("layer0", modLoc("item/framed_item_frame"));
        itemModels().withExistingParent("framed_glowing_item_frame", "item/generated").texture("layer0", modLoc("item/framed_glowing_item_frame"));
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
                .texture("all", TEXTURE);
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
        simpleBlockItem(FBContent.blockFramingSaw.get(), model);
    }



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

    private void simpleBlockWithItem(RegistryObject<Block> block, ModelFile model)
    {
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
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
                .texture("particle", TEXTURE);
    }
}