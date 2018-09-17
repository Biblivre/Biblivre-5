package biblivre.core.utils;

import java.util.function.Supplier;

import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ParagraphAlignment;

public class ParagraphAlignmentUtil {
	public static int getHorizontalAlignmentConfigurationValue(String schema,
			Supplier<? extends Integer> defaultValue) {

		String configurationValue = Configurations.getString(schema,
				Constants.CONFIG_LABEL_PRINT_PARAGRAPH_ALIGNMENT);
		int horizontalAlignment = ParagraphAlignment.valueOf(
				configurationValue)
				.getAlignment().orElseGet(defaultValue);
		return horizontalAlignment;
	}
}
