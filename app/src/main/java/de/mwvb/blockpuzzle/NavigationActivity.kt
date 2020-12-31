package de.mwvb.blockpuzzle

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.mwvb.blockpuzzle.cluster.Cluster1Aufdeckungen
import de.mwvb.blockpuzzle.cluster.ClusterViewModel
import de.mwvb.blockpuzzle.persistence.Persistence
import de.mwvb.blockpuzzle.persistence.PlanetAccess
import kotlinx.android.synthetic.main.activity_start.*

/**
 * Navigation activity
 */
class NavigationActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground);
        }

        // build view ----
        clusterView.setClusterViewParent(clusterViewParent)
        clusterView.setSelectTargetButton(selectTarget)
        selectTarget.setOnClickListener { clusterView.selectTarget() }

        // set data ----
        val per = Persistence(this)
        val pa = PlanetAccess(per)
        clusterView.model = ClusterViewModel(pa.spaceObjects, pa.planet, per, resources)

        // ensure new daily planet is visible if player is already in delta quadrant ----
        Cluster1Aufdeckungen(pa.spaceObjects).fix(per)
    }
}
