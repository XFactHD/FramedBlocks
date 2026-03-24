package io.github.xfacthd.framedblocks.api.datagen.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mojang.math.Quadrant;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.block.dispatch.multipart.CombinedCondition;
import net.minecraft.client.renderer.block.dispatch.multipart.Condition;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.common.util.TransformationHelper;

import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@SuppressWarnings({ "SameParameterValue", "unused", "UnusedReturnValue" })
public abstract class AbstractFramedBlockModelProvider extends ModelProvider
{
    protected static final Identifier FRAMED_CUBE_MODEL = ModelLocationUtils.getModelLocation(Utils.FRAMED_CUBE.value());
    protected static final TextureSlot SLOT_FRAME = TextureSlot.create("frame");
    protected static final TextureSlot SLOT_UNDERLAY = TextureSlot.create("underlay");
    protected static final PropertyDispatch<VariantMutator> ROTATION_FACING_ALT = PropertyDispatch.modify(BlockStateProperties.FACING)
            .select(Direction.DOWN, BlockModelGenerators.X_ROT_270)
            .select(Direction.UP, BlockModelGenerators.X_ROT_90)
            .select(Direction.NORTH, BlockModelGenerators.Y_ROT_180)
            .select(Direction.SOUTH, BlockModelGenerators.NOP)
            .select(Direction.WEST, BlockModelGenerators.Y_ROT_90)
            .select(Direction.EAST, BlockModelGenerators.Y_ROT_270);

    private final PackOutput.PathProvider standalonePathProvider;
    private final Map<StandaloneWrapperKey<?>, FramedBlockModelDefinitionGenerator> standaloneDefinitions = new IdentityHashMap<>();

