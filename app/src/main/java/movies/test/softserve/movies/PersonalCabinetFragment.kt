package movies.test.softserve.movies


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_personal_cabinet.view.*


class PersonalCabinetFragment : Fragment() {

    lateinit var thisView : View

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
                    .into(thisView.profile_photo, object : Callback{
                        override fun onError() {

                        }

                        override fun onSuccess() {
                            
                        }
                    })
        }
    }


    override fun onDestroy() {
        Picasso.with(activity).cancelRequest(thisView.profile_photo)
        super.onDestroy()
    }
}
