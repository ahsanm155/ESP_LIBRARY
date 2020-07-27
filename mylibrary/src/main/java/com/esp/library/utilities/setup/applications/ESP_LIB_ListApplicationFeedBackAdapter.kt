package utilities.adapters.setup.applications

import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.ESP_LIB_Constants
import com.esp.library.utilities.common.ESP_LIB_RoundedPicasso
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.google.gson.GsonBuilder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.addapplication.ESP_LIB_PostApplicationsCriteriaCommentsDAO
import utilities.data.applicants.feedback.ESP_LIB_ApplicationsFeedbackDAO
import java.io.File
import java.util.concurrent.TimeUnit


class ESP_LIB_ListApplicationFeedBackAdapter(private val mApplications: List<ESP_LIB_ApplicationsFeedbackDAO>?, con: ESP_LIB_BaseActivity, internal var searched_text: String) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListApplicationFeedBackAdapter.ParentViewHolder>() {
    internal var pref: ESP_LIB_SharedPreference
    private var context: ESP_LIB_BaseActivity?
    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var user_img: ImageView
        internal var date_feedback: TextView
        internal var feedback: TextView
        internal var edit_feedback: ImageView
        internal var dynamic_fields_div: LinearLayout

        init {
            user_img = itemView.findViewById(R.id.user_img)
            edit_feedback = itemView.findViewById(R.id.edit_feedback)
            date_feedback = itemView.findViewById(R.id.date_feedback)
            feedback = itemView.findViewById(R.id.feedback)
            dynamic_fields_div = itemView.findViewById(R.id.dynamic_fields_div)
        }

    }


    init {
        context = con
        pref = ESP_LIB_SharedPreference(context!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View
        v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_repeater_application_feedback, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val holder = holder_parent as ActivitiesList

        holder.date_feedback.text = ESP_LIB_Shared.getInstance().getDisplayDate(context, mApplications!![position].createdOn, true)

        if (mApplications[position].comment!!.length > 150) {
            holder.feedback.text = ESP_LIB_Shared.getInstance().toSubStr(mApplications[position].comment, 150)
            holder.feedback.tag = context!!.getString(R.string.esp_lib_text_hidden)

        } else {
            holder.feedback.text = mApplications[position].comment
            holder.feedback.tag = context!!.getString(R.string.esp_lib_text_shown)
        }

        holder.feedback.setOnClickListener {
            val status = holder.feedback.tag as String

            if (status != null) {

                if (status.equals(context!!.getString(R.string.esp_lib_text_hidden), ignoreCase = true)) {
                    holder.feedback.text = mApplications[position].comment
                    holder.feedback.tag = context!!.getString(R.string.esp_lib_text_shown)
                } else {
                    holder.feedback.text = ESP_LIB_Shared.getInstance().toSubStr(mApplications[position].comment, 150)
                    holder.feedback.tag = context!!.getString(R.string.esp_lib_text_hidden)
                }
            }
        }


        if (mApplications[position].imageUrl != null && mApplications[position].imageUrl!!.length > 0) {
            Picasso.with(context)
                    .load(mApplications[position].imageUrl)
                    .placeholder(R.drawable.esp_lib_drawable_ic_contact_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(ESP_LIB_RoundedPicasso())
                    .resize(30, 30)
                    .into(holder.user_img)


        }

        var ScropId: String? = null

        try {
            if (ESP_LIB_Shared.getInstance().ReadPref("scropId", "login_info", context) != null) {
                ScropId = ESP_LIB_Shared.getInstance().ReadPref("scropId", "login_info", context)
            }

        } catch (e: Exception) {
        }

        if (ScropId != null) {

            val CommentsId = mApplications[position].commentUserId.toString() + ""
            if (CommentsId == ScropId) {
                //	holder.edit_feedback.setVisibility(View.VISIBLE);
            } else {
                holder.edit_feedback.visibility = View.GONE
            }

        } else {
            holder.edit_feedback.visibility = View.GONE
        }

        if (mApplications[position].attachments != null && mApplications[position].attachments!!.size > 0) {

            holder.dynamic_fields_div.visibility = View.VISIBLE

            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(0, 15, 10, 10)
            val linearLayout = LinearLayout(context)
            val linearParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.layoutParams = linearParams

            for (document in mApplications[position].attachments!!) {

                val document_view = context!!.layoutInflater.inflate(R.layout.esp_lib_repeater_feedback_documents, holder.dynamic_fields_div, false)

                if (document_view != null) {

                    val file_name = document_view.findViewById<TextView>(R.id.file_name)
                    file_name.text = document.name
                }

                linearLayout.addView(document_view)

            }
            holder.dynamic_fields_div.addView(linearLayout)


        } else {
            holder.dynamic_fields_div.visibility = View.GONE
        }

        holder.edit_feedback.setOnClickListener {
            val applicationsFeedbackDAO = mApplications[position]
            AddCriterComments(applicationsFeedbackDAO)
        }

    }//End Holder Class


    override fun getItemCount(): Int {
        return mApplications?.size ?: 0

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun RefreshList() {
        notifyDataSetChanged()
    }

    fun AddCriterComments(post: ESP_LIB_ApplicationsFeedbackDAO?) {

        val builder = AlertDialog.Builder(context!!)

        val view = LayoutInflater.from(context).inflate(R.layout.esp_lib_dialog_layout, null)
        val input = view.findViewById<EditText>(R.id.reason)
        input.setHint(R.string.esp_lib_text_please_add_comment)
        builder.setView(view)

        if (post != null) {
            builder.setTitle(R.string.esp_lib_text_edit_comment)

            if (post.comment != null && post.comment!!.length > 0) {
                input.setText(post.comment)
            }

        } else {
            builder.setTitle(R.string.esp_lib_text_add_comment)
        }



        builder.setPositiveButton(R.string.esp_lib_text_save) { dialog, which ->
            val m_Text = input.text.toString()

            if (m_Text != null && m_Text.length > 0) {

                val post_comments = ESP_LIB_PostApplicationsCriteriaCommentsDAO()
                post_comments.assessmentId = post!!.assessmentId
                post_comments.comments = m_Text

                if (post != null) {
                    post_comments.id = post.id
                }

                AddEditComments(post_comments)

                dialog.cancel()

            }
            dialog.cancel()
        }



        builder.setNegativeButton(R.string.esp_lib_text_cancel) { dialog, which -> dialog.cancel() }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context!!.resources.getColor(R.color.esp_lib_color_black))


        if (post != null && post.comment != null && post.comment!!.length > 0) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        } else {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }


        input.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                       count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                // Check if edittext is empty
                if (TextUtils.isEmpty(s)) {
                    //Disable ok button
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE).isEnabled = false
                } else {
                    // Something into edit text. Enable the button.
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE).isEnabled = true
                }

            }
        })
    }


    fun AddEditComments(ESPLIBPost: ESP_LIB_PostApplicationsCriteriaCommentsDAO) {

        try {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .header("locale", ESP_LIB_Shared.getInstance().getLanguage(context))
                        .header("Authorization", "bearer " + ESP_LIB_ESPApplication.getInstance().user.loginResponse?.access_token)
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            httpClient.connectTimeout(10, TimeUnit.SECONDS)
            httpClient.readTimeout(10, TimeUnit.SECONDS)
            httpClient.writeTimeout(10, TimeUnit.SECONDS)

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create()

            val retrofit = Retrofit.Builder()
                    .baseUrl(ESP_LIB_Constants.base_url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build()


            val apis = retrofit.create(ESP_LIB_APIs::class.java)

            val UserComments = RequestBody.create(MediaType.parse("text/plain"), ESPLIBPost.comments)
            val status_call = apis.EditComments(ESPLIBPost.id, ESPLIBPost.assessmentId, UserComments)
            status_call.enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>?) {

                    if (response != null && response.body() != null && response.body() > 0) {
                        notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable?) {

                    if (t != null && context != null) {
                        ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().application, context?.getString(R.string.esp_lib_text_filter_error), context)

                    }

                }
            })

        } catch (ex: Exception) {
            if (ex != null) {

                if (ex != null && context != null) {
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().application, context?.getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                }
            }
        }

    }


    /*
	private ApplicationsFeedbackDAO DownLoadFile(InputStream inputStream, ApplicationsFeedbackDAO attachmentsDAO){

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		String fileName = "";

		if(attachmentsDAO.getAttachments()!=null && attachmentsDAO.getAttachments().getName()!=null && attachmentsDAO.getAttachments().getName().length()>0){
			fileName = attachmentsDAO.getAttachments().getName();
		}

		if(attachmentsDAO.getAttachments()!=null && attachmentsDAO.getAttachments().getName()!=null && attachmentsDAO.getAttachments().getName().length()>0){
			fileName = attachmentsDAO.getAttachments().getName();
		}


		File file = new File(Constants.FOLDER_PATH+"/"+Constants.FOLDER_NAME,fileName );
		OutputStream output = null;
		try {
			output = new FileOutputStream(file);

			byte[] buffer = new byte[1024]; // or other buffer size
			int read;

			while ((read = inputStream.read(buffer)) != -1) {
				output.write(buffer, 0, read);
			}
			output.flush();
		} catch (IOException e) {
			attachmentsDAO.getAttachments().setFileDownling(false);
			attachmentsDAO.getAttachments().setFileDownloaded(false);
			//RefreshList();

			return attachmentsDAO;
		} finally {
			try {
				if (output != null) {
					output.close();
				}
				else{
				}
			} catch (IOException e){
				attachmentsDAO.getAttachments().setFileDownling(false);
				attachmentsDAO.getAttachments().setFileDownloaded(false);
				//RefreshList();
				return attachmentsDAO;
			}
		}

		attachmentsDAO.getAttachments().setFileDownloaded(true);
		attachmentsDAO.getAttachments().setFileDownling(false);
		return attachmentsDAO;
	}

	*/private fun OpenFile(filePath: String) {
        try {
            val file = File(filePath)
            if (file != null) {

                val builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())

                val i = Intent(Intent.ACTION_VIEW)
                i.setDataAndType(Uri.fromFile(file), ESP_LIB_Shared.getInstance().getMimeType(file.path))
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context!!.startActivity(i)
            }


        } catch (e: Exception) {
            ESP_LIB_Shared.getInstance().messageBox(context!!.getString(R.string.esp_lib_text_appnotavailable), context)

        }

    }

    companion object {

        private val LOG_TAG = "ListApplicationFeedBackAdapter"


    }

}
