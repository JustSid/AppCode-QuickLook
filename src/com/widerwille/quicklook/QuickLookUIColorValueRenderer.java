package com.widerwille.quicklook;

import org.antlr.v4.runtime.misc.Nullable;

import javax.swing.*;
import java.awt.*;

public class QuickLookUIColorValueRenderer extends QuickLookValueRenderer
{
	private QuickLookColorIcon icon;
	private float redValue;
	private float greenValue;
	private float blueValue;
	private float alphaValue;

	QuickLookUIColorValueRenderer(QuickLookValue type)
	{
		super(type);

		try
		{
			QuickLookValue red = type.createVariable("CGFloat", "red");
			QuickLookValue green = type.createVariable("CGFloat", "green");
			QuickLookValue blue = type.createVariable("CGFloat", "blue");
			QuickLookValue alpha = type.createVariable("CGFloat", "alpha");

			QuickLookValue result = type.sendMessage("getRed:&" + red + " green:&" + green + " blue:&" + blue + "  alpha:&" + alpha);
			if(result.getValue().isTrue())
			{
				redValue = red.getFloatValue();
				greenValue = green.getFloatValue();
				blueValue = blue.getFloatValue();
				alphaValue = alpha.getFloatValue();

				icon = new QuickLookColorIcon(new Color((int)(redValue * 255), (int)(greenValue * 255), (int)(blueValue * 255), (int)(alphaValue * 255)));
			}
		}
		catch(Exception e)
		{}
	}

	@Override
	@Nullable
	public String getDisplayValue()
	{
		return "{" + redValue + ", " + greenValue + ", " + blueValue + ", " + alphaValue + "}";
	}

	@Override
	@Nullable
	public Icon getIcon()
	{
		return icon;
	}
}
