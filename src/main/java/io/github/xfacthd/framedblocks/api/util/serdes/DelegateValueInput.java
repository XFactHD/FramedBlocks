package io.github.xfacthd.framedblocks.api.util.serdes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.Optional;
import java.util.Set;

public abstract class DelegateValueInput implements ValueInput {
    protected final ValueInput delegate;

    protected DelegateValueInput(ValueInput delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> Optional<T> read(String key, Codec<T> codec) {
        return delegate.read(key, codec);
    }

    @Override
    @Deprecated
    public <T> Optional<T> read(MapCodec<T> codec) {
        return delegate.read(codec);
    }

    @Override
    public Optional<ValueInput> child(String key) {
        return delegate.child(key);
    }

    @Override
    public ValueInput childOrEmpty(String key) {
        return delegate.childOrEmpty(key);
    }

    @Override
    public Optional<ValueInputList> childrenList(String key) {
        return delegate.childrenList(key);
    }

    @Override
    public ValueInputList childrenListOrEmpty(String key) {
        return delegate.childrenListOrEmpty(key);
    }

    @Override
    public <T> Optional<TypedInputList<T>> list(String key, Codec<T> elementCodec) {
        return delegate.list(key, elementCodec);
    }

    @Override
    public <T> TypedInputList<T> listOrEmpty(String key, Codec<T> elementCodec) {
        return delegate.listOrEmpty(key, elementCodec);
    }

    @Override
    public boolean getBooleanOr(String key, boolean defaultValue) {
        return delegate.getBooleanOr(key, defaultValue);
    }

    @Override
    public byte getByteOr(String key, byte defaultValue) {
        return delegate.getByteOr(key, defaultValue);
    }

    @Override
    public int getShortOr(String key, short defaultValue) {
        return delegate.getShortOr(key, defaultValue);
    }

    @Override
    public Optional<Integer> getInt(String key) {
        return delegate.getInt(key);
    }

    @Override
    public int getIntOr(String key, int defaultValue) {
        return delegate.getIntOr(key, defaultValue);
    }

    @Override
    public long getLongOr(String key, long defaultValue) {
        return delegate.getLongOr(key, defaultValue);
    }

    @Override
    public Optional<Long> getLong(String key) {
        return delegate.getLong(key);
    }

    @Override
    public float getFloatOr(String key, float defaultValue) {
        return delegate.getFloatOr(key, defaultValue);
    }

    @Override
    public double getDoubleOr(String key, double defaultValue) {
        return delegate.getDoubleOr(key, defaultValue);
    }

    @Override
    public Optional<String> getString(String key) {
        return delegate.getString(key);
    }

    @Override
    public String getStringOr(String key, String defaultValue) {
        return delegate.getStringOr(key, defaultValue);
    }

    @Override
    public Optional<int[]> getIntArray(String key) {
        return delegate.getIntArray(key);
    }

    @Override
    @Deprecated
    public HolderLookup.Provider lookup() {
        return delegate.lookup();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public void readChild(String key, ValueIOSerializable object) {
        delegate.readChild(key, object);
    }

    @Override
    public ValueInput rawChildOrEmpty(String key) {
        return delegate.rawChildOrEmpty(key);
    }
}
