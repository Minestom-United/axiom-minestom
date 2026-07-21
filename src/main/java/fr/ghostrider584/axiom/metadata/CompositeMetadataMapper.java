package fr.ghostrider584.axiom.metadata;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.entity.metadata.EntityMeta;

final class CompositeMetadataMapper<T extends EntityMeta> implements MetadataMapper<T> {
	private final MetadataMapper<? super T> base;
	private final MetadataMapper<T> additional;

	CompositeMetadataMapper(MetadataMapper<? super T> base, MetadataMapper<T> additional) {
		this.base = base;
		this.additional = additional;
	}

	@Override
	public void applyFromNBT(T meta, CompoundBinaryTag nbt) {
		base.applyFromNBT(meta, nbt);
		additional.applyFromNBT(meta, nbt);
	}

	@Override
	public CompoundBinaryTag toNBT(T meta) {
		final var baseNbt = base.toNBT(meta);
		final var additionalNbt = additional.toNBT(meta);

		final var merged = CompoundBinaryTag.builder();
		merged.put(baseNbt);
		merged.put(additionalNbt);
		return merged.build();
	}
}