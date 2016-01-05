package com.widerwille.quicklook;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

public class QuickLookUIImageValueRenderer extends QuickLookValueRenderer
{
	private QuickLookValue data;

	QuickLookUIImageValueRenderer(QuickLookValue type)
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


	public boolean hasImageContent()
	{
		return true;
	}
	protected QuickLookValue getDataValue()
	{
		try
		{
			if(data == null)
			{
				QuickLookValue value = getQuickLookValue();
				data = value.evaluate("(NSData *)UIImagePNGRepresentation((UIImage *)" + value.getPointer() + ")");

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
