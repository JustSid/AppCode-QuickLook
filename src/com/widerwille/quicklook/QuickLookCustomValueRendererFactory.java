package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerCommandException;
import com.jetbrains.cidr.execution.debugger.backend.LLValue;
import com.jetbrains.cidr.execution.debugger.backend.LLValueData;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.jetbrains.cidr.execution.debugger.evaluation.ValueRendererFactory;
import com.jetbrains.cidr.execution.debugger.evaluation.renderers.ValueRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuickLookCustomValueRendererFactory implements ValueRendererFactory
{
	@Override
	@Nullable
	public ValueRenderer createRenderer(@NotNull FactoryContext factoryContext) throws ExecutionException, DebuggerCommandException
	{
		LLValueData lldbValueData = factoryContext.getLLValueData();
		LLValue lldbValue = factoryContext.getLLValue();
		EvaluationContext context = factoryContext.getEvaluationContext();


		if(lldbValueData.isValidPointer())
		{
			try
			{
				LLValue debugValue = lldbValue;
				LLValueData responds = context.messageSendData(lldbValue, "respondsToSelector:(SEL)NSSelectorFromString(@\"" + "debugQuickLookObject" + "\")");

				if(responds.isTrue())
					debugValue = context.messageSend(lldbValue, "debugQuickLookObject");

				LLValueData debugValueData = context.getData(debugValue);

				if(debugValueData.isValidPointer())
				{
					QuickLookEvaluationContext quicklookContext = new QuickLookEvaluationContext(context);
					QuickLookValue quickLookValue = new QuickLookValue(factoryContext.getPhysicalValue(), debugValue, debugValueData, quicklookContext);

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
}

