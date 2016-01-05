package com.widerwille.quicklook;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class QuickLookThumbnailComponent extends JComponent
{
	private QuickLookValueRenderer valueRenderer;

	public QuickLookThumbnailComponent()
	{
		setOpaque(true);
	}

	public void setValueRenderer(QuickLookValueRenderer valueRenderer)
	{
		this.valueRenderer = valueRenderer;
		repaint();
	}


	private int getThumbnailWidth()
	{
		return getWidth() - 10;
	}
	private int getThumbnailHeight()
	{
		return getHeight() - 30;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		if(g instanceof Graphics2D)
		{
			Graphics2D g2d = (Graphics2D)g;

			g2d.setComposite(AlphaComposite.Src);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		// Image
		{
			BufferedImage image = valueRenderer.getImageContent();

			float widthFactor = (getThumbnailWidth() - 10) / (float) image.getWidth();
			float heightFactor = (getThumbnailHeight() - 10) / (float) image.getHeight();

			float factor = (widthFactor > heightFactor) ? widthFactor : heightFactor;
			if(factor > 1.0f)
				factor = 1.0f;

			int width = (int) (image.getWidth() * factor);
			int height = (int) (image.getHeight() * factor);

			int x = (int) ((getWidth() - width) * 0.5);

			g.drawImage(image, x, 5, width, height, null);
		}

		// Name
		{
			String text = valueRenderer.getName();

			FontMetrics fm = g.getFontMetrics();
			Rectangle2D r = fm.getStringBounds(text, g);
			int x = (this.getWidth() - (int)r.getWidth()) / 2;
			int y = getHeight() - 5;

			g.setColor(getForeground());
			g.drawString(text, x, y);
		}

		// Type icon
		{
			Icon icon = valueRenderer.getTypeIcon();
			if(icon != null)
			{
				g.setColor(new Color(0, 0, 0, 0));
				icon.paintIcon(null, g, getWidth() - 20, getHeight() - 32);
			}
		}
	}
}
