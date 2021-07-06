package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;

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
        simpleBlock(FBContent.blockFramedTrapDoor.get(), cube);
        simpleBlock(FBContent.blockFramedWallSign.get(), cube);
        simpleBlock(FBContent.blockFramedLattice.get(), cube);
        simpleBlock(FBContent.blockFramedVerticalStairs.get(), cube);
        simpleBlock(FBContent.blockFramedDoubleSlab.get(), cube);
        simpleBlock(FBContent.blockFramedDoublePanel.get(), cube);
        simpleBlock(FBContent.blockFramedDoubleSlope.get(), cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedDoublePrismCorner, cube);
        simpleBlockWithItem(FBContent.blockFramedDoubleThreewayCorner, cube);

        registerFramedSlab(cube);
        registerFramedStairs(cube);
        registerFramedWall(cube);
        registerFramedFence(cube);
        registerFramedGate(cube);
        registerFramedDoor(cube);
        registerFramedPressurePlate(cube);
        registerFramedLadder();
        registerFramedButton(cube);
        registerFramedLever();
        registerFramedSign(cube);
        registerFramedTorch();
        registerFramedWallTorch();
        registerFramedSoulTorch();
        registerFramedSoulWallTorch();
        registerFramedFloorBoard(cube);
        registerFramedCollapsibleBlock();
        registerFramedGhostBlock();
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
        simpleBlock(FBContent.blockFramedGate.get(), cube);
        itemModels().fenceGate("framed_gate", TEXTURE);
    }

    private void registerFramedDoor(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedDoor.get(), cube);
        itemModels().singleTexture("framed_door", mcLoc("item/generated"), "layer0", modLoc("item/framed_door"));
    }

    private void registerFramedPressurePlate(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedPressurePlate.get(), cube);
        itemModels().withExistingParent("framed_pressure_plate", mcLoc("block/pressure_plate_up"))
                .texture("texture", TEXTURE);
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
            Direction dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.getHorizontalAngle() + 90) % 360;
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
            Direction dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
            int rotY = ((int)dir.getHorizontalAngle() + 90) % 360;
            return ConfiguredModel.builder().modelFile(wallTorch).rotationY(rotY).build();
        });
    }

    private void registerFramedFloorBoard(ModelFile cube)
    {
        simpleBlock(FBContent.blockFramedFloor.get(), cube);
        itemModels().carpet("framed_floor_board", TEXTURE);
    }

    private void registerFramedCollapsibleBlock()
    {

    }

    private void registerFramedGhostBlock()
    {
        ModelFile cube = models().cubeAll("framed_ghost_block", modLoc("block/framed_ghost_block"));
        simpleBlock(FBContent.blockFramedGhostBlock.get(), cube);
    }



    private void simpleBlockWithItem(RegistryObject<Block> block, ModelFile model)
    {
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }
}