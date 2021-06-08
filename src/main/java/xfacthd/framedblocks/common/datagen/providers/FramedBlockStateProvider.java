package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;

public class FramedBlockStateProvider extends BlockStateProvider
{
    private final ResourceLocation TEXTURE;

    public FramedBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, FramedBlocks.MODID, fileHelper);
        TEXTURE = modLoc("block/framed_block");
    }

    @Override
    protected void registerStatesAndModels()
    {
        registerFramedCube();
        registerFramedSlope();
        registerFramedCornerSlope();
        registerFramedInnerCornerSlope();
        registerFramedPrismCorner();
        registerFramedInnerPrismCorner();
        registerFramedThreewayCorner();
        registerFramedInnerThreewayCorner();
        registerFramedSlab();
        registerFramedSlabEdge();
        registerFramedCornerPillar();
        registerFramedPanel();
        registerFramedStairs();
        registerFramedWall();
        registerFramedFence();
        registerFramedGate();
        registerFramedDoor();
        registerFramedTrapDoor();
        registerFramedPressurePlate();
        registerFramedLadder();
        registerFramedButton();
        registerFramedLever();
        registerFramedSign();
        registerFramedWallSign();
        registerFramedDoubleSlab();
        registerDoublePanel();
        registerDoubleSlope();
        registerFramedFloorBoard();
        registerFramedLattice();
        registerFramedCollapsibleBlock();
        registerFramedGhostBlock();
    }

    private void registerFramedCube()
    {
        ModelFile cube = models().cubeAll("framed_cube", TEXTURE);
        simpleBlock(FBContent.blockFramedCube, cube);
        simpleBlockItem(FBContent.blockFramedCube, cube);
    }

    private void registerFramedSlope()
    {
        ModelFile slopeBottom = models().getExistingFile(modLoc("block/framed_slope_bottom"));
        ModelFile slopeHorizontal = models().getExistingFile(modLoc("block/framed_slope_horizontal"));
        ModelFile slopeTop = models().getExistingFile(modLoc("block/framed_slope_top"));
        getVariantBuilder(FBContent.blockFramedSlope).forAllStatesExcept(state ->
        {
            SlopeType type = state.get(PropertyHolder.SLOPE_TYPE);
            ModelFile model = type == SlopeType.TOP ? slopeTop : (type == SlopeType.BOTTOM ? slopeBottom : slopeHorizontal);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            return ConfiguredModel.builder().modelFile(model).rotationY(dir.getHorizontalIndex() * 90).uvLock(true).build();
        }, BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedSlope, slopeBottom);
    }

    private void registerFramedCornerSlope()
    {
        ModelFile cornerBottom = models().getExistingFile(modLoc("block/framed_corner_slope_bottom"));
        ModelFile cornerTop = models().getExistingFile(modLoc("block/framed_corner_slope_top"));
        getVariantBuilder(FBContent.blockFramedCornerSlope).forAllStatesExcept(state ->
        {
            CornerType type = state.get(PropertyHolder.CORNER_TYPE);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                ModelFile model = (type == CornerType.HORIZONTAL_BOTTOM_RIGHT || type == CornerType.HORIZONTAL_TOP_LEFT) ? cornerBottom : cornerTop;
                int rotX = type.isTop() ? 90 : -90;
                if (type.isRight()) { dir = dir.getOpposite(); }
                return ConfiguredModel.builder().modelFile(model).rotationX(rotX).rotationY(dir.getHorizontalIndex() * 90).uvLock(true).build();
            }
            else
            {
                ModelFile model = type.isTop() ? cornerTop : cornerBottom;
                return ConfiguredModel.builder().modelFile(model).rotationY(dir.getHorizontalIndex() * 90).uvLock(true).build();
            }
        }, BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedCornerSlope, cornerBottom);
    }

    private void registerFramedInnerCornerSlope()
    {
        ModelFile innerCornerBottom = models().getExistingFile(modLoc("block/framed_inner_corner_slope_bottom"));
        ModelFile innerCornerTop = models().getExistingFile(modLoc("block/framed_inner_corner_slope_top"));
        getVariantBuilder(FBContent.blockFramedInnerCornerSlope).forAllStatesExcept(state ->
        {
            CornerType type = state.get(PropertyHolder.CORNER_TYPE);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                ModelFile model = (type == CornerType.HORIZONTAL_TOP_RIGHT || type == CornerType.HORIZONTAL_BOTTOM_LEFT) ? innerCornerBottom : innerCornerTop;
                int rotX = type.isTop() ? 90 : -90;
                if (!type.isRight()) { dir = dir.getOpposite(); }
                return ConfiguredModel.builder().modelFile(model).rotationX(rotX).rotationY(dir.getHorizontalIndex() * 90).uvLock(true).build();
            }
            else
            {
                ModelFile model = type.isTop() ? innerCornerTop : innerCornerBottom;
                return ConfiguredModel.builder().modelFile(model).rotationY(dir.getHorizontalIndex() * 90).uvLock(true).build();
            }
        }, BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedInnerCornerSlope, innerCornerBottom);
    }

    private void registerFramedPrismCorner()
    {
        ModelFile prismBottom = models().getExistingFile(modLoc("block/framed_prism_corner_bottom"));
        ModelFile prismTop = models().getExistingFile(modLoc("block/framed_prism_corner_top"));
        getVariantBuilder(FBContent.blockFramedPrismCorner).forAllStatesExcept(state ->
        {
            boolean top = state.get(PropertyHolder.TOP);
            ModelFile model = top ? prismTop : prismBottom;
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            return ConfiguredModel.builder().modelFile(model).rotationY(dir.getHorizontalIndex() * 90).uvLock(true).build();
        }, BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedPrismCorner, prismBottom);
    }

    private void registerFramedInnerPrismCorner()
    {
        ModelFile innerPrism = models().getExistingFile(modLoc("block/framed_inner_prism_corner"));
        getVariantBuilder(FBContent.blockFramedInnerPrismCorner).forAllStatesExcept(state ->
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);
            return ConfiguredModel.builder().modelFile(innerPrism).rotationY(dir.getHorizontalIndex() * 90).uvLock(true).build();
        }, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED); //Not ignoring this property does not lead to 8 model instances being loaded

        simpleBlockItem(FBContent.blockFramedInnerPrismCorner, innerPrism);
    }

    private void registerFramedThreewayCorner()
    {
        ModelFile cornerBottom = models().getExistingFile(modLoc("block/framed_threeway_corner_bottom"));
        ModelFile cornerTop = models().getExistingFile(modLoc("block/framed_threeway_corner_top"));
        getVariantBuilder(FBContent.blockFramedThreewayCorner).forAllStatesExcept(state ->
        {
            boolean top = state.get(PropertyHolder.TOP);
            ModelFile model = top ? cornerTop : cornerBottom;
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            return ConfiguredModel.builder().modelFile(model).rotationY(dir.getHorizontalIndex() * 90).uvLock(true).build();
        }, BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedThreewayCorner, cornerBottom);
    }

    private void registerFramedInnerThreewayCorner()
    {
        ModelFile innerCornerBottom = models().getExistingFile(modLoc("block/framed_inner_threeway_corner_bottom"));
        ModelFile innerCornerTop = models().getExistingFile(modLoc("block/framed_inner_threeway_corner_top"));
        getVariantBuilder(FBContent.blockFramedInnerThreewayCorner).forAllStatesExcept(state ->
        {
            boolean top = state.get(PropertyHolder.TOP);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            ModelFile model = top ? innerCornerTop : innerCornerBottom;
            return ConfiguredModel.builder().modelFile(model).rotationY(dir.getHorizontalIndex() * 90).uvLock(true).build();
        }, BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedInnerThreewayCorner, innerCornerBottom);
    }

    private void registerFramedSlab()
    {
        ModelFile bottomSlab = models().slab("framed_slab_bottom", TEXTURE, TEXTURE, TEXTURE);
        ModelFile topSlab = models().slabTop("framed_slab_top", TEXTURE, TEXTURE, TEXTURE);

        getVariantBuilder(FBContent.blockFramedSlab).forAllStatesExcept(state ->
                ConfiguredModel.builder().modelFile(state.get(PropertyHolder.TOP) ? topSlab : bottomSlab).build(),
                BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedSlab, bottomSlab);
    }

    private void registerFramedSlabEdge()
    {
        ModelFile bottomSlab = models().getExistingFile(modLoc("framed_slab_edge_bottom"));
        ModelFile topSlab = models().getExistingFile(modLoc("framed_slab_edge_top"));

        getVariantBuilder(FBContent.blockFramedSlabEdge).forAllStatesExcept(state ->
        {
            int rotY = (int)(state.get(PropertyHolder.FACING_HOR).getHorizontalAngle() + 180) % 360;
            return ConfiguredModel.builder().modelFile(state.get(PropertyHolder.TOP) ? topSlab : bottomSlab).rotationY(rotY).uvLock(true).build();
        }, BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedSlabEdge, bottomSlab);
    }

    private void registerFramedPanel()
    {
        ModelFile panel = models().getExistingFile(modLoc("framed_panel"));

        getVariantBuilder(FBContent.blockFramedPanel).forAllStatesExcept(state ->
        {
            int rotY = (int)(state.get(PropertyHolder.FACING_HOR).getHorizontalAngle() + 180) % 360;
            return ConfiguredModel.builder().modelFile(panel).rotationY(rotY).uvLock(true).build();
        }, BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedPanel, panel);
    }

    private void registerFramedCornerPillar()
    {
        ModelFile pillar = models().getExistingFile(modLoc("framed_corner_pillar"));

        getVariantBuilder(FBContent.blockFramedCornerPillar).forAllStatesExcept(state ->
        {
            int rotY = (int)(state.get(PropertyHolder.FACING_HOR).getHorizontalAngle() + 180) % 360;
            return ConfiguredModel.builder().modelFile(pillar).rotationY(rotY).uvLock(true).build();
        }, BlockStateProperties.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedCornerPillar, pillar);
    }

    private void registerFramedStairs()
    {
        stairsBlock((StairsBlock)FBContent.blockFramedStairs, TEXTURE);

        ModelFile stairs = models().generatedModels.get(modLoc("block/framed_stairs"));
        simpleBlockItem(FBContent.blockFramedStairs, stairs);
    }

    private void registerFramedWall()
    {
        wallBlock((WallBlock)FBContent.blockFramedWall, TEXTURE);

        itemModels().getBuilder("framed_wall")
                .parent(models().getExistingFile(mcLoc("block/wall_inventory")))
                .texture("wall", TEXTURE);
    }

    private void registerFramedFence()
    {
        fenceBlock((FenceBlock)FBContent.blockFramedFence, TEXTURE);

        itemModels().getBuilder("framed_fence")
                .parent(models().getExistingFile(mcLoc("block/fence_inventory")))
                .texture("texture", TEXTURE);
    }

    private void registerFramedGate()
    {
        fenceGateBlock((FenceGateBlock)FBContent.blockFramedGate, TEXTURE);

        ModelFile gate = models().generatedModels.get(modLoc("block/framed_gate"));
        simpleBlockItem(FBContent.blockFramedGate, gate);
    }

    private void registerFramedDoor()
    {
        ModelFile door = models().getExistingFile(modLoc("block/framed_door"));

        getVariantBuilder(FBContent.blockFramedDoor).forAllStatesExcept(state ->
        {
            boolean hingeRight = state.get(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
            boolean open = state.get(DoorBlock.OPEN);

            int yRot = ((int) state.get(DoorBlock.FACING).getHorizontalAngle()) + 180;
            if (open) { yRot += 90; }
            if (hingeRight && open) { yRot += 180; }
            yRot %= 360;

            return ConfiguredModel.builder()
                    .modelFile(door)
                    .rotationY(yRot)
                    .uvLock(true)
                    .build();
        }, DoorBlock.POWERED);

        itemModels().singleTexture("framed_door", mcLoc("item/generated"), "layer0", modLoc("item/framed_door"));
    }

    private void registerFramedTrapDoor()
    {
        ModelFile bottom = models().getExistingFile(modLoc("framed_trapdoor_bottom"));
        ModelFile top = models().getExistingFile(modLoc("framed_trapdoor_top"));
        ModelFile open = models().getExistingFile(modLoc("framed_trapdoor_open"));

        getVariantBuilder(FBContent.blockFramedTrapDoor).forAllStatesExcept(state ->
        {
            int yRot = ((int) state.get(TrapDoorBlock.HORIZONTAL_FACING).getHorizontalAngle()) + 180;
            boolean isOpen = state.get(TrapDoorBlock.OPEN);
            boolean isTop = state.get(TrapDoorBlock.HALF) == Half.TOP;
            ModelFile model = isOpen ? open : isTop ? top : bottom;
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .uvLock(true)
                    .build();
        }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);

        simpleBlockItem(FBContent.blockFramedTrapDoor, bottom);
    }

    private void registerFramedPressurePlate()
    {
        ModelFile plate = models()
                .withExistingParent("framed_pressure_plate", mcLoc("block/pressure_plate_up"))
                .texture("texture", TEXTURE);
        ModelFile plateDown = models()
                .withExistingParent("framed_pressure_plate_down", mcLoc("block/pressure_plate_down"))
                .texture("texture", TEXTURE);

        getVariantBuilder(FBContent.blockFramedPressurePlate).forAllStates(state ->
                ConfiguredModel.builder().modelFile(state.get(PressurePlateBlock.POWERED) ? plateDown : plate).build());

        simpleBlockItem(FBContent.blockFramedPressurePlate, plate);
    }

    private void registerFramedLadder()
    {
        ModelFile model = models()
                .withExistingParent("framed_ladder", mcLoc("block/cube"))
                .texture("0", TEXTURE)
                .texture("particle", TEXTURE)
                .element() //Right leg
                    .from(0, 0, 14)
                    .to(2, 16, 16)
                    .allFaces((dir, face) ->
                    {
                        face.texture("#0");
                        switch (dir)
                        {
                            case NORTH:
                            case WEST:
                                face.uvs(14, 0, 16, 16);
                                break;
                            case EAST:
                            case SOUTH:
                                face.uvs(0, 0, 2, 16);
                                break;
                            case UP:
                                face.uvs(14, 0, 16, 2);
                                break;
                            case DOWN:
                                face.uvs(14, 14, 16, 16);
                                break;
                        }
                    })
                    .end()
                .element() //Left leg
                    .from(14, 0, 14)
                    .to(16, 16, 16)
                    .allFaces((dir, face) ->
                    {
                        face.texture("#0");
                        switch (dir)
                        {
                            case NORTH:
                            case EAST:
                                face.uvs(0, 0, 2, 16);
                                break;
                            case SOUTH:
                            case WEST:
                                face.uvs(14, 0, 16, 16);
                                break;
                            case UP:
                                face.uvs(0, 0, 2, 2);
                                break;
                            case DOWN:
                                face.uvs(0, 14, 2, 16);
                                break;
                        }
                    })
                    .end()
                .element() //Rung 1
                    .from(2, 1.5F, 14.5F)
                    .to(14, 2.5F, 15.5F)
                    .face(Direction.NORTH)
                        .texture("#0")
                        .uvs(2, 13.5F, 14, 14.5F)
                        .end()
                    .face(Direction.SOUTH)
                        .texture("#0")
                        .uvs(2, 13.5F, 14, 14.5F)
                        .end()
                    .face(Direction.UP)
                        .texture("#0")
                        .uvs(2, .5F, 14, 1.5F)
                        .end()
                    .face(Direction.DOWN)
                        .texture("#0")
                        .uvs(2, 14.5F, 14, 15.5F)
                        .end()
                    .end()
                .element() //Rung 2
                    .from(2, 5.5F, 14.5F)
                    .to(14, 6.5F, 15.5F)
                    .face(Direction.NORTH)
                        .texture("#0")
                        .uvs(2, 9.5F, 14, 10.5F)
                        .end()
                    .face(Direction.SOUTH)
                        .texture("#0")
                        .uvs(2, 9.5F, 14, 10.5F)
                        .end()
                    .face(Direction.UP)
                        .texture("#0")
                        .uvs(2, .5F, 14, 1.5F)
                        .end()
                    .face(Direction.DOWN)
                        .texture("#0")
                        .uvs(2, 14.5F, 14, 15.5F)
                        .end()
                    .end()
                .element() //Rung 3
                    .from(2, 9.5F, 14.5F)
                    .to(14, 10.5F, 15.5F)
                    .face(Direction.NORTH)
                        .texture("#0")
                        .uvs(2, 5.5F, 14, 6.5F)
                        .end()
                    .face(Direction.SOUTH)
                        .texture("#0")
                        .uvs(2, 5.5F, 14, 6.5F)
                        .end()
                    .face(Direction.UP)
                        .texture("#0")
                        .uvs(2, .5F, 14, 1.5F)
                        .end()
                    .face(Direction.DOWN)
                        .texture("#0")
                        .uvs(2, 14.5F, 14, 15.5F)
                        .end()
                    .end()
                .element() //Rung 3
                    .from(2, 13.5F, 14.5F)
                    .to(14, 14.5F, 15.5F)
                    .face(Direction.NORTH)
                        .texture("#0")
                        .uvs(2, 1.5F, 14, 2.5F)
                        .end()
                    .face(Direction.SOUTH)
                        .texture("#0")
                        .uvs(2, 1.5F, 14, 2.5F)
                        .end()
                    .face(Direction.UP)
                        .texture("#0")
                        .uvs(2, .5F, 14, 1.5F)
                        .end()
                    .face(Direction.DOWN)
                        .texture("#0")
                        .uvs(2, 14.5F, 14, 15.5F)
                        .end()
                    .end();

        getVariantBuilder(FBContent.blockFramedLadder).forAllStatesExcept(state ->
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int)dir.getHorizontalAngle())
                    .uvLock(true)
                    .build();
        }, BlockStateProperties.WATERLOGGED);
        simpleBlockItem(FBContent.blockFramedLadder, model);
    }

    private void registerFramedButton()
    {
        ModelFile button = models()
                .withExistingParent("framed_button", mcLoc("block/button"))
                .texture("texture", TEXTURE);
        ModelFile buttonPressed = models()
                .withExistingParent("framed_button_pressed", mcLoc("block/button_pressed"))
                .texture("texture", TEXTURE);

        getVariantBuilder(FBContent.blockFramedButton).forAllStates(state ->
        {
            Direction facing = state.get(HorizontalBlock.HORIZONTAL_FACING);
            AttachFace face = state.get(HorizontalFaceBlock.FACE);
            boolean powered = state.get(LeverBlock.POWERED);

            int rotY = (int)(facing.getHorizontalAngle() + 180F) % 360;
            int rotX = face.ordinal() * 90;
            ModelFile model = powered ? buttonPressed : button;
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .uvLock(true)
                    .build();
        });

        itemModels().getBuilder("framed_button")
                .parent(models().getExistingFile(mcLoc("block/button_inventory")))
                .texture("texture", TEXTURE);
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

        getVariantBuilder(FBContent.blockFramedLever).forAllStates(state ->
        {
            Direction facing = state.get(HorizontalBlock.HORIZONTAL_FACING);
            AttachFace face = state.get(HorizontalFaceBlock.FACE);
            boolean powered = state.get(LeverBlock.POWERED);

            int rotY = (int)(facing.getHorizontalAngle() + 180F) % 360;
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

    private void registerFramedSign()
    {
        ModelFile[] signs = new ModelFile[] {
                models().getExistingFile(modLoc("framed_sign")),
                models().getExistingFile(modLoc("framed_sign_225")),
                models().getExistingFile(modLoc("framed_sign_45")),
                models().getExistingFile(modLoc("framed_sign_675"))
        };

        getVariantBuilder(FBContent.blockFramedSign).forAllStatesExcept(state ->
        {
            int rot = ((15 - state.get(BlockStateProperties.ROTATION_0_15)) + 1) % 16;
            ModelFile model = signs[rot % 4];
            int rotY = (rot / 4) * 90;
            return ConfiguredModel.builder().modelFile(model).rotationY(rotY).build();
        }, BlockStateProperties.WATERLOGGED);

        itemModels().singleTexture("framed_sign", mcLoc("item/generated"), "layer0", modLoc("item/framed_sign"));
    }

    private void registerFramedWallSign()
    {
        ModelFile sign = models().getExistingFile(modLoc("framed_wall_sign"));

        getVariantBuilder(FBContent.blockFramedWallSign).forAllStatesExcept(state ->
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);
            int rotY = ((int)dir.getHorizontalAngle() + 180) % 360;
            return ConfiguredModel.builder().modelFile(sign).rotationY(rotY).uvLock(true).build();
        }, BlockStateProperties.WATERLOGGED);
    }

    private void registerFramedDoubleSlab()
    {
        ModelFile doubleSlab = models().getExistingFile(modLoc("framed_double_slab"));
        simpleBlock(FBContent.blockFramedDoubleSlab, doubleSlab);
    }

    private void registerDoublePanel()
    {
        ModelFile doublePanel = models().getExistingFile(modLoc("framed_double_panel"));
        simpleBlock(FBContent.blockFramedDoublePanel, doublePanel);
    }

    private void registerDoubleSlope()
    {
        ModelFile doubleSlope = models().getExistingFile(modLoc("framed_double_slope"));
        simpleBlock(FBContent.blockFramedDoubleSlope, doubleSlope);
        simpleBlockItem(FBContent.blockFramedDoubleSlope, doubleSlope);
    }

    private void registerFramedFloorBoard()
    {
        ModelFile cube = models().carpet("framed_floor_board", TEXTURE);
        simpleBlock(FBContent.blockFramedFloor, cube);
        simpleBlockItem(FBContent.blockFramedFloor, cube);
    }

    private void registerFramedLattice()
    {
        ModelFile cube = models().getExistingFile(modLoc("framed_lattice"));
        simpleBlock(FBContent.blockFramedLattice, cube);
        simpleBlockItem(FBContent.blockFramedLattice, cube);
    }

    private void registerFramedCollapsibleBlock()
    {

    }

    private void registerFramedGhostBlock()
    {
        ModelFile cube = models().cubeAll("framed_ghost_block", modLoc("block/framed_ghost_block"));
        simpleBlock(FBContent.blockFramedGhostBlock, cube);
    }
}