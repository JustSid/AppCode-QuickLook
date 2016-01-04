package com.widerwille.quicklook;

import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class QuickLookUIImageValueRenderer extends QuickLookValueRenderer
{
	private QuickLookValue data;
	private BufferedImage image;
	private QuickLookImageIcon imageIcon;

	QuickLookUIImageValueRenderer(QuickLookValue type)
	{
		super(type);
	}

	@Override
	@Nullable
	public String getDisplayValue()
	{
		if(image == null)
			return "<Unknown image>";

		return "{" + image.getWidth() + ", " + image.getHeight() + "}";
	}

	@Override
	public Icon getIcon()
	{
		getImage();
		return imageIcon;
	}

	public QuickLookValue getDataValue()
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

	public Image getImage()
	{
		if(image == null)
		{
			try
			{
				File file = getDataFile("png");

				image = ImageIO.read(file);
				if(image != null)
					imageIcon = new QuickLookImageIcon(image, 16, 16);
			}
			catch(Exception e)
			{
				image = null;
				imageIcon = null;
			}
		}

		return image;
	}
}
