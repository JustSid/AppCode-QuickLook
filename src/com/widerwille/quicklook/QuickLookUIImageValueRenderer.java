package com.widerwille.quicklook;

import org.jetbrains.annotations.Nullable;


public class QuickLookUIImageValueRenderer extends QuickLookValueRenderer
{
	private QuickLookValue data;

	QuickLookUIImageValueRenderer(QuickLookValue type)
	{
		super(type);
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
	public boolean hasImageContent()
	{
		return true;
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
