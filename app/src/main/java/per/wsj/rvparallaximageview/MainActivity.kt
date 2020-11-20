package per.wsj.rvparallaximageview

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import per.wsj.commonlib.permission.PermissionUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionUtil.with(this)
            .permission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .onGranted {
                Toast.makeText(this, "copy assets/a0.jpg to you sdcard", Toast.LENGTH_LONG).show()
                recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
                recyclerView.adapter = MyAdapter(recyclerView)
            }
            .onDenied {
                Toast.makeText(this, "请授予读写权限", Toast.LENGTH_LONG).show()
            }.start()
    }
}
