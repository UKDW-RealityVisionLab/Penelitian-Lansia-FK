@echo off
"C:\\Users\\HP\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HD:\\Kaleb\\Kuliah\\.KP\\github\\KPTraining\\SinauOpenCVKotlin\\opencv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\HP\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\HP\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\HP\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\HP\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=D:\\Kaleb\\Kuliah\\.KP\\github\\KPTraining\\SinauOpenCVKotlin\\opencv\\build\\intermediates\\cxx\\Debug\\3x1w4e5v\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=D:\\Kaleb\\Kuliah\\.KP\\github\\KPTraining\\SinauOpenCVKotlin\\opencv\\build\\intermediates\\cxx\\Debug\\3x1w4e5v\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BD:\\Kaleb\\Kuliah\\.KP\\github\\KPTraining\\SinauOpenCVKotlin\\opencv\\.cxx\\Debug\\3x1w4e5v\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
