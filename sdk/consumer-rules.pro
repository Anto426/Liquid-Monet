# Preserve metadata used by Kotlin and Compose without preventing R8 from shrinking
# unused LiquidUI components in the consuming application.
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,AnnotationDefault,Signature,InnerClasses,EnclosingMethod
