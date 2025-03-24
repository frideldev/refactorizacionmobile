class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  var l = [];
  var t = 0.0;
  
  void a(String n, double p, int q) {
    setState(() {
      l.add({"n": n, "p": p, "q": q});
      c();
    });
  }
  
  void c() {
    t = 0;
    for (var i = 0; i < l.length; i++) {
      t += l[i]["p"] * l[i]["q"];
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text("Mi App")),
        body: Column(
          children: [
            // Interfaz para agregar productos...
            Text("Total: \$${t.toStringAsFixed(2)}"),
            Expanded(
              child: ListView.builder(
                itemCount: l.length,
                itemBuilder: (context, index) {
                  return ListTile(
                    title: Text(l[index]["n"]),
                    subtitle: Text("Precio: \$${l[index]["p"]} x ${l[index]["q"]}"),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
