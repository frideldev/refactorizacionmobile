class MainActivity : AppCompatActivity() {
    private var productList = ArrayList<HashMap<String, Any>>()
    private var totalAmount = 0.0
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var totalTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupButtonListener()
        loadProducts()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        totalTextView = findViewById(R.id.totalTextView)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(productList) { productName, productPrice ->
            addNewProduct(productName, productPrice)
        }
        recyclerView.adapter = productAdapter
    }

    private fun setupButtonListener() {
        findViewById<Button>(R.id.loadButton).setOnClickListener {
            loadProducts()
        }
    }

    private fun loadProducts() {
        showLoading(true)
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("products").get()
            .addOnSuccessListener { querySnapshot ->
                processProductData(querySnapshot)
                showLoading(false)
            }
            .addOnFailureListener { exception ->
                showError(exception.message)
                showLoading(false)
            }
    }

    private fun processProductData(querySnapshot: QuerySnapshot) {
        productList.clear()

        for (document in querySnapshot) {
            val productMap = HashMap<String, Any>().apply {
                put("id", document.id)
                put("name", document.getString("name") ?: "")
                put("price", document.getDouble("price") ?: 0.0)
                put("quantity", document.getLong("quantity")?.toInt() ?: 1)
            }
            productList.add(productMap)
        }

        productAdapter.notifyDataSetChanged()
        calculateTotal()
    }

    private fun addNewProduct(productName: String, productPrice: Double) {
        val newProduct = HashMap<String, Any>().apply {
            put("name", productName)
            put("price", productPrice)
            put("quantity", 1)
        }

        productList.add(newProduct)
        productAdapter.notifyDataSetChanged()
        calculateTotal()
    }

    private fun calculateTotal() {
        totalAmount = productList.sumByDouble { product ->
            (product["price"] as Double) * (product["quantity"] as Int)
        }

        updateTotalDisplay()
    }

    private fun updateTotalDisplay() {
        totalTextView.text = "Total: $${String.format("%.2f", totalAmount)}"
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(errorMessage: String?) {
        Toast.makeText(
            this,
            "Error: ${errorMessage ?: "Unknown error"}",
            Toast.LENGTH_SHORT
        ).show()
    }

    inner class ProductAdapter(
        private val products: ArrayList<HashMap<String, Any>>,
        private val onProductAdd: (String, Double) -> Unit
    ) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
            val priceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
            val quantityTextView: TextView = itemView.findViewById(R.id.productQuantityTextView)
            val increaseButton: Button = itemView.findViewById(R.id.increaseQuantityButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
            return ProductViewHolder(view)
        }

        override fun getItemCount() = products.size

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = products[position]

            holder.nameTextView.text = product["name"] as String
            holder.priceTextView.text = "$${product["price"] as Double}"
            holder.quantityTextView.text = "Quantity: ${product["quantity"] as Int}"

            holder.increaseButton.setOnClickListener {
                increaseProductQuantity(product)
                holder.quantityTextView.text = "Quantity: ${product["quantity"] as Int}"
                calculateTotal()
            }
        }

        private fun increaseProductQuantity(product: HashMap<String, Any>) {
            val currentQuantity = product["quantity"] as Int
            product["quantity"] = currentQuantity + 1
        }
    }
}
