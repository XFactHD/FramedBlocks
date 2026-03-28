package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.component.PottedFlower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;

import java.util.function.Consumer;

public class FramedFlowerPotBlockEntity extends FramedBlockEntity {
    public static final ModelProperty<Block> FLOWER_BLOCK = new ModelProperty<>();

    private Block flowerBlock = Blocks.AIR;

    public FramedFlowerPotBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_FLOWER_POT.value(), pos, state);
    }

    public void setFlowerBlock(Block flowerBlock) {
        if (flowerBlock != this.flowerBlock) {
            this.flowerBlock = flowerBlock;

            setChangedWithoutSignalUpdate();
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public boolean hasFlowerBlock() {
        return flowerBlock != Blocks.AIR;
    }

    public Block getFlowerBlock() {
        return flowerBlock;
    }

    @Override
    public void addAdditionalDrops(Consumer<ItemStack> drops, boolean dropCamo) {
        super.addAdditionalDrops(drops, dropCamo);
        if (flowerBlock != Blocks.AIR) {
            drops.accept(new ItemStack(flowerBlock));
        }
    }

    @Override
    protected void attachAdditionalModelData(ModelData.Builder builder) {
        builder.with(FLOWER_BLOCK, flowerBlock);
    }

    @Override
    protected void writeToDataPacket(ValueOutput valueOutput) {
        super.writeToDataPacket(valueOutput);
        valueOutput.putString("flower", BuiltInRegistries.BLOCK.getKey(flowerBlock).toString());
    }

    @Override
    protected void readFromDataPacket(NetworkValueInput input) {
        super.readFromDataPacket(input);

        Block flower = BuiltInRegistries.BLOCK.getValue(Identifier.parse(input.getStringOr("flower", "")));
        if (flower != flowerBlock) {
            flowerBlock = flower;
            input.requestRenderUpdate();
        }
    }

    @Override
    protected BlueprintData appendCustomBlueprintData(BlueprintData blueprintData) {
        return blueprintData.withCustomData(FBContent.DC_TYPE_POTTED_FLOWER, new PottedFlower(flowerBlock));
    }

    @Override
    protected void applyCustomDataFromBlueprint(TypedDataComponent<?> auxData) {
        if (auxData.value() instanceof PottedFlower flower && !flower.isEmpty()) {
            flowerBlock = flower.flower();
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput) {
        super.removeComponentsFromTag(valueOutput);
        valueOutput.discard("flower");
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder) {
        if (hasFlowerBlock()) {
            builder.set(FBContent.DC_TYPE_POTTED_FLOWER, new PottedFlower(flowerBlock));
        }
    }

    @Override
    protected void applyMiscComponents(DataComponentGetter input) {
        PottedFlower flower = input.getOrDefault(FBContent.DC_TYPE_POTTED_FLOWER, PottedFlower.EMPTY);
        if (!flower.isEmpty()) {
            flowerBlock = flower.flower();
        }
    }

    @Override
    public void saveAdditional(ValueOutput valueOutput) {
        valueOutput.putString("flower", BuiltInRegistries.BLOCK.getKey(flowerBlock).toString());
        super.saveAdditional(valueOutput);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        flowerBlock = BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(valueInput.getStringOr("flower", "")));
    }
}
