package com.widerwille.quicklook;

import org.jetbrains.annotations.Nullable;


public class QuickLookUIViewValueRenderer extends QuickLookValueRenderer
{
	private QuickLookValue data;

	QuickLookUIViewValueRenderer(QuickLookValue type)
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
				QuickLookValue layer = context.createVariable("CALayer *", "layer");
				QuickLookValue cgContext = context.createVariable("CGContextRef", "context");

				context.evaluate(layer.getName() + " = (CALayer *)[(UIView *)" + value.getPointer() + " layer]");

				context.evaluate("(void)UIGraphicsBeginImageContextWithOptions(((CGRect)[((UIView *)" + value.getPointer() + ") bounds]).size, (BOOL)[((UIView *)" + value.getPointer() + ") isOpaque], 0.0)");
				context.evaluate(cgContext.getName() + " = (CGContextRef)UIGraphicsGetCurrentContext()");

				cgContext.refresh();

				context.evaluate("((void(*)(id, SEL, CGContextRef))objc_msgSend)((id)" + layer.getName() + ", (SEL)NSSelectorFromString(@\"" + "renderInContext:" + "\"), " + cgContext.getName() + ")");
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
