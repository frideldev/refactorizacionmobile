class ActividadPrincipal : AppCompatActivity() {
    var listaProductos = ArrayList<HashMap<String, Any>>()
    var montoTotal = 0.0
    lateinit var recyclerProductos: RecyclerView
    lateinit var adaptadorProductos: AdaptadorProductos
    lateinit var barraProgreso: ProgressBar
    lateinit var textoMontoTotal: TextView

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.a_m) // Asegúrate de que el layout a_m.xml tenga los nuevos IDs

        // Actualiza los IDs a nombres de alto nivel en el XML
        recyclerProductos = findViewById(R.id.recyclerProductos)
        barraProgreso = findViewById(R.id.barraProgreso)
        textoMontoTotal = findViewById(R.id.textoMontoTotal)

        recyclerProductos.layoutManager = LinearLayoutManager(this)
        adaptadorProductos = AdaptadorProductos(listaProductos) { nombreProducto, precioProducto ->
            // Agregar un nuevo producto
            val producto = HashMap<String, Any>()
            producto["nombre"] = nombreProducto
            producto["precio"] = precioProducto
            producto["cantidad"] = 1
            agregarProducto(producto)
        }
        recyclerProductos.adapter = adaptadorProductos

        findViewById<Button>(R.id.btnAgregarProducto).setOnClickListener {
            cargarProductos()
        }
        cargarProductos()
    }

    fun cargarProductos() {
        barraProgreso.visibility = View.VISIBLE
        val baseDeDatos = FirebaseFirestore.getInstance()
        baseDeDatos.collection("productos").get()
            .addOnSuccessListener { documentos ->
                listaProductos.clear()
                for (documento in documentos) {
                    val producto = HashMap<String, Any>()
                    producto["id"] = documento.id
                    producto["nombre"] = documento.getString("nombre") ?: ""
                    producto["precio"] = documento.getDouble("precio") ?: 0.0
                    producto["cantidad"] = documento.getLong("cantidad")?.toInt() ?: 1
                    listaProductos.add(producto)
                }
                adaptadorProductos.notifyDataSetChanged()
                calcularMontoTotal()
                barraProgreso.visibility = View.GONE
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                barraProgreso.visibility = View.GONE
            }
    }

    fun agregarProducto(producto: HashMap<String, Any>) {
        listaProductos.add(producto)
        adaptadorProductos.notifyDataSetChanged()
        calcularMontoTotal()
    }

    fun calcularMontoTotal() {
        montoTotal = 0.0
        for (producto in listaProductos) {
            montoTotal += (producto["precio"] as Double) * (producto["cantidad"] as Int)
        }
        textoMontoTotal.text = "Monto Total: $${montoTotal}"
    }

    inner class AdaptadorProductos(
        private val datosProductos: ArrayList<HashMap<String, Any>>,
        private val onAgregarProducto: (String, Double) -> Unit
    ) : RecyclerView.Adapter<AdaptadorProductos.VistaProducto>() {

        inner class VistaProducto(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textoNombre: TextView = itemView.findViewById(R.id.tvNombre)
            val textoPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
            val textoCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
            val botonIncrementar: Button = itemView.findViewById(R.id.btnIncrementar)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaProducto {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.i_p, parent, false)
            return VistaProducto(itemView)
        }

        override fun getItemCount() = datosProductos.size

        override fun onBindViewHolder(holder: VistaProducto, position: Int) {
            val producto = datosProductos[position]
            holder.textoNombre.text = producto["nombre"] as String
            holder.textoPrecio.text = "$${producto["precio"] as Double}"
            holder.textoCantidad.text = "Cantidad: ${producto["cantidad"] as Int}"

            holder.botonIncrementar.setOnClickListener {
                val nuevaCantidad = (producto["cantidad"] as Int) + 1
                producto["cantidad"] = nuevaCantidad
                holder.textoCantidad.text = "Cantidad: $nuevaCantidad"
                calcularMontoTotal()
            }
        }
    }
}