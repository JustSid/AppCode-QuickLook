package com.widerwille.quicklook;

import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import org.antlr.v4.runtime.misc.Nullable;

public class QuickLookDefaultValueRendererFactory implements QuickLookValueRendererFactory
{
	public QuickLookDefaultValueRendererFactory()
	{}

	@Override
	@Nullable
	public QuickLookValueRenderer createRenderer(QuickLookValue value, QuickLookEvaluationContext context) throws Exception
	{
		/*CidrPhysicalValue physicalValue = value.getOriginalValue();
		String type = physicalValue.getType();*/


		/*
		       try {
            String var3 = var0.getType();
            Boolean var4 = (Boolean)var2.getCachedTypeInfo(var3, IS_STRUCT);
            if(var4 == null) {
                var4 = Boolean.valueOf(var0.getTypesHelper().isStructType(var0, var2));
                var2.putCachedTypeInfo(var3, IS_STRUCT, var4);
            }

            StructValueRenderer var10000;
            try {
                if(var4.booleanValue()) {
                    var10000 = new StructValueRenderer(var0, var1);
                    return var10000;
                }
            } catch (DBUserException var5) {
                throw var5;
            }

            var10000 = null;
            return var10000;
        } catch (DBUserException var6) {
            return null;
        }
		 */


		// iOS / UIKit
		if(value.isKindOfClass("UIColor"))
			return new QuickLookUIColorValueRenderer(value);

		if(value.isKindOfClass("UIImageView"))
		{
			value = value.sendMessage("image");
			if(value.isNilPointer())
				return null;
		}

		if(value.isKindOfClass("UIImage"))
			return new QuickLookUIImageValueRenderer(value);

		if(value.isKindOfClass("UIView"))
			return new QuickLookUIViewValueRenderer(value);

		if(value.isKindOfClass("UIBezierPath"))
			return new QuickLookUIBezierPathValueRenderer(value);

		// OS X / AppKit
		if(value.isKindOfClass("NSColor"))
			return new QuickLookUIColorValueRenderer(value);

		if(value.isKindOfClass("NSImageView"))
		{
			value = value.sendMessage("image");
			if(value.isNilPointer())
				return null;
		}

		if(value.isKindOfClass("NSImage"))
			return new QuickLookNSImageValueRenderer(value);
		if(value.isKindOfClass("NSBitmapImageRep"))
			return new QuickLookNSBitmapImageRepValueRenderer(value);

		if(value.isKindOfClass("NSView"))
			return new QuickLookNSViewValueRenderer(value);


		return null;
	}
}
