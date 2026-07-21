package fr.ghostrider584.axiom.metadata;

import net.minestom.server.codec.Codec;
import net.minestom.server.entity.MetadataDef;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.registry.Registries;
import net.minestom.server.registry.RegistryKey;

import java.util.ArrayList;
import java.util.List;

public final class MetadataMapperBuilder<T extends EntityMeta> {
	private final List<TypedMapping<?, T>> mappings = new ArrayList<>();

	private MetadataMapperBuilder() {
	}

	public static <T extends EntityMeta> MetadataMapperBuilder<T> create() {
		return new MetadataMapperBuilder<>();
	}

	public <V> MetadataMapperBuilder<T> mapping(String path, MetadataDef.Entry<V> entry, Codec<V> codec) {
		mappings.add(new TypedMapping<>(path, entry, codec));
		return this;
	}

	public <V> MetadataMapperBuilder<T> direct(String path, MetadataDef.Entry<V> entry) {
		mappings.add(new TypedMapping<>(path, entry, inferCodec(entry)));
		return this;
	}

	public <V> MetadataMapperBuilder<T> registry(String path, MetadataDef.Entry<RegistryKey<V>> entry, Registries.Selector<V> selector) {
		mappings.add(new TypedMapping<>(path, entry, RegistryKey.codec(selector)));
		return this;
	}

	public MetadataMapper<T> build() {
		return new SimpleMetadataMapper<>(List.copyOf(mappings));
	}

	public <M extends EntityMeta> MetadataMapper<M> extend(MetadataMapper<? super M> base, Class<M> targetType) {
		return new CompositeMetadataMapper<>(base, new SimpleMetadataMapper<>(List.copyOf(mappings)));
	}

	public <M extends EntityMeta> MetadataMapper<M> extend(MetadataMapper<? super M> base) {
		return new CompositeMetadataMapper<>(base, new SimpleMetadataMapper<>(List.copyOf(mappings)));
	}

	@SuppressWarnings("unchecked")
	private static <V> Codec<V> inferCodec(MetadataDef.Entry<V> entry) {
		final var defaultValue = entry.defaultValue();
		return switch (defaultValue) {
			case Integer ignored -> (Codec<V>) Codec.INT;
			case Float ignored -> (Codec<V>) Codec.FLOAT;
			case Boolean ignored -> (Codec<V>) Codec.BOOLEAN;
			case Byte ignored -> (Codec<V>) Codec.BYTE;
			case String ignored -> (Codec<V>) Codec.STRING;
			default -> throw new IllegalArgumentException("Cannot infer codec for type: " + defaultValue.getClass() +
					". Use mapping() with an explicit codec instead.");
		};
	}

	record TypedMapping<V, T extends EntityMeta>(
			String path,
			MetadataDef.Entry<V> entry,
			Codec<V> codec
	) implements MappingEntry<V> {}
}