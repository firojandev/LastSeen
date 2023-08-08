#include "flutter_window.h"

#include <optional>

#include "flutter/generated_plugin_registrant.h"

FlutterWindow::FlutterWindow(const flutter::DartProject& project)
    : project_(project) {}

FlutterWindow::~FlutterWindow() {}

bool FlutterWindow::OnCreate() {

  // Create a platform channel.
    flutter::MethodChannel<> channel(
        flutter_controller_->engine()->messenger(), "com.example.my_timer_app/timer_channel");

    // Set a handler for the `getPlatformName` method.
    channel.SetMethodCallHandler(
        [this](const flutter::MethodCall<>& call, std::unique_ptr<flutter::MethodResult<>> result) {
          // Check if the method name is `getPlatformName`.
          if (call.method_name().compare("getElapsedTime") == 0) {
            // Get the platform name from the native code.
            long time_val = 11;

             // Create a map of the results.
             std::map<std::string, int> results;
             results["mElapsedTime"] = time_val;
            // Return the platform name to the Flutter app.
            result->Success(json::encode(results));
          } else {
            // The method name is not supported.
            result->NotImplemented();
          }
        });

  return true;
}

void FlutterWindow::OnDestroy() {
  if (flutter_controller_) {
    flutter_controller_ = nullptr;
  }

  Win32Window::OnDestroy();
}

LRESULT
FlutterWindow::MessageHandler(HWND hwnd, UINT const message,
                              WPARAM const wparam,
                              LPARAM const lparam) noexcept {
  // Give Flutter, including plugins, an opportunity to handle window messages.
  if (flutter_controller_) {
    std::optional<LRESULT> result =
        flutter_controller_->HandleTopLevelWindowProc(hwnd, message, wparam,
                                                      lparam);
    if (result) {
      return *result;
    }
  }

  switch (message) {
    case WM_FONTCHANGE:
      flutter_controller_->engine()->ReloadSystemFonts();
      break;
  }

  return Win32Window::MessageHandler(hwnd, message, wparam, lparam);
}
