package lowblow.annoying_alarm.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import lowblow.annoying_alarm.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPermissionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.drawOverlayButton.setOnClickListener {
            requestDrawOverlayPermission()
        }

        binding.readExternalButton.setOnClickListener {
            requestReadExternalStoragePermission()
        }

    }

    override fun onResume() {
        super.onResume()

        if (Settings.canDrawOverlays(this) and (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            finish()
        }

        if (Settings.canDrawOverlays(this)) {
            binding.drawOverlayButton.text = "승인되었습니다"
            binding.drawOverlayButton.setOnClickListener {  }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.readExternalButton.text = "승인되었습니다"
            binding.readExternalButton.setOnClickListener {  }
        }
    }

    private fun requestDrawOverlayPermission() {
        val uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        startActivity(intent)
    }

    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            MODE_PRIVATE
        )
    }
}