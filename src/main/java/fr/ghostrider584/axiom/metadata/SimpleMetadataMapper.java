package fr.ghostrider584.axiom.metadata;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.codec.Result;
import net.minestom.server.codec.Transcoder;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.registry.RegistryTranscoder;

import java.util.List;

final class SimpleMetadataMapper<T extends EntityMeta> implements MetadataMapper<T> {
	private static final Transcoder<BinaryTag> REGISTRY_NBT_TRANSCODER = new RegistryTranscoder<>(Transcoder.NBT, MinecraftServer.process());

	private final List<? extends MappingEntry<?>> mappings;

	SimpleMetadataMapper(List<? extends MappingEntry<?>> mappings) {
		this.mappings = List.copyOf(mappings);
	}

	@Override
	public void applyFromNBT(T meta, CompoundBinaryTag nbt) {
		for (final var mapping : mappings) {
			final var value = getNestedValue(nbt, mapping.path());
			if (value != null) {
				applyMapping(meta, mapping, value);
			}
		}
	}

	@Override
	public CompoundBinaryTag toNBT(T meta) {
		final var holder = MetadataFieldAccessor.getMetadataHolder(meta);

		var rootTag = CompoundBinaryTag.empty();
		for (final var mapping : mappings) {
			rootTag = encodeMapping(holder, mapping, rootTag);
		}

		return rootTag;
	}

	private <V> CompoundBinaryTag encodeMapping(
			net.minestom.server.entity.MetadataHolder holder,
			MappingEntry<V> mapping,
			CompoundBinaryTag rootTag
	) {
		final var value = holder.get(mapping.entry());
		final var encoded = mapping.codec().encode(REGISTRY_NBT_TRANSCODER, value);

		return switch (encoded) {
			case Result.Ok<BinaryTag>(BinaryTag tag) -> setNestedValue(rootTag, mapping.path(), tag);
			case Result.Error<BinaryTag>(String message) ->
					throw new MetadataMapperException("Failed to encode '" + mapping.path() + "': " + message);
		};
	}

	private <V> void applyMapping(T meta, MappingEntry<V> mapping, BinaryTag nbtValue) {
		final var holder = MetadataFieldAccessor.getMetadataHolder(meta);
		final var decoded = mapping.codec().decode(REGISTRY_NBT_TRANSCODER, nbtValue);

		switch (decoded) {
			case Result.Ok<V>(V value) -> holder.set(mapping.entry(), value);
			case Result.Error<V>(String message) ->
					throw new MetadataMapperException("Failed to decode '" + mapping.path() + "': " + message);
		}
	}

	private BinaryTag getNestedValue(CompoundBinaryTag nbt, String path) {
		if (!path.contains(".")) {
			return nbt.get(path);
		}

		final var parts = path.split("\\.");
		BinaryTag current = nbt;

		for (final var part : parts) {
			if (current instanceof CompoundBinaryTag compound) {
				current = compound.get(part);
				if (current == null) {
					return null;
				}
			} else {
				return null;
			}
		}

		return current;
	}

	private static CompoundBinaryTag setNestedValue(CompoundBinaryTag existing, String path, BinaryTag value) {
		if (!path.contains(".")) {
			return existing.put(path, value);
		}

		final var parts = path.split("\\.");
		return setNestedValue(existing, parts, 0, value);
	}

	private static CompoundBinaryTag setNestedValue(CompoundBinaryTag current, String[] parts, int index, BinaryTag value) {
		final var key = parts[index];
		if (index == parts.length - 1) {
			return current.put(key, value);
		}

		final var nested = current.getCompound(key);
		final var updatedNested = setNestedValue(nested, parts, index + 1, value);

		return current.put(key, updatedNested);
	}
}