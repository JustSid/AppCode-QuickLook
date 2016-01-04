package com.widerwille.quicklook;

import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import org.antlr.v4.runtime.misc.Nullable;

import javax.swing.*;
import java.awt.*;

public class QuickLookColorValueRenderer extends QuickLookValueRenderer
{
	private QuickLookColorIcon icon;
	private int redValue;
	private int greenValue;
	private int blueValue;
	private int alphaValue;

	QuickLookColorValueRenderer(QuickLookValue type)
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
				redValue = (int)(red.getFloatValue() * 255);
				greenValue = (int)(green.getFloatValue() * 255);
				blueValue = (int)(blue.getFloatValue() * 255);
				alphaValue = (int)(alpha.getFloatValue() * 255);

				icon = new QuickLookColorIcon(new Color(redValue, greenValue, blueValue, alphaValue));
			}
		}
		catch(Exception e)
		{}
	}

	@Override
	@Nullable
	public String getDisplayValue()
	{
		return "<RGBA: " + redValue + ", " + greenValue + ", " + blueValue + ", " + alphaValue + ">";
	}

	@Override
	public Icon getIcon(boolean b, CidrPhysicalValue value)
	{
		if(icon == null)
			return super.getIcon(b, value);

		return icon;
	}

	@Override
	protected boolean shouldPrintChildrenConsoleDescription()
	{
		return false;
	}
}
