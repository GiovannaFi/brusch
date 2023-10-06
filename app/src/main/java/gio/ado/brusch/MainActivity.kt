package gio.ado.brusch

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder


//https://www.youtube.com/watch?v=evI1UTL4RDE&ab_channel=IsaiasCuvula
class MainActivity : AppCompatActivity() {

    private val personalCollectionRef = Firebase.firestore.collection("babbo")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSave = findViewById<Button>(R.id.btn_save_cuteMessage)
        val buttonRetrieve = findViewById<Button>(R.id.btn_retrieve_cuteMessage)
        val textMessage = findViewById<EditText>(R.id.edt_cuteMessage)

        buttonSave.setOnClickListener{
            val message = textMessage.text.toString()
            val cuteMessage = CuteMessage(message = message)
            saveCuteMessage(cuteMessage)
        }
//        buttonRetrieve.setOnClickListener{
//            retrieveCuteMessage()
//        }
        realtimeUpdates()
    }

    private fun saveCuteMessage(cuteMessage: CuteMessage) = CoroutineScope(Dispatchers.IO).launch {
        try {
            personalCollectionRef.add(cuteMessage).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@MainActivity,
                    "messaggio cute salvato con successo",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun realtimeUpdates(){
        personalCollectionRef.addSnapshotListener { querySnapShot, error ->
            error?.let {
                Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapShot?.let {
                val sb = StringBuilder()
                for(document in it){
                    val message = document.toObject<CuteMessage>()
                    sb.append("${message.message}\n")
                }
                val listCuteMessage = findViewById<TextView>(R.id.cute_messages_list)
                listCuteMessage.text = sb.toString()
            }
        }
    }

    private fun retrieveCuteMessage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapShot = personalCollectionRef.get().await()
            val sb = StringBuilder()
            for(document in querySnapShot.documents){
                val message = document.toObject<CuteMessage>()
                sb.append("${message?.message}\n")
            }
            withContext(Dispatchers.Main) {
                val listCuteMessage = findViewById<TextView>(R.id.cute_messages_list)
                listCuteMessage.text= sb.toString()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                Log.e("COSO CAZZU", e.message.orEmpty())
            }
        }
    }
}