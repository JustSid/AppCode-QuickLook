package com.widerwille.quicklook;

import org.jetbrains.annotations.Nullable;

public class QuickLookUIBezierPathValueRenderer extends QuickLookValueRenderer
{
	private QuickLookValue data;

	QuickLookUIBezierPathValueRenderer(QuickLookValue type)
	{
		super(type);
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

				QuickLookValue image = context.createVariable("UIImage *", "image");
				QuickLookValue cgContext = context.createVariable("CGContextRef", "context");

				context.evaluate("(void)UIGraphicsBeginImageContextWithOptions(((CGRect)[((UIBezierPath *)" + value.getPointer() + ") bounds]).size, NO, 0.0)");
				context.evaluate(cgContext.getName() + " = (CGContextRef)UIGraphicsGetCurrentContext()");

				cgContext.refresh();

				context.evaluate("(void)[(UIColor *)[UIColor colorWithRed:0.176 green:0.541 blue:1.0 alpha:0.6] setFill]");
				context.evaluate("(void)[(UIBezierPath *)" + value.getPointer() + " fill]");

				context.evaluate("(void)[(UIColor *)[UIColor colorWithRed:0.176 green:0.541 blue:1.0 alpha:1.0] setStroke]");
				context.evaluate("(void)[(UIBezierPath *)" + value.getPointer() + " stroke]");

				context.evaluate(image.getName() + " = (UIImage *)UIGraphicsGetImageFromCurrentImageContext()");
				context.evaluate("(void)UIGraphicsEndImageContext()");

				image.refresh();
				data = context.evaluate("(NSData *)UIImagePNGRepresentation((UIImage *)" + image.getPointer() + ")");

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
