package com.widerwille.quicklook;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

public class QuickLookNSViewValueRenderer extends QuickLookValueRenderer
{
	private QuickLookValue data;

	QuickLookNSViewValueRenderer(QuickLookValue type)
	{
		super(type);
	}

	@Override
	@Nullable
	public String getDisplayValue()
	{
		BufferedImage image = getImageContent();

		if(image == null)
			return "<Unknown image>";

		return "{" + image.getWidth() + ", " + image.getHeight() + "}";
	}

	@Override
	public boolean hasImageContent()
	{
		return true;
	}

	@Override
	@Nullable
	protected QuickLookValue getDataValue()
	{
		try
		{
			if(data == null)
			{
				QuickLookValue value = getQuickLookValue();
				QuickLookEvaluationContext context = value.getContext();

				QuickLookValue bitmapRef = context.createVariable("NSBitmapImageRep *", "bitmapRef");

				context.evaluate(bitmapRef.getName() + " = (NSBitmapImageRep *)[(NSView *)" + value.getPointer() + " bitmapImageRepForCachingDisplayInRect:(NSRect)[(NSView *)" + value.getPointer() + " bounds]]");

				bitmapRef.refresh();
				bitmapRef.sendMessage("setSize:(CGSize)((CGRect)[(NSView *)" + value.getPointer() + " bounds]).size");

				value.sendMessage("cacheDisplayInRect:(CGSize)((CGRect)[(NSView *)" + value.getPointer() + " bounds]).size toBitmapImageRep:(NSBitmapImageRep *)" + bitmapRef.getPointer(), "void");

				data = context.evaluate("(NSData *)[(NSBitmapImageRep *)" + bitmapRef.getPointer() + " representationUsingType:4 properties:nil]");

				if(!data.isValid() || !data.isPointer())
					data = null;
			}
		}
		catch(Exception e)
		{
			data = null;
		}

		return data;
	}
}
