package com.widerwille.quicklook.helper;

import javax.swing.*;
import java.awt.*;

public class QuickLookColorIcon implements Icon
{
	private Color color;

	public QuickLookColorIcon(Color color)
	{
		this.color = color;
	}

	public final void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.setColor(color);
		g.fillRect(x, y, getIconWidth(), getIconHeight());
	}

	public final int getIconWidth()
	{
		return 16;
	}

	public final int getIconHeight()
	{
		return 16;
	}
}
