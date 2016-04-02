package com.widerwille.quicklook.helper;

import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class QuickLookImageIcon implements Icon
{
	private BufferedImage image = null;
	private int width;
	private int height;

	QuickLookImageIcon(Image image, int width, int height)
	{
		BufferedImage resizedImage = UIUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = resizedImage.createGraphics();

		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

		{
			float widthFactor = width / (float) image.getWidth(null);
			float heightFactor = height / (float) image.getHeight(null);

			float factor = (widthFactor > heightFactor) ? heightFactor : widthFactor;
			if(factor > 1.0f)
				factor = 1.0f;

			int twidth = (int) (image.getWidth(null) * factor);
			int theight = (int) (image.getHeight(null) * factor);

			int x = (int)((width - twidth) * 0.5);
			int y = (int)((height - theight) * 0.5);

			g.drawImage(image, x, y, twidth, theight, null);
		}

		g.dispose();

		this.image = resizedImage;
		this.width = width;
		this.height = height;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.drawImage(image, x, y, getIconWidth(), getIconHeight(), c);
	}


	@Override
	public int getIconWidth()
	{
		return width;
	}

	@Override
	public int getIconHeight()
	{
		return height;
	}
}