    protected AbstractFramedBlockModelProvider(PackOutput output, String modId)
    {
        super(output, modId);
        this.standalonePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, StandaloneWrapperKey.STANDALONE_DEFINITION_FOLDER);
    }

    protected static void variant(BlockModelGenerators blockModels, Holder<Block> block, Function<MultiVariantGenerator.Empty, MultiVariantGenerator> generator)
    {
        blockModels.blockStateOutput.accept(generator.apply(MultiVariantGenerator.dispatch(block.value())));
    }

    protected static void variant(BlockModelGenerators blockModels, Holder<Block> block, MultiVariant baseVariant, UnaryOperator<MultiVariantGenerator> generator)
    {
        blockModels.blockStateOutput.accept(generator.apply(MultiVariantGenerator.dispatch(block.value(), baseVariant)));
    }

    protected static MultiPartGenerator multiPart(BlockModelGenerators blockModels, Holder<Block> block)
    {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(block.value());
        blockModels.blockStateOutput.accept(generator);
        return generator;
    }

    protected static FramedBlockModelDefinitionGenerator framedVariant(BlockModelGenerators blockModels, Holder<Block> block, Function<MultiVariantGenerator.Empty, MultiVariantGenerator> generator)
    {
        BlockStateModelDispatcher dispatcher = generator.apply(MultiVariantGenerator.dispatch(block.value())).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.empty());
        blockModels.blockStateOutput.accept(framedDefinition);
        return framedDefinition;
    }

    protected static FramedBlockModelDefinitionGenerator framedVariant(BlockModelGenerators blockModels, Holder<Block> block, MultiVariant baseVariant, UnaryOperator<MultiVariantGenerator> generator)
    {
        BlockStateModelDispatcher dispatcher = generator.apply(MultiVariantGenerator.dispatch(block.value(), baseVariant)).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.empty());
        blockModels.blockStateOutput.accept(framedDefinition);
        return framedDefinition;
    }

    protected static FramedBlockModelDefinitionGenerator framedMultiPart(BlockModelGenerators blockModels, Holder<Block> block, UnaryOperator<MultiPartGenerator> generator)
    {
        BlockStateModelDispatcher dispatcher = generator.apply(MultiPartGenerator.multiPart(block.value())).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.empty());
        blockModels.blockStateOutput.accept(framedDefinition);
        return framedDefinition;
    }

    protected static FramedBlockModelDefinitionGenerator simpleFramedBlock(BlockModelGenerators blockModels, Holder<Block> block)
    {
        return simpleFramedBlock(blockModels, block, ModelLocationUtils.getModelLocation(block.value()));
    }

    protected static FramedBlockModelDefinitionGenerator simpleFramedBlock(BlockModelGenerators blockModels, Holder<Block> block, Identifier model)
    {
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), singleVariant(model), Optional.empty());
        blockModels.blockStateOutput.accept(framedDefinition);
        return framedDefinition;
    }

    protected static FramedBlockModelDefinitionGenerator simpleFramedBlockWithItem(BlockModelGenerators blockModels, Holder<Block> block, Identifier model)
    {
        FramedBlockModelDefinitionGenerator framedDefinition = simpleFramedBlock(blockModels, block, model);
        framedBlockItemModel(blockModels, block);
        return framedDefinition;
    }

    protected static FramedBlockModelDefinitionGenerator simpleFramedBlockWithItem(BlockModelGenerators blockModels, Holder<Block> block, Identifier model, Consumer<FramedItemModelBuilder> builderConsumer)
    {
        FramedBlockModelDefinitionGenerator framedDefinition = simpleFramedBlock(blockModels, block, model);
        framedBlockItemModel(blockModels, block, builderConsumer);
        return framedDefinition;
    }

    protected final FramedBlockModelDefinitionGenerator framedStandaloneVariant(
            StandaloneWrapperKey<?> wrapperKey, Function<MultiVariantGenerator.Empty, MultiVariantGenerator> generator
    )
    {
        Holder<Block> block = wrapperKey.block();
        BlockStateModelDispatcher dispatcher = generator.apply(MultiVariantGenerator.dispatch(block.value())).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.of(wrapperKey));
        registerStandaloneDefinition(framedDefinition);
        return framedDefinition;
    }

    protected final FramedBlockModelDefinitionGenerator framedStandaloneVariant(
            StandaloneWrapperKey<?> wrapperKey, MultiVariant baseVariant, UnaryOperator<MultiVariantGenerator> generator
    )
    {
        Holder<Block> block = wrapperKey.block();
        BlockStateModelDispatcher dispatcher = generator.apply(MultiVariantGenerator.dispatch(block.value(), baseVariant)).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.of(wrapperKey));
        registerStandaloneDefinition(framedDefinition);
        return framedDefinition;
    }

    protected final FramedBlockModelDefinitionGenerator framedStandaloneMultiPart(
            StandaloneWrapperKey<?> wrapperKey, UnaryOperator<MultiPartGenerator> generator
    )
    {
        Holder<Block> block = wrapperKey.block();
        BlockStateModelDispatcher dispatcher = generator.apply(MultiPartGenerator.multiPart(block.value())).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.of(wrapperKey));
        registerStandaloneDefinition(framedDefinition);
        return framedDefinition;
    }

    protected static void framedBlockFromTemplate(BlockModelGenerators blockModels, Holder<Block> block, ModelTemplate template, TextureMapping textures)
    {
        simpleFramedBlock(blockModels, block, blockModelFromTemplate(blockModels, block, template, textures));
    }

    protected static Identifier blockModelFromTemplate(BlockModelGenerators blockModels, Holder<Block> block, ModelTemplate template, TextureMapping textures)
    {
        Identifier name = ModelLocationUtils.getModelLocation(block.value(), template.suffix.orElse(""));
        return template.create(name, textures, blockModels.modelOutput);
    }

    protected static SingleVariant.Unbaked singleVariant(Identifier model)
    {
        return new SingleVariant.Unbaked(new Variant(model, Variant.SimpleModelState.DEFAULT));
    }

    protected static void framedBlockItemModel(BlockModelGenerators blockModels, Holder<Block> block)
    {
        framedBlockItemModel(blockModels, block, _ -> { });
    }

    protected static void framedBlockItemModel(BlockModelGenerators blockModels, Holder<Block> block, Consumer<FramedItemModelBuilder> builderConsumer)
    {
        Item item = block.value().asItem();
        Preconditions.checkArgument(item != Items.AIR, "Cannot generate item model for block %s without item", block.value());

        FramedItemModelBuilder builder = new FramedItemModelBuilder(block);
        builderConsumer.accept(builder);
        blockModels.itemModelOutput.accept(item, builder.build());
    }

    protected static FramedItemModelBuilder nestedFramedBlockItemModel(Holder<Block> block)
    {
        return new FramedItemModelBuilder(block);
    }

    protected static void blockItemFromTemplate(BlockModelGenerators blockModels, Holder<Block> block, ModelTemplate template, TextureMapping textures)
    {
        Identifier name = ModelLocationUtils.getModelLocation(block.value().asItem(), template.suffix.orElse(""));
        blockModels.registerSimpleItemModel(block.value(), template.create(name, textures, blockModels.modelOutput));
    }

    protected static Identifier makeUnderlayedCube(
            BlockModelGenerators blockModels, Holder<Block> block, Material frameTex, Material underlayTex, Consumer<ExtendedModelTemplateBuilder> consumer
    )
    {
        Identifier name = ModelLocationUtils.getModelLocation(block.value());
        return makeUnderlayedCube(blockModels, name, frameTex, underlayTex, consumer);
    }

    protected static Identifier makeUnderlayedCube(
            BlockModelGenerators blockModels, Identifier name, Material frameTex, Material underlayTex, Consumer<ExtendedModelTemplateBuilder> consumer
    )
    {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(SLOT_FRAME)
                .requiredTextureSlot(SLOT_UNDERLAY)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .parent(Identifier.withDefaultNamespace("block/block"))
                .element(elem -> elem.cube(SLOT_UNDERLAY))
                .element(elem -> elem.cube(SLOT_FRAME));
        consumer.accept(builder);

        ModelTemplate template = builder.build();
        return template.create(
                name.withSuffix(template.suffix.orElse("")),
                new TextureMapping()
                        .put(SLOT_FRAME, frameTex)
                        .put(SLOT_UNDERLAY, underlayTex)
                        .put(TextureSlot.PARTICLE, frameTex),
                blockModels.modelOutput
        );
    }

    protected static void makeOverlayCube(BlockModelGenerators blockModels, Identifier name, Material texture)
    {
        makeOverlayCube(blockModels, name, texture, _ -> {});
    }

    protected static void makeOverlayCube(BlockModelGenerators blockModels, Identifier name, Material texture, Consumer<ExtendedModelTemplateBuilder> consumer)
    {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.ALL)
                .parent(Identifier.withDefaultNamespace("block/cube_all"))
                .rootTransforms(xforms -> xforms
                        .scale(1.002F)
                        .origin(TransformationHelper.TransformOrigin.CENTER)
                );
        consumer.accept(builder);
        builder.build().create(name, TextureMapping.singleSlot(TextureSlot.ALL, texture), blockModels.modelOutput);
    }

    protected static VariantMutator rotationToVariant(int rotation)
    {
        return horDirToVariant(Direction.from2DDataValue(rotation / 4));
    }

    protected static VariantMutator horDirToVariant(Direction dir)
    {
        Quadrant rotValue = switch (dir)
        {
            case NORTH -> Quadrant.R180;
            case SOUTH -> Quadrant.R0;
            case WEST -> Quadrant.R90;
            case EAST -> Quadrant.R270;
            default -> throw new IllegalArgumentException("Invalid direction for Y rotation: " + dir);
        };
        return VariantMutator.Y_ROT.withValue(rotValue);
    }

    protected static Quadrant rotByIdx(int idx)
    {
        return Quadrant.values()[idx];
    }

    protected static Condition and(ConditionBuilder... conditions)
    {
        return new CombinedCondition(CombinedCondition.Operation.AND, Stream.of(conditions).map(ConditionBuilder::build).toList());
    }

    protected static Material mcMaterial(String path)
    {
        return mcMaterial(path, false);
    }

    protected static Material mcMaterial(String path, boolean forceTranslucent)
    {
        return new Material(Identifier.withDefaultNamespace(path), forceTranslucent);
    }

    protected final Material modMaterial(String path)
    {
        return modMaterial(path, false);
    }

    protected final Material modMaterial(String path, boolean forceTranslucent)
    {
        return new Material(Utils.id(modId, path), forceTranslucent);
    }

    private void registerStandaloneDefinition(FramedBlockModelDefinitionGenerator definition)
    {
        StandaloneWrapperKey<?> wrapperKey = definition.getWrapperKey();
        FramedBlockModelDefinitionGenerator old = standaloneDefinitions.putIfAbsent(wrapperKey, definition);
        if (old != null)
        {
            throw new IllegalStateException("Duplicate standalone model definition for '" + wrapperKey + "'");
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output)
    {
        CompletableFuture<?> future = super.run(output);
        if (!standaloneDefinitions.isEmpty())
        {
            Map<StandaloneWrapperKey<?>, BlockStateModelDispatcher> dispatchers = Maps.transformValues(standaloneDefinitions, BlockModelDefinitionGenerator::create);
            Function<StandaloneWrapperKey<?>, Path> pathGetter = wrapperKey -> standalonePathProvider.json(wrapperKey.definitionFile());
            future = CompletableFuture.allOf(future, DataProvider.saveAll(output, BlockStateModelDispatcher.CODEC, pathGetter, dispatchers));
        }
        return future;
    }

    @Override
    protected final Stream<? extends Holder<Item>> getKnownItems()
    {
        return super.getKnownItems().filter(item -> item instanceof BlockItem);
    }

    @Override
    public String getName()
    {
        return "Block Models - " + modId;
    }
}
