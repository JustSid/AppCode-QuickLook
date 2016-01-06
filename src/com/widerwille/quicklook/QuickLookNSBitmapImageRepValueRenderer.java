package com.widerwille.quicklook;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class QuickLookNSBitmapImageRepValueRenderer extends QuickLookValueRenderer
{
	private static Icon TypeIcon = IconLoader.getIcon("/types/UIImage.png");
	private QuickLookValue data;

	QuickLookNSBitmapImageRepValueRenderer(QuickLookValue type)
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

				data = value.evaluate("(NSData *)[(NSBitmapImageRep *)" + value.getPointer() + " representationUsingType:4 properties:nil]");

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
