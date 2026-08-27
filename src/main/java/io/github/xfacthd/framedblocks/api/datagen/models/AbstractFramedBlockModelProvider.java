package io.github.xfacthd.framedblocks.api.datagen.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mojang.math.Quadrant;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

/// Base model provider implementation providing helpers for generating block models, blockstate files and
/// block item models for framed blocks.
@SuppressWarnings({ "SameParameterValue", "unused", "UnusedReturnValue" })
public abstract class AbstractFramedBlockModelProvider extends ModelProvider {
    /// Model ID of the basic Framed Cube block model.
    protected static final Identifier FRAMED_CUBE_MODEL = ModelLocationUtils.getModelLocation(FramedConstants.Objects.FRAMED_CUBE.value());
    /// Texture slot used for the wood frame texture in [underlayed cube models][#makeUnderlayedCube(BlockModelGenerators, Identifier, Material, Material, Consumer)].
    protected static final TextureSlot SLOT_FRAME = TextureSlot.create("frame");
    /// Texture slot used for the underlay texture in [underlayed cube models][#makeUnderlayedCube(BlockModelGenerators, Identifier, Material, Material, Consumer)].
    protected static final TextureSlot SLOT_UNDERLAY = TextureSlot.create("underlay");
    /// Property dispatch for [BlockStateProperties#FACING] with opposite rotation behavior to [BlockModelGenerators#ROTATION_FACING].
    protected static final PropertyDispatch<VariantMutator> ROTATION_FACING_ALT = PropertyDispatch.modify(BlockStateProperties.FACING)
            .select(Direction.DOWN, BlockModelGenerators.X_ROT_270)
            .select(Direction.UP, BlockModelGenerators.X_ROT_90)
            .select(Direction.NORTH, BlockModelGenerators.Y_ROT_180)
            .select(Direction.SOUTH, BlockModelGenerators.NOP)
            .select(Direction.WEST, BlockModelGenerators.Y_ROT_90)
            .select(Direction.EAST, BlockModelGenerators.Y_ROT_270);

    private final PackOutput.PathProvider standalonePathProvider;
    private final Map<StandaloneWrapperKey<?>, FramedBlockModelDefinitionGenerator> standaloneDefinitions = new IdentityHashMap<>();

