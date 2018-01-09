package movies.test.softserve.movies


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_personal_cabinet.view.*
import movies.test.softserve.movies.controller.MainController
import movies.test.softserve.movies.entity.Achievement
import movies.test.softserve.movies.entity.TVEntity
import movies.test.softserve.movies.service.DbMovieServiceRoom
import movies.test.softserve.movies.util.AchievementService
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PersonalCabinetFragment : Fragment() {

    private lateinit var thisView: View
    private val user = MainController.getInstance().user
    private val storage = FirebaseStorage.getInstance().getReference("user").child(user.uid).child("user_Photo")
    private val dbService = DbMovieServiceRoom.getInstance()

    private lateinit var fileWithLastPhoto: File

    companion object {
        val NAME_OF_FILE = "img"
        val SELECT_PICTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileWithLastPhoto = File(context!!.filesDir, NAME_OF_FILE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        thisView = inflater.inflate(R.layout.fragment_personal_cabinet, container, false)
        thisView.setOnClickListener {}
        thisView.profile_photo.setOnClickListener {
            pickImage()
        }

        Thread {
            val tvShowsCount = dbService.getMovieCount(TVEntity.TYPE.TV_SHOW)
            val movieCount = dbService.getMovieCount(TVEntity.TYPE.MOVIE)
            val achievementsDone = Achievement.getAchievements().size - AchievementService.getInstance().achievementsSize

            try {
                thisView.watched_movies.text = getString(R.string.movies_watched,movieCount)
                thisView.watched_tv_shows.text = getString(R.string.tv_shows_watched,tvShowsCount)
                thisView.achievements_done.text = getString(R.string.achievements_done,achievementsDone)
            } finally {

            }

        }.start()

        thisView.nickname.setText(user.displayName)

        try {
            if (fileWithLastPhoto.exists()) {
                Picasso.with(activity).load(fileWithLastPhoto).into(thisView.profile_photo)
            }
        } catch (e: Exception) {
            Log.e("Smth wrong with image", e.message)
        }

        storage.downloadUrl.addOnSuccessListener { uri ->
            Picasso.with(activity).cancelRequest(thisView.profile_photo)
            Picasso.with(activity).load(uri).noPlaceholder().centerCrop().fit()
                    .into(thisView.profile_photo, object : Callback {
                        override fun onSuccess() {
                            tacticalSleeping()

                            val bitmap = buildBitmapFromImage(thisView.profile_photo)

                            saveToFile(bitmap)
                        }

                        override fun onError() {
                        }

                    })

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
            Picasso.with(activity).cancelRequest(thisView.profile_photo)
            Picasso.with(activity).load(data!!.data).noPlaceholder().centerCrop().fit()
                    .into(thisView.profile_photo, object : Callback {
                        override fun onError() {

                        }

                        override fun onSuccess() {
                            tacticalSleeping()
                            val bitmap = buildBitmapFromImage(thisView.profile_photo)

                            saveToFile(bitmap)

                            saveToFirebase(bitmap)
                        }
                    })
        }
    }


    override fun onDestroy() {
        Picasso.with(activity).cancelRequest(thisView.profile_photo)
        super.onDestroy()
    }

    private fun tacticalSleeping() {
        Thread.sleep(200)
    }

    private fun buildBitmapFromImage(image: ImageView): Bitmap {
        image.isDrawingCacheEnabled = true
        image.buildDrawingCache()
        return image.drawingCache
    }

    private fun saveToFile(bitmap: Bitmap) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(fileWithLastPhoto)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (out != null) {
                    out.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveToFirebase(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        storage.putBytes(data)
    }
}
