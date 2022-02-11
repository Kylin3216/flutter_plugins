// Mocks generated by Mockito 5.0.17 from annotations
// in webview_flutter_wkwebview/test/src/web_kit_webview_widget_test.dart.
// Do not manually edit this file.

import 'dart:async' as _i5;

import 'package:mockito/mockito.dart' as _i1;
import 'package:webview_flutter_platform_interface/src/types/javascript_channel.dart'
    as _i4;
import 'package:webview_flutter_platform_interface/src/types/types.dart' as _i6;
import 'package:webview_flutter_platform_interface/webview_flutter_platform_interface.dart'
    as _i3;
import 'package:webview_flutter_wkwebview/src/web_kit/web_kit.dart' as _i2;
import 'package:webview_flutter_wkwebview/src/web_kit_webview_widget.dart'
    as _i7;

// ignore_for_file: avoid_redundant_argument_values
// ignore_for_file: avoid_setters_without_getters
// ignore_for_file: comment_references
// ignore_for_file: implementation_imports
// ignore_for_file: invalid_use_of_visible_for_testing_member
// ignore_for_file: prefer_const_constructors
// ignore_for_file: unnecessary_parenthesis
// ignore_for_file: camel_case_types

class _FakeWKWebView_0 extends _i1.Fake implements _i2.WKWebView {}

/// A class which mocks [WKWebView].
///
/// See the documentation for Mockito's code generation for more information.
class MockWKWebView extends _i1.Mock implements _i2.WKWebView {
  MockWKWebView() {
    _i1.throwOnMissingStub(this);
  }
}

/// A class which mocks [WKWebViewConfiguration].
///
/// See the documentation for Mockito's code generation for more information.
class MockWKWebViewConfiguration extends _i1.Mock
    implements _i2.WKWebViewConfiguration {
  MockWKWebViewConfiguration() {
    _i1.throwOnMissingStub(this);
  }

  @override
  set allowsInlineMediaPlayback(bool? allow) =>
      super.noSuchMethod(Invocation.setter(#allowsInlineMediaPlayback, allow),
          returnValueForMissingStub: null);
  @override
  set mediaTypesRequiringUserActionForPlayback(
          Set<_i2.WKAudiovisualMediaType>? types) =>
      super.noSuchMethod(
          Invocation.setter(#mediaTypesRequiringUserActionForPlayback, types),
          returnValueForMissingStub: null);
}

/// A class which mocks [JavascriptChannelRegistry].
///
/// See the documentation for Mockito's code generation for more information.
class MockJavascriptChannelRegistry extends _i1.Mock
    implements _i3.JavascriptChannelRegistry {
  MockJavascriptChannelRegistry() {
    _i1.throwOnMissingStub(this);
  }

  @override
  Map<String, _i4.JavascriptChannel> get channels =>
      (super.noSuchMethod(Invocation.getter(#channels),
              returnValue: <String, _i4.JavascriptChannel>{})
          as Map<String, _i4.JavascriptChannel>);
  @override
  void onJavascriptChannelMessage(String? channel, String? message) =>
      super.noSuchMethod(
          Invocation.method(#onJavascriptChannelMessage, [channel, message]),
          returnValueForMissingStub: null);
  @override
  void updateJavascriptChannelsFromSet(Set<_i4.JavascriptChannel>? channels) =>
      super.noSuchMethod(
          Invocation.method(#updateJavascriptChannelsFromSet, [channels]),
          returnValueForMissingStub: null);
}

/// A class which mocks [WebViewPlatformCallbacksHandler].
///
/// See the documentation for Mockito's code generation for more information.
class MockWebViewPlatformCallbacksHandler extends _i1.Mock
    implements _i3.WebViewPlatformCallbacksHandler {
  MockWebViewPlatformCallbacksHandler() {
    _i1.throwOnMissingStub(this);
  }

  @override
  _i5.FutureOr<bool> onNavigationRequest({String? url, bool? isForMainFrame}) =>
      (super.noSuchMethod(
          Invocation.method(#onNavigationRequest, [],
              {#url: url, #isForMainFrame: isForMainFrame}),
          returnValue: Future<bool>.value(false)) as _i5.FutureOr<bool>);
  @override
  void onPageStarted(String? url) =>
      super.noSuchMethod(Invocation.method(#onPageStarted, [url]),
          returnValueForMissingStub: null);
  @override
  void onPageFinished(String? url) =>
      super.noSuchMethod(Invocation.method(#onPageFinished, [url]),
          returnValueForMissingStub: null);
  @override
  void onProgress(int? progress) =>
      super.noSuchMethod(Invocation.method(#onProgress, [progress]),
          returnValueForMissingStub: null);
  @override
  void onWebResourceError(_i6.WebResourceError? error) =>
      super.noSuchMethod(Invocation.method(#onWebResourceError, [error]),
          returnValueForMissingStub: null);
}

/// A class which mocks [WebViewProxy].
///
/// See the documentation for Mockito's code generation for more information.
class MockWebViewProxy extends _i1.Mock implements _i7.WebViewProxy {
  MockWebViewProxy() {
    _i1.throwOnMissingStub(this);
  }

  @override
  _i2.WKWebView createWebView(_i2.WKWebViewConfiguration? configuration) =>
      (super.noSuchMethod(Invocation.method(#createWebView, [configuration]),
          returnValue: _FakeWKWebView_0()) as _i2.WKWebView);
}