    /// @param output The output to generate the models into
    /// @param modId  The mod ID to generate the models for
    protected AbstractFramedBlockModelProvider(PackOutput output, String modId) {
        super(output, modId);
        this.standalonePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, StandaloneWrapperKey.STANDALONE_DEFINITION_FOLDER);
    }

    /// Generate a vanilla variant blockstate file for the given block.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate file for
    /// @param generator   The function generating the variant definition
    protected static void variant(BlockModelGenerators blockModels, Holder<Block> block, Function<MultiVariantGenerator.Empty, MultiVariantGenerator> generator) {
        blockModels.blockStateOutput.accept(generator.apply(MultiVariantGenerator.dispatch(block.value())));
    }

    /// Generate a vanilla variant blockstate file for the given block.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate file for
    /// @param baseVariant The base variant (i.e. primary/only model)
    /// @param generator   The function generating the variant definition
    protected static void variant(BlockModelGenerators blockModels, Holder<Block> block, MultiVariant baseVariant, UnaryOperator<MultiVariantGenerator> generator) {
        blockModels.blockStateOutput.accept(generator.apply(MultiVariantGenerator.dispatch(block.value(), baseVariant)));
    }

    /// Generate a vanilla multipart blockstate file for the given block.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate file for
    /// @return the multipart generator
    protected static MultiPartGenerator multiPart(BlockModelGenerators blockModels, Holder<Block> block) {
        MultiPartGenerator generator = MultiPartGenerator.multiPart(block.value());
        blockModels.blockStateOutput.accept(generator);
        return generator;
    }

    /// Generate a variant blockstate file with model wrapping for the given framed block.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate file for
    /// @param generator   The function generating the variant definition
    /// @return the model definition generator
    protected static FramedBlockModelDefinitionGenerator framedVariant(BlockModelGenerators blockModels, Holder<Block> block, Function<MultiVariantGenerator.Empty, MultiVariantGenerator> generator) {
        BlockStateModelDispatcher dispatcher = generator.apply(MultiVariantGenerator.dispatch(block.value())).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.empty());
        blockModels.blockStateOutput.accept(framedDefinition);
        return framedDefinition;
    }

    /// Generate a variant blockstate file with model wrapping for the given framed block.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate file for
    /// @param baseVariant The base variant (i.e. primary/only model)
    /// @param generator   The function generating the variant definition
    /// @return the model definition generator
    protected static FramedBlockModelDefinitionGenerator framedVariant(BlockModelGenerators blockModels, Holder<Block> block, MultiVariant baseVariant, UnaryOperator<MultiVariantGenerator> generator) {
        BlockStateModelDispatcher dispatcher = generator.apply(MultiVariantGenerator.dispatch(block.value(), baseVariant)).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.empty());
        blockModels.blockStateOutput.accept(framedDefinition);
        return framedDefinition;
    }

    /// Generate a multipart blockstate file with model wrapping for the given framed block.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate file for
    /// @param generator   The function generating the multipart definition
    /// @return the model definition generator
    protected static FramedBlockModelDefinitionGenerator framedMultiPart(BlockModelGenerators blockModels, Holder<Block> block, UnaryOperator<MultiPartGenerator> generator) {
        BlockStateModelDispatcher dispatcher = generator.apply(MultiPartGenerator.multiPart(block.value())).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.empty());
        blockModels.blockStateOutput.accept(framedDefinition);
        return framedDefinition;
    }

    /// Generate a single-variant blockstate file with model wrapping for the given framed block, using the model derived from the
    /// block's ID as the base model.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate file for
    /// @return the model definition generator
    protected static FramedBlockModelDefinitionGenerator simpleFramedBlock(BlockModelGenerators blockModels, Holder<Block> block) {
        return simpleFramedBlock(blockModels, block, ModelLocationUtils.getModelLocation(block.value()));
    }

    /// Generate a single-variant blockstate file with model wrapping for the given framed block, using the given model as the base model.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate file for
    /// @param model       The base model
    /// @return the model definition generator
    protected static FramedBlockModelDefinitionGenerator simpleFramedBlock(BlockModelGenerators blockModels, Holder<Block> block, Identifier model) {
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), singleVariant(model), Optional.empty());
        blockModels.blockStateOutput.accept(framedDefinition);
        return framedDefinition;
    }

    /// Generate a single-variant blockstate file with model wrapping for the given framed block, using the given model as the base model,
    /// and generate a client item file with a dynamic, camo aware item model for the associated block item.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate and client item files for
    /// @param model       The base model of the block model
    /// @return the model definition generator
    protected static FramedBlockModelDefinitionGenerator simpleFramedBlockWithItem(BlockModelGenerators blockModels, Holder<Block> block, Identifier model) {
        FramedBlockModelDefinitionGenerator framedDefinition = simpleFramedBlock(blockModels, block, model);
        framedBlockItemModel(blockModels, block);
        return framedDefinition;
    }

    /// Generate a single-variant blockstate file with model wrapping for the given framed block, using the given model as the base model,
    /// and generate a client item file with a dynamic, camo aware item model for the associated block item.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate and client item files for
    /// @param model       The base model of the block model
    /// @param itemBuilder A function for adjusting the item model
    /// @return the model definition generator
    protected static FramedBlockModelDefinitionGenerator simpleFramedBlockWithItem(BlockModelGenerators blockModels, Holder<Block> block, Identifier model, Consumer<FramedItemModelBuilder> itemBuilder) {
        FramedBlockModelDefinitionGenerator framedDefinition = simpleFramedBlock(blockModels, block, model);
        framedBlockItemModel(blockModels, block, itemBuilder);
        return framedDefinition;
    }

    /// Generate a variant blockstate file with model wrapping for the given standalone wrapper.
    ///
    /// @param wrapperKey The standalone wrapper key to generate the blockstate file for
    /// @param generator  The function generating the variant definition
    /// @return the model definition generator
    protected final FramedBlockModelDefinitionGenerator framedStandaloneVariant(StandaloneWrapperKey<?> wrapperKey, Function<MultiVariantGenerator.Empty, MultiVariantGenerator> generator) {
        Holder<Block> block = wrapperKey.block();
        BlockStateModelDispatcher dispatcher = generator.apply(MultiVariantGenerator.dispatch(block.value())).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.of(wrapperKey));
        registerStandaloneDefinition(framedDefinition);
        return framedDefinition;
    }

    /// Generate a variant blockstate file with model wrapping for the given standalone wrapper.
    ///
    /// @param wrapperKey  The standalone wrapper key to generate the blockstate file for
    /// @param baseVariant The base variant (i.e. primary/only model)
    /// @param generator   The function generating the variant definition
    /// @return the model definition generator
    protected final FramedBlockModelDefinitionGenerator framedStandaloneVariant(StandaloneWrapperKey<?> wrapperKey, MultiVariant baseVariant, UnaryOperator<MultiVariantGenerator> generator) {
        Holder<Block> block = wrapperKey.block();
        BlockStateModelDispatcher dispatcher = generator.apply(MultiVariantGenerator.dispatch(block.value(), baseVariant)).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.of(wrapperKey));
        registerStandaloneDefinition(framedDefinition);
        return framedDefinition;
    }

    /// Generate a multipart blockstate file with model wrapping for the given standalone wrapper.
    ///
    /// @param wrapperKey The standalone wrapper key to generate the blockstate file for
    /// @param generator  The function generating the multipart definition
    /// @return the model definition generator
    protected final FramedBlockModelDefinitionGenerator framedStandaloneMultiPart(StandaloneWrapperKey<?> wrapperKey, UnaryOperator<MultiPartGenerator> generator) {
        Holder<Block> block = wrapperKey.block();
        BlockStateModelDispatcher dispatcher = generator.apply(MultiPartGenerator.multiPart(block.value())).create();
        FramedBlockModelDefinitionGenerator framedDefinition = new FramedBlockModelDefinitionGenerator(block.value(), dispatcher, Optional.of(wrapperKey));
        registerStandaloneDefinition(framedDefinition);
        return framedDefinition;
    }

    /// Generate a single-variant blockstate file with model wrapping for the given framed block, using the model
    /// generated from the given template as the base model.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the blockstate file for
    /// @param template    The template to generate the base model from
    /// @param textures    The texture mapping to use in the generated base model
    protected static void framedBlockFromTemplate(BlockModelGenerators blockModels, Holder<Block> block, ModelTemplate template, TextureMapping textures) {
        simpleFramedBlock(blockModels, block, blockModelFromTemplate(blockModels, block, template, textures));
    }

    /// Generate a block model for the given block from the given template.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the model for
    /// @param template    The template to generate the model from
    /// @param textures    The texture mapping to use in the generated model
    /// @return the path of the generated model
    protected static Identifier blockModelFromTemplate(BlockModelGenerators blockModels, Holder<Block> block, ModelTemplate template, TextureMapping textures) {
        Identifier name = ModelLocationUtils.getModelLocation(block.value(), template.suffix.orElse(""));
        return template.create(name, textures, blockModels.modelOutput);
    }

    /// {@return an untransformed single-variant blockstate entry with the given model}
    ///
    /// @param model The model to specify in the single-variant entry
    /// @return the single-variant blockstate entry
    protected static SingleVariant.Unbaked singleVariant(Identifier model) {
        return new SingleVariant.Unbaked(new Variant(model, Variant.SimpleModelState.DEFAULT));
    }

    /// Generate a client item file with a dynamic, camo-aware item model for the block item of the given block.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the client item file for
    protected static void framedBlockItemModel(BlockModelGenerators blockModels, Holder<Block> block) {
        framedBlockItemModel(blockModels, block, _ -> { });
    }

    /// Generate a client item file with a dynamic, camo-aware item model for the block item of the given block.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the client item file for
    /// @param itemBuilder A function for adjusting the item model
    protected static void framedBlockItemModel(BlockModelGenerators blockModels, Holder<Block> block, Consumer<FramedItemModelBuilder> itemBuilder) {
        Item item = block.value().asItem();
        Preconditions.checkArgument(item != Items.AIR, "Cannot generate item model for block %s without item", block.value());

        FramedItemModelBuilder builder = new FramedItemModelBuilder(block);
        itemBuilder.accept(builder);
        blockModels.itemModelOutput.accept(item, builder.build());
    }

    /// Create a builder for a freestanding item model definition for the given block for nesting in another
    /// item model in a client item file.
    ///
    /// @param block The block to create the item model for
    /// @return the item model builder
    protected static FramedItemModelBuilder nestedFramedBlockItemModel(Holder<Block> block) {
        return new FramedItemModelBuilder(block);
    }

    /// Generate an item model for the given block from the given template.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the item model for
    /// @param template    The template to generate the item model from
    /// @param textures    The texture mapping to use in the generated model
    protected static void blockItemFromTemplate(BlockModelGenerators blockModels, Holder<Block> block, ModelTemplate template, TextureMapping textures) {
        Identifier name = ModelLocationUtils.getModelLocation(block.value().asItem(), template.suffix.orElse(""));
        blockModels.registerSimpleItemModel(block.value(), template.create(name, textures, blockModels.modelOutput));
    }

    /// Generate a two-element block model for the given block with a frame texture overlayed onto an arbitrary texture.
    ///
    /// @param blockModels The block model generators to use
    /// @param block       The block to generate the model for
    /// @param frameTex    The frame texture to use
    /// @param underlayTex The underlayed textured
    /// @param consumer    A function to adjust the template
    /// @return the path of the generated model
    protected static Identifier makeUnderlayedCube(BlockModelGenerators blockModels, Holder<Block> block, Material frameTex, Material underlayTex, Consumer<ExtendedModelTemplateBuilder> consumer) {
        Identifier name = ModelLocationUtils.getModelLocation(block.value());
        return makeUnderlayedCube(blockModels, name, frameTex, underlayTex, consumer);
    }

    /// Generate a two-element block model with the given path with a frame texture overlayed onto an arbitrary texture.
    ///
    /// @param blockModels The block model generators to use
    /// @param name        The param to generate the model at
    /// @param frameTex    The frame texture to use
    /// @param underlayTex The underlayed textured
    /// @param consumer    A function to adjust the template
    /// @return the path of the generated model
    protected static Identifier makeUnderlayedCube(BlockModelGenerators blockModels, Identifier name, Material frameTex, Material underlayTex, Consumer<ExtendedModelTemplateBuilder> consumer) {
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

    /// Generate a block model with a slightly expanded full-size cube element with the given texture for overlaying over arbitrary geometry.
    ///
    /// @param blockModels The block model generators to use
    /// @param name        The path to generate the model at
    /// @param texture     The texture to apply to the model
    protected static void makeOverlayCube(BlockModelGenerators blockModels, Identifier name, Material texture) {
        makeOverlayCube(blockModels, name, texture, _ -> {});
    }

    /// Generate a block model with a slightly expanded full-size cube element with the given texture for overlaying over arbitrary geometry.
    ///
    /// @param blockModels The block model generators to use
    /// @param name        The path to generate the model at
    /// @param texture     The texture to apply to the model
    /// @param consumer    A function to adjust the template
    protected static void makeOverlayCube(BlockModelGenerators blockModels, Identifier name, Material texture, Consumer<ExtendedModelTemplateBuilder> consumer) {
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

    /// {@return a function creating a multi-variant generator switching between the two given models depending on the given property}
    ///
    /// @param property The property to use for selecting the model
    /// @param ifFalse  The model to apply when the property is false
    /// @param ifTrue   The model to apply when the property is true
    protected static Function<MultiVariantGenerator.Empty, MultiVariantGenerator> boolVariant(BooleanProperty property, Identifier ifFalse, Identifier ifTrue) {
        return variant -> variant.with(
                PropertyDispatch.initial(property)
                        .select(false, BlockModelGenerators.plainVariant(ifFalse))
                        .select(true, BlockModelGenerators.plainVariant(ifTrue))
        );
    }

    /// {@return a variant mutator applying rotation in 90 degree increments based on the given 16-step rotation}
    ///
    /// @param rotation The 16-step rotation value
    protected static VariantMutator rotationToVariant(int rotation) {
        return horDirToVariant(Direction.from2DDataValue(rotation / 4));
    }

    /// {@return a variant mutator applying rotation in 90 degree increments based on the given horizontal direction}
    ///
    /// @param dir The horizontal rotation value
    protected static VariantMutator horDirToVariant(Direction dir) {
        Quadrant rotValue = switch (dir) {
            case NORTH -> Quadrant.R180;
            case SOUTH -> Quadrant.R0;
            case WEST -> Quadrant.R90;
            case EAST -> Quadrant.R270;
            default -> throw new IllegalArgumentException("Invalid direction for Y rotation: " + dir);
        };
        return VariantMutator.Y_ROT.withValue(rotValue);
    }

    /// {@return the rotation quadrant for the given index}
    ///
    /// @param idx The index to get the quadrant for
    protected static Quadrant rotByIdx(int idx) {
        return Quadrant.values()[idx];
    }

    /// {@return a condition which is true when all given conditions are true}
    ///
    /// @param conditions The conditions to combine
    protected static Condition and(ConditionBuilder... conditions) {
        return new CombinedCondition(CombinedCondition.Operation.AND, Stream.of(conditions).map(ConditionBuilder::build).toList());
    }

    /// {@return a material with the `minecraft` namespace and the given path}
    ///
    /// @param path The texture path
    protected static Material mcMaterial(String path) {
        return mcMaterial(path, false);
    }

    /// {@return a material with the `minecraft` namespace and the given path and `forceTranslucent` flag}
    ///
    /// @param path             The texture path
    /// @param forceTranslucent Whether the material should ignore the texture's actual translucency and always render in the translucent layer
    protected static Material mcMaterial(String path, boolean forceTranslucent) {
        return new Material(Identifier.withDefaultNamespace(path), forceTranslucent);
    }

    /// {@return a material with the namespace this provider is generating for and the given path}
    ///
    /// @param path The texture path
    protected final Material modMaterial(String path) {
        return modMaterial(path, false);
    }

    /// {@return a material with the namespace this provider is generating for and the given path and `forceTranslucent` flag}
    ///
    /// @param path             The texture path
    /// @param forceTranslucent Whether the material should ignore the texture's actual translucency and always render in the translucent layer
    protected final Material modMaterial(String path, boolean forceTranslucent) {
        return new Material(Utils.id(modId, path), forceTranslucent);
    }

    private void registerStandaloneDefinition(FramedBlockModelDefinitionGenerator definition) {
        StandaloneWrapperKey<?> wrapperKey = definition.getWrapperKey();
        FramedBlockModelDefinitionGenerator old = standaloneDefinitions.putIfAbsent(wrapperKey, definition);
        if (old != null) {
            throw new IllegalStateException("Duplicate standalone model definition for '" + wrapperKey + "'");
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        CompletableFuture<?> future = super.run(output);
        if (!standaloneDefinitions.isEmpty()) {
            Map<StandaloneWrapperKey<?>, BlockStateModelDispatcher> dispatchers = Maps.transformValues(standaloneDefinitions, BlockModelDefinitionGenerator::create);
            Function<StandaloneWrapperKey<?>, Path> pathGetter = wrapperKey -> standalonePathProvider.json(wrapperKey.definitionFile());
            future = CompletableFuture.allOf(future, DataProvider.saveAll(output, BlockStateModelDispatcher.CODEC, pathGetter, dispatchers));
        }
        return future;
    }

    @Override
    protected final Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(item -> item instanceof BlockItem);
    }

    @Override
    public String getName() {
        return "Block Models - " + modId;
    }
}
