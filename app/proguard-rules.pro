# Application R8 Configuration for LiquidMonet App (Aggressive Optimization Mode)

# Aggressive Optimization & Inlining flags
-allowaccessmodification
-repackageclasses ''
-optimizationpasses 5
-overloadaggressively
-mergeinterfacesaggressively

# Dead code elimination and member stripping
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature
-renamesourcefileattribute 'SourceFile'

