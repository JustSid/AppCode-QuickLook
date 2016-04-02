package com.widerwille.quicklook.renderer;

import com.widerwille.quicklook.QuickLookValue;
import com.widerwille.quicklook.QuickLookValueRenderer;
import com.widerwille.quicklook.QuickLookEvaluationContext;

import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.Nullable;


public class QuickLookUIImageValueRenderer extends QuickLookValueRenderer
{
	private static final Key<Boolean> IS_UIIMAGE = Key.create("IS_UIIMAGE");

	private QuickLookValue data;

	public static QuickLookValueRenderer createRendererIfPossible(QuickLookValue value)
	{
		try
		{
			CidrPhysicalValue physicalValue = value.getPhysicalValue();
			EvaluationContext context = value.getContext().getUnderlyingContext();


			String type = physicalValue.getType();
			Boolean isImage = context.getCachedTypeInfo(type, IS_UIIMAGE);

			if(isImage == null)
			{
				isImage = isImageType(value);
				context.putCachedTypeInfo(type, IS_UIIMAGE, isImage);
			}

			if(isImage)
				return new QuickLookUIImageValueRenderer(value);
		}
		catch(Exception e)
		{}

		return null;
	}

	private static boolean isImageType(QuickLookValue value)
	{
		try
		{
			return value.isKindOfClass("UIImage");
		}
		catch(Exception e)
		{
			return false;
		}
	}


	protected QuickLookUIImageValueRenderer(QuickLookValue type)
	{
		super(type);
		setEvaluator(new BufferedImageEvaluator());
	}

	@Override
	@Nullable
	public String getDisplayValue()
	{
		try
		{
			QuickLookValue value = getQuickLookValue();
			QuickLookEvaluationContext context = value.getContext();

			QuickLookValue width = context.createVariable("CGFloat", "width");
			QuickLookValue height = context.createVariable("CGFloat", "height");

			context.evaluate(width.getName() + " = [((UIImage *)" + value.getPointer() + ") size].width");
			context.evaluate(height.getName() + " = [((UIImage *)" + value.getPointer() + ") size].height");
			
			return "{" + width.getFloatValue() + ", " + height.getFloatValue() + "}";
		}
		catch(Exception e)
		{
			return "<unknown image>";
		}
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

				data = context.evaluate("(NSData *)UIImagePNGRepresentation((UIImage *)" + value.getPointer() + ")");

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
