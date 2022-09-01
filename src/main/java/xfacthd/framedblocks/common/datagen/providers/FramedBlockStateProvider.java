package xfacthd.framedblocks.common.datagen.providers;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.client.loader.overlay.OverlayLoaderBuilder;
import xfacthd.framedblocks.client.model.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedBlockStateProvider extends BlockStateProvider
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
        simpleBlock(FBContent.blockFramedPanel.get(), cube);
        simpleBlock(FBContent.blockFramedCornerPillar.get(), cube);
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
        simpleBlock(FBContent.blockFramedPillar.get(), cube);
        simpleBlock(FBContent.blockFramedHalfPillar.get(), cube);
        simpleBlock(FBContent.blockFramedPost.get(), cube);
        simpleBlock(FBContent.blockFramedHalfStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedPrism, cube);
        simpleBlockWithItem(FBContent.blockFramedSlopedPrism, cube);
        simpleBlockWithItem(FBContent.blockFramedSlopeSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedElevatedSlopeSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopeSlab, cube);
        simpleBlockWithItem(FBContent.blockFramedInverseDoubleSlopeSlab, cube);
        simpleBlock(FBContent.blockFramedVerticalHalfStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedExtendedSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedInverseDoubleSlopePanel, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleStairs, cube);
        simpleBlockWithItem(FBContent.blockFramedVerticalDoubleStairs, cube);
        simpleBlock(FBContent.blockFramedWallBoard.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedPyramid, cube);
        simpleBlockWithItem(FBContent.blockFramedPyramidSlab, cube);
        simpleBlock(FBContent.blockFramedLargeButton.get(), cube);
        simpleBlock(FBContent.blockFramedHorizontalPane.get(), cube);
        simpleBlock(FBContent.blockFramedGate.get(), cube);

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

        getVariantBuilder(FBContent.blockFramedStonePressurePlate.get()).forAllStates(state ->
        {
            boolean pressed = state.getValue(PressurePlateBlock.POWERED);
            return ConfiguredModel.builder()
                    .modelFile(pressed ? modelDown : modelUp)
                    .build();
        });

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

        getVariantBuilder(FBContent.blockFramedObsidianPressurePlate.get()).forAllStates(state ->
        {
            boolean pressed = state.getValue(PressurePlateBlock.POWERED);
            return ConfiguredModel.builder()
                    .modelFile(pressed ? modelDown : modelUp)
                    .build();
        });

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

        getVariantBuilder(FBContent.blockFramedGoldPressurePlate.get()).forAllStates(state ->
        {
            boolean pressed = state.getValue(WeightedPressurePlateBlock.POWER) > 0;
            return ConfiguredModel.builder()
                    .modelFile(pressed ? modelDown : modelUp)
                    .build();
        });

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

        getVariantBuilder(FBContent.blockFramedIronPressurePlate.get()).forAllStates(state ->
        {
            boolean pressed = state.getValue(WeightedPressurePlateBlock.POWER) > 0;
            return ConfiguredModel.builder()
                    .modelFile(pressed ? modelDown : modelUp)
                    .build();
        });

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
                .texture("base", TEXTURE)
                .texture("particle", TEXTURE);
        ModelFile leverOn = models()
                .withExistingParent("framed_lever_on", mcLoc("block/lever_on"))
                .texture("base", TEXTURE)
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
                .texture("torch", modLoc("block/framed_soul_torch"))
                .texture("particle", modLoc("block/framed_soul_torch"))
                .texture("top", mcLoc("block/soul_torch"));
        simpleBlock(FBContent.blockFramedSoulTorch.get(), torch);
        itemModels().withExistingParent("framed_soul_torch", "item/generated").texture("layer0", modLoc("block/framed_soul_torch"));
    }

    private void registerFramedSoulWallTorch()
    {
        ModelFile wallTorch = models().withExistingParent("framed_soul_wall_torch", modLoc("framed_wall_torch"))
                .texture("torch", modLoc("block/framed_soul_torch"))
                .texture("particle", modLoc("block/framed_soul_torch"))
                .texture("top", mcLoc("block/soul_torch"));
        getVariantBuilder(FBContent.blockFramedSoulWallTorch.get()).forAllStates(state ->
        {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.toYRot() + 90) % 360;
            return ConfiguredModel.builder().modelFile(wallTorch).rotationY(rotY).build();
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
        ModelFile block = models().cubeAll("framed_collapsible_block", modLoc("block/framed_collapsible_block"));
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



    private void simpleBlockWithItem(RegistryObject<Block> block, ModelFile model)
    {
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }
}