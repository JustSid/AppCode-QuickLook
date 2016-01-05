package com.widerwille.quicklook;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class QuickLookUIViewValueRenderer extends QuickLookValueRenderer
{
	private static Icon TypeIcon = IconLoader.getIcon("/types/UIView.png");
	private QuickLookValue data;

	QuickLookUIViewValueRenderer(QuickLookValue type)
	{
		super(type);
	}

	@Override
	@Nullable
	public Icon getTypeIcon()
	{
		return TypeIcon;
	}

	@Override
	@Nullable
	public String getDisplayValue()
	{
		return getQuickLookValue().getDescription();
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
				QuickLookValue layer = value.createVariable("CALayer *", "layer");
				QuickLookValue context = value.createVariable("CGContextRef", "context");

				value.evaluate(layer.getName() + " = (CALayer *)[(UIView *)" + value.getPointer() + " layer]");

				value.evaluate("(void)UIGraphicsBeginImageContextWithOptions(((CGRect)[((UIView *)" + value.getPointer() + ") bounds]).size, (BOOL)[((UIView *)" + value.getPointer() + ") isOpaque], 0.0)");
				value.evaluate(context.getName() + " = (CGContextRef)UIGraphicsGetCurrentContext()");

				context.refresh();

				value.evaluate("((void(*)(id, SEL, CGContextRef))objc_msgSend)((id)" + layer.getName() + ", (SEL)NSSelectorFromString(@\"" + "renderInContext:" + "\"), " + context.getName() + ")");
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
