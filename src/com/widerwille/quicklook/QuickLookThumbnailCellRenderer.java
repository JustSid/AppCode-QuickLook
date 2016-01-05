package com.widerwille.quicklook;

import org.intellij.images.ui.ImageComponent;
import org.intellij.images.ui.ThumbnailComponent;

import javax.swing.*;
import java.awt.*;

public class QuickLookThumbnailCellRenderer extends ThumbnailComponent implements ListCellRenderer
{
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		if(!(value instanceof QuickLookValueRenderer))
			return null;

		QuickLookValueRenderer renderer = (QuickLookValueRenderer)value;

		setFileName(renderer.getValue().getName());
		setToolTipText(renderer.getDisplayValue());
		setDirectory(false);
		setFileSize(0);
		setFormat("");

		ImageComponent imageComponent = getImageComponent();
		imageComponent.getDocument().setValue(renderer.getImageContent());

		if(isSelected)
		{
			setForeground(list.getSelectionForeground());
			setBackground(list.getSelectionBackground());
		}
		else
		{
			setForeground(list.getForeground());
			setBackground(list.getBackground());
		}

		return this;
	}
}
