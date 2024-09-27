package biblivre.core.enums;

import java.util.Optional;

import com.lowagie.text.Element;

public enum ParagraphAlignment {
	ALIGN_JUSTIFIED,
	ALIGN_JUSTIFIED_ALL,
	ALIGN_LEFT,
	ALIGN_RIGHT,
	ALIGN_CENTER;	// Default

	public Optional<Integer> getAlignment() {
		try {
			return Optional.of(Element.class.getField(this.name()).getInt(null));
		} catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			// This shouldn't happen except for a typo
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
