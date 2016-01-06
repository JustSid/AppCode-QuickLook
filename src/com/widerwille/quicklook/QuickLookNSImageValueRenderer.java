package com.widerwille.quicklook;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class QuickLookNSImageValueRenderer extends QuickLookValueRenderer
{
	private static Icon TypeIcon = IconLoader.getIcon("/types/UIImage.png");
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
	@Nullable
	public Icon getTypeIcon()
	{
		return TypeIcon;
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
				QuickLookValue cgImage = value.createVariable("CGImageRef", "cgImage");
				QuickLookValue bitmapRef = value.createVariable("NSBitmapImageRep *", "bitmapRef");

				value.evaluate(cgImage.getName() + " = (CGImageRef)[(NSImage *)" + value.getPointer() + " CGImageForProposedRect:NULL context:nil hints:nil]");
				value.evaluate(bitmapRef.getName() + " = (NSBitmapImageRep *)[[NSBitmapImageRep alloc] initWithCGImage:" + cgImage.getName() + "]");
				value.evaluate("[(NSBitmapImageRep *)" + bitmapRef.getName() + " setSize:(CGSize)[(NSImage *)" + value.getPointer() + " size]]");

				cgImage.refresh();
				bitmapRef.refresh();

				data = value.evaluate("(NSData *)[(NSBitmapImageRep *)" + bitmapRef.getName() + " representationUsingType:4 properties:nil]");

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
