package com.widerwille.quicklook;

import com.widerwille.quicklook.renderer.*;
import org.jetbrains.annotations.Nullable;

public class QuickLookDefaultValueRendererFactory implements QuickLookValueRendererFactory
{
	public QuickLookDefaultValueRendererFactory()
	{}

	private QuickLookValueRenderer createDefaultPlatformRenderer(QuickLookValue value, QuickLookEvaluationContext context) {
		QuickLookValueRenderer renderer;

		renderer = QuickLookUIColorValueRenderer.createRendererIfPossible(value);
		return renderer;
	}

	@Override
	@Nullable
	public QuickLookValueRenderer createRenderer(QuickLookValue value, QuickLookEvaluationContext context) {
		QuickLookValueRenderer renderer;

		switch(context.getPlatform())
		{
			case iPhone:
			{
				renderer = QuickLookUIImageValueRenderer.createRendererIfPossible(value);
				if(renderer != null)
					return renderer;

				renderer = QuickLookUIViewValueRenderer.createRendererIfPossible(value);
				if(renderer != null)
					return renderer;

				renderer = QuickLookUIBezierPathValueRenderer.createRendererIfPossible(value);
				if(renderer != null)
					return renderer;

				renderer = QuickLookCALayerValueRenderer.createRendererIfPossible(value);
				if(renderer != null)
					return renderer;

				return createDefaultPlatformRenderer(value, context);
			}

			case Mac:
			{
				renderer = QuickLookNSImageValueRenderer.createRendererIfPossible(value);
				if(renderer != null)
					return renderer;

				renderer = QuickLookNSBitmapImageRepValueRenderer.createRendererIfPossible(value);
				if(renderer != null)
					return renderer;

				renderer = QuickLookNSViewValueRenderer.createRendererIfPossible(value);
				if(renderer != null)
					return renderer;

				return createDefaultPlatformRenderer(value, context);
			}

			case Unknown:
				break;
		}

		return null;
	}
}
