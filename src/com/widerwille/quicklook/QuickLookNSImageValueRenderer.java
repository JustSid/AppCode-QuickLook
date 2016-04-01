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
		try
		{

			QuickLookValue value = getQuickLookValue();
			QuickLookEvaluationContext context = value.getContext();

			QuickLookValue cgImage = context.createVariable("CGImageRef", "cgImage");

			context.evaluate(cgImage.getName() + " = (CGImageRef)[(NSImage *)" + value.getPointer() + " CGImageForProposedRect:NULL context:nil hints:nil]");

			cgImage.refresh();


			QuickLookValue width = context.createVariable("CGFloat", "width");
			QuickLookValue height = context.createVariable("CGFloat", "height");

			context.evaluate(width.getName() + " = CGImageGetWidth(" + cgImage.getName() + ")");
			context.evaluate(height.getName() + " = CGImageGetHeight(" + cgImage.getName() + ")");

			return "{" + width.getFloatValue() + ", " + height.getFloatValue() + "}";
		}
		catch(Exception e)
		{
			return "<unknown image>";
		}
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
