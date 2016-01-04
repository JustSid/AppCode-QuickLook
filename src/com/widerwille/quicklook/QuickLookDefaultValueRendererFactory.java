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
			return new QuickLookUIColorValueRenderer(value);

		if(value.isKindOfClass("UIImageView"))
		{
			value = value.sendMessage("image");
			if(value.isNilPointer())
				return null;
		}

		if(value.isKindOfClass("UIImage"))
			return new QuickLookUIImageValueRenderer(value);

		return null;
	}
}
