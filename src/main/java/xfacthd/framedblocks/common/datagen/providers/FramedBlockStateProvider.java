package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.client.model.FramedMarkedPressurePlateModel;
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
        ModelFile cube = models().cubeAll("framed_cube", TEXTURE).renderType("cutout");

        simpleBlockWithItem(FBContent.blockFramedCube, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedCornerSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInnerCornerSlope, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedPrismCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInnerPrismCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedThreewayCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInnerThreewayCorner, cube, "cutout");
        simpleBlock(FBContent.blockFramedSlabEdge.get(), cube);
        simpleBlock(FBContent.blockFramedSlabCorner.get(), cube);
        simpleBlock(FBContent.blockFramedPanel.get(), cube);
        simpleBlock(FBContent.blockFramedCornerPillar.get(), cube);
        simpleBlock(FBContent.blockFramedTrapDoor.get(), cube);
        simpleBlock(FBContent.blockFramedWallSign.get(), cube);
        simpleBlock(FBContent.blockFramedLattice.get(), cube);
        simpleBlock(FBContent.blockFramedVerticalStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoublePanel, cube, "cutout");
        simpleBlock(FBContent.blockFramedDoubleSlope.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoublePrismCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleThreewayCorner, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedRailSlope, cube, "cutout");
        simpleBlock(FBContent.blockFramedPillar.get(), cube);
        simpleBlock(FBContent.blockFramedHalfPillar.get(), cube);
        simpleBlock(FBContent.blockFramedPost.get(), cube);
        simpleBlock(FBContent.blockFramedHalfStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedPrism, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedSlopedPrism, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedSlopeSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedElevatedSlopeSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopeSlab, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInverseDoubleSlopeSlab, cube, "cutout");
        simpleBlock(FBContent.blockFramedVerticalHalfStairs.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedExtendedSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedInverseDoubleSlopePanel, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedDoubleStairs, cube, "cutout");
        simpleBlockWithItem(FBContent.blockFramedVerticalDoubleStairs, cube, "cutout");
        simpleBlock(FBContent.blockFramedWallBoard.get(), cube);

        registerFramedSlab(cube);
        registerFramedStairs(cube);
        registerFramedWall(cube);
        registerFramedFence(cube);
        registerFramedGate(cube);
        registerFramedDoor(cube);
        registerFramedPressurePlate(cube);
        registerFramedStonePressurePlate();
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
        registerFramedIronDoor();
        registerFramedIronTrapDoor();
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
        simpleBlock(FBContent.blockFramedGate.get(), cube);
        itemModels().fenceGate("framed_gate", TEXTURE).renderType("cutout");
    }

    private void registerFramedDoor(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedDoor.get(), cube);
        simpleItem(FBContent.blockFramedDoor, "cutout");
    }

    private void registerFramedPressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedPressurePlate.get(), cube);
        itemModels().withExistingParent("framed_pressure_plate", mcLoc("block/pressure_plate_up"))
                .texture("texture", TEXTURE).renderType("cutout");
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

        models().withExistingParent(
                FramedMarkedPressurePlateModel.STONE_FRAME_LOCATION.getPath(),
                modLoc("block/framed_pressure_plate_frame_up")
        ).texture("texture", modLoc("block/stone_plate_frame"));
        models().withExistingParent(
                FramedMarkedPressurePlateModel.STONE_FRAME_DOWN_LOCATION.getPath(),
                modLoc("block/framed_pressure_plate_frame_down")
        ).texture("texture", modLoc("block/stone_plate_frame"));

        itemModels().withExistingParent("framed_stone_pressure_plate", modLoc("block/framed_stone_pressure_plate_up")).renderType("cutout");
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

        models().withExistingParent(
                FramedMarkedPressurePlateModel.GOLD_FRAME_LOCATION.getPath(),
                modLoc("block/framed_pressure_plate_frame_up")
        ).texture("texture", modLoc("block/gold_plate_frame"));
        models().withExistingParent(
                FramedMarkedPressurePlateModel.GOLD_FRAME_DOWN_LOCATION.getPath(),
                modLoc("block/framed_pressure_plate_frame_down")
        ).texture("texture", modLoc("block/gold_plate_frame"));

        itemModels().withExistingParent("framed_gold_pressure_plate", modLoc("block/framed_gold_pressure_plate_up")).renderType("cutout");
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

        models().withExistingParent(
                FramedMarkedPressurePlateModel.IRON_FRAME_LOCATION.getPath(),
                modLoc("block/framed_pressure_plate_frame_up")
        ).texture("texture", modLoc("block/iron_plate_frame"));
        models().withExistingParent(
                FramedMarkedPressurePlateModel.IRON_FRAME_DOWN_LOCATION.getPath(),
                modLoc("block/framed_pressure_plate_frame_down")
        ).texture("texture", modLoc("block/iron_plate_frame"));

        itemModels().withExistingParent("framed_iron_pressure_plate", modLoc("block/framed_iron_pressure_plate_up")).renderType("cutout");
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
                .texture("torch", modLoc("block/framed_soul_torch"))
                .texture("particle", modLoc("block/framed_soul_torch"))
                .texture("top", mcLoc("block/soul_torch"));
        simpleBlock(FBContent.blockFramedSoulTorch.get(), torch);
        simpleItem(FBContent.blockFramedSoulTorch, "block/framed_soul_torch", "cutout");
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
        simpleBlock(FBContent.blockFramedBars.get(), cube);
        simpleItem(FBContent.blockFramedBars, "cutout");
    }

    private void registerFramedPaneBlock(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedPane.get(), cube);
        simpleItem(FBContent.blockFramedPane, TEXTURE.getPath(), "cutout");
    }

    private void registerFramedFlowerPotBlock(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedFlowerPot.get(), cube);
        simpleItem(FBContent.blockFramedFlowerPot, "cutout");
    }

    private void registerFramedCollapsibleBlock()
    {
        ModelFile block = models().cubeAll("framed_collapsible_block", modLoc("block/framed_collapsible_block")).renderType("solid");
        simpleBlockWithItem(FBContent.blockFramedCollapsibleBlock, block, "cutout");
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

        simpleBlockWithItem(FBContent.blockFramedBouncyCube, block, "cutout");

        models().cubeAll("slime_frame", modLoc("block/slime_frame"));
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

        simpleBlockWithItem(FBContent.blockFramedRedstoneBlock, block, "cutout");

        models().cubeAll("redstone_frame", modLoc("block/redstone_frame"));
    }

    private void registerFramedIronDoor()
    {
        ModelFile door = models().getExistingFile(modLoc("block/framed_iron_door"));
        doorBlock(
                (DoorBlock) FBContent.blockFramedIronDoor.get(),
                door, door, door, door, door, door, door, door
        );
        simpleItem(FBContent.blockFramedIronDoor, "cutout");
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
        simpleBlockItem(FBContent.blockFramedIronTrapDoor, trapdoorBot, "cutout");
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
}