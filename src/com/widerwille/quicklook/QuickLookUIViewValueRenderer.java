package com.widerwille.quicklook;

import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.Nullable;


public class QuickLookUIViewValueRenderer extends QuickLookValueRenderer
{
	private static final Key<Boolean> IS_UIVIEW = Key.create("IS_UIVIEW");

	private QuickLookValue data;

	public static QuickLookValueRenderer createRendererIfPossible(QuickLookValue value)
	{
		try
		{
			CidrPhysicalValue physicalValue = value.getPhysicalValue();
			EvaluationContext context = value.getContext().getUnderlyingContext();


			String type = physicalValue.getType();
			Boolean isView = context.getCachedTypeInfo(type, IS_UIVIEW);

			if(isView == null)
			{
				isView = isViewType(value);
				context.putCachedTypeInfo(type, IS_UIVIEW, isView);
			}

			if(isView)
				return new QuickLookUIViewValueRenderer(value);
		}
		catch(Exception e)
		{}

		return null;
	}

	private static boolean isViewType(QuickLookValue value)
	{
		try
		{
			return value.isKindOfClass("UIView");
		}
		catch(Exception e)
		{
			return false;
		}
	}



	protected QuickLookUIViewValueRenderer(QuickLookValue type)
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
				QuickLookValue layer = context.createVariable("CALayer *", "layer");
				QuickLookValue cgContext = context.createVariable("CGContextRef", "context");

				context.evaluate(layer.getName() + " = (CALayer *)[(UIView *)" + value.getPointer() + " layer]");

				context.evaluate("(void)UIGraphicsBeginImageContextWithOptions(((CGRect)[((UIView *)" + value.getPointer() + ") bounds]).size, (BOOL)[((UIView *)" + value.getPointer() + ") isOpaque], 0.0)");
				context.evaluate(cgContext.getName() + " = (CGContextRef)UIGraphicsGetCurrentContext()");

				cgContext.refresh();

				context.evaluate("((void(*)(id, SEL, CGContextRef))objc_msgSend)((id)" + layer.getName() + ", (SEL)NSSelectorFromString(@\"" + "renderInContext:" + "\"), " + cgContext.getName() + ")");
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
