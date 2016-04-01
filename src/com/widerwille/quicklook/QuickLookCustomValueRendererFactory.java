package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.execution.debugger.CidrDebuggerSettings;
import com.jetbrains.cidr.execution.debugger.backend.LLValue;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.CustomValueRendererFactory;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.jetbrains.cidr.execution.debugger.evaluation.renderers.ValueRenderer;
import org.jetbrains.annotations.Nullable;

public class QuickLookCustomValueRendererFactory implements CustomValueRendererFactory
{
	@Override
	@Nullable
	public ValueRenderer createRendererLeading(CidrDebuggerSettings settings, CidrPhysicalValue value, LLValue lldbValue, EvaluationContext context) throws ExecutionException
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
					QuickLookEvaluationContext quicklookContext = new QuickLookEvaluationContext(context);
					QuickLookValue quickLookValue = new QuickLookValue(value, debugValue, quicklookContext);

					for(QuickLookValueRendererFactory factory : QuickLookValueRendererFactory.EP_NAME.getExtensions())
					{
						try
						{
							return factory.createRenderer(quickLookValue, quicklookContext);
						}
						catch(Exception e)
						{
						}
					}

				}
			}
			catch(Exception e)
			{}
		}

		return null;
	}

	@Override
	@Nullable
	public ValueRenderer createRendererTrailing(CidrDebuggerSettings settings, CidrPhysicalValue value, LLValue lldbValue, EvaluationContext context) throws ExecutionException
	{
		return null;
	}
}

