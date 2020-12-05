package de.mwvb.blockpuzzle

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.mwvb.blockpuzzle.cluster.ClusterViewModel
import de.mwvb.blockpuzzle.persistence.Persistence
import de.mwvb.blockpuzzle.persistence.PlanetAccess
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
        clusterView.setClusterViewParent(clusterViewParent)
        clusterView.setSelectTargetButton(selectTarget)
        selectTarget.setOnClickListener { clusterView.selectTarget() }

        // set data ----
        val per = Persistence(this)
        val pa = PlanetAccess(per)
        clusterView.model = ClusterViewModel(pa.planets, pa.planet, per, resources)
    }
}
