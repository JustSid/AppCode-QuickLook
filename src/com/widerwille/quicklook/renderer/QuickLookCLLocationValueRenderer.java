package com.widerwille.quicklook.renderer;

import com.widerwille.quicklook.QuickLookValue;
import com.widerwille.quicklook.QuickLookValueRenderer;
import com.widerwille.quicklook.QuickLookEvaluationContext;

import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.widerwille.quicklook.helper.QuickLookHelper;
import javax.swing.*;
import java.net.URL;

public class QuickLookCLLocationValueRenderer extends QuickLookValueRenderer
{
	private static final Key<Boolean> IS_LOCATION = Key.create("IS_LOCATION");

	public static QuickLookValueRenderer createRendererIfPossible(QuickLookValue value)
	{
		try
		{
			CidrPhysicalValue physicalValue = value.getPhysicalValue();
			EvaluationContext context = value.getContext().getUnderlyingContext();


			String type = physicalValue.getType();
			Boolean isLocation = context.getCachedTypeInfo(type, IS_LOCATION);

			if(isLocation == null)
			{
				isLocation = isLocationType(value);
				context.putCachedTypeInfo(type, IS_LOCATION, isLocation);
			}

			if(isLocation)
				return new QuickLookCLLocationValueRenderer(value);
		}
		catch(Exception e)
		{}

		return null;
	}

	private static boolean isLocationType(QuickLookValue value)
	{
		try
		{
			return value.isKindOfClass("CLLocation");
		}
		catch(Exception e)
		{
			return false;
		}
	}


	private QuickLookCLLocationValueRenderer(QuickLookValue value)
	{
		super(value);
		setEvaluator(new LocationEvaluator());
	}


	public class LocationEvaluator implements Evaluator<URL>
	{
		@Override
		public URL evaluate()
		{
			try
			{
				QuickLookValue value = getQuickLookValue();
				QuickLookEvaluationContext context = value.getContext();

				QuickLookValue latitude = context.createVariable("CLLocationDegrees", "latitude");
				QuickLookValue longitude = context.createVariable("CLLocationDegrees", "longitude");

				context.evaluate(latitude.getName() + " = (CLLocationDegrees)([((CLLocation *)" + value.getPointer() + ") coordinate].latitude)");
				context.evaluate(longitude.getName() + " = (CLLocationDegrees)([((CLLocation *)" + value.getPointer() + ") coordinate].longitude)");

				return new URL("https://www.google.com/maps/preview/@" + latitude.getFloatValue() + "," + longitude.getFloatValue() + ",18z");
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
