package com.widerwille.quicklook;

import com.intellij.openapi.project.Project;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;

import java.util.ArrayList;

public class QuickLookContext
{
	private EvaluationContext context;
	private ArrayList<QuickLookValueRenderer> renderers = new ArrayList<>();
	private QuickLookManager manager;

	public QuickLookContext(EvaluationContext context, QuickLookManager manager)
	{
		this.context = context;
		this.manager = manager;
	}

	public void prune()
	{
		for(QuickLookValueRenderer renderer : renderers)
			renderer.close();
	}

	public ArrayList<QuickLookValueRenderer> getRenderers()
	{
		return renderers;
	}

	public void addValueRenderer(QuickLookValueRenderer renderer)
	{
		renderers.add(renderer);

		if(renderer.hasImageContent())
			manager.addContentRenderer(renderer);
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
