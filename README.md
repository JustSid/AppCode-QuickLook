# Overview

Simple plugin, and actually more proof of concept than anything, that brings support for quick look value renderers known from Xcode
to AppCode. It currently only supports `UIColor`, `UIImage` and `UIImageView`, as well as methods implementing the
`debugQuickLookObject` method (and when it returns one of the aforementioned types). There are plans for further development though,
a complete list of supported types in Xcode for reference can be found [here](https://developer.apple.com/library/ios/documentation/IDEs/Conceptual/CustomClassDisplay_in_QuickLook/CH02-std_objects_support/CH02-std_objects_support.html).

# Current limitations and future plans

Right now it only works with Objective-C, there is no Swift support whatsoever.

Also, of course, support for all the Xcode supported data types is planned

I also want to make a sane plugin API to support arbitrary type renderings and easy access to getting data out of the app, technically this
would be AppCodes job, but why re-invent the wheel and rewrite the methods I've been writing (as long as official support is missing that is, of course)

# Screenshot

Just to give you an idea of what's there already:

![Screenshot](./Screenshot0.png)
![Screenshot](./Screenshot1.png)

# License

The code is available under the MIT license