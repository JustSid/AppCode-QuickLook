package com.widerwille.quicklook;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

public class QuickLookNSBitmapImageRepValueRenderer extends QuickLookValueRenderer
{
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

				data = context.evaluate("(NSData *)[(NSBitmapImageRep *)" + value.getPointer() + " representationUsingType:4 properties:nil]");

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
