package com.widerwille.quicklook;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
				QuickLookValue image = value.createVariable("UIImage *", "image");
				QuickLookValue context = value.createVariable("CGContextRef", "context");

				value.evaluate("(void)UIGraphicsBeginImageContextWithOptions(((CGRect)[((UIBezierPath *)" + value.getPointer() + ") bounds]).size, NO, 0.0)");
				value.evaluate(context.getName() + " = (CGContextRef)UIGraphicsGetCurrentContext()");

				context.refresh();

				value.evaluate("(void)[(UIColor *)[UIColor colorWithRed:0.176 green:0.541 blue:1.0 alpha:0.6] setFill]");
				value.evaluate("(void)[(UIBezierPath *)" + value.getPointer() + " fill]");

				value.evaluate("(void)[(UIColor *)[UIColor colorWithRed:0.176 green:0.541 blue:1.0 alpha:1.0] setStroke]");
				value.evaluate("(void)[(UIBezierPath *)" + value.getPointer() + " stroke]");

				value.evaluate(image.getName() + " = (UIImage *)UIGraphicsGetImageFromCurrentImageContext()");
				value.evaluate("(void)UIGraphicsEndImageContext()");

				image.refresh();
				data = value.evaluate("(NSData *)UIImagePNGRepresentation((UIImage *)" + image.getPointer() + ")");

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
