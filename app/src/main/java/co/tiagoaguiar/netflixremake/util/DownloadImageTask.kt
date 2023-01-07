package co.tiagoaguiar.netflixremake.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class DownloadImageTask(private val callback: DownloadImageTask.CallBack) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface CallBack {
        fun onResult(bitmap: Bitmap)
    }

    fun execute(url: String) {
        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null


            try {
                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection // abrir conexão
                urlConnection.readTimeout = 2000 // tempo de leitura (2 segundos)
                urlConnection.connectTimeout = 2000 // tempo de conexão (2 segundos)

                val statusCode: Int = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor!")
                }

                stream = urlConnection.inputStream // sequencia de bytes
                val bitmap = BitmapFactory.decodeStream(stream)

                handler.post {
                    callback.onResult(bitmap)
                }
            } catch (e: IOException) {

            } finally {
                urlConnection?.disconnect()
                stream?.close()
            }
        }
    }
}