package com.widerwille.quicklook.renderer;

import com.widerwille.quicklook.QuickLookValue;
import com.widerwille.quicklook.QuickLookValueRenderer;
import com.widerwille.quicklook.QuickLookEvaluationContext;

import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.Nullable;

public class QuickLookNSViewValueRenderer extends QuickLookValueRenderer
{
	private static final Key<Boolean> IS_NSVIEW = Key.create("IS_NSVIEW");

	private QuickLookValue data;

	public static QuickLookValueRenderer createRendererIfPossible(QuickLookValue value)
	{
		try
		{
			CidrPhysicalValue physicalValue = value.getPhysicalValue();
			EvaluationContext context = value.getContext().getUnderlyingContext();


			String type = physicalValue.getType();
			Boolean isView = context.getCachedTypeInfo(type, IS_NSVIEW);

			if(isView == null)
			{
				isView = isViewType(value);
				context.putCachedTypeInfo(type, IS_NSVIEW, isView);
			}

			if(isView)
				return new QuickLookNSViewValueRenderer(value);
		}
		catch(Exception e)
		{}

		return null;
	}

	private static boolean isViewType(QuickLookValue value)
	{
		try
		{
			return value.isKindOfClass("NSView");
		}
		catch(Exception e)
		{
			return false;
		}
	}

	protected QuickLookNSViewValueRenderer(QuickLookValue type)
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

				QuickLookValue bitmapRef = context.createVariable("NSBitmapImageRep *", "bitmapRef");

				context.evaluate(bitmapRef.getName() + " = (NSBitmapImageRep *)[(NSView *)" + value.getPointer() + " bitmapImageRepForCachingDisplayInRect:(NSRect)[(NSView *)" + value.getPointer() + " bounds]]");

				bitmapRef.refresh();
				bitmapRef.sendMessage("setSize:(NSSize)((NSRect)[(NSView *)" + value.getPointer() + " bounds]).size");

				value.sendMessage("cacheDisplayInRect:((NSRect)[(NSView *)" + value.getPointer() + " bounds]) toBitmapImageRep:(NSBitmapImageRep *)" + bitmapRef.getPointer(), "void");

				data = context.evaluate("(NSData *)[(NSBitmapImageRep *)" + bitmapRef.getPointer() + " representationUsingType:NSBitmapImageFileTypePNG properties:nil]");

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
