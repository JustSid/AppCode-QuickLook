package com.widerwille.quicklook.renderer;

import com.widerwille.quicklook.QuickLookValue;
import com.widerwille.quicklook.QuickLookValueRenderer;

import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.widerwille.quicklook.helper.QuickLookHelper;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;

public class QuickLookNSURLValueRenderer extends QuickLookValueRenderer
{
	private static final Key<Boolean> IS_URL = Key.create("IS_URL");

	public static QuickLookValueRenderer createRendererIfPossible(QuickLookValue value)
	{
		try
		{
			CidrPhysicalValue physicalValue = value.getPhysicalValue();
			EvaluationContext context = value.getContext().getUnderlyingContext();


			String type = physicalValue.getType();
			Boolean isURL = context.getCachedTypeInfo(type, IS_URL);

			if(isURL == null)
			{
				isURL = isURLType(value);
				context.putCachedTypeInfo(type, IS_URL, isURL);
			}

			if(isURL)
				return new QuickLookNSURLValueRenderer(value);
		}
		catch(Exception e)
		{}

		return null;
	}

	private static boolean isURLType(QuickLookValue value)
	{
		try
		{
			return value.isKindOfClass("NSURL");
		}
		catch(Exception e)
		{
			return false;
		}
	}



	private QuickLookNSURLValueRenderer(QuickLookValue value)
	{
		super(value);
		setEvaluator(new URLEvaluator());
	}

	@Override
	@Nullable
	public String getDisplayValue()
	{
		try
		{
			QuickLookValue value = getQuickLookValue();
			String absoluteURL = value.sendMessage("absoluteString", "NSString *").sendMessage("UTF8String").getValue().getReadableValue();

			return absoluteURL.replace("\"", "");
		}
		catch(Exception e)
		{
			return null;
		}
	}


	public class URLEvaluator implements Evaluator<URL>
	{
		@Override
		public URL evaluate()
		{
			try
			{
				QuickLookValue value = getQuickLookValue();
				String absoluteURL = value.sendMessage("absoluteString", "NSString *").sendMessage("UTF8String").getValue().getReadableValue();

				return new URL(absoluteURL.replace("\"", ""));
			}
			catch(Exception e)
			{
				return null;
			}
		}

		@Override
		public JComponent createComponent(URL data)
		{
			return QuickLookHelper.createWebView(data);
		}
	}
}
