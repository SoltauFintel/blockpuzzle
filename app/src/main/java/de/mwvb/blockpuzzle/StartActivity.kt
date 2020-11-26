package de.mwvb.blockpuzzle

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.cluster.Cluster1
import kotlinx.android.synthetic.main.activity_start.*

/**
 * Navigation activity
 */
class StartActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // build view ----
        clusterView.setParent(clusterViewParent)
        clusterView.setSelectTargetButton(selectTarget)
        selectTarget.setOnClickListener { clusterView.selectTarget() }

        // set data ----
        clusterView.cluster = GameState.cluster
    }
}
