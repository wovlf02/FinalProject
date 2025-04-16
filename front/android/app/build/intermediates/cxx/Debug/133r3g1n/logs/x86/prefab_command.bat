@echo off
"C:\\Program Files\\Java\\jdk-17\\bin\\java" ^
  --class-path ^
  "C:\\Users\\user\\.gradle\\caches\\modules-2\\files-2.1\\com.google.prefab\\cli\\2.1.0\\aa32fec809c44fa531f01dcfb739b5b3304d3050\\cli-2.1.0-all.jar" ^
  com.google.prefab.cli.AppKt ^
  --build-system ^
  cmake ^
  --platform ^
  android ^
  --abi ^
  x86 ^
  --os-version ^
  24 ^
  --stl ^
  c++_shared ^
  --ndk-version ^
  26 ^
  --output ^
  "C:\\Users\\user\\AppData\\Local\\Temp\\agp-prefab-staging17189371925508407314\\staged-cli-output" ^
  "C:\\Users\\user\\.gradle\\caches\\8.12.1\\transforms\\31e7d0551132f51f9ca82a81dc561321\\transformed\\react-android-0.76.6-debug\\prefab" ^
  "C:\\FinalProject\\front\\android\\app\\build\\intermediates\\cxx\\refs\\react-native-reanimated\\542923c4" ^
  "C:\\Users\\user\\.gradle\\caches\\8.12.1\\transforms\\5dc14df0f7610ebd19cc671373d38b04\\transformed\\hermes-android-0.76.6-debug\\prefab" ^
  "C:\\Users\\user\\.gradle\\caches\\8.12.1\\transforms\\ac57595d62612a02ce0c1b7f5921ff2a\\transformed\\fbjni-0.6.0\\prefab"
