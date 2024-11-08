package com.google.devrel.Katty_Zhang_LAB143

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {
    private var interpreter: Interpreter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cargar el modelo TFLite
        try {
            interpreter = Interpreter(loadModelFile("model.tflite"))
        } catch (e: IOException) {
            Log.e("Error", "No se pudo cargar el modelo TFLite: ${e.message}")
            return
        }

        val img: ImageView = findViewById(R.id.imageToLabel)
        val txtOutput: TextView = findViewById(R.id.txtOutput)
        val btn: Button = findViewById(R.id.btnTest)

        // Nombre del archivo de imagen en assets
        val fileName = "Benign_1798_E.jpg"
        val bitmap: Bitmap? = assetsToBitmap(fileName)
        bitmap?.apply {
            img.setImageBitmap(this) // Mostrar la imagen
        } ?: run {
            Log.e("Error", "No se pudo cargar la imagen desde assets.")
            txtOutput.text = "Error al cargar la imagen"
            return
        }

        btn.setOnClickListener {
            if (bitmap != null) {
                // Redimensionar la imagen a 150x150 para que coincida con el modelo
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true)

                // Convertir la imagen a ByteBuffer para TFLite
                val inputByteBuffer = convertBitmapToByteBuffer(resizedBitmap)

                // Ejecutar la inferencia con TFLite
                val output = Array(1) { FloatArray(1) }  // Para resultados binarios (malignant vs benign)

                interpreter?.run(inputByteBuffer, output)

                // Mostrar el resultado
                val result = if (output[0][0] >= 0.5) "malignant" else "benign"
                txtOutput.text = "Predicción: $result"
                Log.d("Inference", "Resultado: $result")
            }
        }
    }

    // Función para cargar el modelo TFLite desde los assets
    @Throws(IOException::class)
    private fun loadModelFile(filename: String): File {
        val file = File(cacheDir, filename)
        if (!file.exists()) {
            assets.open(filename).use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file
    }

    // Función para cargar la imagen desde los assets
    fun Context.assetsToBitmap(fileName: String): Bitmap? {
        return try {
            with(assets.open(fileName)) {
                BitmapFactory.decodeStream(this)
            }
        } catch (e: IOException) {
            Log.e("Error", "Error al cargar la imagen: ${e.message}")
            null
        }
    }

    // Función para convertir la imagen a ByteBuffer
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 150 * 150 * 3) // 150x150, 3 canales
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(150 * 150)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixelIndex = 0
        for (pixel in intValues) {
            byteBuffer.putFloat(((pixel shr 16 and 0xFF) / 255.0f)) // Red
            byteBuffer.putFloat(((pixel shr 8 and 0xFF) / 255.0f))  // Green
            byteBuffer.putFloat(((pixel and 0xFF) / 255.0f))       // Blue
        }

        return byteBuffer
    }

    override fun onDestroy() {
        super.onDestroy()
        interpreter?.close() // Liberar el intérprete al destruir la actividad
    }
}
