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
		QuickLookManager manager = context.getFrame().getProcess().getProject().getComponent(QuickLookManager.class);
		QuickLookContext quickLookContext = manager.contextForEvaluationContext(context);

		if(quickLookContext == null)
			return null;

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
					QuickLookValue quickLookValue = new QuickLookValue(value, debugValue, context);

					for(QuickLookValueRendererFactory factory : QuickLookValueRendererFactory.EP_NAME.getExtensions())
					{
						try
						{
							QuickLookValueRenderer result = factory.createRenderer(quickLookValue);
							if(result != null)
							{
								quickLookContext.addValueRenderer(result);
								return result;
							}
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
