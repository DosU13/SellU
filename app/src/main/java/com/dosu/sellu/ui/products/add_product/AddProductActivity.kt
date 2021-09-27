package com.dosu.sellu.ui.products.add_product

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.R
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.product.model.ProductWithoutId
import com.dosu.sellu.databinding.ActivityAddProductBinding
import com.dosu.sellu.ui.products.util.AddProductListener
import com.dosu.sellu.ui.products.util.ImageListener
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import com.dosu.sellu.util.toByteArray
import com.dosu.sellu.util.toDrawable
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.util.*

class AddProductActivity : AppCompatActivity(), DIAware, AddProductListener, ImageListener {
    override val di: DI by closestDI()
    private val productViewModelFactory: ProductsViewModelFactory by instance()
    private lateinit var productsViewModel: ProductsViewModel
    private var _binding: ActivityAddProductBinding? = null
    private val binding get() = _binding!!
    private var isNewProduct: Boolean = true
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productsViewModel = ViewModelProvider(this, productViewModelFactory).get(ProductsViewModel::class.java)
        productsViewModel.setListener(this as ImageListener)
        productsViewModel.setListener(this as AddProductListener)
        isNewProduct = intent.getBooleanExtra("isNewProduct", true)
        if(!isNewProduct) {
            intent.getStringExtra("productId")?.let { productsViewModel.getProduct(it) }
            binding.addProductComplete.text = getString(R.string.change_product_btn)
        }
        binding.imageStorage.setOnClickListener { imageFromGalleryPressed() }
        binding.addProductComplete.setOnClickListener{finishBtnClicked()}
    }

    private fun updateUIWithProduct(){
        productsViewModel.downloadImages(product.productId, product.numOfImages.toInt())
        binding.run{
            editName.setText(product.name)
            editDescription.setText(product.description)
            editPrize.setText(product.prize.toString())
            editOnwPrize.setText(product.ownPrize.toString())
            editQuantity.setText(product.quantity.toString())
        }
    }

    private fun updateImages(){
        try {for(i in 0..4) imageView(i).setImageDrawable(images[i].toDrawable(resources))}
        catch (ignored: IndexOutOfBoundsException){}
    }

    private fun imageFromGalleryPressed(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val images = mutableListOf<ByteArray>()
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            if(images.size<=5){
                data?.data?.let{images.add(it.toByteArray(this))}
            }else{
                Toast.makeText(this, getString(R.string.max_5_img), Toast.LENGTH_SHORT).show()
            }
            updateImages()
        }
    }

    private fun finishBtnClicked(){
        val name: String= binding.editName.text.toString()
        val numOfImages: Long = images.size.toLong()
        val description: String = binding.editDescription.text.toString()
        val prize: Double = binding.editPrize.text.toString().toDouble()
        val ownPrize: Double = binding.editOnwPrize.text.toString().toDouble()
        val quantity: Long = binding.editQuantity.text.toString().toLong()
        if(isNewProduct) productsViewModel.addProduct(ProductWithoutId(name, numOfImages, description, prize, quantity,
            emptyMap(), ownPrize, Date(), 0))
        else productsViewModel.updateProductDetails(product.productId, name, numOfImages, description, prize, ownPrize, quantity)
    }

    override fun getProduct(product: Product) {
        this.product = product
        updateUIWithProduct()
    }

    override fun downloadImage(byteArray: ByteArray, productId: String, imagePos: Int){
        images.add(byteArray)
        updateImages()
    }

    override fun addProductSucceeded(productName: String, productId: String) {
        Toast.makeText(this, productName+getString(R.string.successfully_added), Toast.LENGTH_SHORT).show()
        val imageByteArrays = List(images.size){images[it]}
        productsViewModel.uploadImages(productId, imageByteArrays)
        this.finish()
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Toast.makeText(this, "Firebase error on AddProductActivity", Toast.LENGTH_LONG).show()
    }

    private fun imageView(index: Int): ImageView{
        return when(index){
            0 -> binding.image0
            1 -> binding.image1
            2 -> binding.image2
            3 -> binding.image3
            4 -> binding.image4
            else -> ImageView(this)
        }
    }
}