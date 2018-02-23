package org.nutz.plugins.dict;

import org.nutz.plugins.dict.chain.EditableSelectProcessor;
import org.nutz.plugins.dict.chain.JqgridSelectProcessor;

public class TestSelectBuilder {
	public static void main(String[] args) {
		Selects.custom().addProcessorFirst(new JqgridSelectProcessor()).addProcessorLast(new EditableSelectProcessor()).setPackages("org.nutz.plugins.dict").setJsonFilePath("e:/dict").build();
	}
}
