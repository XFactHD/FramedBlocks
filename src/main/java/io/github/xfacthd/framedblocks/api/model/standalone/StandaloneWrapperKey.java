package io.github.xfacthd.framedblocks.api.model.standalone;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

public final class StandaloneWrapperKey<T> {
    public static final String STANDALONE_DEFINITION_FOLDER = "specialstates";

    private final Holder<Block> block;
    private final Identifier definitionFile;
    private final StandaloneModelKey<T> modelKey;
    private final boolean forceCt;

    public StandaloneWrapperKey(Holder<Block> block, Identifier definitionFile) {
        this(block, definitionFile, false);
    }

    public StandaloneWrapperKey(Holder<Block> block, Identifier definitionFile, boolean forceCt) {
        this.block = block;
        this.definitionFile = definitionFile;
        this.modelKey = new StandaloneModelKey<>(definitionFile::toString);
        this.forceCt = forceCt;
    }

    public Holder<Block> block() {
        return block;
    }

    public Identifier definitionFile() {
        return definitionFile;
    }

    public StandaloneModelKey<T> modelKey() {
        return modelKey;
    }

    public boolean isForceCt() {
        return forceCt;
    }

    @Override
    public String toString() {
        return "StandaloneWrapperKey{block=" + block + ", definitionFile=" + definitionFile + "}";
    }
}
