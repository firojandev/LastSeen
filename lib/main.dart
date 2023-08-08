import 'dart:async';
import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _time = 0;

  var platform = const MethodChannel('com.example.my_timer_app/timer_channel');
  var myEventChannel =
      EventChannel('com.example.restart_trigger/timer_event_channel');

  @override
  void initState() {
    getElapsedTime();
    _startReading();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text("You left from the app ${_time.toString()} seconds ago!!"),
          ],
        ),
      ),
    );
  }

  Future<void> getElapsedTime() async {
    try {
      Map data = await platform.invokeMethod('getElapsedTime');
      setState(() {
        _time = data["mElapsedTime"];
      });
    } on PlatformException catch (e) {
      print('Failed to invoke method: ${e.message}');
    }
  }

  _startReading() {
    StreamSubscription eventSubscription =
        myEventChannel.receiveBroadcastStream().listen((event) {
          print(event);
          if(event){
            getElapsedTime();
          }
        });
  }
}
