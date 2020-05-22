import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.tpv.android.model.network.AgentLocationRequest
import com.tpv.android.ui.home.HomeViewModel
import com.tpv.android.ui.home.clocktime.ClockTimeViewModel

class ClockTimeLocationListener(homeViewModel: HomeViewModel, clockTimeViewModel: ClockTimeViewModel) : LocationListener {

    var mHomeViewModel = homeViewModel
    var mClockTimeViewModel = clockTimeViewModel

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            if (location.hasAccuracy()) {
                mHomeViewModel.location = location
                mClockTimeViewModel.setLocation(AgentLocationRequest(
                        lat = location.latitude?.toString(),
                        lng = location.longitude?.toString()
                ))

            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }
}