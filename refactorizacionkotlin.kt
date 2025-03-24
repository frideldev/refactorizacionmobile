package com.example
class MA : AppCompatActivity() {
    var i = ArrayList<HashMap<String, Any>>()
    var t = 0.0
    lateinit var rv: RecyclerView
    lateinit var ad: RA
    lateinit var pb: ProgressBar
    lateinit var tv: TextView

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.a_m)

        rv = findViewById(R.id.rv)
        pb = findViewById(R.id.pb)
        tv = findViewById(R.id.tv)

        rv.layoutManager = LinearLayoutManager(this)
        ad = RA(i) { p, q ->
            // Agregar producto
            val m = HashMap<String, Any>()
            m["nombre"] = nombre
            m["precio"] = precio
            m["cantidad"] = 1
            a(m)
        }

        rv.adapter = ad

        findViewById<Button>(R.id.btn).setOnClickListener {
            ld()
        }

        ld()
    }

    fun ld() {
        pb.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        db.collection("precio").get()
            .addOnSuccessListener { d ->
                i.clear()
                for (doc in d) {
                    val m = HashMap<String, Any>()
                    m["id"] = doc.id
                    m["nombre"] = doc.getString("nombre") ?: ""
                    m["precio"] = doc.getDouble("precio") ?: 0.0
                    m["cantidad"] = doc.getLong("cantidad")?.toInt() ?: 1
                    i.add(m)
                }
                ad.notifyDataSetChanged()
                c()
                pb.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                pb.visibility = View.GONE
            }
    }

    fun a(m: HashMap<String, Any>) {
        i.add(m)
        ad.notifyDataSetChanged()
        c()
    }

    fun c() {
        t = 0.0
        for (j in i) {
            t += (j["precio"] as Double) * (j["cantidad"] as Int)
        }
        tv.text = "Total: $${t}"
    }

    inner class RA(private val d: ArrayList<HashMap<String, Any>>, private val cl: (String, Double) -> Unit) :
        RecyclerView.Adapter<RA.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val nombre: TextView = v.findViewById(R.id.n)
            val precio: TextView = v.findViewById(R.id.p)
            val cantidad: TextView = v.findViewById(R.id.q)
            val btn: Button = v.findViewById(R.id.btn)
        }

        override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
            val v = LayoutInflater.from(p.context).inflate(R.layout.i_p, p, false)
            return VH(v)
        }

        override fun getItemCount() = d.size

        override fun onBindViewHolder(h: VH, pos: Int) {
            val item = d[pos]
            h.nombre.text = item["nombre"] as String
            h.precio.text = "$${item["p"] as Double}"
            h.cantidad.text = "Cantidad: ${item["cantidad"] as Int}"

            h.btn.setOnClickListener {
                val newQ = (item["cantidad"] as Int) + 1
                item["cantidad"] = newQ
                h.cantidad.text = "Cantidad: $newQ"
                c()
            }
        }
    }
}
