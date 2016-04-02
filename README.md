# Overview

Plugin that aims to provide similar functionality to Xcode's QuickLook debug renderer, which allows Xcode to render a plethora of values inline while debugging. A full reference of all the supported types supported by Xcode can be found [here](https://developer.apple.com/library/ios/documentation/IDEs/Conceptual/CustomClassDisplay_in_QuickLook/CH02-std_objects_support/CH02-std_objects_support.html).

The plugin displays a `QuickLook` link in the debugger, clicking it will bring up the QuickLook content viewer.

# Supported types

Right now this plugin only supports Objective-C. Furthermore, not all types supported by Xcode are supported in this plugin, however, these types are supported:

* UIImage
* NSImage
* NSBitmapImageRep
* UIImageView
* NSImageView
* UIColor
* NSColor
* UIBezierPath
* UIView classes
* NSView classes
* NSURL
* Custom classes that implement `debugQuickLookObject` and which return one of the above objects

# License

The code is available under the MIT license