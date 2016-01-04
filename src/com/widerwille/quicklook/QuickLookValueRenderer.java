package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.jetbrains.cidr.execution.debugger.evaluation.renderers.ValueRenderer;
import org.antlr.v4.runtime.misc.Nullable;
import org.jetbrains.annotations.NotNull;

public class QuickLookValueRenderer extends ValueRenderer
{
	private QuickLookValue value;

	public QuickLookValueRenderer(QuickLookValue value)
	{
		super(value.getOriginalValue());
		this.value = value;
	}

	@Nullable
	public String getDisplayValue()
	{
		return null;
	}

	@NotNull
	public String computeValue(@NotNull EvaluationContext context) throws ExecutionException
	{
		String value = getDisplayValue();
		if(value == null)
			return super.computeValue(context);

		return value;
	}


	public QuickLookValue getQuickLookValue()
	{
		return value;
	}
	public QuickLookValue getDataValue()
	{
		return null;
	}
}
