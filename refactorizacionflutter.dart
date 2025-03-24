class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  var productos = [];
  var total = 0.0;
  

  //Función que recopila la id, precio y cantidad para agregar un nuevo producto. 
  void agregarProducto(String id, double precio, int cantidad) {

    setState(() {
      productos.add({"id": id, "precio": precio, "cantidad": cantidad});
      calcularTotalProducto();
    });

  }
  
  //Obtiene el precio y cantidad del producto en posición "i", para luego obtener el total.
  void calcularTotalProducto() {
    for (var i = 0; i < productos.length; i++) {
      total += productos[i]["precio"] * productos[i]["cantidad"];
    }
  }
  
  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text("Mi App")),
        body: Column(
          children: [
            //INTERFAZ PARA MOSTRAR LOS PRODUCTOS
            Text("Total: \$${total.toStringAsFixed(2)}"),
            Expanded(

              child: ListView.builder(

                itemCount: productos.length,
                itemBuilder: (context, index) {

                  return ListTile(

                    title: Text(productos[index]["id"]),
                    subtitle: Text("Precio: \$${productos[index]["precio"]} x ${productos[index]["cantidad"]}"),

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
