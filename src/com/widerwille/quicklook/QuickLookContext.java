package com.widerwille.quicklook;

import com.intellij.openapi.project.Project;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;

import java.util.ArrayList;

public class QuickLookContext
{
	private EvaluationContext context;
	private ArrayList<QuickLookValueRenderer> renderers = new ArrayList<>();

	public QuickLookContext(EvaluationContext context)
	{
		this.context = context;
	}

	public void prune()
	{
		for(QuickLookValueRenderer renderer : renderers)
			renderer.close();
	}

	public void addValueRenderer(QuickLookValueRenderer renderer)
	{
		renderers.add(renderer);
	}

	public EvaluationContext getContext()
	{
		return context;
	}
	public Project getProject()
	{
		return context.getFrame().getProcess().getProject();
	}
}
