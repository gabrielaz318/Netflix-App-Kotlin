package co.tiagoaguiar.netflixremake.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: CallBack) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface CallBack {
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
    }

    fun execute(url: String) {
        handler.post {
            callback.onPreExecute()
        }

        // Nesse momento, estamos utilizando a UI-Thread (1)
        executor.execute {
            // Nesse momento, estamos utilizando a NOVA-Thread [processo paralelo] (2)
            var urlConnection: HttpsURLConnection? = null
            var buffer: BufferedInputStream? = null
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

                // Forma 1 para ler os dados (simples e rápida):
                //val jsonAsString = stream.bufferedReader().use { it.readText() } // anexar/analisar todos os bytes e ler como texto

                // Forma 2 para ler os dados:
                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                // JSON preparado para ser convertido em um DATA CLASS

                val categories = toCategories(jsonAsString)

                handler.post {
                    // Aqui roda dentro da UI-Thread
                    callback.onResult(categories)
                }


            } catch (e: IOException) {
                val message = e.message ?: "Erro desconhecido"

                handler.post {
                    callback.onFailure(message)
                }

            } finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }

        }
    }

    private fun toCategories(jsonAsString: String): List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")
        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)
            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }

            categories.add(Category(title, movies))
        }

        return categories
    }

    private fun toString(stream: InputStream): String {
        val baos = ByteArrayOutputStream()
        val bytes = ByteArray(1024)
        var read: Int

        while(true) {
            read = stream.read(bytes)
            if(read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }

        return String(baos.toByteArray())
    }
}