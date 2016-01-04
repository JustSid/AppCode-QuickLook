package com.widerwille.quicklook;

import org.antlr.v4.runtime.misc.Nullable;

public class QuickLookDefaultValueRendererFactory implements QuickLookValueRendererFactory
{
	public QuickLookDefaultValueRendererFactory()
	{}

	@Override
	@Nullable
	public QuickLookValueRenderer createRenderer(QuickLookValue value) throws Exception
	{
		if(value.isKindOfClass("UIColor"))
			return new QuickLookColorValueRenderer(value);

		return null;
	}
}
