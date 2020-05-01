package com.widerwille.quicklook.renderer;

import com.widerwille.quicklook.QuickLookValue;
import com.widerwille.quicklook.QuickLookValueRenderer;
import com.widerwille.quicklook.QuickLookEvaluationContext;

import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.Nullable;


public class QuickLookCALayerValueRenderer extends QuickLookValueRenderer
{
	private static final Key<Boolean> IS_CALAYER = Key.create("IS_CALAYER");

	private QuickLookValue data;

	public static QuickLookValueRenderer createRendererIfPossible(QuickLookValue value)
	{
		try
		{
			CidrPhysicalValue physicalValue = value.getPhysicalValue();
			EvaluationContext context = value.getContext().getUnderlyingContext();


			String type = physicalValue.getType();
			Boolean isLayer = context.getCachedTypeInfo(type, IS_CALAYER);

			if(isLayer == null)
			{
				isLayer = isLayerType(value);
				context.putCachedTypeInfo(type, IS_CALAYER, isLayer);
			}

			if(isLayer)
				return new QuickLookCALayerValueRenderer(value);
		}
		catch(Exception e)
		{}

		return null;
	}

	private static boolean isLayerType(QuickLookValue value)
	{
		try
		{
			return value.isKindOfClass("CALayer");
		}
		catch(Exception e)
		{
			return false;
		}
	}

	protected QuickLookCALayerValueRenderer(QuickLookValue type)
	{
		super(type);
		setEvaluator(new BufferedImageEvaluator());
	}

	@Override
	@Nullable
	protected QuickLookValue getDataValue()
	{
		try
		{
			if(data == null)
			{
				QuickLookValue value = getQuickLookValue();
				QuickLookEvaluationContext context = value.getContext();

				QuickLookValue image = context.createVariable("UIImage *", "image");
				QuickLookValue cgContext = context.createVariable("CGContextRef", "context");

				context.evaluate("(void)UIGraphicsBeginImageContextWithOptions(((CGRect)[((CALayer *)" + value.getPointer() + ") bounds]).size, (BOOL)[((CALayer *)" + value.getPointer() + ") isOpaque], 0.0)");
				context.evaluate(cgContext.getName() + " = (CGContextRef)UIGraphicsGetCurrentContext()");

				cgContext.refresh();

				context.evaluate("((void(*)(id, SEL, CGContextRef))objc_msgSend)((id)" + value.getPointer() + ", (SEL)NSSelectorFromString(@\"" + "renderInContext:" + "\"), " + cgContext.getName() + ")");
				context.evaluate(image.getName() + " = (UIImage *)UIGraphicsGetImageFromCurrentImageContext()");
				context.evaluate("(void)UIGraphicsEndImageContext()");

				image.refresh();
				data = context.evaluate("(NSData *)UIImagePNGRepresentation((UIImage *)" + image.getPointer() + ")");

				if(!data.isValid() || !data.isPointer())
					data = null;
			}
		}
		catch(Exception e)
		{
			data = null;
		}

		return data;
	}
}
