package movies.test.softserve.movies


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_personal_cabinet.view.*
import movies.test.softserve.movies.controller.MainController
import java.io.ByteArrayOutputStream


class PersonalCabinetFragment : Fragment() {

    private lateinit var thisView: View
    private val storage = FirebaseStorage.getInstance().getReference("user").child(MainController.getInstance().user.uid).child("user_Photo")

    companion object {
        val SELECT_PICTURE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        thisView = inflater.inflate(R.layout.fragment_personal_cabinet, container, false)
        thisView.setOnClickListener {}
        thisView.profile_photo.setOnClickListener {
            pickImage()
        }

        storage.downloadUrl.addOnSuccessListener { uri ->
            Picasso.with(activity).load(uri).noPlaceholder().centerCrop().fit()
                    .into(thisView.profile_photo)
        }

        return thisView
    }


    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Picasso.with(activity).load(data!!.data).noPlaceholder().centerCrop().fit()
                    .into(thisView.profile_photo, object : Callback {
                        override fun onError() {

                        }

                        override fun onSuccess() {
                            Thread.sleep(200)
                            thisView.profile_photo.isDrawingCacheEnabled = true
                            thisView.profile_photo.buildDrawingCache()
                            val bitmap = thisView.profile_photo.drawingCache
                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val data = baos.toByteArray()

                            storage.putBytes(data)
                        }
                    })
        }
    }


    override fun onDestroy() {
        Picasso.with(activity).cancelRequest(thisView.profile_photo)
        super.onDestroy()
    }
}
