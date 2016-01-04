package com.widerwille.quicklook;

import com.jetbrains.cidr.execution.debugger.backend.LLValue;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.CustomValueRendererFactory;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.jetbrains.cidr.execution.debugger.evaluation.renderers.ValueRenderer;

public class QuickLookCustomValueRendererFactory implements CustomValueRendererFactory
{
	public ValueRenderer createRenderer(CidrPhysicalValue value, LLValue lldbValue, EvaluationContext context)
	{
		if(lldbValue.isValidPointer() && lldbValue.isNSObject())
		{
			try
			{
				LLValue debugValue = lldbValue;
				LLValue responds = context.messageSend(lldbValue, "respondsToSelector:(SEL)NSSelectorFromString(@\"" + "debugQuickLookObject" + "\")");

				if(responds.isTrue())
					debugValue = context.messageSend(lldbValue, "debugQuickLookObject");

				if(debugValue.isValidPointer())
				{
					context.checkExpiration();

					QuickLookValue quickLookValue = new QuickLookValue(value, debugValue, context);

					for(QuickLookValueRendererFactory factory : QuickLookValueRendererFactory.EP_NAME.getExtensions())
					{
						try
						{
							QuickLookValueRenderer result = factory.createRenderer(quickLookValue);
							if(result != null)
								return result;
						}
						catch(Exception e)
						{}
					}

				}
			}
			catch(Exception e)
			{}
		}

		return null;
	}

}
