package com.widerwille.quicklook.renderer;

import com.widerwille.quicklook.QuickLookValue;
import com.widerwille.quicklook.QuickLookValueRenderer;
import com.widerwille.quicklook.QuickLookEvaluationContext;

import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.Nullable;

public class QuickLookNSImageValueRenderer extends QuickLookValueRenderer
{
	private static final Key<Boolean> IS_NSIMAGE = Key.create("IS_NSIMAGE");

	private QuickLookValue data;

	public static QuickLookValueRenderer createRendererIfPossible(QuickLookValue value)
	{
		try
		{
			CidrPhysicalValue physicalValue = value.getPhysicalValue();
			EvaluationContext context = value.getContext().getUnderlyingContext();


			String type = physicalValue.getType();
			Boolean isImage = context.getCachedTypeInfo(type, IS_NSIMAGE);

			if(isImage == null)
			{
				isImage = isImageType(value);
				context.putCachedTypeInfo(type, IS_NSIMAGE, isImage);
			}

			if(isImage)
				return new QuickLookNSImageValueRenderer(value);
		}
		catch(Exception e)
		{}

		return null;
	}

	private static boolean isImageType(QuickLookValue value)
	{
		try
		{
			return value.isKindOfClass("NSImage");
		}
		catch(Exception e)
		{
			return false;
		}
	}



	protected QuickLookNSImageValueRenderer(QuickLookValue type)
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

			QuickLookValue cgImage = context.createVariable("CGImageRef", "cgImage");

			context.evaluate(cgImage.getName() + " = (CGImageRef)[(NSImage *)" + value.getPointer() + " CGImageForProposedRect:NULL context:nil hints:nil]");

			cgImage.refresh();


			QuickLookValue width = context.createVariable("CGFloat", "width");
			QuickLookValue height = context.createVariable("CGFloat", "height");

			context.evaluate(width.getName() + " = CGImageGetWidth(" + cgImage.getName() + ")");
			context.evaluate(height.getName() + " = CGImageGetHeight(" + cgImage.getName() + ")");

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

				QuickLookValue cgImage = context.createVariable("CGImageRef", "cgImage");
				QuickLookValue bitmapRef = context.createVariable("NSBitmapImageRep *", "bitmapRef");

				context.evaluate(cgImage.getName() + " = (CGImageRef)[(NSImage *)" + value.getPointer() + " CGImageForProposedRect:NULL context:nil hints:nil]");
				context.evaluate(bitmapRef.getName() + " = (NSBitmapImageRep *)[[NSBitmapImageRep alloc] initWithCGImage:" + cgImage.getName() + "]");
				context.evaluate("[(NSBitmapImageRep *)" + bitmapRef.getName() + " setSize:(CGSize)[(NSImage *)" + value.getPointer() + " size]]");

				cgImage.refresh();
				bitmapRef.refresh();

				data = context.evaluate("(NSData *)[(NSBitmapImageRep *)" + bitmapRef.getName() + " representationUsingType:4 properties:nil]");

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
