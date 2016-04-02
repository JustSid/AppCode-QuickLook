package com.widerwille.quicklook.renderer;

import com.widerwille.quicklook.QuickLookValue;
import com.widerwille.quicklook.QuickLookValueRenderer;
import com.widerwille.quicklook.QuickLookEvaluationContext;
import com.widerwille.quicklook.helper.QuickLookColorIcon;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.execution.debugger.backend.DBCannotEvaluateException;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class QuickLookUIColorValueRenderer extends QuickLookValueRenderer
{
	private static final Key<Boolean> IS_COLOR = Key.create("IS_COLOR");

	private QuickLookColorIcon icon;
	private float redValue;
	private float greenValue;
	private float blueValue;
	private float alphaValue;

	public static QuickLookValueRenderer createRendererIfPossible(QuickLookValue value)
	{
		try
		{
			CidrPhysicalValue physicalValue = value.getPhysicalValue();
			EvaluationContext context = value.getContext().getUnderlyingContext();


			String type = physicalValue.getType();
			Boolean isColor = context.getCachedTypeInfo(type, IS_COLOR);

			if(isColor == null)
			{
				isColor = isColorType(value);
				context.putCachedTypeInfo(type, IS_COLOR, isColor);
			}

			if(isColor)
				return new QuickLookUIColorValueRenderer(value);
		}
		catch(Exception e)
		{}

		return null;
	}

	private static boolean isColorType(QuickLookValue value)
	{
		try
		{
			return (value.isKindOfClass("UIColor") || value.isKindOfClass("NSColor"));
		}
		catch(Exception e)
		{
			return false;
		}
	}

	private QuickLookUIColorValueRenderer(QuickLookValue type) throws DBCannotEvaluateException, ExecutionException
	{
		super(type);

		QuickLookEvaluationContext context = type.getContext();

		QuickLookValue red = context.createVariable("CGFloat", "red");
		QuickLookValue green = context.createVariable("CGFloat", "green");
		QuickLookValue blue = context.createVariable("CGFloat", "blue");
		QuickLookValue alpha = context.createVariable("CGFloat", "alpha");

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
