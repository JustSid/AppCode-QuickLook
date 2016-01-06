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
		// iOS / UIKit
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

		if(value.isKindOfClass("UIView"))
			return new QuickLookUIViewValueRenderer(value);

		if(value.isKindOfClass("UIBezierPath"))
			return new QuickLookUIBezierPathValueRenderer(value);

		// OS X / AppKit
		if(value.isKindOfClass("NSColor"))
			return new QuickLookUIColorValueRenderer(value);

		if(value.isKindOfClass("NSImageView"))
		{
			value = value.sendMessage("image");
			if(value.isNilPointer())
				return null;
		}

		if(value.isKindOfClass("NSImage"))
			return new QuickLookNSImageValueRenderer(value);
		if(value.isKindOfClass("NSBitmapImageRep"))
			return new QuickLookNSBitmapImageRepValueRenderer(value);

		if(value.isKindOfClass("NSView"))
			return new QuickLookNSViewValueRenderer(value);


		return null;
	}
}
