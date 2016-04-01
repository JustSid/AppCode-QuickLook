package com.widerwille.quicklook;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

public class QuickLookNSImageValueRenderer extends QuickLookValueRenderer
{
	private QuickLookValue data;

	QuickLookNSImageValueRenderer(QuickLookValue type)
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

				QuickLookValue cgImage = context.createVariable("CGImageRef", "cgImage");
				QuickLookValue bitmapRef = context.createVariable("NSBitmapImageRep *", "bitmapRef");

				context.evaluate(cgImage.getName() + " = (CGImageRef)[(NSImage *)" + value.getPointer() + " CGImageForProposedRect:NULL context:nil hints:nil]");
				context.evaluate(bitmapRef.getName() + " = (NSBitmapImageRep *)[[NSBitmapImageRep alloc] initWithCGImage:" + cgImage.getName() + "]");
				context.evaluate("[(NSBitmapImageRep *)" + bitmapRef.getName() + " setSize:(CGSize)[(NSImage *)" + value.getPointer() + " size]]");

				cgImage.refresh();
				bitmapRef.refresh();

				data = context.evaluate("(NSData *)[(NSBitmapImageRep *)" + bitmapRef.getName() + " representationUsingType:4 properties:nil]");

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
