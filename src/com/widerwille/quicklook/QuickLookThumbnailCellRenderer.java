package com.widerwille.quicklook;

import javax.swing.*;
import java.awt.*;

public class QuickLookThumbnailCellRenderer implements ListCellRenderer
{
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		if(!(value instanceof QuickLookValueRenderer))
			return null;

		QuickLookValueRenderer renderer = (QuickLookValueRenderer)value;
		QuickLookThumbnailComponent component = new QuickLookThumbnailComponent();

		component.setValueRenderer(renderer);

		if(isSelected)
		{
			component.setForeground(list.getSelectionForeground());
			component.setBackground(list.getSelectionBackground());
		}
		else
		{
			component.setForeground(list.getForeground());
			component.setBackground(list.getBackground());
		}

		return component;
	}
}
