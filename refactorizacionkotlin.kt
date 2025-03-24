class ProductListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var totalTextView: TextView
    
    private val productList = mutableListOf<Product>()
    private var totalPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        initializeViews(recyclerViewId = R.id.recyclerView, progressBarId = R.id.progressBar,totalTextViewId = R.id.totalTextView)        setupRecyclerView()
        setupEventListeners()

        loadProducts()

    }

private fun initializeViews(
    recyclerViewId: Int,
    progressBarId: Int,
    totalTextViewId: Int
) {
    recyclerView = findViewById(recyclerViewId)
    progressBar = findViewById(progressBarId)
    totalTextView = findViewById(totalTextViewId)
}

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(productList) { productName, productPrice ->
            addProduct(Product(name = productName, price = productPrice, quantity = 1))
        }
        recyclerView.adapter = productAdapter
    }

    private fun setupEventListeners() {
        findViewById<Button>(R.id.refreshButton).setOnClickListener {
            loadProducts()
        }
    }

    private fun loadProducts() {
        progressBar.visibility = View.VISIBLE
        FirebaseFirestore.getInstance().collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                handleProductsSuccess(querySnapshot)
            }
            .addOnFailureListener { exception ->
                handleProductsError(exception)
            }
    }

    private fun handleProductsSuccess(querySnapshot: QuerySnapshot) {
        productList.clear()
        productList.addAll(querySnapshot.map { document ->
            DocumentToProductMapper.map(document)
        })
        productAdapter.notifyDataSetChanged()
        calculateTotal()
        progressBar.visibility = View.GONE
    }

    private fun handleProductsError(exception: Exception) {
        Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
    }

    private fun addProduct(product: Product) {
        productList.add(product)
        productAdapter.notifyItemInserted(productList.lastIndex)
        calculateTotal()
    }

    private fun calculateTotal() {
        totalPrice = productList.sumOf { it.price * it.quantity }
        totalTextView.text = "Total: $${String.format("%.2f", totalPrice)}"
    }

    private inner class ProductAdapter(
        private val products: List<Product>,
        private val onAddProduct: (String, Double) -> Unit
    ) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameTextView: TextView = itemView.findViewById(R.id.productName)
            private val priceTextView: TextView = itemView.findViewById(R.id.productPrice)
            private val quantityTextView: TextView = itemView.findViewById(R.id.productQuantity)
            private val increaseButton: Button = itemView.findViewById(R.id.increaseQuantityButton)

            fun bind(product: Product) {
                nameTextView.text = product.name
                priceTextView.text = "$${product.price}"
                quantityTextView.text = "Quantity: ${product.quantity}"
                
                increaseButton.setOnClickListener {
                    updateProductQuantity(product)
                }
            }

            private fun updateProductQuantity(product: Product) {
                product.quantity++
                quantityTextView.text = "Quantity: ${product.quantity}"
                calculateTotal()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.bind(products[position])
        }

        override fun getItemCount() = products.size
    }

    private data class Product(
        val id: String = "",
        val name: String,
        val price: Double,
        var quantity: Int
    )

    private object DocumentToProductMapper {
        fun map(document: DocumentSnapshot): Product = Product(
            id = document.id,
            name = document.getString("name") ?: "",
            price = document.getDouble("price") ?: 0.0,
            quantity = document.getLong("quantity")?.toInt() ?: 1
        )
    }
}