package com.dosu.sellu.ui.products.add_product

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import com.dosu.sellu.util.loadImage
import com.dosu.sellu.util.toByteArray
import com.dosu.sellu.util.toThumbnailByteArray
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.util.*

class AddProductActivity : AppCompatActivity(), DIAware, AddProductListener {
    override val di: DI by closestDI()
    private val productViewModelFactory: ProductsViewModelFactory by instance()
    private lateinit var productsViewModel: ProductsViewModel
    private var _binding: ActivityAddProductBinding? = null
    private val binding get() = _binding!!
    private var isNewProduct: Boolean = true
    private lateinit var product: Product
    private lateinit var imageViews: ArrayList<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageViews = arrayListOf(binding.image0, binding.image1, binding.image2, binding.image3, binding.image4)
        productsViewModel = ViewModelProvider(this, productViewModelFactory).get(ProductsViewModel::class.java)
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
        product.images.forEachIndexed{i, uri ->
            imageViews[i].loadImage(this, Uri.parse(uri))
        }
        binding.run{
            editName.setText(product.name)
            editDescription.setText(product.description)
            editPrize.setText(product.prize.toString())
            editOnwPrize.setText(product.ownPrize.toString())
            editQuantity.setText(product.quantity.toString())
        }
    }

    private fun updateImages(){
        for((i, imageView) in imageViews.withIndex()){
            if(i<images.size) imageView.setImageURI(images[i])
            else imageView.setImageResource(R.drawable.ic_baseline_shopping_bag_24)
        }
    }

    private fun imageFromGalleryPressed(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private var images = mutableListOf<Uri>()
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            if(images.size<=5){
                data?.data?.let{images.add(it)}
            }else{
                Toast.makeText(this, getString(R.string.max_5_img), Toast.LENGTH_SHORT).show()
            }
            updateImages()
        }
    }

    private fun finishBtnClicked(){
        val name: String= binding.editName.text.toString()
        val description: String = binding.editDescription.text.toString()
        val prize: Double = binding.editPrize.text.toString().toDouble()
        val ownPrize: Double = binding.editOnwPrize.text.toString().toDouble()
        val quantity: Long = binding.editQuantity.text.toString().toLong()
        if(isNewProduct) productsViewModel.addProduct(ProductWithoutId(name, null, emptyList(), description, prize, quantity,
            ownPrize, Date(), 0))
        else productsViewModel.updateProductDetails(product.productId, name, description, prize, ownPrize, quantity)
    }

    override fun getProduct(product: Product) {
        this.product = product
        updateUIWithProduct()
    }

    override fun addProductSucceeded(productName: String, productId: String) {
        Toast.makeText(this, productName+getString(R.string.successfully_added), Toast.LENGTH_SHORT).show()
        val thumbnail = images[0].toThumbnailByteArray(this)
        productsViewModel.uploadThumbnail(productId, thumbnail)
        val imageByteArrays = List(images.size){images[it].toByteArray(this)}
        productsViewModel.uploadImages(productId, imageByteArrays)
    }

    private var thumbnailSucceed = false
    private var imagesSucceed = false

    override fun thumbnailSucceed() {
        Toast.makeText(this, getString(R.string.thumbnailSuccess), Toast.LENGTH_SHORT).show()
        thumbnailSucceed = true
        if(thumbnailSucceed and imagesSucceed) finish()
    }

    override fun imagesSucceed() {
        Toast.makeText(this, "Images successfully uploaded", Toast.LENGTH_SHORT).show()
        imagesSucceed = true
        if(thumbnailSucceed and imagesSucceed) finish()
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Toast.makeText(this, "Firebase error on AddProductActivity", Toast.LENGTH_LONG).show()
    }
}