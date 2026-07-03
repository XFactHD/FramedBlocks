package io.github.xfacthd.framedblocks.api.model.standalone;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

/// Describes a standalone model using the model wrapper system for processing its backing blockstate models.
public final class StandaloneWrapperKey<T> {
    public static final String STANDALONE_DEFINITION_FOLDER = "specialstates";

    private final Holder<Block> block;
    private final Identifier definitionFile;
    private final StandaloneModelKey<T> modelKey;
    private final boolean forceCt;

    /// @param block          The block using the standalone model represented by this key
    /// @param definitionFile The location of the definition file to load the backing blockstate models from
    public StandaloneWrapperKey(Holder<Block> block, Identifier definitionFile) {
        this(block, definitionFile, false);
    }

    /// @param block          The block using the standalone model represented by this key
    /// @param definitionFile The location of the definition file to load the backing blockstate models from
    /// @param forceCt        Whether the standalone model requires CT processing without taking the framed block into account
    public StandaloneWrapperKey(Holder<Block> block, Identifier definitionFile, boolean forceCt) {
        this.block = block;
        this.definitionFile = definitionFile;
        this.modelKey = new StandaloneModelKey<>(definitionFile::toString);
        this.forceCt = forceCt;
    }

    /// {@return the block using the standalone model represented by this key}
    public Holder<Block> block() {
        return block;
    }

    /// {@return the location of the definition file to load the backing blockstate models from}
    public Identifier definitionFile() {
        return definitionFile;
    }

    /// {@return the model key for retrieving the baked standalone model}
    public StandaloneModelKey<T> modelKey() {
        return modelKey;
    }

    /// {@return whether the standalone model requires CT processing without taking the framed block into account}
    public boolean isForceCt() {
        return forceCt;
    }

    @Override
    public String toString() {
        return "StandaloneWrapperKey{block=" + block + ", definitionFile=" + definitionFile + "}";
    }
}
