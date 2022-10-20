package io.github.bradleyiw.ns.client.dashboard.presentation

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.bradleyiw.ns.R
import io.github.bradleyiw.ns.client.calls.presentation.CallLogsListAdapter
import io.github.bradleyiw.ns.client.calls.utils.EmptyCallLogsDataObserver
import io.github.bradleyiw.ns.core.extension.showSnackbar
import io.github.bradleyiw.ns.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard),
    ActivityCompat.OnRequestPermissionsResultCallback {

    @Inject
    lateinit var dashboardViewModel: DashboardViewModel

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS
    )

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsStatusMap ->
            if (permissionsStatusMap.containsValue(false)) {
                binding.root.showSnackbar(
                    R.string.call_monitoring_permissions_denied,
                    Snackbar.LENGTH_INDEFINITE,
                    R.string.ok
                )
            } else {
                onCallMonitoringButtonClicked()
                binding.root.showSnackbar(
                    R.string.call_monitoring_permissions_granted,
                    Snackbar.LENGTH_LONG,
                    R.string.ok
                )
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = dashboardViewModel
        binding.lifecycleOwner = this

        initCallMonitoringButton()
        initAndSubscribeCallLogs()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.onServerDataRequested()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAndSubscribeCallLogs() {
        val recyclerView = binding.callLogsRecyclerView
        val emptyDataObserver = EmptyCallLogsDataObserver(recyclerView, binding)
        val listAdapter = CallLogsListAdapter()
        listAdapter.registerAdapterDataObserver(emptyDataObserver)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        recyclerView.adapter = listAdapter
        lifecycleScope.launch {
            dashboardViewModel.callLogs
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect {
                    listAdapter.submitList(it)
                }
        }
    }

    private fun initCallMonitoringButton() {
        binding.startOrStopServerButton.setOnClickListener {
            checkPermissions {
                onCallMonitoringButtonClicked()
            }
        }
    }

    private fun onCallMonitoringButtonClicked() {
        dashboardViewModel.onMonitoringCallsButtonClicked()
    }

    private fun checkPermissions(block: () -> Unit) {
        if (permissions.all {
                PermissionChecker.checkSelfPermission(
                    requireContext(),
                    it
                ) == PermissionChecker.PERMISSION_GRANTED
            }) {
            block()
        } else {
            requestPermissionsLauncher.launch(permissions)
        }
    }
}
