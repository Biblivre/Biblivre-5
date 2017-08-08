package org.jzkit.search.util.RecordModel.marc;

import org.jzkit.search.util.RecordModel.ExplicitRecordFormatSpecification;

public class iso2709 extends org.jzkit.search.util.RecordModel.iso2709 {
	private static final long serialVersionUID = 1L;

	public iso2709(Object source) {
		super(source);
	}

	public iso2709(Object source, String character_encoding) {
		super(source, character_encoding);
	}

	public iso2709(String source_repository, String source_collection_name, ExplicitRecordFormatSpecification spec, Object handle, Object source) {
		super(source_repository, source_collection_name, spec, handle, source);
	}

	public iso2709(String source_repository, String source_collection_name, ExplicitRecordFormatSpecification spec, Object handle, Object source, String character_encoding) {
		super(source_repository, source_collection_name, spec, handle, source, character_encoding);
	}
}
