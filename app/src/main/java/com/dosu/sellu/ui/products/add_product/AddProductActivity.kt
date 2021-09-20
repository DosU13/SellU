package com.dosu.sellu.ui.products.add_product

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.R
import com.dosu.sellu.data.network.product.model.ProductWithoutId
import com.dosu.sellu.databinding.ActivityAddProductBinding
import com.dosu.sellu.ui.products.util.AddProductListener
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModel
import com.dosu.sellu.ui.products.viewmodel.ProductsViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import com.dosu.sellu.util.toByteArray
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

class AddProductActivity : AppCompatActivity(), DIAware, AddProductListener {
    override val di: DI by closestDI()
    private val productViewModelFactory: ProductsViewModelFactory by instance()
    private lateinit var productsViewModel: ProductsViewModel
    private var _binding: ActivityAddProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productsViewModel = ViewModelProvider(this, productViewModelFactory).get(ProductsViewModel::class.java)
        productsViewModel.setListener(this)
        binding.imageStorage.setOnClickListener { imageFromGalleryPressed() }
        binding.addProductComplete.setOnClickListener{addBtnClicked()}
    }

    private fun imageFromGalleryPressed(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val images = mutableListOf<Uri?>()
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            if(images.size<=5){
                images.add(data?.data)
            }else{
                Toast.makeText(this, getString(R.string.max_5_img), Toast.LENGTH_SHORT).show()
            }
            try {
                binding.image0.setImageURI(images[0])
                binding.image1.setImageURI(images[1])
                binding.image2.setImageURI(images[2])
                binding.image3.setImageURI(images[3])
                binding.image4.setImageURI(images[4])
            }catch (ignored: IndexOutOfBoundsException){}
        }
    }

    private fun addBtnClicked(){
        val name: String= binding.editName.text.toString()
        val numOfImages: Long = images.size.toLong()
        val description: String = binding.editDescription.text.toString()
        val prize: Double = binding.editPrize.text.toString().toDouble()
        val quantity: Long = binding.editQuantity.text.toString().toLong()
        productsViewModel.addProduct(ProductWithoutId(name, numOfImages, description, prize, quantity))
    }

    private fun uploadImages(): List<String>{
        val result = emptyList<ByteArray>()
        for(u in images){

        }
        return emptyList() //// HERE HERE
    }

    override fun addProductSucceeded(productName: String, productId: String) {
        Toast.makeText(this, productName+getString(R.string.successfully_added), Toast.LENGTH_SHORT).show()
        val imageByteArrays = List(images.size){images[it]!!.toByteArray(this)}
        productsViewModel.uploadImages(productId, imageByteArrays)
        this.finish()
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Toast.makeText(this, "Firebase error on AddProductActivity", Toast.LENGTH_LONG).show()
    }
}