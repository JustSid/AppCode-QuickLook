package com.widerwille.quicklook;

import com.intellij.openapi.extensions.ExtensionPointName;
import org.jetbrains.annotations.Nullable;

public interface QuickLookValueRendererFactory
{
	ExtensionPointName<QuickLookValueRendererFactory> EP_NAME = ExtensionPointName.create("com.widerwille.quicklook.quickLookValueRendererFactory");

	@Nullable
	QuickLookValueRenderer createRenderer(QuickLookValue value, QuickLookEvaluationContext context) throws Exception;
}