package com.st10028058.prog7313_part2.ui

import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityExportBinding
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ExportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExportCsv.setOnClickListener {
            exportCSV()
        }

        binding.btnExportPdf.setOnClickListener {
            exportPDF()
        }
        binding.btnBackDashboard.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun exportCSV() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("expenses").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { snapshot ->
                val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloads, "expenses.csv")
                BufferedWriter(FileWriter(file)).use { writer ->
                    writer.write("Date,Category,Description,Amount\n")
                    snapshot.forEach {
                        val e = it.toObject(Expense::class.java)
                        writer.write("${e.date},${e.category},\"${e.description}\",${e.amount}\n")
                    }
                }
                Toast.makeText(this, "CSV saved to Downloads", Toast.LENGTH_SHORT).show()
                openFile(file, "text/csv")
            }
    }

    private fun exportPDF() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("expenses").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { snapshot ->
                val doc = PdfDocument()
                val paint = Paint().apply { textSize = 12f }
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                var page = doc.startPage(pageInfo)
                var y = 30f
                page.canvas.drawText("Expenses Report", 200f, y, paint)
                y += 20f

                snapshot.forEach {
                    val e = it.toObject(Expense::class.java)
                    page.canvas.drawText("${e.date} | ${e.category} | ${e.description} | R${e.amount}", 20f, y, paint)
                    y += 20f
                    if (y > 800f) {
                        doc.finishPage(page)
                        page = doc.startPage(pageInfo)
                        y = 30f
                    }
                }

                doc.finishPage(page)
                val filename = "expenses_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
                doc.writeTo(FileOutputStream(file))
                doc.close()

                Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()
                openFile(file, "application/pdf")
            }
    }

    private fun openFile(file: File, mime: String) {
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mime)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}
